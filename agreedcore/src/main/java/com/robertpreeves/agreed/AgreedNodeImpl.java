package com.robertpreeves.agreed;

import com.robertpreeves.agreed.observer.Observer;
import com.robertpreeves.agreed.paxos.PaxosAcceptor;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Commit;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

class AgreedNodeImpl<T> implements AgreedNode<T> {
    private static final Logger logger = LogManager.getLogger(AgreedNodeImpl.class);
    private List<Observer<T>> consensusObservers = new ArrayList<>();
    private final PaxosAcceptor acceptorsProxy;

    public AgreedNodeImpl(PaxosAcceptor acceptorsProxy) {
        this.acceptorsProxy = acceptorsProxy;
    }

    @Override
    public void propose(T value) {
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
        notify(value);
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
}
