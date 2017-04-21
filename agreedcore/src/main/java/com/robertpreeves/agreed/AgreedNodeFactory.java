package com.robertpreeves.agreed;

import java.util.List;

public class AgreedNodeFactory {
    public static <T> AgreedNode<T> create(List<AgreedNodeEndpoint> otherNodes) {
        return new AgreedNodeImpl(otherNodes);
    }
}
