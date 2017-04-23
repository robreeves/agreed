package com.robertpreeves.lock;

import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.AgreedNodeEndpoint;
import com.robertpreeves.agreed.AgreedNodeFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusLogger;

import java.util.Arrays;

public class Run {
    public static void main(String[] args) {
        //StatusLogger.getLogger().setLevel(Level.DEBUG);

        AgreedNode<LockState> agreeNode = AgreedNodeFactory.create(Arrays.asList(new
                AgreedNodeEndpoint("localhost", 8111, 1)));




        //LockNode lockNode = new LockNode(agreeNode);

//        try {
//            //run forever
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
