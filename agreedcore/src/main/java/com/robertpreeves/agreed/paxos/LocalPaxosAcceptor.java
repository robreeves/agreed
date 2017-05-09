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
    private Accept<T> currentAcceptedValue;

    @Override
    public synchronized Promise prepare(Prepare prepare) {
        Promise<T> promise;
        if (Long.compareUnsigned(prepare.sequenceNumber, promisedSequenceNumber) > 0) {
            promisedSequenceNumber = prepare.sequenceNumber;
            promise = new Promise(true, currentAcceptedValue);
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
            currentAcceptedValue = accept;
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
        //if the accept sequence number is less don't update the currentAcceptedValue and tell
        // the proposer
        //about the higher sequence number that has been accepted

        Accepted accepted = new Accepted(currentAcceptedValue.sequenceNumber);
        LOGGER.info("Accept: {}\nAccepted: {}\nAcceptor: {}", accept, accepted, this);
        return accepted;
    }

    @Override
    public synchronized void commit(Accept<T> accepted) {
        //todo
        currentAcceptedValue = null;
        LOGGER.info("Commit: {}\nAcceptor: {}", accepted, this);
    }

    @Override
    public synchronized Accept<T> getCurrent() {
        return currentAcceptedValue;
    }

    @Override
    public synchronized String toString() {
        return String.format("{promisedSequenceNumber: %s, currentAcceptedValue: %s}",
                promisedSequenceNumber,
                currentAcceptedValue);
    }
}
