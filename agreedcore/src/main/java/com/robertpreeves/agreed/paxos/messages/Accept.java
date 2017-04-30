package com.robertpreeves.agreed.paxos.messages;

public class Accept<T> extends Prepare {
    /**
     * The value that is being proposed
     */
    public final T value;

    public Accept(long sequenceNumber, T value) {
        super(sequenceNumber);
        this.value = value;
    }
}
