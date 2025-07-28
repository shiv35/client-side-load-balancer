package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.config.ServerConfig;
import org.example.model.ServerNode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Service
public class LoadBalancerService {
    private final List<ServerNode> servers = ServerConfig.getServerList();
    private ServerNode currentBest = servers.get(0);
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendLoad(int count) {
        for (int i = 0; i < count; i++) {
            rebalanceIfNeeded();
            long start = System.currentTimeMillis();
            try {
                restTemplate.getForObject(currentBest.getUrl(), String.class);
            } catch (Exception e) {
                log.error("Error sending to {}", currentBest.getUrl(), e);
            }
            long responseTime = System.currentTimeMillis() - start;
            currentBest.setAvgResponseTime(responseTime);
            log.info("Sent system {} to {} | Response: {}ms", (i + 1), currentBest.getUrl(), responseTime);
        }
    }

    private void rebalanceIfNeeded() {
        ServerNode fastest = selectBestService();
        if (!fastest.getUrl().equals(currentBest.getUrl()) &&
                fastest.getAvgResponseTime() < currentBest.getAvgResponseTime() - 20) {
            log.info("Rebalancing from {} to {}", currentBest.getUrl(), fastest.getUrl());
            currentBest = fastest;
        }
    }

    private ServerNode selectBestService() {
        ServerNode best = currentBest;
        for (ServerNode server : servers) {
            long start = System.currentTimeMillis();
            try {
                restTemplate.getForObject(server.getUrl(), String.class);
            } catch (Exception ignored) {}
            long time = System.currentTimeMillis() - start;
            server.setAvgResponseTime(time);
            if (time < best.getAvgResponseTime()) {
                best = server;
            }
        }
        return best;
    }
}