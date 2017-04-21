package com.robertpreeves.lock;

public class LockState {
    private final String id;
    private final Boolean locked;

    public LockState(String id, Boolean locked) {
        this.id = id;
        this.locked = locked;
    }

    public String getId() {
        return id;
    }

    public Boolean isLocked() {
        return locked;
    }
}
