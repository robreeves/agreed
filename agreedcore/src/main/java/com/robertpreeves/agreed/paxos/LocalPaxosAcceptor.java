package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalPaxosAcceptor<T> implements PaxosAcceptor<T> {
    private static final Logger LOGGER = LogManager.getLogger(LocalPaxosAcceptor.class);

    /**
     * The most recently promised sequence number.
     */
    private long promisedSequenceNumber;

    /**
     * The current accepted value.
     */
    private Accept<T> acceptedValue;

    @Override
    public synchronized Promise prepare(Prepare prepare) {
        Promise<T> promise;
        if (Long.compareUnsigned(prepare.sequenceNumber, promisedSequenceNumber) > 0) {
            promisedSequenceNumber = prepare.sequenceNumber;
            promise = new Promise(true, acceptedValue);
        } else {
            promise = new Promise(false, null);
        }

        LOGGER.info("Prepare: {}\nPromise: {}\nAcceptor: {}", prepare, promise, this);

        return promise;
    }

    @Override
    public synchronized Accepted accept(Accept<T> accept) {
        int seqNumCompare = Long.compare(accept.sequenceNumber, promisedSequenceNumber);
        if (seqNumCompare == 0) {
            //accept value
            acceptedValue = accept;
        } else if (seqNumCompare > 0) {
            //this is unexpected. this means the prepare message was never received for this
            //sequence number. the proposer should not send an accept message if the prepare
            //message never received a promise from the acceptor.
            throw new IllegalStateException(
                    String.format("no promise received for this sequence number. " +
                                    "current sequence number: %s, accept received: %s",
                            accept.sequenceNumber,
                            accept));
        }
        //if the accept sequence number is less don't update the acceptedValue and tell the proposer
        //about the higher sequence number that has been accepted

        Accepted accepted = new Accepted(acceptedValue.sequenceNumber);
        LOGGER.info("Accept: {}\nAccepted: {}\nAcceptor: {}", accept, accepted, this);
        return accepted;
    }

    @Override
    public synchronized void commit(Accepted accepted) {
        LOGGER.info("Commit: {}\nAcceptor: {}", accepted, this);
        //todo
        accepted = null;
    }

    @Override
    public synchronized Accept<T> getAccepted() {
        return acceptedValue;
    }

    @Override
    public synchronized String toString() {
        return String.format("{promisedSequenceNumber: %s, acceptedValue: %s}",
                promisedSequenceNumber,
                acceptedValue);
    }
}
