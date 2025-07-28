
# ⚖️ Client-Side Load Balancer with Rebalancing

This project demonstrates a client-side load balancing system with automatic rebalancing based on real-time RTT (Round Trip Time) metrics. The setup simulates multiple backend services and distributes incoming requests based on latency, adapting to failures and varying performance.

---

## 🚀 How to Launch

1. **Run the JAR file (single process that hosts both backend and client controllers).**

2. **Start the backends manually using different ports (8081, 8082, 8083):**

```bash
java -jar target/client-side-rebalancer-1.0.0.jar --server.port=8081
java -jar target/client-side-rebalancer-1.0.0.jar --server.port=8082
java -jar target/client-side-rebalancer-1.0.0.jar --server.port=8083
```

3. **Start the client process via HTTP request:**

```bash
  [POST /client/start?count=200](http://localhost:8080/client/start?count=200)
```

---

## ⚙️ How It Works

- The **LoadBalancerService** sends 200 HTTP requests.
- It always pings all active servers and measures their round-trip time (RTT).
- Chooses the fastest responding server for each task.
- Rebalances when a server becomes measurably faster than the current one.

### 📋 Console Logs Display:

- Warm-up RTTs
- Per-request RTT and chosen backend
- Rebalancing events when the selected server changes

---

## 📊 Load Distribution Outcomes

- **With 3 servers:** Around 67, 67, and 66 requests are distributed depending on latency.
- **With 2 servers:** Each server receives about 100 requests.
- **Server failure:** A failing server’s RTT spikes to infinity, and it's avoided for the remaining requests.

---

## ✅ Why This Solves the Load Balancing Problem

- **Client-side decision-making:** Every request is dynamically assigned based on live latency.
- **Shard analogy:** Each task is like a shard, dynamically routed by current RTT rather than a static hash/modulo.
- **No central load balancer needed:** Clients automatically detect and react to performance shifts or failures.
- **Extendable:** Current static `ServerConfig` can be extended to support dynamic discovery or external config sources.

---

## 📦 Components

- `ClientController`: Triggers the load test and handles rebalancing.
- `LoadBalancerService`: Measures RTT, selects the best server, sends the request.
- `BackendController`: Simulated backend server to handle requests.

---

## 🔧 Build & Run

Ensure Java and Maven are installed.

```bash
mvn clean install
```

To run:
In order to invoke the client server
```bash
java -jar target/client-side-rebalancer-1.0.0.jar --server.port=8080
```
---
