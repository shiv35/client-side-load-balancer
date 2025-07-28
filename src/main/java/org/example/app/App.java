package org.example.app;

import org.example.model.Machines;
import org.example.model.Shard;
import org.example.rebalancer.ClientRebalancer;
import org.example.server.ServerPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point:
 * - ServerPanel decides which machines are up
 * - ClientRebalancer distributes 200 shards among them
 * - Prints out the assignment
 */
public class App {
    public static void main(String[] args) {
        final int TOTAL_SHARDS = 200;

        // 1. Simulate server consensus: pick 3 machines
        List<Machines> machines = new ArrayList<>();
        machines.add(new Machines("Machine-1"));
        machines.add(new Machines("Machine-2"));
        machines.add(new Machines("Machine-3"));
        // To test 4 machines, uncomment the next line:
        // machines.add(new Machine("Machine-4"));

        ServerPanel server = new ServerPanel(machines);
        ClientRebalancer rebalancer = new ClientRebalancer(TOTAL_SHARDS);

        // 2. Fetch active machines and rebalance
        List<Machines> active = server.getActiveMachines();
        rebalancer.rebalance(active);

        // 3. Print results
        for (Machines m : active) {
            System.out.println("== " + m.getName() + " ==");
            System.out.println("Assigned shards: " + m.getAssignedShards().size());
            System.out.print("Shard IDs: ");
            for (Shard s : m.getAssignedShards()) {
                System.out.print(s.getId() + " ");
            }
            System.out.println("\n");
        }
    }
}

