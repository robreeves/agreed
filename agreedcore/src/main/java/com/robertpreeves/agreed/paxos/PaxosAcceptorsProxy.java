package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Commit;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import java.util.Set;

public class PaxosAcceptorsProxy implements PaxosAcceptor {
    private final Set<String> otherNodes;

    public PaxosAcceptorsProxy(Set<String> otherNodes) {
        this.otherNodes = otherNodes;
    }

    @Override
    public Promise prepare(Prepare prepare) {
        return new Promise();
    }

    @Override
    public Accepted accept(Accept accept) {
        return new Accepted();
    }

    @Override
    public Boolean commit(Commit commit) {
        return true;
    }
}
