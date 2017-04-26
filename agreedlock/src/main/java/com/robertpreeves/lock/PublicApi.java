package com.robertpreeves.lock;


import com.google.gson.Gson;

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

    public PublicApi(int port) {

        Service http = ignite()
                .port(port);

        //post an update to a file
        http.post(String.format("/api/file/%s/%s", FILE_PARAM, CONTENT_PARAM), (request,
                response) -> {
            response.type(MIME_JSON);
            return postToFile(request.params(FILE_PARAM), request.params(CONTENT_PARAM));
        }, GSON::toJson);

        //get file content
        http.get(String.format("/api/file/%s", FILE_PARAM), (request, response) -> {
            response.type(MIME_TEXT);
            try (PrintWriter writer = response.raw().getWriter()) {
                readFile(request.params(FILE_PARAM), writer);
            }

            return null;
        });

        http.awaitInitialization();

        LOGGER.info("Listening on {}", port);
    }

    public void readFile(String fileName, PrintWriter writer) {
        writer.println("file name: " + fileName);
        writer.println("todo");
    }

    public UpdateFileResponse postToFile(String fileName, String content) {
        return new UpdateFileResponse(fileName, content);
    }
}
