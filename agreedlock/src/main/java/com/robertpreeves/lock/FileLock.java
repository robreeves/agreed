package com.robertpreeves.lock;

import java.util.UUID;

public class FileLock {
    private final String fileId;
    private final String lockId = UUID.randomUUID().toString();

    public FileLock(String fileId) {
        this.fileId = fileId;
    }

    public String getFileId() {
        return fileId;
    }

    public String getLockId() {
        return lockId;
    }
}
