package com.robertpreeves.lock;


import com.google.gson.Gson;
import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.NoConsensusException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.ConcurrentHashMap;

import spark.Request;
import spark.Response;
import spark.Service;

import static spark.Service.ignite;

public class PublicApi {
    private static final Logger LOGGER = LogManager.getLogger(PublicApi.class);
    private static final String FILE_PARAM = ":fileName";
    private static final String CONTENT_PARAM = ":content";
    private static final String MIME_TEXT = "text/plain";

    private final AgreedNode<FileLock> agreedNode;

    public PublicApi(int port, AgreedNode<FileLock> agreedNode) {
        this.agreedNode = agreedNode;
        initHttpAPI(port);
    }

    private void initHttpAPI(int port) {
        Service http = ignite()
                .port(port);

        //post an update to a file
        http.post(String.format("/api/file/%s/%s", FILE_PARAM, CONTENT_PARAM), this::updateFile);

        //get file content
        http.get(String.format("/api/file/%s", FILE_PARAM), this::readFile);

        http.awaitInitialization();

        LOGGER.info("Listening on {}", port);
    }

//    /**
//     * Attempts to obtain the lock for the file.
//     *
//     * @return If the lock is obtained, the lock is returned. If the lock is not obtained, null is
//     * returned.
//     */
//    private FileLock lock(String fileName) {
//        FileLock lock = locks.get(fileName);
//        if (lock == null) {
//            //file not locked
//            //attempt to lock file
//            FileLock newLock = new FileLock(fileName);
//            agreedNode.propose(newLock);
//
//            //check if locked
//            lock = locks.get(fileName);
//            if (lock == null) {
//                //this is probably a bug
//                //log it to make troubleshooting easier
//                LOGGER.info("File lock for '{}' not obtained. Lock is still free.",
//                        fileName);
//                return null;
//
//            } else if (!lock.getLockId().equals(newLock.getLockId())) {
//                //another request got the lock
//                LOGGER.info("File lock for '{}' not obtained. Lock id '{}' has it.",
//                        fileName, lock.getLockId());
//                return null;
//            } else {
//                LOGGER.info("File lock obtained. {}", lock);
//                return newLock;
//            }
//        } else {
//            //file already locked
//            LOGGER.info("File lock for '{}' not available. Lock id '{}' has it.",
//                    fileName, lock.getLockId());
//            return null;
//        }
//    }
//
//    private void unlock(FileLock lock) {
//        //verify this is the correct lock
//        //this is likely a bug if not
//        FileLock currentLock = locks.get(lock.getFileId());
//        if (currentLock == null || !currentLock.getLockId().equals(lock.getLockId())) {
//            throw new IllegalStateException(String.format("Cant unlock because it doesnt have " +
//                    "lock. Current %s. This %s", currentLock, lock));
//        }
//
//        //unlock
//        agreedNode.propose(lock.unlock());
//
//        //verify lock is released
//        currentLock = locks.get(lock.getFileId());
//        if (currentLock != null && currentLock.getLockId().equals(lock.getLockId())) {
//            throw new IllegalStateException(String.format("Lock not released %s", lock));
//        }
//
//        LOGGER.info("File lock released. {}", lock);
//    }

    private FileLock lock() throws Exception {
        FileLock lock = agreedNode.getCurrent();
        if (lock == null) {
            lock = new FileLock();
        }

        if (!lock.isLocked()) {
            //lock is free
            //attempt to lock it
            if (!agreedNode.propose(lock.lock())) {
                //todo retry?
                throw new Exception("couldnt get lock");
            }

            return lock;
        } else {
            //lock is taken
            return null;
        }
    }

    private void unlock(FileLock lock) throws NoConsensusException {
        //this is not required, but do it as a safety check
        FileLock currentLock = agreedNode.getCurrent();
        if (!lock.getLockId().equals(currentLock.getLockId())) {
            throw new IllegalStateException(String.format("Unexpected lock %s. Should be %s",
                    currentLock.getLockId(), lock.getLockId()));
        }

        //unlock
        agreedNode.propose(lock.unlock());
    }

