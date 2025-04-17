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
    private int duration;
    private int totalAgentCount;
    // Key = timeStep where the agents are entering the scenario
    // Value = [start location, velocity] for each of the entering agents
    private HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocity;





    // Constructors
    public MAPFScenario(Ramp ramp, MAPFState initialState,
                        HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocity, int duration) {
        this.ramp = ramp;
        this.initialState = initialState;
        this.newAgentLocationVelocity = newAgentLocationVelocity;
        this.duration = duration;
    }


    // Methods
    Ramp getRamp() {
        // Returns ramp
        return this.ramp;
    }

    HashMap<Integer, ArrayList<int[]>> getNewAgentLocationVelocity() {
        // Returns agentList
        return this.newAgentLocationVelocity;
    }

    public int getDuration() {
        // Return duration
        return this.duration;
    }

    public void setInitialState(MAPFState initialState) {
        this.initialState = initialState;
    }

    public int getTotalAgentCount() {
        return this.totalAgentCount;
    }

    public void addTotalAgentCount(int nrOfNewAgents) {
        this.totalAgentCount += nrOfNewAgents;
    }

    public void setTotalAgentCount(int totalAgentCount) {
        this.totalAgentCount = totalAgentCount;
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

    public HashMap<Integer, Integer> fetchAgentLocations() {
        return this.initialState.getAgentLocations();
    }

    public HashMap<Integer, Integer> fetchAgentVelocities() {
        return this.initialState.getAgentVelocities();
    }

    public HashMap<Integer, ArrayList<Integer>> fetchAdjList() {
        return this.ramp.getAdjList();
    }
}
