package com.robertpreeves.agreed.paxos.messages;

public class Promise<T> {
    /**
     * True if the acceptor has promised to accept value from proposer and ignore lower sequence
     * numbers. False if it has seen a greater sequence number already.
     */
    public final boolean promised;

    /**
     * If the node has accepted a value aready, the value it has accepted.
     * If it has not accepted a value this will be null.
     */
    public final Accept<T> acceptedValue;

    public Promise(boolean promised, Accept<T> acceptedValue) {
        this.promised = promised;
        this.acceptedValue = acceptedValue;
    }

    @Override
    public String toString() {
        return String.format("{promised: %s, acceptedValue: %s}", promised, acceptedValue);
    }
}
