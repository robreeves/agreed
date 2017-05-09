package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.NoConsensusException;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PaxosProposer<T> implements AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(PaxosProposer.class);
    private final PaxosAcceptorsProxy<T> acceptorsProxy;
    private final byte nodeId;
    private long lastKnownSequenceNumber;

    public PaxosProposer(byte nodeId, PaxosAcceptorsProxy<T> acceptorsProxy) {
        this.nodeId = nodeId;
        this.acceptorsProxy = acceptorsProxy;
    }

    public synchronized T propose(T value) throws NoConsensusException {
        //prepare message
        lastKnownSequenceNumber = SequenceNumber.getNext(nodeId, lastKnownSequenceNumber);
        Prepare prepare = new Prepare(lastKnownSequenceNumber);
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
            lastKnownSequenceNumber = accepted.sequenceNumber;
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
}
