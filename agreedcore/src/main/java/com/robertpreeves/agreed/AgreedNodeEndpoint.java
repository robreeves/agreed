package com.robertpreeves.agreed;

public class AgreedNodeEndpoint {
    private final String hostname;
    private final int port;

    public AgreedNodeEndpoint(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }
}
