package com.robertpreeves.agreed;

import com.robertpreeves.agreed.observer.Observer;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

class AgreedNodeImpl<T> implements AgreedNode<T> {
    private static final Logger logger = LogManager.getLogger(AgreedNodeImpl.class);
    private List<Observer<T>> observers = new ArrayList<Observer<T>>();

    public AgreedNodeImpl(List<AgreedNodeEndpoint> otherNodes) {
        //todo

        logger.debug("test");
    }

    @Override
    public void propose(T value) {
        /*todo
        1. connect to all other nodes
        2. do paxos stuff
        3.notify observers
         */
    }

    /**
     * Subscribes to consensus notifications
     * @param observer The object to notify when consensus is reached
     */
    @Override
    public void subscribe(Observer<T> observer) {
        observers.add(observer);
    }

    private void notify(final T value) {
        observers.forEach(observer -> observer.notify(value));
    }
}
