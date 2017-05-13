package com.robertpreeves.agreed.paxos;

import com.google.gson.Gson;
import com.robertpreeves.agreed.NoConsensusException;
import com.robertpreeves.agreed.paxos.messages.Accept;
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

public class PaxosHttpAcceptor<T> implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(PaxosHttpAcceptor.class);
    private static final Gson GSON = new Gson();
    private static final String MIME_JSON = "application/json";
    private final PaxosAcceptor<T> acceptor;
    private final Service httpSvr = Service.ignite();

    public PaxosHttpAcceptor(int port, PaxosAcceptor<T> acceptor) {
        this.acceptor = acceptor;
        initServer(port);
    }

    private void initServer(int port) {
        httpSvr.port(port);

        httpSvr.post(Uris.PREPARE, MIME_JSON, (request, response) ->
                        process(request, response, Prepare.class, acceptor::prepare),
                GSON::toJson);

        httpSvr.post(Uris.ACCEPT, MIME_JSON, (request, response) ->
                        process(request, response, Accept.class, acceptor::accept),
                GSON::toJson);

        httpSvr.post(
                Uris.COMMIT, MIME_JSON,
                (request, response) -> {
                    process(request, response, Accept.class, accepted -> {
                        try {
                            acceptor.commit(accepted);
                        } catch (NoConsensusException e) {
                            response.status(500);
                        }
                        return null;
                    });

                    return response;
                }
        );

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

    @Override
    public void close() {
        httpSvr.stop();
    }
}
