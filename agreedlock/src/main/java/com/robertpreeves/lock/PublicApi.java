package com.robertpreeves.lock;


import com.google.gson.Gson;
import com.robertpreeves.agreed.AgreedNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;

import spark.Service;

import static spark.Service.ignite;

public class PublicApi {
    private static final Logger LOGGER = LogManager.getLogger(PublicApi.class);
    private static final String FILE_PARAM = ":fileName";
    private static final String CONTENT_PARAM = ":content";
    private static final Gson GSON = new Gson();
    private static final String MIME_JSON = "application/json";
    private static final String MIME_TEXT = "text/plain";

    private final AgreedNode<LockState> agreedNode;

    public PublicApi(int port, AgreedNode<LockState> agreedNode) {
        this.agreedNode = agreedNode;

        //Subscribe to lock state updates
        agreedNode.subscribe(update -> lockUpdate(update));

        initHttpAPI(port);
    }

    private void initHttpAPI(int port) {
        Service http = ignite()
                .port(port);

        //post an update to a file
        http.post(String.format("/api/file/%s/%s", FILE_PARAM, CONTENT_PARAM), (request,
                response) -> {
            String fileName = request.params(FILE_PARAM);
            if (lock(fileName)) {
                try {
                    response.type(MIME_JSON);
                    return postToFile(fileName, request.params(CONTENT_PARAM));
                }
                finally {
                    unlock(fileName);
                }
            } else {
                response.status(503); //todo
                return null;
            }
        }, GSON::toJson);

        //get file content
        http.get(String.format("/api/file/%s", FILE_PARAM), (request, response) -> {
            String fileName = request.params(FILE_PARAM);
            if (lock(fileName)) {
                try {
                    response.type(MIME_TEXT);
                    try (PrintWriter writer = response.raw().getWriter()) {
                        readFile(fileName, writer);
                    }

                    return null;
                } finally {
                    unlock(fileName);
                }
            } else {
                response.status(503); //todo
                return null;
            }
        });

        http.awaitInitialization();

        LOGGER.info("Listening on {}", port);
    }

    private boolean lock(String fileName) {
        boolean lockObtained = true;
        //todo
        LOGGER.info("File lock request for '{}'. Lock obtained: {}", fileName, lockObtained);
        return lockObtained;
    }

    private void unlock(String fileName) {
        //todo
        LOGGER.info("File lock released for '{}'", fileName);
    }

    private void readFile(String fileName, PrintWriter writer) {
        writer.println("file name: " + fileName);
        writer.println("todo");
    }

    private UpdateFileResponse postToFile(String fileName, String content) {
        return new UpdateFileResponse(fileName, content);
    }

    private void lockUpdate(LockState lockUpdate) {

    }
}
