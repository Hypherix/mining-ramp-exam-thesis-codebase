package org.example;

public class Agent {
    // Data members
    public int id;
    public int velocity;
    public int direction;
    public boolean higherPrio;
    public boolean passBayAble;


    // Constructors
    public Agent(int id, int velocity, int direction, boolean passBayAble) {
        this.id = id;
        this.velocity = velocity;
        this.direction = direction;
        this.higherPrio = false;

        if (direction == Constants.UP) {
            this.higherPrio = true;

            if(passBayAble) {
                this.passBayAble = true;
            }
            else {
                this.passBayAble = false;
            }
        }
        else if (direction == Constants.DOWN) {
            this.passBayAble = true;
        }

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
