package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.observer.Observable;
import com.robertpreeves.agreed.observer.Observer;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class LocalPaxosAcceptor<T> implements PaxosAcceptor<T>, Observable<T> {
    private static final Logger LOGGER = LogManager.getLogger(LocalPaxosAcceptor.class);

    /**
     * The list of subscribers to notify when a value is accepted
     */
    private List<Observer<T>> consensusObservers = new ArrayList<>();

    /**
     * The most recently seen acceptance number.
     * This is updated in the prepare phase.
     */
    private long currentSeqNumber;

    /**
     * The most recently accepted value.
     * This is updated when a value is accepted.
     */
    private Accept<T> acceptedValue;

    @Override
    public synchronized Promise prepare(Prepare prepare) {
        if (Long.compareUnsigned(prepare.sequenceNumber, currentSeqNumber) > 0) {
            currentSeqNumber = prepare.sequenceNumber;
            return new Promise(true, acceptedValue);
        } else {
            return new Promise(false, null);
        }
    }

    @Override
    public synchronized Accepted accept(Accept accept) {
        int seqNumCompare = Long.compare(accept.sequenceNumber, currentSeqNumber);
        if (seqNumCompare == 0) {
            //accept value
            acceptedValue = accept;

            //notify observers
            consensusObservers.forEach(observer -> {
                try {

                } catch (Exception e) {
                    //dont let a runtime exception of one observer stop another observer
                    //from being notified
                    LOGGER.error(e);
                }
            });
        } else if (seqNumCompare > 0) {
            //this is unexpected. this means the prepare message was never received for this
            //sequence number. the proposer should not send an accept message if the prepare
            //message never received a promise from the acceptor.
            //todo throw exception?
        }

        return new Accepted(acceptedValue.sequenceNumber);
    }

    @Override
    public synchronized void subscribe(Observer<T> observer) {
        //notify subscriber for current value
        if (acceptedValue != null) {
            observer.notify(acceptedValue.value);
        }

        consensusObservers.add(observer);
    }
}
