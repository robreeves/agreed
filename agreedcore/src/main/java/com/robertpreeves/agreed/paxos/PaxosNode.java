package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.observer.Observer;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Commit;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class PaxosNode<T> implements AgreedNode<T>, PaxosAcceptor {
    private static final Logger logger = LogManager.getLogger(PaxosNode.class);
    private List<Observer<T>> consensusObservers = new ArrayList<>();
    private final PaxosAcceptor acceptorsProxy;
    private final Log<T> log;

    public PaxosNode(PaxosAcceptor acceptorsProxy, Log<T> log) {
        this.acceptorsProxy = acceptorsProxy;
        this.log = log;
    }

    @Override
    public synchronized void propose(T value) {
        //prepare message
        Prepare prepare = new Prepare();
        Promise promise = acceptorsProxy.prepare(prepare);
        //todo check promise response

        //accept message
        Accept accept = new Accept();
        Accepted accepted = acceptorsProxy.accept(accept);
        //todo check accepted response

        //commit message
        Commit commit = new Commit();
        acceptorsProxy.commit(commit);
        //todo check commit response

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
        log.replay(observer::notify);
    }

    @Override
    public synchronized Promise prepare(Prepare prepare) {
        return new Promise();
    }

    @Override
    public synchronized Accepted accept(Accept accept) {
        return new Accepted();
    }

    @Override
    public synchronized Boolean commit(Commit commit) {
        return true;
    }
}
