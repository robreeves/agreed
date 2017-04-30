package com.robertpreeves.lock;


import com.google.gson.Gson;
import com.robertpreeves.agreed.AgreedNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import spark.Request;
import spark.Response;
import spark.Route;
import spark.Service;

import static spark.Service.ignite;

public class PublicApi {
    private static final Logger LOGGER = LogManager.getLogger(PublicApi.class);
    private static final String FILE_PARAM = ":fileName";
    private static final String CONTENT_PARAM = ":content";
    private static final Gson GSON = new Gson();
    private static final String MIME_JSON = "application/json";
    private static final String MIME_TEXT = "text/plain";

    private final AgreedNode<FileLock> agreedNode;
    private final ConcurrentHashMap<String, FileLock> locks = new ConcurrentHashMap<>();

    public PublicApi(int port, AgreedNode<FileLock> agreedNode) {
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
            FileLock lock = lock(fileName);
            if (lock != null) {
                try {
                    response.type(MIME_JSON);
                    return postToFile(fileName, request.params(CONTENT_PARAM));
                }
                finally {
                    unlock(lock);
                }
            } else {
                response.status(503); //todo
                return null;
            }
        }, GSON::toJson);

        //get file content
        http.get(String.format("/api/file/%s", FILE_PARAM), this::readFile);

        http.awaitInitialization();

        LOGGER.info("Listening on {}", port);
    }

    /**
     * Attempts to obtain the lock for the file.
     * @param fileName
     * @return If the lock is obtained, the lock is returned.
     * If the lock is not obtained, null is returned.
     */
    private FileLock lock(String fileName) {
        FileLock lock = locks.get(fileName);
        if (lock == null) {
            //file not locked
            //attempt to lock file
            FileLock newLock = new FileLock(fileName);
            agreedNode.propose(newLock);

            //check if locked
            lock = locks.get(fileName);
            if (lock == null) {
                //this is probably a bug
                //log it to make troubleshooting easier
                LOGGER.info("File lock for '{}' not obtained. Lock is still free.",
                        fileName);
                return null;

            } else if (!lock.getLockId().equals(newLock.getLockId())) {
                //another request got the lock
                LOGGER.info("File lock for '{}' not obtained. Lock id '{}' has it.",
                        fileName, lock.getLockId());
                return null;
            } else {
                LOGGER.info("File lock for '{}' obtained. Lock id '{}'.",
                        fileName, lock.getLockId());
                return newLock;
            }
        } else {
            //file already locked
            LOGGER.info("File lock for '{}' not available. Lock id '{}' has it.",
                    fileName, lock.getLockId());
            return null;
        }
    }

    private void unlock(FileLock lock) {

        //todo
        //LOGGER.info("File lock released for '{}'", fileName);
    }

    private Object lockedInvoke(Request request, Response response, RequestHandler handler) {
        String fileName = request.params(FILE_PARAM);
        FileLock lock = lock(fileName);
        if (lock != null) {
            try {
                return handler.handle(fileName, request, response);
            } finally {
                unlock(lock);
            }
        } else {
            response.body("Couldn't obtain lock");
            response.status(503);
            return null;
        }
    }

    private interface RequestHandler {
        Object handle(String fileName, Request request, Response response);
    }

    private Object readFile(Request request, Response response) {
        return lockedInvoke(request, response, (req, res, fileName) -> {
            //set response content type
            response.type(MIME_TEXT);

            //write file content to body
            try (PrintWriter writer = response.raw().getWriter()) {
                writer.println("file name: " + fileName);
                writer.println("todo");
            } catch (IOException e) {
                LOGGER.error(e);
                response.status(500);
            }

            return null;
        });
    }

    private UpdateFileResponse postToFile(String fileName, String content) {
        return new UpdateFileResponse(fileName, content);
    }

    private void lockUpdate(FileLock lockUpdate) {

    }
}
