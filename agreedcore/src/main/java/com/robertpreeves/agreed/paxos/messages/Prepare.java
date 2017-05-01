package com.robertpreeves.agreed.paxos.messages;

public class Prepare {
    /**
     * The proposed sequence number for the consensus round
     */
    public final long sequenceNumber;

    public Prepare(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    @Override
    public String toString() {
        return String.format("{sequenceNumber: %s}", Long.toUnsignedString(sequenceNumber));
    }
}
