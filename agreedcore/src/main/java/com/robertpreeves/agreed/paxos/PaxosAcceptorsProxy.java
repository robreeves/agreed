package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import java.util.Set;

public class PaxosAcceptorsProxy implements PaxosAcceptor {
    private final PaxosAcceptor localAcceptor;
    private final Set<String> otherNodes;
    private final int majorityCount;

    public PaxosAcceptorsProxy(PaxosAcceptor localAcceptor, Set<String> otherNodes) {
        this.localAcceptor = localAcceptor;
        this.otherNodes = otherNodes;

        //the total number of nodes must be odd so otherNodes will be even.
        //the majority is half of the otherNodes + 1.
        majorityCount = (otherNodes.size() / 2) + 1;
    }

    @Override
    public Promise prepare(Prepare prepare) {
        //todo prepare message to all nodes
        return localAcceptor.prepare(prepare);
    }

    @Override
    public Accepted accept(Accept accept) {
        //todo accept message to all nodes
        return localAcceptor.accept(accept);
    }
}
