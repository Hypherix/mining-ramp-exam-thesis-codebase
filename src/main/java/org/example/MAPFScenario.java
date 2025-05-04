package org.example;

import java.util.ArrayList;
import java.util.Collection;
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
    private int duration;       // Specifies the latest timeStep at which new agents can enter
    private int totalAgentCount;

    private AgentEntries agentEntries;


    public MAPFScenario(Ramp ramp, AgentEntries agentEntries, int duration) {
        this.ramp = ramp;
        this.agentEntries = agentEntries;
        this.duration = duration;
        generateInitialState(0);
    }

    public MAPFScenario(Ramp ramp, MAPFState initialState, int duration) {
        // Used in ICTS since initialState is provided with known agentLocations
        // This constructor is only used for the ICTS root ICT node

        this.ramp = ramp;
        this.initialState = initialState;
        this.agentEntries = null;
        this.duration = duration;
        this.totalAgentCount = initialState.getAgentLocations().size();
    }


    // Methods

    private int getSurfaceQFree(int timeStep) {
        // Task: Get the first free vertex in the surface queue

        // If start of scenario, the first free surface queue vertex is always surface start
        if(timeStep == 0) {
            return fetchSurfaceStart() - 1;
        }

        HashMap<Agent, Integer> agentLocations = fetchAgentLocations();
        Collection<Integer> occupiedVertices = agentLocations.values();

        int surfaceStart = ramp.getSurfaceStart();

        ArrayList<Integer> verticesInSurfaceQ = ramp.getVerticesInSurfaceQ();

        for(int i = verticesInSurfaceQ.size() - 1; i >= 0; i--) {
            Integer vertex = verticesInSurfaceQ.get(i);
            if(!occupiedVertices.contains(vertex)) {
                return vertex;
            }
        }

        System.out.println("MAPFScenario->getSurfaceQFree: SURFACE QUEUE IS FULL!");
        return -1;
    }

    private int getUndergroundQFree(int timeStep) {
        // Task: Get the first free vertex in the underground queue

        // If start of scenario, the first free underground queue vertex is always underground start
        if(timeStep == 0) {
            return fetchUndergroundStart() + 1;
        }

        HashMap<Agent, Integer> agentLocations = fetchAgentLocations();
        Collection<Integer> occupiedVertices = agentLocations.values();

        int undergroundStart = ramp.getUndergroundStart();

        ArrayList<Integer> verticesInUndergroundQ = ramp.getVerticesInUndergroundQ();
        for(Integer vertex : verticesInUndergroundQ) {
            if(!occupiedVertices.contains(vertex)) {
                return vertex;
            }
        }
        System.out.println("MAPFScenario->getUndergroundQFree: UNDERGROUND QUEUE IS FULL!");
        return -1;
    }

    public void putNewAgentsInQueue(Ramp ramp, ArrayList<Agent> newAgentsThisTimeStep,
                                    HashMap<Agent, Integer> newAgentLocations, int timeStep) {
        // Task: If multiple agents in the same start vertex, put them in queue instead
        // newAgentLocations is a hashmap of the agent id and its start vertex (either surface or underground)
        // of ONLY the new agents that are joining the ramp.
        // TODO? Note! After attaining the free vertices in the beginning, this method does NOT check if
        //  the queues get full when trying to add further agents. To make this method check it,
        //  make it call getSurface/UndergroundQFree() for each respective if-statement in the for loop

        int surfaceQFree = getSurfaceQFree(timeStep);
        int undergroundQFree = getUndergroundQFree(timeStep);

        // Put excessive starting agents in their corresponding queues
        for(Agent agent : newAgentsThisTimeStep) {
            // Agents starting from the surface (i.e. downgoing)
            if(agent.direction == Constants.DOWN) {
                newAgentLocations.put(agent, surfaceQFree);
                surfaceQFree--;
            }
            // Agents starting from the underground
            else if (agent.direction == Constants.UP){
                newAgentLocations.put(agent, undergroundQFree);
                undergroundQFree++;
            }
        }
    }

    public void generateInitialState(int timeStep) {
        // Task: From the MAPFScenario, generate the first initial MAPFState
        // The MAPFState only contains the ramp, agent locations and cost

        HashMap<Integer, ArrayList<Agent>> entries = this.agentEntries.getEntries();

        // Extract every new agent entering this TimeStep
        ArrayList<Agent> newAgentsThisTimeStep = entries.get(timeStep);

        HashMap<Agent, Integer> newAgentLocations = new HashMap<>();

        // If multiple starting agents, they will occupy the same start vertex --> put in queue instead
        putNewAgentsInQueue(this.ramp, newAgentsThisTimeStep, newAgentLocations, timeStep);

        // Get the number of new agents at this timeStep
        int nrOfNewAgentsThisTimeStep = entries.get(timeStep).size();
        // Update scenario's totalAgentCount
        addTotalAgentCount(nrOfNewAgentsThisTimeStep);

        if(timeStep == 0) {
            // If scenario is new, newAgentLocations are the only ones existing
            setInitialState(new MAPFState(ramp, newAgentLocations, 0, timeStep));
        }
        else {
            // Add new newAgentLocations to the already existing newAgentLocations if scenario is not new
            HashMap<Agent, Integer> finalAgentLocations;
            finalAgentLocations = fetchAgentLocations();    // agentLocations from before
            finalAgentLocations.putAll(newAgentLocations);  // add the new agentLocations to those from before

            // Update scenario's activeAgents
            this.initialState.addActiveAgents(newAgentsThisTimeStep);

            int newGcost = fetchNumOfActiveAgents();    // TODO CHECK: Correct gCost?

            setInitialState(new MAPFState(ramp, finalAgentLocations, newGcost, timeStep));
        }
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

    public int fetchSurfaceExit() {
        return this.ramp.getSurfaceExit();
    }

    public int fetchUndergroundExit() {
        return this.ramp.getUndergroundExit();
    }

    public int fetchRampLength() {
        return this.ramp.getRampLength();
    }

    public HashMap<Agent, Integer> fetchAgentLocations() {
        return this.initialState.getAgentLocations();
    }

    public HashMap<Integer, UpDownNeighbourList> fetchAdjList() {
        return this.ramp.getAdjList();
    }

    public MAPFState getInitialState() {
        return this.initialState;
    }

    public int fetchNumOfActiveAgents() {
        return this.initialState.getNumOfActiveAgents();
    }

    public ArrayList<Integer> fetchVerticesInPassingBays() {
        return this.ramp.getVerticesInPassingBays();
    }

    public ArrayList<ArrayList<Integer>> fetchPassingBayVertices() {
        return this.ramp.getPassingBayVertices();
    }

    public ArrayList<Integer> fetchVerticesInSurfaceQ() {
        return this.ramp.getVerticesInSurfaceQ();
    }

    public ArrayList<Integer> fetchVerticesInUndergroundQ() {
        return this.ramp.getVerticesInUndergroundQ();
    }
}
