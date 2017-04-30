package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.observer.Observer;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PaxosProposer<T> implements AgreedNode<T> {
    private static final Logger LOGGER = LogManager.getLogger(PaxosProposer.class);
    private List<Observer<T>> consensusObservers = new ArrayList<>();
    private final PaxosAcceptor acceptorsProxy;

    public PaxosProposer(PaxosAcceptor acceptorsProxy) {
        this.acceptorsProxy = acceptorsProxy;
    }

    @Override
    public synchronized void propose(T value) {
        //prepare message
        Prepare prepare = null;
        Promise promise = acceptorsProxy.prepare(prepare);
        //todo check promise response

        //accept message
        Accept accept = null;
        Accepted accepted = acceptorsProxy.accept(accept);
        //todo check accepted response

        //todo notify observers after commit
        consensusObservers.forEach(observer -> observer.notify(value));
    }

    /**
     * Subscribes to consensus notifications
     *
     * @param observer The object to notify when consensus is reached
     */
    @Override
    public synchronized void subscribe(Observer<T> observer) {
        consensusObservers.add(observer);
    }
}
