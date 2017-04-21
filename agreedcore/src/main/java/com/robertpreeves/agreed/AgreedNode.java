package com.robertpreeves.agreed;

public interface AgreedNode<T> {
    /**
     * Proposes the next value. This does not guarantees the value will be accepted.
     * When the method returns it guarantees, the nodes have came to an agreement.
     * @param value The new value
     */
    void propose(T value);
}
