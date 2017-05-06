package com.robertpreeves.agreed.paxos;

import com.google.gson.Gson;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.Prepare;
import com.robertpreeves.agreed.paxos.messages.Promise;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PaxosAcceptorsProxy<T> implements PaxosAcceptor<T>, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger(PaxosAcceptorsProxy.class);
    private static final Gson GSON = new Gson();
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
        HttpClient httpClient = HttpClients.createDefault();

        //Send prepare messages
        List<Future<Promise<T>>> promises = new ArrayList<>();

        //Local acceptor
        Future<Promise<T>> localPromise = executor.submit(() -> localAcceptor.prepare(prepare));
        promises.add(localPromise);

        //Remote acceptors
        StringEntity requestBody = new StringEntity(GSON.toJson(prepare), "UTF-8");
        otherNodes.forEach(otherNode -> {
            Future<Promise<T>> promiseFuture = executor.submit(() -> {
                //Create HTTP request
                String uri = String.format("http://%s%s", otherNode, Uris.PREPARE);
                HttpPost request = new HttpPost(uri);
                request.setHeader("content-type", "application/json");
                request.setEntity(requestBody);

                Promise<T> promise;
                try {
                    //Make request
                    HttpResponse response = httpClient.execute(request);

                    //Process promise response
                    HttpEntity body = response.getEntity();
                    if (response.getStatusLine().getStatusCode() == 200
                            && body != null
                            && body.getContent() != null) {
                        try (InputStreamReader reader = new InputStreamReader(body.getContent());
                             BufferedReader bufferReader = new BufferedReader(reader)
                        ) {
                            promise = GSON.fromJson(bufferReader, Promise.class);
                            LOGGER.info("Promise response from {}: {}", otherNode, promise);
                        }
                    } else {
                        LOGGER.info("Prepare request message failure from {}. {}", otherNode,
                                response.getStatusLine());
                        promise = new Promise<>(false, null);
                    }
                } catch (IOException e) {
                    LOGGER.info("Prepare request message failure from {}", otherNode, e);
                    promise = new Promise<>(false, null);
                }

                return promise;
            });

            promises.add(promiseFuture);
        });

        Promise<T> promiseAggregate = reducePromises(promises);
        LOGGER.info("Reduced Prepare: {}, Promise: {}", prepare, promiseAggregate);
        return promiseAggregate;
    }

    private Promise<T> reducePromises(List<Future<Promise<T>>> promises) {
        //Get promises
        int promiseCount = 0;
        Accept<T> previouslyAccepted = null;
        for (Future<Promise<T>> promiseFuture :
                promises) {
            Promise<T> promise;
            try {
                promise = promiseFuture.get(10, TimeUnit.SECONDS);
            } catch (Exception e) {
                LOGGER.info("Promise response failure", e);
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
