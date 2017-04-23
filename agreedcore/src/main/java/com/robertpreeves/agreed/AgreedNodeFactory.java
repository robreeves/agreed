package com.robertpreeves.agreed;

import java.util.List;

public class AgreedNodeFactory {
    /**
     * Creates a consensus node
     * @param thisNodeId The id of this consensus node
     * @param nodes All nodes that will participate in consensus, including this node.
     * @param <T> The type of values the consensus log will store
     * @return The consensus node
     */
    public static <T> AgreedNode<T> create(byte thisNodeId, List<AgreedNodeEndpoint> nodes) {
        if (nodes.size() % 2 == 0) {
            throw new IllegalArgumentException("There must be an odd number of total nodes so " +
                    "that a majority can always be reached when coming to consensus");
        }

        return new AgreedNodeImpl(thisNodeId, nodes);
    }
}
