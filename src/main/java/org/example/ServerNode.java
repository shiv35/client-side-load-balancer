package org.example;


import java.util.concurrent.atomic.AtomicInteger;

public class ServerNode {
    private final String nodeId;
    private final String ipAddress;
    private final int port;
    private volatile int currentLoad;
    private volatile boolean isHealthy;
    private final AtomicInteger requestCount;

    public ServerNode(String nodeId, String ipAddress, int port) {
        this.nodeId = nodeId;
        this.ipAddress = ipAddress;
        this.port = port;
        this.currentLoad = 0;
        this.isHealthy = true;
        this.requestCount = new AtomicInteger(0);
    }

    public void incrementLoad() {
        this.currentLoad++;
        this.requestCount.incrementAndGet();
    }

    public void decrementLoad() {
        this.currentLoad = Math.max(0, this.currentLoad - 1);
    }

    // Getters and setters
    public String getNodeId() { return nodeId; }
    public String getIpAddress() { return ipAddress; }
    public int getPort() { return port; }
    public int getCurrentLoad() { return currentLoad; }
    public boolean isHealthy() { return isHealthy; }
    public void setHealthy(boolean healthy) { isHealthy = healthy; }
    public int getRequestCount() { return requestCount.get(); }

    @Override
    public String toString() {
        return String.format("Node[%s:%s:%d] - Load: %d, Healthy: %s",
                nodeId, ipAddress, port, currentLoad, isHealthy);
    }
}