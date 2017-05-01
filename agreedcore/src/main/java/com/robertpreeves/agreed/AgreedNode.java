package com.robertpreeves.agreed;


import java.util.function.Supplier;

public interface AgreedNode<T> {
    /**
     * Proposes the next value. This does not guarantees the value will be accepted.
     * When the method returns it guarantees, the nodes have came to an agreement.
     *
     * @param value The new value
     */
    T propose(T value) throws NoConsensusException;

    /**
     * Gets the current value
     * @return The current value
     * @throws NoConsensusException
     */
    T getCurrent() throws NoConsensusException;
}
