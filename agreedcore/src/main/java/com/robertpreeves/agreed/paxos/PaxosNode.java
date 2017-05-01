package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.observer.Observer;

public class PaxosNode<T> implements AgreedNode<T> {
    private final LocalPaxosAcceptor<T> acceptor;
    private final PaxosProposer<T> proposer;

    public PaxosNode(LocalPaxosAcceptor<T> acceptor, PaxosProposer<T> proposer) {
        this.acceptor = acceptor;
        this.proposer = proposer;
    }

    @Override
    public Boolean propose(T value) {
        return proposer.propose(value);
    }

    @Override
    public T getCurrent() {
        return proposer.getCurrent();
    }
}
