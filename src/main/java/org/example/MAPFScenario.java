package org.example;

import java.util.HashMap;

// This class represents a MAPF problem/scenario

/*
 * Contents
 *   - Ramp (graph)
 *   - HashMap where key is time step and values are
 *     arrays of start vertices and velocities for new agents
 *   - Duration that specifies max lifetime of the scenario
 *
 * */

public class MAPFScenario {

    // Data members
    private Ramp ramp;
    private HashMap<Integer, int[]> agentList;
    private int duration;


    // Constructors
    public MAPFScenario(Ramp ramp, HashMap<Integer, int[]> agentList, int duration) {
        this.ramp = ramp;
        this.agentList = agentList;
        this.duration = duration;
    }

    // Methods
    
}
