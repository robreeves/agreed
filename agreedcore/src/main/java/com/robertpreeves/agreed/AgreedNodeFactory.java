package com.robertpreeves.agreed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class AgreedNodeFactory {
    /**
     * Creates a consensus node
     * @param thisNodeId The id of this consensus node
     * @param nodes All nodes that will participate in consensus, including this node.
     * @param <T> The type of values the consensus log will store
     * @return The consensus node
     */
    public static <T> AgreedNode<T> create(byte thisNodeId, Map<Byte, AgreedNodeEndpoint> nodes) {
        if (nodes.size() % 2 == 0) {
            throw new IllegalArgumentException("There must be an odd number of total nodes so " +
                    "that a majority can always be reached when coming to consensus");
        } else if (nodes.size() < 3) {
            throw new IllegalArgumentException("There must be at least three nodes");
        }

        AgreedNodeEndpoint thisNode = nodes.remove(thisNodeId);
        if(thisNode == null) {
            throw new IllegalArgumentException(String.format("No node with id %s", thisNodeId));
        }

        Logger logger = LogManager.getLogger(AgreedNodeImpl.class);
        logger.info("Creating node at {} {}", thisNodeId, thisNode);
        nodes.forEach((k, v) -> logger.info("Known node {} {}", k, v));

        return new AgreedNodeImpl(thisNodeId, thisNode, nodes);
    }
}
