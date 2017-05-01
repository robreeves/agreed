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

public class PaxosProposer<T> {
    private static final Logger LOGGER = LogManager.getLogger(PaxosProposer.class);
    private final PaxosAcceptor<T> acceptorsProxy;

    public PaxosProposer(PaxosAcceptor<T> acceptorsProxy) {
        this.acceptorsProxy = acceptorsProxy;
    }

    public synchronized Boolean propose(T value) {
        //prepare message
        Prepare prepare = null;
        Promise promise = acceptorsProxy.prepare(prepare);
        //todo check promise response

        //accept message
        Accept accept = null;
        Accepted accepted = acceptorsProxy.accept(accept);
        //todo check accepted response

        return true;
    }

    public T getCurrent() {
        Accept<T> acceptedValue = acceptorsProxy.getAccepted();
        if (acceptedValue != null) {
            return acceptedValue.value;
        } else {
            return null;
        }
    }
}
