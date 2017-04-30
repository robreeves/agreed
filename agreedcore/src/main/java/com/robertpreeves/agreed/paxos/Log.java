package com.robertpreeves.agreed.paxos;

import java.util.function.Consumer;

public interface Log<T> {
    /**
     * Gets the most recent sequence number
     * @return
     */
    long getSequenceNumber();

    /**
     * Adds a new entry to the log
     * @param value
     */
    void push(long sequenceNumber, T value);

    /**
     * Replays all log entries from oldest to newest
     * @param consumer
     */
    void replay(Consumer<T> consumer);
}
