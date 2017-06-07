package com.robertpreeves.leader;

public class Leader {
    private final String endpoint;
    private final long leadershipStartTime;

    public Leader(String endpoint, long leadershipStartTime) {
        this.endpoint = endpoint;
        this.leadershipStartTime = leadershipStartTime;
    }
}
