package com.robertpreeves.lock;

import com.beust.jcommander.JCommander;
import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.AgreedNodeFactory;

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

        byte nodeId = 0;
        try (AgreedNode<FileLock> agreeNode =
                     AgreedNodeFactory.create(nodeId, cli.getAgreedPort(), cli.getNodes())) {
            //testing
            FileLock lock = new FileLock().lock();
            agreeNode.propose(lock);

            lock = lock.unlock();
            agreeNode.propose(lock);

            //        PublicApi api = new PublicApi(cli.getPort(), agreeNode);
//
//        try {
//            //run forever
//            Thread.currentThread().join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
