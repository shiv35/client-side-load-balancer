package org.example;

// SystemUnit.java

public class SystemUnit {
    private final String systemId;
    private final String systemType;
    private volatile String assignedNode;
    private final long createdTime;

    public SystemUnit(String systemId, String systemType) {
        this.systemId = systemId;
        this.systemType = systemType;
        this.createdTime = System.currentTimeMillis();
    }

    public String getSystemId() {
        return systemId;
    }

    public String getSystemType() {
        return systemType;
    }

    public String getAssignedNode() {
        return assignedNode;
    }

    public void setAssignedNode(String assignedNode) {
        this.assignedNode = assignedNode;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    @Override
    public String toString() {
        return String.format("System[%s:%s] -> %s", systemId, systemType, assignedNode);
    }
}