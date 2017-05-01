package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

public interface PaxosAcceptor<T> {
    Promise prepare(Prepare prepare);
    Accepted accept(Accept<T> accept);
    Accept<T> getAccepted();
}
