package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.NoConsensusException;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import java.util.Random;

public class PaxosProposer<T> implements AutoCloseable {
    private static final Random RANDOM = new Random();
    private final PaxosAcceptorsProxy<T> acceptorsProxy;
    private final byte nodeId;

    public PaxosProposer(byte nodeId, PaxosAcceptorsProxy<T> acceptorsProxy) {
        this.nodeId = nodeId;
        this.acceptorsProxy = acceptorsProxy;
    }

    public synchronized T propose(T value) throws NoConsensusException {
        randomWait();

        //prepare message
        long sequenceNumber = SequenceNumber.getNext(nodeId, acceptorsProxy.getSequenceNumber());
        Prepare prepare = new Prepare(sequenceNumber);
        Promise<T> promise = acceptorsProxy.prepare(prepare);
        if (!promise.promised) {
            throw new NoConsensusException(String.format("Not accepted as proposer. %s", prepare));
        } else if (promise.acceptedValue != null) {
            value = promise.acceptedValue.value;
        }

        //accept message
        Accept<T> accept = new Accept(prepare.sequenceNumber, value);
        Accepted accepted = acceptorsProxy.accept(accept);
        if (Long.compareUnsigned(accept.sequenceNumber, accepted.sequenceNumber) != 0) {
            throw new NoConsensusException("Didn't accept value");
        }

        //commit value
        acceptorsProxy.commit(accept);

        return value;

    }

    public T getCurrent() throws NoConsensusException {
        Accept<T> acceptedValue = acceptorsProxy.getCurrent();
        if (acceptedValue != null) {
            return acceptedValue.value;
        } else {
            return null;
        }
    }

    @Override
    public void close() throws Exception {
        acceptorsProxy.close();
    }

    /**
     * Waits for a random amount of time.
     * This is used to reduce the chances of proposers always superseding an in progress proposal.
     * This scenario prevents a value for ever making to the the commit phase.
     * The wait should make it so a proposal has time to get to the commit state and no pattern
     * of propose, get superseded, try to propose again, get superseded,... occurs.
     */
    private void randomWait() {
        try {
            Thread.sleep(RANDOM.nextInt(100));
        } catch (InterruptedException e) {
        }
    }
}
