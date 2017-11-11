package com.robertpreeves.agreed.paxos;

import com.google.gson.Gson;
import com.robertpreeves.agreed.NoConsensusException;
import com.robertpreeves.agreed.paxos.messages.Accept;
import com.robertpreeves.agreed.paxos.messages.Accepted;
import com.robertpreeves.agreed.paxos.messages.PaxosResponseException;
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
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PaxosAcceptorsProxy<T> implements PaxosAcceptor<T>, AutoCloseable {
    private static final Logger LOGGER = LogManager.getLogger("[PROPOSER]");
    private static final int TIMEOUT = 180000; //high for demo purposes
    private static final Gson GSON = new Gson();
    private final LocalPaxosAcceptor<T> localAcceptor;
    private final List<InetSocketAddress> otherNodes;
    private final int majorityCount;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public PaxosAcceptorsProxy(
            LocalPaxosAcceptor<T> localAcceptor,
            List<InetSocketAddress> otherNodes) {
        this.localAcceptor = localAcceptor;
        this.otherNodes = otherNodes;

        //the total number of nodes must be odd so otherNodes will be even.
        //the majority is half of the otherNodes + 1.
        majorityCount = (otherNodes.size() / 2) + 1;
    }

    /**
     * Gets the most recently promised sequence number
     * @return
     */
    public long getSequenceNumber() {
        return localAcceptor.getSequenceNumber();
    }

    @Override
    public Promise<T> prepare(Prepare prepare) {
        //Send prepare messages to remote acceptors
        List<Future<Promise>> promises = makeRequests(Uris.PREPARE, prepare, Promise.class);

        //Send prepare message to local acceptor
        Future<Promise> localPromise = executor.submit(() -> localAcceptor.prepare(prepare));
        promises.add(localPromise);

        Promise reducedPromise = reducePromises(promises);
        LOGGER.info("Prepare: {}, Reduced Promise: {}", prepare, reducedPromise);
        return reducedPromise;
    }

    private Promise<T> reducePromises(List<Future<Promise>> promises) {
        //Get promises
        int promiseCount = 0;
        Accept<T> previouslyAccepted = null;
        for (Future<Promise> promiseFuture :
                promises) {
            Promise<T> promise;
            try {
                promise = promiseFuture.get(TIMEOUT, TimeUnit.SECONDS);
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

        return promiseAggregate;
    }

    @Override
    public Accepted accept(Accept<T> accept) {
        //Send accept messages to remote acceptors
        List<Future<Accepted>> accepteds = makeRequests(Uris.ACCEPT, accept, Accepted.class);

        //Send accept message to local acceptor
        Future<Accepted> localAccepted = executor.submit(() -> localAcceptor.accept(accept));
        accepteds.add(localAccepted);

        Accepted reducedAccepteds = reduceAccepteds(accept.sequenceNumber, accepteds);
        LOGGER.info("Accept: {}, Reduced Accepteds: {}", accept, reducedAccepteds);
        return reducedAccepteds;
    }

    private Accepted reduceAccepteds(long sequenceNumber, List<Future<Accepted>> accepteds) {
        int acceptedCount = 0;

        long highestSequenceNumber = 0;
        for (Future<Accepted> acceptedFuture :
                accepteds) {
            Accepted accepted = null;
            try {
                accepted = acceptedFuture.get(TIMEOUT, TimeUnit.SECONDS);
            } catch (Exception e) {
            }

            if (accepted != null) {
                if (Long.compareUnsigned(accepted.sequenceNumber, sequenceNumber) == 0) {
                    ++acceptedCount;
                }

                if (Long.compareUnsigned(accepted.sequenceNumber, highestSequenceNumber) > 0) {
                    highestSequenceNumber = accepted.sequenceNumber;
                }
            }
        }

        Accepted reducedAccepted;
        if (acceptedCount >= majorityCount) {
            reducedAccepted = new Accepted(sequenceNumber);
        } else {
            reducedAccepted = new Accepted(highestSequenceNumber);
        }

        return reducedAccepted;
    }

    @Override
    public void commit(Accept<T> accepted) throws NoConsensusException {
        //Send commit messages to remote acceptors
        List<Future<Boolean>> commits = makeRequests(Uris.COMMIT, accepted);

        //Send commit message to local acceptor
        Future<Boolean> localCommit = executor.submit(() -> {
            localAcceptor.commit(accepted);
            return true;
        });

        commits.add(localCommit);

        int currentCount = 0;
        for (Future<Boolean> commit : commits){
            try {
                if (commit.get(TIMEOUT, TimeUnit.SECONDS)) {
                    ++currentCount;
                }
            } catch (Exception e) {

            }
        }

        if (currentCount < majorityCount) {
            throw new NoConsensusException("Could not commit value");
        }
    }

    @Override
    public void close() throws Exception {
        executor.shutdown();
    }

    @Override
    public Accept<T> getCurrent() throws NoConsensusException {
        //Send request to remote acceptors
        List<Future<Accept>> currents = makeRequests(Uris.READ, null, Accept.class);

        //Send request to local acceptor
        Future<Accept> localCurrent = executor.submit(() -> localAcceptor.getCurrent());
        currents.add(localCurrent);

        return reduceCurrents(currents);
    }

    private Accept reduceCurrents(List<Future<Accept>> currents) throws NoConsensusException {
        int currentCount = 0;
        Accept maxCurrent = null;

        for (Future<Accept> currentFuture :
                currents) {

            Accept current;
            try {
                current = currentFuture.get(TIMEOUT, TimeUnit.SECONDS);
            } catch (Exception e) {
                continue;
            }

            ++currentCount;
            if (current != null) {
                if (maxCurrent == null) {
                    maxCurrent = current;
                } else if (Long.compareUnsigned(
                        current.sequenceNumber, maxCurrent.sequenceNumber) > 0) {
                    maxCurrent = current;
                }
            }
        }

        if (currentCount >= majorityCount) {
            return maxCurrent;
        } else {
            throw new NoConsensusException("Did not receive read responses from majority nodes");
        }
    }

    private <TRequest, TResponse> List<Future<TResponse>> makeRequests(
            String relativeUri, TRequest requestBody, Class<TResponse> responseClass) {
        HttpClient httpClient = HttpClients.createDefault();

        //Send prepare messages
        List<Future<TResponse>> futures = new ArrayList<>();

        //Create response body
        final StringEntity requestEntity;
        if (requestBody != null) {
            requestEntity = new StringEntity(GSON.toJson(requestBody), "UTF-8");
        } else {
            requestEntity = null;
        }

        otherNodes.forEach(otherNode -> {

            Future<TResponse> future = executor.submit(() -> {
                //Create HTTP request
                String uri = String.format("http://%s:%s%s", otherNode.getHostName(), otherNode.getPort(), relativeUri);
                HttpPost request = new HttpPost(uri);

                if (requestEntity != null) {
                    request.setHeader("content-type", "application/json");
                    request.setEntity(requestEntity);
                }

                TResponse responseBody = null;
                try {
                    //Make request
                    HttpResponse response = httpClient.execute(request);

                    //Process response
                    HttpEntity body = response.getEntity();
                    if (response.getStatusLine().getStatusCode() == 200
                            && body != null
                            && body.getContent() != null) {
                        try (InputStreamReader reader = new InputStreamReader(body.getContent());
                             BufferedReader bufferReader = new BufferedReader(reader)
                        ) {
                            responseBody = GSON.fromJson(bufferReader, responseClass);
                        }
                    } else {
                        throw new PaxosResponseException();
                    }
                } catch (IOException e) {
                    throw new PaxosResponseException();
                }

                return responseBody;
            });

            futures.add(future);
        });

        return futures;
    }

    private <TRequest> List<Future<Boolean>> makeRequests(
            String relativeUri, TRequest requestBody) {
        HttpClient httpClient = HttpClients.createDefault();

        //Send prepare messages
        List<Future<Boolean>> futures = new ArrayList<>();

        //Create response body
        final StringEntity requestEntity =
                new StringEntity(GSON.toJson(requestBody), "UTF-8");

        otherNodes.forEach(otherNode -> {

            Future future = executor.submit(() -> {
                //Create HTTP request
                String uri = String.format("http://%s%s", otherNode, relativeUri);
                HttpPost request = new HttpPost(uri);
                request.setHeader("content-type", "application/json");
                request.setEntity(requestEntity);

                try {
                    //Make request
                    HttpResponse response = httpClient.execute(request);

                    //Process response
                    HttpEntity body = response.getEntity();
                    if (response.getStatusLine().getStatusCode() != 200) {
                        throw new PaxosResponseException();
                    }
                } catch (IOException e) {
                    throw new PaxosResponseException();
                }

                return true;
            });

            futures.add(future);
        });

        return futures;
    }
}
