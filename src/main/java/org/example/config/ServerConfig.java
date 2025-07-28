package org.example.config;


import org.example.model.ServerNode;

import java.util.Arrays;
import java.util.List;

public class ServerConfig {
    public static List<ServerNode> getServerList() {
        return Arrays.asList(
                new ServerNode("http://localhost:8081/process"),
                new ServerNode("http://localhost:8082/process"),
                new ServerNode("http://localhost:8083/process")
        );
    }
}