package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

public class LocalPaxosAcceptor<T> implements PaxosAcceptor<T>, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger("[ACCEPTOR]");
    private final PaxosAcceptorState<T> acceptorState;
    private final boolean slow;

    public LocalPaxosAcceptor(PaxosAcceptorState<T> acceptorState, boolean slow) {
        this.acceptorState = acceptorState;
        this.slow = slow;
    }

    /**
     * Gets the most recently promised sequence number
     * @return
     */
    public synchronized long getSequenceNumber() {
        return acceptorState.getPromised();
    }

    @Override
    public Promise prepare(Prepare prepare) {
        slowPause("Acceptor before prepare");

        Promise<T> promise;
        synchronized (this) {
            if (Long.compareUnsigned(prepare.sequenceNumber, acceptorState.getPromised()) > 0) {
                acceptorState.setPromised(prepare.sequenceNumber);
                promise = new Promise(true, acceptorState.getAccepted());
            } else {
                promise = new Promise(false, null);
            }

            LOGGER.info("Prepare: {}\nPromise: {}\nAcceptorState: {}", prepare, promise, this);
        }

        slowPause("Acceptor after prepare");

        return promise;
    }

    @Override
    public Accepted accept(Accept<T> accept) {
        slowPause("Acceptor before accept");

        Accepted accepted;
        synchronized (this) {
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
            accepted = new Accepted(acceptorState.getAccepted().sequenceNumber);
            LOGGER.info("Accept: {}\nAccepted: {}\nAcceptorState: {}", accept, accepted, this);
        }

        slowPause("Acceptor after accept");

        return accepted;
    }

    @Override
    public void commit(Accept<T> committed) {
        slowPause("Acceptor before commit");

        synchronized (this) {
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

            LOGGER.info("Commit: {}\nAcceptorState: {}", committed, this);
        }

        slowPause("Acceptor after commit");
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
    public synchronized void close() throws Exception {
        acceptorState.close();
    }

    private void slowPause(String message) {
        if (slow) {
            System.out.println(String.format("***Break***: %s", message));
            Scanner s = new Scanner(System.in);
            s.nextLine();
        }
    }
}
