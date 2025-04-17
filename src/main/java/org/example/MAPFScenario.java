package org.example;

import java.util.ArrayList;
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
    private MAPFState initialState;
    private HashMap<Integer, int[]> agentList;
    private int duration;


    // Constructors
    public MAPFScenario(Ramp ramp, MAPFState initialState, HashMap<Integer, int[]> agentList, int duration) {
        this.ramp = ramp;
        this.initialState = initialState;
        this.agentList = agentList;
        this.duration = duration;
    }


    // Methods
    Ramp getRamp() {
        // Returns ramp
        return this.ramp;
    }

    HashMap<Integer, int[]> getAgentList() {
        // Returns agentList
        return this.agentList;
    }

    int getDuration() {
        // Return duration
        return this.duration;
    }

    public int fetchSurfaceStart() {
        return this.ramp.getSurfaceStart();
    }

    public int fetchUndergroundStart() {
        return this.ramp.getUndergroundStart();
    }

    public int fetchVerticesInActualRamp() {
        return this.ramp.getVerticesInActualRamp();
    }

    public HashMap<Integer, ArrayList<Integer>> fetchAdjList() {
        return this.ramp.getAdjList();
    }
}
