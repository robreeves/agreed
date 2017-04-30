package com.robertpreeves.agreed.paxos.messages;

public class Accepted {
    /**
     * The sequence number of the currently accepted value.
     * The proposer can use this to see if its message was accepted.
     */
    public final long sequenceNumber;

    public Accepted(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
