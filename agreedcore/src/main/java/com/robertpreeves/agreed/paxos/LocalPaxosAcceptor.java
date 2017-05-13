package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LocalPaxosAcceptor<T> implements PaxosAcceptor<T>, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(LocalPaxosAcceptor.class);
    private final PaxosAcceptorState<T> acceptorState;

    public LocalPaxosAcceptor(PaxosAcceptorState<T> acceptorState) {
        this.acceptorState = acceptorState;
    }

    /**
     * Gets the most recently promised sequence number
     * @return
     */
    public synchronized long getSequenceNumber() {
        return acceptorState.getPromised();
    }

    @Override
    public synchronized Promise prepare(Prepare prepare) {
        Promise<T> promise;
        if (Long.compareUnsigned(prepare.sequenceNumber, acceptorState.getPromised()) > 0) {
            acceptorState.setPromised(prepare.sequenceNumber);
            promise = new Promise(true, acceptorState.getAccepted());
        } else {
            promise = new Promise(false, null);
        }

        LOGGER.info("Prepare: {}\nPromise: {}\nAcceptor: {}", prepare, promise, this);

        return promise;
    }

    @Override
    public synchronized Accepted accept(Accept<T> accept) {
        int seqNumCompare = Long.compare(accept.sequenceNumber, acceptorState.getPromised());
        if (seqNumCompare == 0) {
            //accept value
            acceptorState.setAccepted(accept);
        } else if (seqNumCompare > 0) {
            //accept value
            acceptorState.setAccepted(accept);

            //update promised seq number since this is greater than the current promised seq number
            acceptorState.setPromised(accept.sequenceNumber);
        }

        //Return the current accepted value.
        //The proposer will check the sequence number to see if its value was accepted.
        Accepted accepted = new Accepted(acceptorState.getAccepted().sequenceNumber);
        LOGGER.info("Accept: {}\nAccepted: {}\nAcceptor: {}", accept, accepted, this);
        return accepted;
    }

    @Override
    public synchronized void commit(Accept<T> committed) {
        int seqNumCompare = Long.compare(committed.sequenceNumber, acceptorState.getPromised());
        if (seqNumCompare == 0) {
           //commit value
            acceptorState.setCommitted(committed);
        } else if (seqNumCompare > 0) {
            //commit value
            acceptorState.setCommitted(committed);

            //set promised value since it is greater than the current one
            acceptorState.setPromised(committed.sequenceNumber);
        }

        //Clear the current accepted value if this is greater.
        //Once we've committed a newer value the old accepted value is invalid.
        //Only the current value is maintained in the implementation.
        Accept<T> accepted = acceptorState.getAccepted();
        if (accepted != null) {
            int accSeqNumCmp = Long.compare(committed.sequenceNumber, accepted.sequenceNumber);
            if (accSeqNumCmp >= 0) {
                acceptorState.setAccepted(null);
            }
        }

        LOGGER.info("Commit: {}\nAcceptor: {}", committed, this);
    }

    @Override
    public synchronized Accept<T> getCurrent() {
        return acceptorState.getCommitted();
    }

    @Override
    public synchronized String toString() {
        return acceptorState.toString();
    }

    @Override
    public void close() throws Exception {
        acceptorState.close();
    }
}
