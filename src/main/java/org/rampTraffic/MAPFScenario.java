package org.rampTraffic;

import java.util.*;

public class MAPFScenario {

    // Data members
    private final Ramp ramp;
    private MAPFState initialState;
    private final int lifespan;
    private int totalAgentCount;
    private final AgentEntries agentEntries;

    // Constraints (used by CBS)
    // <constrainedAgent, <timeStep, prohibitedVertexSet>>
    private HashMap<Agent, HashMap<Integer, Set<Integer>>> vertexConstraints;
    // <constrainedAgent, <timeStep, set of (fromVertex, toVertex)>>
    private HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> edgeConstraints;


    // Constructors

    public MAPFScenario(Ramp ramp, AgentEntries agentEntries, int lifespan) {
        this.ramp = ramp;
        this.agentEntries = agentEntries;
        this.lifespan = lifespan;
        this.initialState = generateState(0, null);
    }

    public MAPFScenario(Ramp ramp, MAPFState initialState, int lifespan) {
        // Used in ICTS since initialState is provided with known agentLocations
        // Only used by the ICTS root ICT node
        this.ramp = ramp;
        this.initialState = initialState;
        this.agentEntries = null;
        this.lifespan = lifespan;
        this.totalAgentCount = initialState.getAgentLocations().size();
    }

    public MAPFScenario(Ramp ramp, MAPFState initialState, int lifespan,
                        HashMap<Agent, HashMap<Integer, Set<Integer>>> vertexConstraints,
                        HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> edgeConstraints) {
        // Used in CBS
        this.ramp = ramp;
        this.initialState = initialState;
        this.agentEntries = null;
        this.lifespan = lifespan;
        this.totalAgentCount = initialState.getAgentLocations().size();

        this.vertexConstraints = vertexConstraints;
        this.edgeConstraints = edgeConstraints;
    }

    // Methods

    private int getSurfaceQFree(int timeStep, MAPFState knownState) {
        // Returns the first free vertex in the surface queue

        if(timeStep == 0) {
            return fetchSurfaceStart() - 1;
        }

        // If knownState != null, get known agent locations from knownState
        HashMap<Agent, Integer> agentLocations;
        if(knownState == null) {
            agentLocations = fetchAgentLocations();
        }
        else {
            agentLocations = knownState.getAgentLocations();
        }

        Collection<Integer> occupiedVertices = agentLocations.values();
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

    private int getUndergroundQFree(int timeStep, MAPFState knownState) {
        // Returns the first free vertex in the underground queue

        if(timeStep == 0) {
            return fetchUndergroundStart() + 1;
        }

        // If knownState != null, get known agent locations from knownState
        HashMap<Agent, Integer> agentLocations;
        if(knownState == null) {
            agentLocations = fetchAgentLocations();
        }
        else {
            agentLocations = knownState.getAgentLocations();
        }

        Collection<Integer> occupiedVertices = agentLocations.values();
        ArrayList<Integer> verticesInUndergroundQ = ramp.getVerticesInUndergroundQ();
        for(Integer vertex : verticesInUndergroundQ) {
            if(!occupiedVertices.contains(vertex)) {
                return vertex;
            }
        }
        System.out.println("MAPFScenario->getUndergroundQFree: UNDERGROUND QUEUE IS FULL!");
        return -1;
    }

    private void putNewAgentsInQueue(Ramp ramp, ArrayList<Agent> newAgentsThisTimeStep,
                                    HashMap<Agent, Integer> newAgentLocations, int timeStep,
                                     MAPFState knownState) {
        // Task: If multiple agents are in the same start vertex, put them in queue instead.
        // newAgentLocations is a hashmap of the agent id and its start vertex (either surface or underground)
        // of ONLY the new agents that are joining the ramp.
        // knownState is null if we work with agent locations from the scenario initialState,
        // knownState is not null if we work with agent locations from a known state

        int surfaceQFree = getSurfaceQFree(timeStep, knownState);
        int undergroundQFree = getUndergroundQFree(timeStep, knownState);

        // Put excessive starting agents in their corresponding queues
        for(Agent agent : newAgentsThisTimeStep) {
            if(agent.direction == Constants.DOWN) {
                newAgentLocations.put(agent, surfaceQFree);
                surfaceQFree--;
            }
            else if (agent.direction == Constants.UP){
                newAgentLocations.put(agent, undergroundQFree);
                undergroundQFree++;
            }
        }
    }

    public MAPFState generateState(int timeStep, MAPFState knownState) {
        // Task: From the MAPFScenario, generate the first initial MAPFState

        HashMap<Integer, ArrayList<Agent>> entries = this.agentEntries.getEntries();

        ArrayList<Agent> newAgentsThisTimeStep = entries.get(timeStep);

        // If multiple starting agents, they will occupy the same start vertex --> put in queue instead
        HashMap<Agent, Integer> newAgentLocations = new HashMap<>();
        putNewAgentsInQueue(this.ramp, newAgentsThisTimeStep, newAgentLocations, timeStep, knownState);

        int nrOfNewAgentsThisTimeStep = entries.get(timeStep).size();
        addTotalAgentCount(nrOfNewAgentsThisTimeStep);

        if(timeStep == 0) {
            return new MAPFState(ramp, newAgentLocations, 0, 0, timeStep);
        }
        else {
            // Add new newAgentLocations to the already existing newAgentLocations if scenario is not new
            HashMap<Agent, Integer> finalAgentLocations;

            // Get agent locations from before
            if(knownState == null) {
                finalAgentLocations = fetchAgentLocations();
            }
            else {
                finalAgentLocations = knownState.getAgentLocations();
            }
            finalAgentLocations.putAll(newAgentLocations);

            this.initialState.addActiveAgents(newAgentsThisTimeStep);

            int newGcost;
            int newGcostPrio;
            if(knownState == null) {
                newGcost = fetchNumOfActiveAgents();
                newGcostPrio = fetchNumOfActivePrioAgents();
            }
            else {
                newGcost = knownState.getGCost();
                newGcostPrio = knownState.getgCostPrio();
            }

            return new MAPFState(ramp, finalAgentLocations, newGcost, newGcostPrio, timeStep);
        }
    }


    Ramp getRamp() {
        return this.ramp;
    }

    public HashMap<Integer, ArrayList<Agent>> fetchAgentEntries() {
        return this.agentEntries.getEntries();
    }

    public int getLifespan() {
        return this.lifespan;
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

    public int fetchNumOfActivePrioAgents() {
        return this.initialState.getNumOfActivePrioAgents();
    }

    public ArrayList<Integer> fetchVerticesInPassingBays() {
        return this.ramp.getVerticesInPassingBays();
    }

    public ArrayList<ArrayList<Integer>> fetchPassingBayVertices() {
        return this.ramp.getPassingBayVertexPairs();
    }

    public ArrayList<Integer> fetchVerticesInSurfaceQ() {
        return this.ramp.getVerticesInSurfaceQ();
    }

    public ArrayList<Integer> fetchVerticesInUndergroundQ() {
        return this.ramp.getVerticesInUndergroundQ();
    }

    public HashMap<Agent, HashMap<Integer, Set<Integer>>> getVertexConstraints() {
        return this.vertexConstraints;
    }

    public HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> getEdgeConstraints() {
        return this.edgeConstraints;
    }
}
