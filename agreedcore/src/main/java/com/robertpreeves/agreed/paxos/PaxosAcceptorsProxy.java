package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.AgreedNodeEndpoint;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Commit;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import java.util.List;

public class PaxosAcceptorsProxy implements PaxosAcceptor {
    private final List<AgreedNodeEndpoint> otherNodes;

    public PaxosAcceptorsProxy(List<AgreedNodeEndpoint> otherNodes) {
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
