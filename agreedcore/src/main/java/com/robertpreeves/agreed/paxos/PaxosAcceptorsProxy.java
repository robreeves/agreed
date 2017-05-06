package com.robertpreeves.agreed.paxos;

import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class PaxosAcceptorsProxy<T> implements PaxosAcceptor<T>, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(PaxosAcceptorsProxy.class);
    private final PaxosAcceptor localAcceptor;
    private final Set<String> otherNodes;
    private final int majorityCount;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public PaxosAcceptorsProxy(PaxosAcceptor<T> localAcceptor, Set<String> otherNodes) {
        this.localAcceptor = localAcceptor;
        this.otherNodes = otherNodes;

        //the total number of nodes must be odd so otherNodes will be even.
        //the majority is half of the otherNodes + 1.
        majorityCount = (otherNodes.size() / 2) + 1;
    }

    @Override
    public Promise<T> prepare(Prepare prepare) {
        //Send prepare messages
        List<Future<Promise<T>>> promises = new ArrayList<>();
        Future<Promise<T>> localPromise = executor.submit(() -> localAcceptor.prepare(prepare));
        promises.add(localPromise);
        otherNodes.forEach(otherNode -> {
            //todo
            Future<Promise<T>> todoPromise =
                    new FutureTask<Promise<T>>(() -> new Promise<>(true, null));
            promises.add(todoPromise);
        });

        //Get promises
        int promiseCount = 0;
        Accept<T> previouslyAccepted = null;
        for (Future<Promise<T>> promiseFuture :
                promises) {
            Promise<T> promise;
            try {
                promise = promiseFuture.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                promise = new Promise<>(false, null);
            }

            if (promise.promised) {
                ++promiseCount;
            }

            //Check if any acceptors have accepted a previous value.
            //Choose the value with the highest sequence number.
            if (promise.acceptedValue != null) {
                if (previouslyAccepted == null) {
                    previouslyAccepted = promise.acceptedValue;
                } else {
                    int sequenceCompare = Long.compareUnsigned(
                            promise.acceptedValue.sequenceNumber,
                            previouslyAccepted.sequenceNumber);

                    if (sequenceCompare > 0) {
                        previouslyAccepted = promise.acceptedValue;
                    }
                }
            }
        }

        //Verify a quorum has promised
        Promise<T> promiseAggregate;
        if (promiseCount >= majorityCount) {
            promiseAggregate = new Promise<>(true, previouslyAccepted);
        } else {
            promiseAggregate = new Promise<>(false, null);
        }

        LOGGER.info("Prepare: {}, Promise: {}", prepare, promiseAggregate);

        return promiseAggregate;
    }

    @Override
    public Accepted accept(Accept<T> accept) {
        //todo accept message to all nodes
        //todo once quorum is accepted return
        return localAcceptor.accept(accept);
    }

    @Override
    public Accept<T> getAccepted() {
        return null;
    }

    @Override
    public void commit(Accept<T> accepted) {
        localAcceptor.commit(accepted);
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }
}
