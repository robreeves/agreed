package com.robertpreeves.agreed.paxos;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

import sun.plugin.dom.exception.InvalidStateException;

public class MemoryLog<T> implements Log<T> {
    private final Queue<T> queue = new LinkedList<>();
    private long sequenceNumber;

    @Override
    public long getSequenceNumber() {
        return sequenceNumber;
    }

    @Override
    public void push(long sequenceNumber, T value) {
        if (Long.compareUnsigned(this.sequenceNumber, sequenceNumber) >= 0) {
            throw new InvalidStateException(String.format("Sequence number must be greater than " +
                            "current sequence number. Cur: %s, New: %s",
                    Long.toUnsignedString(this.sequenceNumber),
                    Long.toUnsignedString(sequenceNumber)));
        }

        this.sequenceNumber = sequenceNumber;
        queue.add(value);
    }

    @Override
    public void replay(Consumer<T> consumer) {
        queue.forEach(entry -> consumer.accept(entry));
    }
}
