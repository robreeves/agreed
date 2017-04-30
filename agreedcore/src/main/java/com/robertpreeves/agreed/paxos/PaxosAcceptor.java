package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Commit;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

public interface PaxosAcceptor {
    Promise prepare(Prepare prepare);
    Accepted accept(Accept accept);
    void commit(Commit commit);
}
