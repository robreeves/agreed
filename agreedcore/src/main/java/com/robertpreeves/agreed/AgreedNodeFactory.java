package com.robertpreeves.agreed;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class AgreedNodeFactory {
    /**
     * Creates a consensus node
     * @param thisNodeIndex The index for this node in the nodes list.
     * @param nodes All nodes that will participate in consensus, including this node.
     * @param <T> The type of values the consensus log will store
     * @return The local consensus node
     */
    public static <T> AgreedNode<T> create(int thisNodeIndex, List<AgreedNodeEndpoint> nodes) {
        if (nodes.size() % 2 == 0) {
            throw new IllegalArgumentException("There must be an odd number of total nodes so " +
                    "that a majority can always be reached when coming to consensus");
        } else if (nodes.size() < 3) {
            throw new IllegalArgumentException("There must be at least three nodes");
        }

        if(thisNodeIndex >= nodes.size()) {
            throw new IllegalArgumentException("Node index is invalid");
        }

        Logger logger = LogManager.getLogger(AgreedNodeImpl.class);
        nodes.forEach(node -> logger.info("Known node {}", node));
        logger.info("Creating node at index {}", thisNodeIndex);

        return new AgreedNodeImpl(thisNodeIndex, nodes);
    }
}
