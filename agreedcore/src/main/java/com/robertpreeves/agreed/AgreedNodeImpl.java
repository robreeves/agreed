package com.robertpreeves.agreed;

import com.robertpreeves.agreed.observer.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

class AgreedNodeImpl<T> implements AgreedNode<T> {
    private static final Logger logger = LogManager.getLogger(AgreedNodeImpl.class);
    private List<Observer<T>> consensusObservers = new ArrayList<>();
    private final int thisNodeIndex;
    private final List<AgreedNodeEndpoint> nodes;

    public AgreedNodeImpl(int thisNodeIndex, List<AgreedNodeEndpoint> nodes) {
        this.thisNodeIndex = thisNodeIndex;
        this.nodes = nodes;

        //todo start listening on supplied port
    }

    @Override
    public void propose(T value) {
        /*todo
        1. connect to all other nodes
        2. do paxos stuff
        3.notify consensusObservers
         */
    }

    /**
     * Subscribes to consensus notifications
     * @param observer The object to notify when consensus is reached
     */
    @Override
    public void subscribe(Observer<T> observer) {
        consensusObservers.add(observer);
    }

    private void notify(final T value) {
        consensusObservers.forEach(observer -> observer.notify(value));
    }

    private void logMembership() {

    }
}
