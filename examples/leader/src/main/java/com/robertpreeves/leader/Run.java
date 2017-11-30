package com.robertpreeves.leader;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.AgreedNodeFactory;

import java.util.HashSet;
import java.util.Set;

public class Run {
    public static void main(String[] args) {
        //todo node id
        byte nodeId = 1;
        //todo get port number for this service (-port <port>)
        int port = 8080;
        //todo get other nodes (-nodes host:port,host:port)
        Set<String> otherNodes = new HashSet<>();

//        AgreedNode<com.robertpreeves.leader.Leader> node = AgreedNodeFactory.create(
//                nodeId,
//                7220,
//                otherNodes,
//                );
    }
}
