package com.robertpreeves.lock;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.robertpreeves.agreed.AgreedNode;
import com.robertpreeves.agreed.AgreedNodeFactory;


public class Run {
    public static void main(String[] args) {
        CommandLine cli = new CommandLine();
        JCommander cliParser = JCommander.newBuilder()
                .addObject(cli)
                .build();
        cliParser.setProgramName(Run.class.getCanonicalName());

        try {
            cliParser.parse(args);
        } catch (ParameterException e) {
            System.err.println(e.getMessage());
        }

        if (cli.showHelp()) {
            cliParser.usage();
            return;
        }

        try (AgreedNode<String> agreeNode =
                     AgreedNodeFactory.create(
                             cli.getNodeId(), cli.getAgreedPort(), cli.getNodes())) {

            Api api = new Api(cli.getPort(), agreeNode);

            //run forever
            Thread.currentThread().join();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
