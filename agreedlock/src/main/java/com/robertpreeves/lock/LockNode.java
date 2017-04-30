package com.robertpreeves.lock;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.observer.Observer;

import java.util.HashMap;
import java.util.Map;

public class LockNode implements Observer<FileLock> {
    private final Map<String, FileLock> locks = new HashMap<String, FileLock>();
    private final AgreedNode<FileLock> lockNegotiator;

    public LockNode(AgreedNode<FileLock> lockNegotiator) {
        this.lockNegotiator = lockNegotiator;
    }

    public void notify(FileLock value) {
        //todo update locks state
    }
}
