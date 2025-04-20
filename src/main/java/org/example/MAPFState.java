package org.example;

import java.util.Comparator;
import java.util.HashMap;

/*
* - The purpose of MAPFState is to act as input for the MAPF algorithms. It only contains
*   the ramp adjacency list with agent locations and velocity information.
*
* - A MAPFState is created by the MAPFSolver. It takes its MAPFScenario and creates a
*   MAPFState from it. The MAPFScenario has agentList that specifies whenever new agents
*   enter the scenario. When that happens, MAPFSolver creates a new MAPFState and calls
*   the MAPF algorithm anew. This is the replan.
*
* */
public class MAPFState {

    // Data members
    private Ramp ramp;
    private HashMap<Integer, Integer> agentLocations;
    private HashMap<Integer, Integer> agentVelocities;
    private int cost;

    // Constructors
    public MAPFState(Ramp ramp, HashMap<Integer, Integer> agentLocation,
                     HashMap<Integer, Integer> agentVelocity) {
        this.ramp = ramp;
        this.agentLocations = agentLocation;
        this.agentVelocities = agentVelocity;
        //this.cost = calculateCost;
    }

    public MAPFState(Ramp ramp) {
        this.ramp = ramp;
        this.agentLocations = new HashMap<Integer, Integer>();
        this.agentVelocities = new HashMap<Integer, Integer>();
    }

    // Methods
    public int calculateCost() {
        // TODO: calculateCost
        // Undo comment in constructor when done
        return 0;
    }

    Ramp getRamp() {
        return this.ramp;
    }

    HashMap<Integer, Integer> getAgentLocations() {
        return this.agentLocations;
    }

    HashMap<Integer, Integer> getAgentVelocities() {
        return this.agentVelocities;
    }

    public int getCost() {
        return this.cost;
    }
}

