package org.example.rebalancer;

import org.example.model.Machines;
import org.example.model.Shard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Handles client-side rebalancing of shards across machines.
 */
public class ClientRebalancer {
    private final List<Shard> allShards;

    /**
     * Pre-generate N shards (systems).
     */
    public ClientRebalancer(int totalShards) {
        this.allShards = new ArrayList<>(totalShards);
        for (int i = 0; i < totalShards; i++) {
            allShards.add(new Shard(i));
        }
    }

    /**
     * Evenly redistributes all shards across the provided machines.
     */
    public void rebalance(List<Machines> machines) {
        // 1. Clear previous assignments
        for (Machines m : machines) {
            m.clearShards();
        }
        // 2. (Optional) Shuffle for random distribution on tie
        Collections.shuffle(allShards);
        // 3. Assign by modulo
        int mCount = machines.size();
        for (int i = 0; i < allShards.size(); i++) {
            Machines target = machines.get(i % mCount);
            target.assignShard(allShards.get(i));
        }
    }
}

