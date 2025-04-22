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
        // The MAPFState only contains the ramp, agent locations and cost

        HashMap<Integer, ArrayList<Agent>> entries = this.agentEntries.getEntries();

        // Extract every new agent entering this TimeStep
        ArrayList<Agent> newAgentsThisTimeStep = entries.get(timeStep);

        HashMap<Agent, Integer> newAgentLocations = new HashMap<>();

        // If multiple starting agents, they will occupy the same start vertex --> put in queue instead
        putNewAgentsInQueue(this.ramp, newAgentsThisTimeStep, newAgentLocations);

        // Get the number of new agents at this timeStep
        int nrOfNewAgentsThisTimeStep = entries.get(timeStep).size();
        // Update scenario's totalAgentCount
        addTotalAgentCount(nrOfNewAgentsThisTimeStep);
        
        if(timeStep == 0) {
            // If scenario is new, newAgentLocations are the only ones existing
            setInitialState(new MAPFState(ramp, newAgentLocations));
        }
        else {
            // Add new newAgentLocations to the already existing newAgentLocations if scenario is not new
            HashMap<Agent, Integer> finalAgentLocations;
            finalAgentLocations = fetchAgentLocations();
            finalAgentLocations.putAll(newAgentLocations);

            setInitialState(new MAPFState(ramp, finalAgentLocations));
        }
    }

    public void putNewAgentsInQueue(Ramp ramp, ArrayList<Agent> newAgentsThisTimeStep,
                                    HashMap<Agent, Integer> newAgentLocations) {
        // Task: If multiple agents in the same start vertex, put them in queue instead
        // newAgentLocations is a hashmap of the agent id and its start vertex (either surface or underground)
        // of ONLY the new agents that are joining the ramp.

        // TODO: Create helper function for checking queueFree vertices. Probably don't have as a ramp
        //  data member. Instead, the helper function takes the ramp and checks for the earliest free
        //  vertices in both queues!!

        int surfaceQFree = ramp.getSurfaceQFree();
        int undergroundQFree = ramp.getUndergroundQFree();
        int surfaceStart = ramp.getSurfaceStart();
        int undergroundStart = ramp.getUndergroundStart();

        // Put excessive starting agents in their corresponding queues
        // Direction = 1 means upgoing. Direction = 0 means downgoing.
        int surfaceCount = 0;
        int undergroundCount = 0;
        for(Agent agent : newAgentsThisTimeStep) {
            // Agents starting from the surface (i.e. downgoing --> direction = 0)
            if(agent.direction == Constants.DOWN) {
                newAgentLocations.put(agent, surfaceQFree);     // TODO: Ska newAgentLocations ha Agent som key eller Agent.id?
                surfaceQFree--;
            }
            // Agents starting from the underground
            else if (agent.direction == Constants.UP){
                newAgentLocations.put(agent, undergroundQFree);
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

    HashMap<Integer, ArrayList<Agent>> fetchAgentEntries() {
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

    public HashMap<Agent, Integer> fetchAgentLocations() {
        return this.initialState.getAgentLocations();
    }

    public HashMap<Integer, ArrayList<Integer>> fetchAdjList() {
        return this.ramp.getAdjList();
    }
}
