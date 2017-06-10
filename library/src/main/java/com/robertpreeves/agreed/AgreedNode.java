package com.robertpreeves.agreed;


/**
 * Represents a single node in the group of consensus nodes.
 * This is the main interface for proposing a new value and getting the current value.
 * @param <T>
 */
public interface AgreedNode<T> extends AutoCloseable {
    /**
     * Proposes a new consensus value. This does not guaruntee that the propose value will be accepted.
     * The value will be proposed, but another value may become the next value.
     * @param value The proposed next value
     * @return The chosen value for the proposal. This could be a value other than the proposed value.
     * @throws NoConsensusException Thrown when a consensus cannot be reached for the proposal.
     */
    T propose(T value) throws NoConsensusException;

    /**
     * @return The most recent consensus value. Returns null if a value has never been proposed.
     * @throws NoConsensusException Thrown when a consensus cannot be reached for what the current value is.
     */
    T getCurrent() throws NoConsensusException;
}
