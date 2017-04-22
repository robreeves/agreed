package com.robertpreeves.agreed;

public class AgreedNodeEndpoint {
    private final String hostname;
    private final int port;
    private final int priority;

    public AgreedNodeEndpoint(String hostname, int port, int priority) {
        this.hostname = hostname;
        this.port = port;
        this.priority = priority;
    }

    public String getId() {
        return String.format("%s:%s", hostname, port);
    }
}