    private Object lockedInvoke(Request request, Response response, RequestHandler handler) {
        String fileName = request.params(FILE_PARAM);
        FileLock lock;
        try {
            lock = lock();
        } catch (Exception e) {
            LOGGER.error("Error getting lock", e);
            response.status(503);
            return "Could not get the lock";
        }

        if (lock != null) {
            try {
                return handler.handle(request, response, fileName);
            } finally {
                try {
                    unlock(lock);
                } catch (NoConsensusException e) {
                    //todo this is really and and needs to be handled in a better way
                    //need to give back lock always
                    e.printStackTrace();
                }
            }
        } else {
            response.status(503);
            return "Lock not free";
        }
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

    private Object updateFile(Request request, Response response) {
        return lockedInvoke(request, response, (req, res, fileName) -> {
            //todo update file

            response.status(200);
            return "updated";
        });
    }

    private interface RequestHandler {
        Object handle(Request request, Response response, String fileName);
    }
}


//package com.robertpreeves.lock;
//
//
//import com.google.gson.Gson;
//import com.robertpreeves.agreed.AgreedNode;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import java.io.IOException;
//import java.io.PrintWriter;
//import java.util.concurrent.ConcurrentHashMap;
//
//import spark.Request;
//import spark.Response;
//import spark.Service;
//
//import static spark.Service.ignite;
//
//public class PublicApi {
//    private static final Logger LOGGER = LogManager.getLogger(PublicApi.class);
//    private static final String FILE_PARAM = ":fileName";
//    private static final String CONTENT_PARAM = ":content";
//    private static final String MIME_TEXT = "text/plain";
//
//    private final AgreedNode<ConcurrentHashMap<String, FileLock>> agreedNode;
//    private ConcurrentHashMap<String, FileLock> locks = new ConcurrentHashMap<>();
//
//    public PublicApi(int port, AgreedNode<ConcurrentHashMap<String, FileLock>> agreedNode) {
//        this.agreedNode = agreedNode;
//
//        //Subscribe to lock state updates
//        agreedNode.subscribe(update -> lockUpdate(update));
//
//        initHttpAPI(port);
//    }
//
//    private void initHttpAPI(int port) {
//        Service http = ignite()
//                .port(port);
//
//        //post an update to a file
//        http.post(String.format("/api/file/%s/%s", FILE_PARAM, CONTENT_PARAM), this::updateFile);
//
//        //get file content
//        http.get(String.format("/api/file/%s", FILE_PARAM), this::readFile);
//
//        http.awaitInitialization();
//
//        LOGGER.info("Listening on {}", port);
//    }
//
//    /**
//     * Attempts to obtain the lock for the file.
//     *
//     * @return If the lock is obtained, the lock is returned. If the lock is not obtained, null is
//     * returned.
//     */
//    private FileLock lock(String fileName) {
//        FileLock lock = locks.get(fileName);
//        if (lock == null) {
//            //file not locked
//            //attempt to lock file
//            FileLock newLock = new FileLock(fileName);
//            agreedNode.propose(newLock);
//
//            //check if locked
//            lock = locks.get(fileName);
//            if (lock == null) {
//                //this is probably a bug
//                //log it to make troubleshooting easier
//                LOGGER.info("File lock for '{}' not obtained. Lock is still free.",
//                        fileName);
//                return null;
//
//            } else if (!lock.getLockId().equals(newLock.getLockId())) {
//                //another request got the lock
//                LOGGER.info("File lock for '{}' not obtained. Lock id '{}' has it.",
//                        fileName, lock.getLockId());
//                return null;
//            } else {
//                LOGGER.info("File lock obtained. {}", lock);
//                return newLock;
//            }
//        } else {
//            //file already locked
//            LOGGER.info("File lock for '{}' not available. Lock id '{}' has it.",
//                    fileName, lock.getLockId());
//            return null;
//        }
//    }
//
//    private void unlock(FileLock lock) {
//        //verify this is the correct lock
//        //this is likely a bug if not
//        FileLock currentLock = locks.get(lock.getFileId());
//        if (currentLock == null || !currentLock.getLockId().equals(lock.getLockId())) {
//            throw new IllegalStateException(String.format("Cant unlock because it doesnt have " +
//                    "lock. Current %s. This %s", currentLock, lock));
//        }
//
//        //unlock
//        agreedNode.propose(lock.unlock());
//
//        //verify lock is released
//        currentLock = locks.get(lock.getFileId());
//        if (currentLock != null && currentLock.getLockId().equals(lock.getLockId())) {
//            throw new IllegalStateException(String.format("Lock not released %s", lock));
//        }
//
//        LOGGER.info("File lock released. {}", lock);
//    }
//
//    private Object lockedInvoke(Request request, Response response, RequestHandler handler) {
//        String fileName = request.params(FILE_PARAM);
//        FileLock lock = lock(fileName);
//        if (lock != null) {
//            try {
//                return handler.handle(request, response, fileName);
//            } finally {
//                unlock(lock);
//            }
//        } else {
//            response.status(503);
//            return "Couldn't obtain lock";
//        }
//    }
//
//    private Object readFile(Request request, Response response) {
//        return lockedInvoke(request, response, (req, res, fileName) -> {
//            //set response content type
//            response.type(MIME_TEXT);
//
//            //write file content to body
//            try (PrintWriter writer = response.raw().getWriter()) {
//                writer.println("file name: " + fileName);
//                writer.println("todo");
//            } catch (IOException e) {
//                LOGGER.error(e);
//                response.status(500);
//            }
//
//            return null;
//        });
//    }
//
//    private Object updateFile(Request request, Response response) {
//        return lockedInvoke(request, response, (req, res, fileName) -> {
//            //todo update file
//
//            response.status(200);
//            return "updated";
//        });
//    }
//
//    private void lockUpdate(ConcurrentHashMap<String, FileLock> locks) {
//        this.locks = locks;
//    }
//
//    private interface RequestHandler {
//        Object handle(Request request, Response response, String fileName);
//    }
//}
