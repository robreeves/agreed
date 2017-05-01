package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.NoConsensusException;
import com.robertpreeves.agreed.observer.Observer;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PaxosProposer<T> {
    private static final Logger LOGGER = LogManager.getLogger(PaxosProposer.class);
    private final PaxosAcceptor<T> acceptorsProxy;
    private final byte nodeId = 1;
    private long lastKnownSequenceNumber;

    public PaxosProposer(PaxosAcceptor<T> acceptorsProxy) {
        this.acceptorsProxy = acceptorsProxy;
    }

    public synchronized T propose(T value) throws NoConsensusException {
        //prepare message
        Prepare prepare = new Prepare(SequenceNumber.getNext(nodeId, lastKnownSequenceNumber));
        Promise<T> promise = acceptorsProxy.prepare(prepare);
        if (!promise.promised) {
            throw new NoConsensusException(String.format("Not accepted as proposer. %s", prepare));
        } else if (promise.acceptedValue != null) {
            value = promise.acceptedValue.value;
        }

        //accept message
        Accept accept = new Accept(prepare.sequenceNumber, value);
        Accepted accepted = acceptorsProxy.accept(accept);
        if (Long.compareUnsigned(accept.sequenceNumber, accepted.sequenceNumber) == 0) {
            return value;
        } else {
            throw new NoConsensusException("Didn't accept value");
        }
    }

    public T getCurrent() {
        Accept<T> acceptedValue = acceptorsProxy.getAccepted();
        if (acceptedValue != null) {
            return acceptedValue.value;
        } else {
            return null;
        }
    }
}
