package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

public class LocalPaxosAcceptor<T> implements PaxosAcceptor<T> {
    /**
     * The most recently seen acceptance number.
     * This is updated in the prepare phase.
     */
    private long currentSeqNumber;

    /**
     * The most recently accepted sequence number.
     * This is updated when a value is accepted.
     */
    private long acceptedSeqNumber;

    /**
     * The most recently accepted value.
     * This is updated when a value is accepted.
     */
    private T acceptedValue;

    @Override
    public Promise prepare(Prepare prepare) {
        return null;
    }

    @Override
    public Accepted accept(Accept accept) {
        return null;
    }
}
