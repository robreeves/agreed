package com.robertpreeves.agreed.observer;

public interface Observable<T> {
    void subscribe(Observer<T> observer);
}
