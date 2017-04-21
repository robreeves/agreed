package com.robertpreeves.lock;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.AgreedNodeEndpoint;
import com.robertpreeves.agreed.AgreedNodeFactory;

import java.util.Arrays;

public class Run {
    public static void main(String[] args) {
        AgreedNode<LockState> agreeNode = AgreedNodeFactory.create(Arrays.asList(new
                AgreedNodeEndpoint("localhost", 8111)));

        LockNode lockNode = new LockNode(agreeNode);

        try {
            //run forever
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
