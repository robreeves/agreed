package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.NoConsensusException;

public class PaxosNode<T> implements AgreedNode<T> {
    private final LocalPaxosAcceptor<T> acceptor;
    private final PaxosProposer<T> proposer;
    private final PaxosHttpAcceptor<T> httpAcceptor;

    public PaxosNode(LocalPaxosAcceptor<T> acceptor, PaxosProposer<T> proposer, PaxosHttpAcceptor<T> httpAcceptor) {
        this.acceptor = acceptor;
        this.proposer = proposer;
        this.httpAcceptor = httpAcceptor;
    }

    @Override
    public T propose(T value) throws NoConsensusException {
        return proposer.propose(value);
    }

    @Override
    public T getCurrent() throws NoConsensusException {
        return proposer.getCurrent();
    }

    @Override
    public void close() throws Exception {
        httpAcceptor.close();
    }
}
