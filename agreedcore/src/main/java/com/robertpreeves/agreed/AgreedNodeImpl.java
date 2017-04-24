package com.robertpreeves.agreed;

import com.robertpreeves.agreed.observer.Observer;
import com.robertpreeves.agreed.paxos.PaxosServer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

import spark.Spark;

class AgreedNodeImpl<T> implements AgreedNode<T> {
    private static final Logger logger = LogManager.getLogger(AgreedNodeImpl.class);
    private List<Observer<T>> consensusObservers = new ArrayList<>();
    private final int thisNodeIndex;
    private final List<AgreedNodeEndpoint> nodes;

    public AgreedNodeImpl(int thisNodeIndex, List<AgreedNodeEndpoint> nodes) {
        this.thisNodeIndex = thisNodeIndex;
        this.nodes = nodes;

        int port = nodes.get(thisNodeIndex).getPort();
        Spark.port(port);
        String uri = "/agreed";
        Spark.webSocket(uri, PaxosServer.class);
        Spark.init();
        Spark.awaitInitialization();
        logger.info("Listening on {} port {}", uri, port);
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
