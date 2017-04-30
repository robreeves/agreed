package com.robertpreeves.agreed;

import com.robertpreeves.agreed.paxos.LocalPaxosAcceptor;
import com.robertpreeves.agreed.paxos.PaxosAcceptor;
import com.robertpreeves.agreed.paxos.PaxosAcceptorsProxy;
import com.robertpreeves.agreed.paxos.PaxosHttpAcceptor;
import com.robertpreeves.agreed.paxos.PaxosProposer;

import java.util.Set;

public class AgreedNodeFactory {
    public static <T> AgreedNode<T> create(int port, Set<String> otherNodes) {
        //validate group size
        if (otherNodes.size() % 2 > 0) {
            throw new IllegalArgumentException("There must be an odd number of total nodes so " +
                    "that a majority can always be reached when coming to consensus");
        } else if (otherNodes.size() < 2) {
            throw new IllegalArgumentException("There must be at least three nodes");
        }

        PaxosAcceptor localAcceptor = new LocalPaxosAcceptor();
        PaxosAcceptor acceptorsProxy = new PaxosAcceptorsProxy(localAcceptor, otherNodes);
        PaxosHttpAcceptor<T> acceptorSvc = new PaxosHttpAcceptor<>(port, localAcceptor);
        PaxosProposer<T> localProposer = new PaxosProposer<>(acceptorsProxy);

        return localProposer;
    }
}
