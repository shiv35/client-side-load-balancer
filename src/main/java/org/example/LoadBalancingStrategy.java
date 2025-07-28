//package org.example;
//
//// LoadBalancingStrategy.java (Interface)
//package com.loadbalancer.client.strategy;
//
//import com.loadbalancer.client.ServerNode;
//import java.util.List;
//
//public interface LoadBalancingStrategy {
//    ServerNode selectNode(List<ServerNode> availableNodes, String systemId);
//    String getStrategyName();
//}
//
//// RoundRobinStrategy.java
//package com.loadbalancer.client.strategy;
//
//        import com.loadbalancer.client.ServerNode;
//        import java.util.List;
//        import java.util.concurrent.atomic.AtomicInteger;
//
//public class RoundRobinStrategy implements LoadBalancingStrategy {
//    private final AtomicInteger counter = new AtomicInteger(0);
//
//    @Override
//    public ServerNode selectNode(List<ServerNode> availableNodes, String systemId) {
//        if (availableNodes.isEmpty()) {
//            throw new IllegalArgumentException("No available nodes for round robin selection");
//        }
//
//        int index = counter.getAndIncrement() % availableNodes.size();
//        return availableNodes.get(index);
//    }
//
//    @Override
//    public String getStrategyName() {
//        return "ROUND_ROBIN";
//    }
//
//    public int getCurrentPosition() {
//        return counter.get();
//    }
//
//    public void reset() {
//        counter.set(0);
//    }
//}
//
//// LeastLoadedStrategy.java
//package com.loadbalancer.client.strategy;
//
//        import com.loadbalancer.client.ServerNode;
//        import java.util.Comparator;
//        import java.util.List;
//        import java.util.Optional;
//
//public class LeastLoadedStrategy implements LoadBalancingStrategy {
//
//    @Override
//    public ServerNode selectNode(List<ServerNode> availableNodes, String systemId) {
//        if (availableNodes.isEmpty()) {
//            throw new IllegalArgumentException("No available nodes for least loaded selection");
//        }
//
//        Optional<ServerNode> selectedNode = availableNodes.stream()
//                .min(Comparator.comparingInt(ServerNode::getCurrentLoad)
//                        .thenComparing(ServerNode::getRequestCount));
//
//        return selectedNode.orElseThrow(() ->
//                new IllegalStateException("Unable to select node despite available nodes"));
//    }
//
//    @Override
//    public String getStrategyName() {
//        return "LEAST_LOADED";
//    }
//}
//
//// ConsistentHashStrategy.java
//package com.loadbalancer.client.strategy;
//
//        import com.loadbalancer.client.ServerNode;
//        import java.util.List;
//        import java.util.SortedMap;
//        import java.util.TreeMap;
//
//public class ConsistentHashStrategy implements LoadBalancingStrategy {
//    private final int virtualNodes;
//    private final SortedMap<Integer, ServerNode> ring;
//
//    public ConsistentHashStrategy(int virtualNodes) {
//        this.virtualNodes = virtualNodes;
//        this.ring = new TreeMap<>();
//    }
//
//    @Override
//    public ServerNode selectNode(List<ServerNode> availableNodes, String systemId) {
//        if (availableNodes.isEmpty()) {
//            throw new IllegalArgumentException("No available nodes for consistent hash selection");
//        }
//
//        // Rebuild ring if needed (in production, this should be optimized)
//        updateRing(availableNodes);
//
//        if (ring.isEmpty()) {
//            throw new IllegalStateException("Consistent hash ring is empty");
//        }
//
//        int hash = Math.abs(systemId.hashCode());
//
//        // Find the first node clockwise from the hash
//        SortedMap<Integer, ServerNode> tailMap = ring.tailMap(hash);
//        int nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
//
//        return ring.get(nodeHash);
//    }
//
//    private void updateRing(List<ServerNode> availableNodes) {
//        ring.clear();
//
//        for (ServerNode node : availableNodes) {
//            for (int i = 0; i < virtualNodes; i++) {
//                String virtualNodeId = node.getNodeId() + "-virtual-" + i;
//                int hash = Math.abs(virtualNodeId.hashCode());
//                ring.put(hash, node);
//            }
//        }
//    }
//
//    @Override
//    public String getStrategyName() {
//        return "CONSISTENT_HASH";
//    }
//
//    public int getVirtualNodes() {
//        return virtualNodes;
//    }
//
//    public int getRingSize() {
//        return ring.size();
//    }
//}
//
//// WeightedRoundRobinStrategy.java
//package com.loadbalancer.client.strategy;
//
//        import com.loadbalancer.client.ServerNode;
//        import java.util.List;
//        import java.util.concurrent.atomic.AtomicInteger;
//
//public class WeightedRoundRobinStrategy implements LoadBalancingStrategy {
//    private final AtomicInteger counter = new AtomicInteger(0);
//
//    @Override
//    public ServerNode selectNode(List<ServerNode> availableNodes, String systemId) {
//        if (availableNodes.isEmpty()) {
//            throw new IllegalArgumentException("No available nodes for weighted round robin selection");
//        }
//
//        // Calculate weights based on inverse of current load
//        int totalWeight = 0;
//        for (ServerNode node : availableNodes) {
//            // Weight is inversely proportional to current load + 1 (to avoid division by zero)
//            totalWeight += (100 - node.getCurrentLoad() + 1);
//        }
//
//        if (totalWeight <= 0) {
//            // Fallback to simple round robin
//            int index = counter.getAndIncrement() % availableNodes.size();
//            return availableNodes.get(index);
//        }
//
//        int randomWeight = counter.getAndIncrement() % totalWeight;
//        int currentWeight = 0;
//
//        for (ServerNode node : availableNodes) {
//            currentWeight += (100 - node.getCurrentLoad() + 1);
//            if (randomWeight < currentWeight) {
//                return node;
//            }
//        }
//
//        // Fallback (should not reach here)
//        return availableNodes.get(0);
//    }
//
//    @Override
//    public String getStrategyName() {
//        return "WEIGHTED_ROUND_ROBIN";
//    }
//}
//
//// StrategyFactory.java
//package com.loadbalancer.client.strategy;
//
//        import com.loadbalancer.client.config.LoadBalancerConfig;
//
//public class StrategyFactory {
//
//    public static LoadBalancingStrategy createStrategy(LoadBalancerConfig config) {
//        String strategyName = config.getLoadBalancingStrategy().toUpperCase();
//
//        switch (strategyName) {
//            case "ROUND_ROBIN":
//                return new RoundRobinStrategy();
//            case "LEAST_LOADED":
//                return new LeastLoadedStrategy();
//            case "CONSISTENT_HASH":
//                return new ConsistentHashStrategy(150); // 150 virtual nodes
//            case "WEIGHTED_ROUND_ROBIN":
//                return new WeightedRoundRobinStrategy();
//            default:
//                throw new IllegalArgumentException("Unknown load balancing strategy: " + strategyName);
//        }
//    }
//
//    public static LoadBalancingStrategy createStrategy(String strategyName) {
//        switch (strategyName.toUpperCase()) {
//            case "ROUND_ROBIN":
//                return new RoundRobinStrategy();
//            case "LEAST_LOADED":
//                return new LeastLoadedStrategy();
//            case "CONSISTENT_HASH":
//                return new ConsistentHashStrategy(150);
//            case "WEIGHTED_ROUND_ROBIN":
//                return new WeightedRoundRobinStrategy();
//            default:
//                throw new IllegalArgumentException("Unknown load balancing strategy: " + strategyName);
//        }
//    }
//}