package org.example;

// LoadBalancerConfig.java

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadBalancerConfig {
    private static final Logger logger = LoggerFactory.getLogger(LoadBalancerConfig.class);
    private final Properties properties;

    // Default configuration values
    private static final int DEFAULT_TOTAL_SYSTEMS = 200;
    private static final int DEFAULT_TOTAL_SHARDS = 64;
    private static final int DEFAULT_MAX_SYSTEMS_PER_NODE = 100;
    private static final double DEFAULT_REBALANCE_THRESHOLD = 0.2;
    private static final long DEFAULT_REBALANCE_INTERVAL = 300000; // 5 minutes
    private static final double DEFAULT_CONSENSUS_FAILURE_RATE = 0.1; // 10% failure rate
    private static final int DEFAULT_CONSENSUS_MAX_RETRIES = 3;
    private static final long DEFAULT_CONSENSUS_RETRY_BACKOFF = 1000; // 1 second

    public LoadBalancerConfig() {
        this.properties = new Properties();
        loadConfiguration();
    }

    public LoadBalancerConfig(String configFile) {
        this.properties = new Properties();
        loadConfiguration(configFile);
    }

    private void loadConfiguration() {
        loadConfiguration("application.properties");
    }

    private void loadConfiguration(String configFile) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFile)) {
            if (input != null) {
                properties.load(input);
                logger.info("Configuration loaded from {}", configFile);
            } else {
                logger.warn("{} not found, using default values", configFile);
            }
        } catch (IOException e) {
            logger.error("Error loading configuration from {}", configFile, e);
        }
    }

    // System Configuration
    public int getTotalSystems() {
        return getIntProperty("loadbalancer.systems.total", DEFAULT_TOTAL_SYSTEMS);
    }

    public int getTotalShards() {
        return getIntProperty("loadbalancer.shards.total", DEFAULT_TOTAL_SHARDS);
    }

    public int getMaxSystemsPerNode() {
        return getIntProperty("loadbalancer.node.max.systems", DEFAULT_MAX_SYSTEMS_PER_NODE);
    }

    // Rebalancing Configuration
    public double getRebalanceThreshold() {
        return getDoubleProperty("loadbalancer.rebalance.threshold", DEFAULT_REBALANCE_THRESHOLD);
    }

    public long getRebalanceInterval() {
        return getLongProperty("loadbalancer.rebalance.interval", DEFAULT_REBALANCE_INTERVAL);
    }

    // Consensus Configuration
    public double getConsensusFailureRate() {
        return getDoubleProperty("loadbalancer.consensus.failure.rate", DEFAULT_CONSENSUS_FAILURE_RATE);
    }

    public int getConsensusMaxRetries() {
        return getIntProperty("loadbalancer.consensus.max.retries", DEFAULT_CONSENSUS_MAX_RETRIES);
    }

    public long getConsensusRetryBackoff() {
        return getLongProperty("loadbalancer.consensus.retry.backoff", DEFAULT_CONSENSUS_RETRY_BACKOFF);
    }

    // Node Configuration
    public String getNodeHost(int nodeIndex) {
        return getProperty(String.format("loadbalancer.node.%d.host", nodeIndex), "localhost");
    }

    public int getNodePort(int nodeIndex) {
        return getIntProperty(String.format("loadbalancer.node.%d.port", nodeIndex), 8080 + nodeIndex);
    }

    public String getNodeId(int nodeIndex) {
        return getProperty(String.format("loadbalancer.node.%d.id", nodeIndex), "node-" + (nodeIndex + 1));
    }

    public int getTotalNodes() {
        return getIntProperty("loadbalancer.nodes.total", 3);
    }

    // Load Balancing Strategy
    public String getLoadBalancingStrategy() {
        return getProperty("loadbalancer.strategy", "ROUND_ROBIN");
    }

    // Generic property accessors
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    public int getIntProperty(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warn("Invalid integer property {}, using default {}", key, defaultValue);
            return defaultValue;
        }
    }

    public long getLongProperty(String key, long defaultValue) {
        try {
            return Long.parseLong(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warn("Invalid long property {}, using default {}", key, defaultValue);
            return defaultValue;
        }
    }

    public double getDoubleProperty(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            logger.warn("Invalid double property {}, using default {}", key, defaultValue);
            return defaultValue;
        }
    }

    public boolean getBooleanProperty(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }

    public void printConfiguration() {
        logger.info("=== Load Balancer Configuration ===");
        logger.info("Total Systems: {}", getTotalSystems());
        logger.info("Total Shards: {}", getTotalShards());
        logger.info("Total Nodes: {}", getTotalNodes());
        logger.info("Max Systems per Node: {}", getMaxSystemsPerNode());
        logger.info("Rebalance Threshold: {}", getRebalanceThreshold());
        logger.info("Rebalance Interval: {}ms", getRebalanceInterval());
        logger.info("Load Balancing Strategy: {}", getLoadBalancingStrategy());
        logger.info("Consensus Max Retries: {}", getConsensusMaxRetries());
        logger.info("=====================================");
    }
}