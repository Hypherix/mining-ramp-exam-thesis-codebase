package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private AgentEntries agentEntries;





    // Constructors
//    public MAPFScenario(Ramp ramp, MAPFState initialState,
//                        HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocityDirection, int duration) {
//        this.ramp = ramp;
//        this.initialState = initialState;
//        this.newAgentLocationVelocityDirection = newAgentLocationVelocityDirection;
//        this.duration = duration;
//    }

    public MAPFScenario(Ramp ramp, AgentEntries agentEntries, int duration) {
        this.ramp = ramp;
        this.agentEntries = agentEntries;
        this.duration = duration;
        generateInitialState(0);
    }


    // Methods
    public void generateInitialState(int timeStep) {
        // Task: From the MAPFScenario, generate the first initial MAPFState
        // The MAPFState only contains the ramp, agent location and velocity

        HashMap<Integer, ArrayList<int[]>> entries = agentEntries.getEntries();
        HashMap<Integer, Integer> newAgentLocations = new HashMap<>();
        HashMap<Integer, Integer> newAgentVelocities = new HashMap<>();
        HashMap<Integer, Integer> newAgentDirections = new HashMap<>();

        // Get the number of new agents at this timeStep
        int nrOfNewAgentsThisTimeStep = entries.get(timeStep).size();

        // Extract the [direction, velocity] of every new agent this TimeStep
        ArrayList<int[]> newAgentDirectionVelocity = entries.get(timeStep);

        // Add each new agent's location, velocity and direction in respective HashMap
        // Use totalAgentCount to ensure new agents have the correct key (id) in the hashmap
        for (int i = totalAgentCount; i < nrOfNewAgentsThisTimeStep + totalAgentCount; i++) {
            newAgentDirections.put(i, newAgentDirectionVelocity.get(i)[0]);
            newAgentVelocities.put(i, newAgentDirectionVelocity.get(i)[1]);
        }

        // If multiple starting agents, they will occupy the same start vertex --> put in queue instead
        putNewAgentsInQueue(this.ramp, newAgentDirections, newAgentLocations);

        // Update scenario's totalAgentCount
        addTotalAgentCount(nrOfNewAgentsThisTimeStep);

        HashMap<Integer, Integer> finalAgentLocations;
        HashMap<Integer, Integer> finalAgentVelocities;

        if(timeStep == 0) {
            // If scenario is new, newAgentLocations/Velocities are the only ones existing
            finalAgentLocations = newAgentLocations;
            finalAgentVelocities = newAgentVelocities;
        }
        else {
            // Add new newAgentLocations to the already existing newAgentLocations if scenario is not new
            finalAgentLocations = fetchAgentLocations();
            finalAgentLocations.putAll(newAgentLocations);

            // Do the same for velocities
            finalAgentVelocities = fetchAgentVelocities();
            finalAgentVelocities.putAll(newAgentVelocities);
        }

        setInitialState(new MAPFState(ramp, finalAgentLocations, finalAgentVelocities));
    }

    public void putNewAgentsInQueue(Ramp ramp, HashMap<Integer, Integer> newAgentDirections,
                                    HashMap<Integer, Integer> newAgentLocations) {
        // Task: If multiple agents in the same start vertex, put them in queue instead
        // newAgentLocations is a hashmap of the agent id and its start vertex (either surface or underground)
        // of ONLY the new agents that are joining the ramp.

        int surfaceQFree = ramp.getSurfaceQFree();
        int undergroundQFree = ramp.getUndergroundQFree();
        int surfaceStart = ramp.getSurfaceStart();
        int undergroundStart = ramp.getUndergroundStart();

        // Put excessive starting agents in their corresponding queues
        // Direction = 1 means upgoing. Direction = 0 means downgoing.
        int surfaceCount = 0;
        int undergroundCount = 0;
        for (Map.Entry<Integer, Integer> entry : newAgentDirections.entrySet()) {
            // Agents starting from the surface (i.e. downgoing --> direction = 0)
            if (entry.getValue() == Constants.DOWN) {
                newAgentLocations.put(entry.getKey(), surfaceQFree);
                surfaceQFree--;
            }
            // Agents starting from the underground
            else if (entry.getValue() == Constants.UP) {
                newAgentLocations.put(entry.getKey(), undergroundQFree);
                undergroundQFree++;
            }
        }

        // Update the surface and underground queue free statuses
        ramp.setSurfaceQFree(surfaceQFree);
        ramp.setUndergroundQFree(undergroundQFree);
    }

    Ramp getRamp() {
        // Returns ramp
        return this.ramp;
    }

    HashMap<Integer, ArrayList<int[]>> fetchAgentEntries() {
        // Returns agentList
        return this.agentEntries.getEntries();
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
