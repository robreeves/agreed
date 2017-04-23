package com.robertpreeves.agreed;

public class AgreedNodeEndpoint {
    private final String hostname;
    private final int port;
    private final byte nodeId;

    public AgreedNodeEndpoint(String hostname, int port, byte nodeId) {
        this.hostname = hostname;
        this.port = port;
        this.nodeId = nodeId;
    }
}
