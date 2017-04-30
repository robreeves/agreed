package com.robertpreeves.agreed.paxos;

import com.google.gson.Gson;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Commit;
import com.robertpreeves.agreed.paxos.messages.Prepare;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;

import spark.Request;
import spark.Response;
import spark.Service;

public class PaxosHttpAcceptor<T> {
    private static final Logger LOGGER = LogManager.getLogger(PaxosHttpAcceptor.class);
    private static final Gson GSON = new Gson();
    private static final String MIME_JSON = "application/json";
    private final PaxosNode<T> node;
    private final Service httpSvr = Service.ignite();

    public PaxosHttpAcceptor(int port, PaxosNode<T> node) {
        this.node = node;
        initServer(port);
    }

    private void initServer(int port) {
        httpSvr.port(port);

        httpSvr.post(Uris.PREPARE, MIME_JSON, (request, response) ->
                        process(request, response, Prepare.class, node::prepare),
                GSON::toJson);

        httpSvr.post(Uris.ACCEPT, MIME_JSON, (request, response) ->
                        process(request, response, Accept.class, node::accept),
                GSON::toJson);

        httpSvr.post(Uris.COMMIT, MIME_JSON, (request, response) ->
                        process(request, response, Commit.class, node::commit),
                GSON::toJson);

        httpSvr.awaitInitialization();
        LOGGER.info("Paxos HTTP acceptor listening on {}", port);
    }

    private <TIn, TOut> Object process(
            Request request,
            Response response,
            Class<TIn> inClass,
            Function<TIn, TOut> func) throws IOException {

        TIn in;
        try (InputStreamReader reader = new InputStreamReader(request.raw().getInputStream());
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            in = GSON.fromJson(bufferedReader, inClass);
        }

        if (in != null) {
            response.type(MIME_JSON);
            return func.apply(in);
        } else {
            response.status(400);
            return String.format("Invalid JSON for input type %s", inClass.getSimpleName());
        }
    }

    public PaxosNode<T> getNode() {
        return node;
    }
}
