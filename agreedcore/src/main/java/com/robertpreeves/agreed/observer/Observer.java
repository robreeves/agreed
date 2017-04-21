package com.robertpreeves.agreed.observer;

public interface Observer<T> {
    void notify(T value);
}
