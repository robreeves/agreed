package com.robertpreeves.lock;

import com.beust.jcommander.Parameter;
import org.apache.commons.lang3.StringUtils;

public class CommandLine {
    @Parameter(
            names = "-port",
            description = "The port for the public REST API"
    )
    private Integer port;

    @Parameter(
            names = "-dir",
            description = "The directory where the files for distributed access are located. This" +
                    " should be a path that all nodes can access."
    )
    private String directory;

    @Parameter(
            names = "-nodes",
            description = "The path to the file with the list of nodes."
    )
    private String nodesFile;

    @Parameter(
            names = "-node-index",
            description = "The index for this node in the -nodes file."
    )
    private Integer nodeIndex;

    @Parameter(
            names = "-help",
            description = "Documentation for this CLI."
    )
    private Boolean help;

    public Integer getPort() {
        return port;
    }

    public String getDirectory() {
        return directory;
    }

    public String getNodesFile() {
        return nodesFile;
    }

    public Integer getNodeIndex() {
        return nodeIndex;
    }

    public boolean showHelp() {
        return (help != null && help)
                || (port == null || port < 1)
                || StringUtils.isBlank(directory)
                || StringUtils.isBlank(nodesFile)
                || (nodeIndex == null || nodeIndex < 0);
    }
}
