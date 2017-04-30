package com.robertpreeves.lock;

import com.beust.jcommander.JCommander;
import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.AgreedNodeEndpoint;
import com.robertpreeves.agreed.AgreedNodeFactory;

import java.util.Arrays;
import java.util.List;

public class Run {
    public static void main(String[] args) {
        CommandLine cli = new CommandLine();
        JCommander cliParser = JCommander.newBuilder()
                .addObject(cli)
                .build();
        cliParser.setProgramName(Run.class.getCanonicalName());
        cliParser.parse(args);

        if (cli.showHelp()) {
            cliParser.usage();
            return;
        }


        List<AgreedNodeEndpoint> nodes = Arrays.asList(
                new AgreedNodeEndpoint("localhost", 8111),
                new AgreedNodeEndpoint("localhost", 8112),
                new AgreedNodeEndpoint("localhost", 8113)
        );

        AgreedNode<FileLock> agreeNode = AgreedNodeFactory.create(0, nodes);

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

        PublicApi api = new PublicApi(8080, agreeNode);

        try {
            //run forever
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
