package com.robertpreeves.agreed.paxos;

import com.google.gson.Gson;
import com.robertpreeves.agreed.paxos.messages.Accept;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.Serializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;

public class MapDbAcceptorState<T> implements PaxosAcceptorState<T> {
    private static final Logger LOGGER = LogManager.getLogger(MapDbAcceptorState.class);
    private static final Gson GSON = new Gson();
    private static final String PROMISED = "promised";
    private static final String ACCEPTED = "accepted";
    private static final String COMMITTED = "committed";
    private static final String DIR = "acceptor";
    private final DB db;
    private final Map<String, byte[]> mapStore;

    public MapDbAcceptorState(byte nodeId) {
        File dir = new File(String.format("%s/%s", DIR, nodeId));
        dir.mkdirs();

        String filePath = String.format("%s/acceptor.db", dir.getAbsolutePath());

        db = DBMaker
                .fileDB(filePath)
                .fileMmapEnable()
                .transactionEnable() //reduces change of corruption from crash without closing
                .make();

        mapStore = db.hashMap("map", Serializer.STRING, Serializer.BYTE_ARRAY)
                .createOrOpen();

        LOGGER.info("Acceptor state stored at {}.\nStarting state {}",
                new File(filePath).getAbsolutePath(), this);
    }

    @Override
    public long getPromised() {
        byte[] bytes = mapStore.get(PROMISED);
        if (bytes != null) {
            return ByteBuffer.wrap(bytes).getLong();
        } else {
            return 0l;
        }
    }

    @Override
    public void setPromised(long seqNumber) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(seqNumber);
        mapStore.put(PROMISED, buffer.array());
        db.commit();
    }

    @Override
    public Accept<T> getAccepted() {
        return get(ACCEPTED);
    }

    @Override
    public void setAccepted(Accept<T> accepted) {
        mapStore.put(ACCEPTED, serialize(accepted));
        db.commit();
    }

    @Override
    public Accept<T> getCommitted() {
        return get(COMMITTED);
    }

    @Override
    public void commitAccepted() {
        byte[] accepted = mapStore.remove(ACCEPTED);
        mapStore.put(COMMITTED, accepted);
        db.commit();
    }

    @Override
    public void close() throws Exception {
        db.close();
    }

    private byte[] serialize(Accept<T> accept) {
        //todo do this in a more efficient way
        String json = GSON.toJson(accept);
        return json.getBytes();
    }

    private Accept<T> get(String key) {
        byte[] bytes = mapStore.get(key);
        if (bytes != null) {
            return deserialize(bytes);
        } else {
            return null;
        }
    }

    private Accept<T> deserialize(byte[] acceptBytes) {
        try (InputStream in = new ByteArrayInputStream(acceptBytes);
             InputStreamReader reader = new InputStreamReader(in);
             BufferedReader buffer = new BufferedReader(reader)) {
            return GSON.fromJson(buffer, Accept.class);
        } catch (IOException e) {
            LOGGER.error("Error deserializing", e);
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("{promised: %s, accepted: %s, committed: %s}",
                getPromised(),
                getAccepted(),
                getCommitted());
    }
}
