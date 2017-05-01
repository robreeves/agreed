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
     * The most recently accepted value.
     * This is updated when a value is accepted.
     */
    private Accept<T> acceptedValue;

    @Override
    public synchronized Promise prepare(Prepare prepare) {
        if (Long.compareUnsigned(prepare.sequenceNumber, currentSeqNumber) > 0) {
            currentSeqNumber = prepare.sequenceNumber;
            return new Promise(true, acceptedValue);
        } else {
            return new Promise(false, null);
        }
    }

    @Override
    public synchronized Accepted accept(Accept accept) {
        int seqNumCompare = Long.compare(accept.sequenceNumber, currentSeqNumber);
        if (seqNumCompare == 0) {
            acceptedValue = accept;
        } else if (seqNumCompare > 0) {
            //this is unexpected. this means the prepare message was never received for this
            //sequence number. the proposer should not send an accept message if the prepare
            //message never received a promise from the acceptor.
            //todo throw exception?
        }

        return new Accepted(acceptedValue.sequenceNumber);
    }
}
