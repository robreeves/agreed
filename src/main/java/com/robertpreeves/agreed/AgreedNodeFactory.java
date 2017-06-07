package com.robertpreeves.agreed;

import com.robertpreeves.agreed.paxos.LocalPaxosAcceptor;
import com.robertpreeves.agreed.paxos.MapDbAcceptorState;
import com.robertpreeves.agreed.paxos.PaxosAcceptorState;
import com.robertpreeves.agreed.paxos.PaxosAcceptorsProxy;
import com.robertpreeves.agreed.paxos.PaxosHttpAcceptor;
import com.robertpreeves.agreed.paxos.PaxosNode;
import com.robertpreeves.agreed.paxos.PaxosProposer;

import java.util.Set;

public class AgreedNodeFactory {
    /**
     * Creates a new agreed node. This is a single node in a cluster of nodes used to achieve
     * consensus.
     *
     * @param nodeId     The node Id. This must be unique within the cluster.
     * @param port       The port that this node will use for internal consensus communication.
     * @param otherNodes The other nodes in the cluser in the format hostname:port.
     * @param <T>        The types of values that will be agreed on.
     * @return The local agreed node.
     */
    public static <T> AgreedNode<T> create(
            byte nodeId, int port,
            Set<String> otherNodes,
            boolean slow,
            boolean slowP) {
        //validate group size
        if (otherNodes.size() % 2 > 0) {
            throw new IllegalArgumentException("There must be an odd number of total nodes so " +
                    "that a majority can always be reached when coming to consensus");
        } else if (otherNodes.size() < 2) {
            throw new IllegalArgumentException("There must be at least three nodes");
        }

        PaxosAcceptorState acceptorState = new MapDbAcceptorState<>(nodeId);
        LocalPaxosAcceptor localAcceptor = new LocalPaxosAcceptor(acceptorState, slow);
        PaxosAcceptorsProxy acceptorsProxy = new PaxosAcceptorsProxy(localAcceptor, otherNodes, slowP);
        PaxosHttpAcceptor acceptorSvc = new PaxosHttpAcceptor(port, localAcceptor);
        PaxosProposer localProposer = new PaxosProposer(nodeId, acceptorsProxy);

        return new PaxosNode(localAcceptor, localProposer, acceptorSvc);
    }
}