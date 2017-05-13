package com.robertpreeves.lock;

public class TimeResponse {
    public final long timestamp;
    private final String source;

    public TimeResponse(long timestamp, String source) {
        this.timestamp = timestamp;
        this.source = source;
    }
}
