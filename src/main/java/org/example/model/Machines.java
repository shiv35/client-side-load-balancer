package org.example.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents one machine (node) which can handle multiple Shards.
 */
public class Machines {
    private final String name;
    private final List<Shard> assignedShards = new ArrayList<>();

    public Machines(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void assignShard(Shard shard) {
        assignedShards.add(shard);
    }

    public void clearShards() {
        assignedShards.clear();
    }

    public List<Shard> getAssignedShards() {
        return assignedShards;
    }
}
