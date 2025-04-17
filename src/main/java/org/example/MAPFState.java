package org.example;

import java.util.ArrayList;
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
    private HashMap<Integer, Integer> agentLocation;
    private HashMap<Integer, Integer> agentVelocity;

    // Constructors
    public MAPFState(Ramp ramp, HashMap<Integer, Integer> agentLocation,
                     HashMap<Integer, Integer> agentVelocity) {
        this.ramp = ramp;
        this.agentLocation = agentLocation;
        this.agentVelocity = agentVelocity;
    }

    // Methods
    Ramp getRamp() {
        return this.ramp;
    }

    HashMap<Integer, Integer> getAgentLocation() {
        return this.agentLocation;
    }

    HashMap<Integer, Integer> getAgentVelocity() {
        return this.agentVelocity;
    }
}
