package com.robertpreeves.agreed;


import com.robertpreeves.agreed.observer.Observable;

public interface AgreedNode<T> extends Observable<T> {
    /**
     * Proposes the next value. This does not guarantees the value will be accepted.
     * When the method returns it guarantees, the nodes have came to an agreement.
     *
     * @param value The new value
     */
    void propose(T value);
}
