package com.robertpreeves.agreed;


public class AgreedNodeEndpoint {
    private final String hostname;
    private final int port;

    public AgreedNodeEndpoint(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString() {
        return String.format("%s:%s", hostname, port);
    }
}
