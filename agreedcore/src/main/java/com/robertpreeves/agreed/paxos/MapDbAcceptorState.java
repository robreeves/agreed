package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;

public class MapDbAcceptorState<T> implements PaxosAcceptorState<T> {
    /**
     * The most recently promised sequence number as an unsigned long
     */
    private long promised;

    private Accept<T> accepted;

    private Accept<T> committed;

    @Override
    public long getPromised() {
        return promised;
    }

    @Override
    public void setPromised(long seqNumber) {
        this.promised = seqNumber;
    }

    @Override
    public Accept<T> getAccepted() {
        return accepted;
    }

    @Override
    public void setAccepted(Accept<T> accepted) {
        this.accepted = accepted;
    }

    @Override
    public Accept<T> getCommitted() {
        return committed;
    }

    @Override
    public void setCommitted(Accept<T> committed) {
        this.committed = committed;
    }
}
