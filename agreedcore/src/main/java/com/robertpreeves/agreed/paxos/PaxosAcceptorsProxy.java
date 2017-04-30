package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import java.util.Set;

public class PaxosAcceptorsProxy implements PaxosAcceptor {
    private final PaxosAcceptor localAcceptor;
    private final Set<String> otherNodes;

    public PaxosAcceptorsProxy(PaxosAcceptor localAcceptor, Set<String> otherNodes) {
        this.localAcceptor = localAcceptor;
        this.otherNodes = otherNodes;
    }

    @Override
    public Promise prepare(Prepare prepare) {
        return null;
    }

    @Override
    public Accepted accept(Accept accept) {
        return null;
    }
}
