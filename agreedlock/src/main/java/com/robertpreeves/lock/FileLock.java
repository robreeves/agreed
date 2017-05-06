package com.robertpreeves.lock;

import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

public class FileLock {
    private String lockId;

    public boolean isLocked() {
        return StringUtils.isNoneBlank(lockId);
    }

    public FileLock lock() {
        if (isLocked()) {
            throw new IllegalStateException();
        }

        lockId = UUID.randomUUID().toString();
        return this;
    }

    public String getLockId() {
        return lockId;
    }

    public FileLock unlock() {
        if (!isLocked()) {
            throw new IllegalStateException();
        }

        lockId = null;
        return this;
    }

    @Override
    public String toString() {
        return String.format("{lockId: %s}", lockId);
    }
}
