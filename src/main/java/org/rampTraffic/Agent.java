package org.rampTraffic;

public class Agent {
    // Data members
    public int id;
    public int velocity;        // currently not used
    public int direction;
    public boolean higherPrio;
    public boolean passBayAble;


    // Constructors
    public Agent(int id, int velocity, int direction, boolean passBayAble, boolean higherPrio) {
        this.id = id;
        this.velocity = velocity;
        this.direction = direction;
        this.higherPrio = higherPrio;
        this.passBayAble = passBayAble;
    }

    // Copy constructor
    public Agent(Agent other) {
        this.id = other.id;
        this.velocity = other.velocity;
        this.direction = other.direction;
        this.higherPrio = other.higherPrio;
        this.passBayAble = other.passBayAble;
    }


    // Methods
    @Override
    public boolean equals(Object o) {
        // Used to compare two agent instances

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
