package com.robertpreeves.agreed.paxos;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;


@WebSocket
public class PaxosServer {
    private static final Logger logger = LogManager.getLogger(PaxosServer.class);



    @OnWebSocketConnect
    public void connected(Session session) {
        logger.info("Connected {}", session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void closed(Session session, int statusCode, String reason) {
        logger.info("Closed {}", session.getRemoteAddress());
    }

    @OnWebSocketMessage
    public void message(Session session, String message) throws IOException {
        logger.info("Message '{}' from {}", message, session.getRemoteAddress());
    }
}
