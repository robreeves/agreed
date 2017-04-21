package com.robertpreeves.lock;

import com.robertpreeves.agreed.AgreedNode;

import java.util.HashMap;
import java.util.Map;

public class LockNode {
    private final Map<String, LockState> locks = new HashMap<String, LockState>();
    private final AgreedNode<LockState> lockNegotiator;

    public LockNode(AgreedNode<LockState> lockNegotiator) {
        this.lockNegotiator = lockNegotiator;
    }
}
