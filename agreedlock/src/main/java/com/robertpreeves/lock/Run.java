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

        //A lock subscriber will subscribe to the agreeNode
        //It will be responsible for maintaining all lock states
        //agreeNode.subscribe(todo);

        //REST API will take a file name and contents to append
        //It will check the lock state. If it is free then it will propose a new value
        //If it is not free it will reject the request
        //agreeNode.propose(todo);
        //after the propose step is done then it will check the lock again and see if
        //the nodes agreed to give it the lock



        //LockNode lockNode = new LockNode(agreeNode);

//        try {
//            //run forever
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}