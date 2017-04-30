package com.robertpreeves.lock;

import com.beust.jcommander.Parameter;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class CommandLine {
    @Parameter(
            names = "-port",
            description = "The port for the public REST API"
    )
    private Integer port;

    @Parameter(
            names = "-agreed-port",
            description = "The port for internal agreed communication"
    )
    private Integer agreedPort;

    @Parameter(
            names = "-dir",
            description = "The directory where the files for distributed access are located. This" +
                    " should be a path that all nodes can access."
    )
    private String directory;

    @Parameter(
            names = "-nodes",
            description = "The list of other nodes in the group in the format hostname:port," +
                    "hostname:port,..."
    )
    private String nodes;

    @Parameter(
            names = "-help",
            description = "Documentation for this CLI."
    )
    private Boolean help;

    public Integer getPort() {
        return port;
    }

    public Integer getAgreedPort() {
        return agreedPort;
    }

    public String getDirectory() {
        return directory;
    }

    public Set<String> getNodes() {
        Set<String> nodesSet = new HashSet<>();
        String[] nodesArray = nodes.split(",");
        for (String node :
                nodesArray) {
            nodesSet.add(node);
        }

        return nodesSet;
    }

    public boolean showHelp() {
        return (help != null && help)
                || (port == null || port < 1)
                || (agreedPort == null || agreedPort < 1)
                || StringUtils.isBlank(directory)
                || StringUtils.isBlank(nodes);
    }
}
