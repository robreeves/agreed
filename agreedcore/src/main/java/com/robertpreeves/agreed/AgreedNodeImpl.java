package com.robertpreeves.agreed;

import com.robertpreeves.agreed.observer.Observer;

import java.util.ArrayList;
import java.util.List;

class AgreedNodeImpl<T> implements AgreedNode<T> {
    private List<Observer<T>> observers = new ArrayList<Observer<T>>();

    public AgreedNodeImpl(List<AgreedNodeEndpoint> otherNodes) {
        //todo
    }

    public void propose(T value) {
        //todo
    }

    public void subscribe(Observer<T> observer) {
        observers.add(observer);
    }

    private void notify(final T value) {
        observers.forEach(observer -> observer.notify(value));
    }
}
