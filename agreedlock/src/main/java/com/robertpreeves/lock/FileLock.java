package com.robertpreeves.lock;

import java.util.UUID;

public class FileLock {
    private final String fileId;
    private final String lockId;

    public FileLock(String fileId) {
        this(fileId, UUID.randomUUID().toString());
    }

    private FileLock(String fileId, String lockId) {
        this.fileId = fileId;
        this.lockId = lockId;
    }

    public String getFileId() {
        return fileId;
    }

    /**
     * Gets the lock Id
     *
     * @return If the Id is non-null, the file is locked. If the Id is null the file is unlocked.
     */
    public String getLockId() {
        return lockId;
    }

    @Override
    public String toString() {
        return String.format("{fileId: '%s', lockId: '%s'}", fileId, lockId);
    }

    public FileLock unlock() {
        return new FileLock(fileId, null);
    }
}
