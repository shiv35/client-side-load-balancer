package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ShardManager {
    private final Logger logger = LoggerFactory.getLogger(ShardManager.class);
    private final int totalShards;
    private final Map<Integer, String> shardToNodeMapping;
    private final Map<String, Integer> systemToShardMapping;

    public ShardManager(int totalShards) {
        this.totalShards = totalShards;
        this.shardToNodeMapping = new ConcurrentHashMap<>();
        this.systemToShardMapping = new ConcurrentHashMap<>();
    }

    public int calculateShard(String systemId) {
        // Use consistent hashing based on system ID
        int hash = Math.abs(systemId.hashCode());
        int shard = hash % totalShards;

        // Store the mapping for future reference
        systemToShardMapping.put(systemId, shard);

        logger.debug("System {} mapped to shard {}", systemId, shard);
        return shard;
    }

    public void updateShardMapping(int shardId, String nodeId) {
        String previousNode = shardToNodeMapping.put(shardId, nodeId);

        if (previousNode != null && !previousNode.equals(nodeId)) {
            logger.info("Shard {} remapped from node {} to node {}", shardId, previousNode, nodeId);
        } else {
            logger.debug("Shard {} mapped to node {}", shardId, nodeId);
        }
    }

    public String getNodeForShard(int shardId) {
        return shardToNodeMapping.get(shardId);
    }

    public String getNodeForSystem(String systemId) {
        Integer shardId = systemToShardMapping.get(systemId);
        if (shardId != null) {
            return shardToNodeMapping.get(shardId);
        }
        return null;
    }

    public Map<Integer, String> getAllShardMappings() {
        return new HashMap<>(shardToNodeMapping);
    }

    public Map<String, Integer> getSystemShardMappings() {
        return new HashMap<>(systemToShardMapping);
    }

    public void clearMappings() {
        shardToNodeMapping.clear();
        systemToShardMapping.clear();
        logger.info("All shard mappings cleared");
    }

    public int getTotalShards() {
        return totalShards;
    }

    public int getAssignedShardsCount() {
        return shardToNodeMapping.size();
    }

    public void printShardDistribution() {
        Map<String, Integer> nodeShardCount = new HashMap<>();

        for (String nodeId : shardToNodeMapping.values()) {
            nodeShardCount.merge(nodeId, 1, Integer::sum);
        }

        logger.info("=== Shard Distribution ===");
        for (Map.Entry<String, Integer> entry : nodeShardCount.entrySet()) {
            logger.info("Node {}: {} shards", entry.getKey(), entry.getValue());
        }
        logger.info("Total assigned shards: {}/{}", getAssignedShardsCount(), totalShards);
    }
}