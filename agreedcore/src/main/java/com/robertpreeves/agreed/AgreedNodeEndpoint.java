package com.robertpreeves.agreed;

public class AgreedNodeEndpoint {
    private final String hostname;
    private final int port;

    public AgreedNodeEndpoint(String hostname, int port, byte nodeId) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", hostname, port);
    }
}
