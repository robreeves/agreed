package com.robertpreeves.agreed;


public interface AgreedNode<T> extends AutoCloseable {
    /**
     * Proposes the next value. This does not guarantees the value will be accepted.
     * When the method returns it guarantees, the nodes have came to an agreement.
     *
     * @param value The new value
     */
    T propose(T value) throws NoConsensusException;

    /**
     * Gets the current value
     *
     * @return The current value
     */
    T getCurrent() throws NoConsensusException;
}
