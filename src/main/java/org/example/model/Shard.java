package org.example.model;
/**
 * Represents one unit of work or system to be assigned.
 */
public class Shard {
    private final int id;

    public Shard(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
