package org.example;

public class Agent {
    // Data members
    public int id;
    public int velocity;
    public int direction;
    public boolean higherPrio;


    // Constructors
    public Agent(int id, int velocity, int direction) {
        this.id = id;
        this.velocity = velocity;
        this.direction = direction;
        this.higherPrio = false;

        if (direction == Constants.UP) {
            this.higherPrio = true;
        }
    }

    // Methods
    @Override
    public boolean equals(Object o) {
        if (this == o) {        // True if same reference
            return true;
        }
        if (o == null || getClass() != o.getClass()) {  // False if null or different class
            return false;
        }

        Agent otherAgent = (Agent) o;

        return id == otherAgent.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
