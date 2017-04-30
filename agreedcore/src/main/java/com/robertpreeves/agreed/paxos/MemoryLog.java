package com.robertpreeves.agreed.paxos;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

public class MemoryLog<T> implements Log<T> {
    private final Queue<T> queue = new LinkedList<>();

    @Override
    public void push(T value) {
        queue.add(value);
    }

    @Override
    public void replay(Consumer<T> consumer) {
        queue.forEach(entry -> consumer.accept(entry));
    }
}
