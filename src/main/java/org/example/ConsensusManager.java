package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class ConsensusManager {
    private final Logger logger = LoggerFactory.getLogger(ConsensusManager.class);
    private final List<ServerNode> serverNodes;
    private final LoadBalancerConfig config;
    private final Random random;

    public ConsensusManager(List<ServerNode> serverNodes, LoadBalancerConfig config) {
        this.serverNodes = new ArrayList<>(serverNodes);
        this.config = config;
        this.random = new Random();
    }

    public boolean reachConsensus(Map<String, List<String>> proposedMapping) {
        logger.info("Starting consensus process with {} nodes", serverNodes.size());

        int agreements = 0;
        int totalHealthyNodes = 0;

        for (ServerNode node : serverNodes) {
            if (node.isHealthy()) {
                totalHealthyNodes++;
                if (simulateNodeAgreement(node, proposedMapping)) {
                    agreements++;
                }
            }
        }

        if (totalHealthyNodes == 0) {
            logger.error("No healthy nodes available for consensus");
            return false;
        }

        // Require majority consensus
        boolean consensusReached = agreements > totalHealthyNodes / 2;
        double agreementPercentage = (double) agreements / totalHealthyNodes * 100;

        logger.info("Consensus result: {} ({}/{} nodes agreed - {:.1f}%)",
                consensusReached, agreements, totalHealthyNodes, agreementPercentage);

        return consensusReached;
    }

    private boolean simulateNodeAgreement(ServerNode node, Map<String, List<String>> mapping) {
        try {
            // Simulate network latency
            Thread.sleep(ThreadLocalRandom.current().nextInt(5, 20));

            // Validate the proposed mapping for this node
            List<String> assignedSystems = mapping.get(node.getNodeId());
            if (assignedSystems == null) {
                logger.debug("Node {} has no systems assigned", node.getNodeId());
                return true; // Empty assignment is valid
            }

            // Check if the load is reasonable
            int proposedLoad = assignedSystems.size();
            boolean loadAcceptable = proposedLoad <= config.getMaxSystemsPerNode();

            // Simulate agreement based on load acceptability and random factor
            boolean agrees = loadAcceptable && (random.nextDouble() > config.getConsensusFailureRate());

            logger.debug("Node {} {} the proposal (proposed load: {}, max allowed: {})",
                    node.getNodeId(),
                    agrees ? "accepts" : "rejects",
                    proposedLoad,
                    config.getMaxSystemsPerNode());

            return agrees;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Consensus interrupted for node {}", node.getNodeId());
            return false;
        }
    }

    public boolean retryConsensus(Map<String, List<String>> proposedMapping, int maxRetries) {
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            logger.info("Consensus attempt {}/{}", attempt, maxRetries);

            if (reachConsensus(proposedMapping)) {
                logger.info("Consensus reached on attempt {}", attempt);
                return true;
            }

            if (attempt < maxRetries) {
                try {
                    // Exponential backoff
                    long backoffMs = config.getConsensusRetryBackoff() * (1L << (attempt - 1));
                    logger.info("Consensus failed, retrying in {}ms", backoffMs);
                    Thread.sleep(backoffMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Consensus retry interrupted");
                    return false;
                }
            }
        }

        logger.error("Consensus failed after {} attempts", maxRetries);
        return false;
    }
}