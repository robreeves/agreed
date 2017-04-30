package com.robertpreeves.agreed.paxos;

public class PaxosValue<T> {
    public final T value;
    public final long sequenceNumber;

    public PaxosValue(T value, long sequenceNumber) {
        this.value = value;
        this.sequenceNumber = sequenceNumber;
    }
}
