package com.robertpreeves.agreed;

import com.robertpreeves.agreed.paxos.MemoryLog;
import com.robertpreeves.agreed.paxos.PaxosAcceptorsProxy;
import com.robertpreeves.agreed.paxos.PaxosHttpAcceptor;
import com.robertpreeves.agreed.paxos.PaxosNode;

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

        PaxosNode<T> node = new PaxosNode<>(new PaxosAcceptorsProxy(otherNodes), new MemoryLog());
        PaxosHttpAcceptor<T> acceptorSvc = new PaxosHttpAcceptor<>(port, node);
        return acceptorSvc.getNode();
    }
}
