package org.example;

public class Vertex {

    // Data members
    private int id;
    private char occupiedBy;            // the agent that occupies the vertex


    // Constructors
    public Vertex(int id) {
        this.id = id;
    }

    // Methods

    int getID() {
        return this.id;
    }
}
