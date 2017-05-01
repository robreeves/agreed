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
    public void subscribe(Observer<T> observer) {
        acceptor.subscribe(observer);
    }

    @Override
    public void propose(T value) {
        proposer.propose(value);
    }
}
