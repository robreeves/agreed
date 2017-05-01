package com.robertpreeves.agreed.paxos.messages;

public class Accept<T> {
    /**
     * The proposed sequence number for the consensus round
     */
    public final long sequenceNumber;

    /**
     * The value that is being proposed
     */
    public final T value;

    public Accept(long sequenceNumber, T value) {
        this.sequenceNumber = sequenceNumber;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("{sequenceNumber: %s, value: %s}",
                Long.toUnsignedString(sequenceNumber),
                value
        );
    }
}
