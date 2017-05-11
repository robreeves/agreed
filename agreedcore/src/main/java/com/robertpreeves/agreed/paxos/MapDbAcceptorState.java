package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;

public class MapDbAcceptorState<T> implements PaxosAcceptorState<T> {
    @Override
    public long getPromised() {
        return 0;
    }

    @Override
    public void setPromised(long seqNumber) {

    }

    @Override
    public Accept<T> getAccepted() {
        return null;
    }

    @Override
    public void setAccepted(Accept<T> accepted) {

    }

    @Override
    public Accept<T> getCommitted() {
        return null;
    }

    @Override
    public void setCommitted(Accept<T> committed) {

    }
}
