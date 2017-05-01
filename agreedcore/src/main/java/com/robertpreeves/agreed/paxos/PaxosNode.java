package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.NoConsensusException;

public class PaxosNode<T> implements AgreedNode<T> {
    private final LocalPaxosAcceptor<T> acceptor;
    private final PaxosProposer<T> proposer;

    public PaxosNode(LocalPaxosAcceptor<T> acceptor, PaxosProposer<T> proposer) {
        this.acceptor = acceptor;
        this.proposer = proposer;
    }

    @Override
    public T propose(T value) throws NoConsensusException {
        return proposer.propose(value);
    }

    @Override
    public T getCurrent() throws NoConsensusException {
        return proposer.getCurrent();
    }
}
