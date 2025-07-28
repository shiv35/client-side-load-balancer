package org.example.model;


public class ServerNode {
    private String url;
    private long avgResponseTime;

    public ServerNode(String url) {
        this.url = url;
        this.avgResponseTime = Long.MAX_VALUE;
    }

    public String getUrl() {
        return url;
    }

    public long getAvgResponseTime() {
        return avgResponseTime;
    }

    public void setAvgResponseTime(long responseTime) {
        this.avgResponseTime = responseTime;
    }
}
