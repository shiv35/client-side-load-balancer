package org.example.server;
import org.example.model.Machines;

import java.util.List;
/**
 * Simulates the server's consensus on which machines are currently active.
 */
public class ServerPanel {
    private final List<Machines> activeMachines;

    public ServerPanel(List<Machines> activeMachines) {
        this.activeMachines = activeMachines;
    }

    /**
     * Returns the list of machines that the client should use.
     */
    public List<Machines> getActiveMachines() {
        return activeMachines;
    }
}

