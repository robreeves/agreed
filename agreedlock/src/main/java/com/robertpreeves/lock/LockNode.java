package com.robertpreeves.lock;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.observer.Observer;

import java.util.HashMap;
import java.util.Map;

public class LockNode implements Observer<LockState> {
    private final Map<String, LockState> locks = new HashMap<String, LockState>();
    private final AgreedNode<LockState> lockNegotiator;

    public LockNode(AgreedNode<LockState> lockNegotiator) {
        this.lockNegotiator = lockNegotiator;
    }

    public void notify(LockState value) {
        //todo update locks state
    }
}
