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

        Accepted accepted = new Accepted(acceptorState.getAccepted().sequenceNumber);
        LOGGER.info("Accept: {}\nAccepted: {}\nAcceptor: {}", accept, accepted, this);
        return accepted;
    }

    @Override
    public synchronized void commit(Accept<T> accepted) {
        //If this is the current accepted value then move it to the committed state.
        //Other commit values could come from previous rounds so this check is required
        int seqNumCompare = Long.compare(accepted.sequenceNumber,
                acceptorState.getAccepted().sequenceNumber);
        if (seqNumCompare == 0) {
            acceptorState.setAccepted(null);
        } else if (seqNumCompare > 0) {
            throw new IllegalStateException(
                    String.format("Commit value greater than any value that has been accepted. " +
                            "A value cannot be committed without being accepted. " +
                            "Accepted: %s, This: %s", acceptorState.getAccepted(), accepted));
        }
        //since only the current committed value is maintained old commit messages are ignored

        LOGGER.info("Commit: {}\nAcceptor: {}", accepted, this);
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
