package com.robertpreeves.lock;


import com.google.gson.Gson;
import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.NoConsensusException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.Random;

import spark.Response;
import spark.Service;

import static spark.Service.ignite;

public class Api {
    private static final Logger LOGGER = LogManager.getLogger(Api.class);
    private static final Random RANDOM = new Random();
    private static final Gson GSON = new Gson();
    private static final String URI_TIME = "/api/time";
    private static final String URI_LEADER = "/api/leader/time";
    private static final HttpClient HTTP = HttpClients.createDefault();
    private final String hostnamePort;
    private final AgreedNode<String> agreedNode;

    public Api(int port, AgreedNode<String> agreedNode) throws UnknownHostException {
        this.agreedNode = agreedNode;
        this.hostnamePort = String.format("%s:%s", InetAddress.getLocalHost().getHostName(), port);
        initHttpAPI(port);
    }

    private void initHttpAPI(int port) {
        Service http = ignite()
                .port(port);

        http.get(URI_LEADER, (request, response) -> getLocalTime(response));
        http.get(URI_TIME, (request, response) -> getLeaderTime(response), GSON::toJson);

        http.awaitInitialization();
        LOGGER.info("Listening on {}", port);
    }

    private byte[] getLocalTime(Response response) {
        response.type("application/octet-stream");

        ByteBuffer timeBuffer = ByteBuffer.allocate(Long.BYTES);
        timeBuffer.putLong(new Date().getTime());
        return timeBuffer.array();
    }

    private Object getLeaderTime(Response response) {
        response.type("application/json");

        try {
            //Get the current leader
            String leaderHostPort = agreedNode.getCurrent();
            if (StringUtils.isBlank(leaderHostPort)) {
                LOGGER.info("No leader. Proposing to be leader ({})...", hostnamePort);
                leaderHostPort = proposeLeader();
            }

            //Get leader time
            TimeResponse timeResponse;
            timeResponse = getLeaderTime(leaderHostPort);
            if (timeResponse == null) {
                LOGGER.info("Leader {} failed. Proposing to be leader ({})...",
                        leaderHostPort, hostnamePort);
                String newLeaderHostPort = proposeLeader();

                //try to get the time again
                //it doesnt matter if this host is the leader, just that a leader was chosen
                //fail if cant get time again
                timeResponse = getLeaderTime(newLeaderHostPort);
                if (timeResponse == null) {
                    response.status(503);
                    response.type("text/plain");
                    return String.format("Failed after two attempts. " +
                                    "Could not get time from leader %s and %s",
                            leaderHostPort, newLeaderHostPort);
                }
            }

            return timeResponse;

        } catch (NoConsensusException e) {
            LOGGER.error("Consensus issue", e);
            response.status(503);
            response.type("text/plain");
            return "Nodes could not come to consensus to determine a leader";
        }
    }

    private TimeResponse getLeaderTime(String leaderHostPort) {
        if (StringUtils.equalsIgnoreCase(leaderHostPort, hostnamePort)) {
            //this is the leader
            return new TimeResponse(new Date().getTime(), leaderHostPort);
        } else {
            //leader is a remote node
            long timestamp = -1;

            //Get leader time
            HttpGet get = new HttpGet(String.format("http://%s%s", leaderHostPort, URI_LEADER));
            try {
                HttpResponse leaderResponse = HTTP.execute(get);
                int statusCode = leaderResponse.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    try (InputStream in = leaderResponse.getEntity().getContent()) {
                        byte[] timestampBytes = new byte[Long.BYTES];
                        in.read(timestampBytes, 0, timestampBytes.length);
                        timestamp = ByteBuffer.wrap(timestampBytes).getLong();
                    }
                } else if (statusCode >= 500) {
                    LOGGER.info("Leader {} failed with status code {}", leaderHostPort, statusCode);
                }
            } catch (IOException e) {
            }

            return timestamp == -1 ? null : new TimeResponse(timestamp, leaderHostPort);
        }
    }

    private String proposeLeader() throws NoConsensusException {
        //This is only for demonstration purposes and would not be in a production version of this.
        //This is used to show how to requests that detect failures handle competing proposals.
        //For demonstrations two requests are made in synchronously and this random sleeps allow
        //them to propose a new leader in a random order.
        try {
            Thread.sleep(RANDOM.nextInt(25));
        } catch (InterruptedException e) {
        }

        return agreedNode.propose(hostnamePort);
    }
}