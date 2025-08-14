package org.rampTraffic;

import java.util.*;

/*
 * - The purpose of MAPFState is to act as input for the MAPF algorithms. It only contains
 *   the ramp adjacency list with agent locations. Key = vertex. Value = agent occupying the vertex.
 *   Thus, velocity, direction etc can be accessed through the agent in agentLocations.
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
    private HashMap<Agent, Integer> agentLocations;
    private int fCost;      // = gcost + hcost
    private int gCost;      // sum of agent actions
    private int hCost;      // sum of agent location h values
    private int fCostPrio;
    private int gCostPrio;
    private int hcostPrio;
    private int timeStep;
    public MAPFState parent;
    public ArrayList<Agent> activeAgents;      // Agents not in an exit
    public ArrayList<Agent> inactiveAgents;    // Agents in an exit


    // Constructors
    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocations, int gCost, int timeStep) {
        // Used by ICTS since it does not care about higher priority agents. It is based on all agents' costs

        this.ramp = ramp;
        this.agentLocations = agentLocations;
        this.gCost = gCost;
        this.hCost = calculateHCost();
        this.fCost = this.gCost + this.hCost;
        this.parent = null;
        this.timeStep = timeStep;

        // Categorise the agents as active or inactive
        this.activeAgents = new ArrayList<>();
        this.inactiveAgents = new ArrayList<>();
        int surfaceExit = fetchSurfaceExit();
        int undergroundExit = fetchUndergroundExit();

        for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (location == surfaceExit || location == undergroundExit) {
                this.inactiveAgents.add(agent);
            } else {
                this.activeAgents.add(agent);
            }
        }
    }

    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocations, int gCost, int gCostPrio, int timeStep) {
        // Used by everything but ICTS since they should be able to prioritise based on higherPrio agents

        this.ramp = ramp;
        this.agentLocations = agentLocations;
        this.gCost = gCost;
        this.hCost = calculateHCost();
        this.fCost = this.gCost + this.hCost;
        this.gCostPrio = gCostPrio;
        this.hcostPrio = calculateHcostPrio();
        this.fCostPrio = this.gCostPrio + this.hcostPrio;
        this.parent = null;
        this.timeStep = timeStep;

        // Categorise the agents as active or inactive
        this.activeAgents = new ArrayList<>();
        this.inactiveAgents = new ArrayList<>();
        int surfaceExit = fetchSurfaceExit();
        int undergroundExit = fetchUndergroundExit();
        for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (location == surfaceExit || location == undergroundExit) {
                this.inactiveAgents.add(agent);
            } else {
                this.activeAgents.add(agent);
            }
        }
    }

    // Constructor with concurrentStatesInFrontier/Explored and parent set (for A* rollback)
    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocations, int gCost, int gCostPrio, int timeStep,
                     MAPFState parent) {
        this.ramp = ramp;
        this.agentLocations = agentLocations;
        this.gCost = gCost;
        this.hCost = calculateHCost();
        this.fCost = this.gCost + this.hCost;
        this.gCostPrio = gCostPrio;
        this.hcostPrio = calculateHcostPrio();
        this.fCostPrio = this.gCostPrio + this.hcostPrio;
        this.parent = parent;
        this.timeStep = timeStep;

        // Categorise the agents as active or inactive
        this.activeAgents = new ArrayList<>();
        this.inactiveAgents = new ArrayList<>();
        int surfaceExit = fetchSurfaceExit();
        int undergroundExit = fetchUndergroundExit();
        for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (location == surfaceExit || location == undergroundExit) {
                this.inactiveAgents.add(agent);
            } else {
                this.activeAgents.add(agent);
            }
        }
    }

    // Copy constructor
    public MAPFState(MAPFState other) {
        // Deep copy the ramp
        this.ramp = new Ramp(other.ramp);

        // Deep copy agentLocations
        this.agentLocations = new HashMap<>();
        for (Map.Entry<Agent, Integer> entry : other.agentLocations.entrySet()) {
            Agent copiedAgent = new Agent(entry.getKey());
            Integer copiedLocation = entry.getValue();
            this.agentLocations.put(copiedAgent, copiedLocation);
        }

        // Copy primitive fields
        this.fCost = other.fCost;
        this.gCost = other.gCost;
        this.hCost = other.hCost;
        this.fCostPrio = other.fCostPrio;
        this.gCostPrio = other.gCostPrio;
        this.hcostPrio = other.hcostPrio;
        this.timeStep = other.timeStep;
        this.parent = other.parent;

        // Deep copy activeAgents list
        this.activeAgents = new ArrayList<>();
        for (Agent agent : other.activeAgents) {
            this.activeAgents.add(new Agent(agent));
        }

        // Deep copy inactiveAgents list
        this.inactiveAgents = new ArrayList<>();
        for (Agent agent : other.inactiveAgents) {
            this.inactiveAgents.add(new Agent(agent));
        }
    }


    // Methods
    private int calculateHCost() {
        int h = 0;

        for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (agent.direction == Constants.DOWN) {
                h += ramp.hDowngoing.get(location);
            } else if (agent.direction == Constants.UP) {
                h += ramp.hUpgoing.get(location);
            } else {
                System.out.println("UNKNOWN DIRECTION WHEN CALCULATING STATE COST!");
            }
        }
        return h;
    }

    private int calculateHcostPrio() {
        int h = 0;

        for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (agent.higherPrio) {
                if (agent.direction == Constants.DOWN) {
                    h += ramp.hDowngoing.get(location);
                } else if (agent.direction == Constants.UP) {
                    h += ramp.hUpgoing.get(location);
                } else {
                    System.out.println("UNKNOWN DIRECTION WHEN CALCULATING STATE PRIO COST!");
                }
            }
        }
        return h;
    }

    @Override
    public boolean equals(Object o) {
        // MAPFState equality is based on agentLocations and gCost

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MAPFState otherState = (MAPFState) o;
        return agentLocations.equals(otherState.agentLocations) && gCost == otherState.gCost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentLocations, gCost);
    }

    public Ramp getRamp() {
        return this.ramp;
    }

    public HashMap<Agent, Integer> getAgentLocations() {
        return this.agentLocations;
    }

    public void setAgentLocations(HashMap<Agent, Integer> agentLocations) {
        this.agentLocations = agentLocations;
    }

    public int getFCost() {
        return this.fCost;
    }

    public void setFCost(int f) {
        this.fCost = f;
    }

    public int getGCost() {
        return this.gCost;
    }

    public void setGCost(int g) {
        this.gCost = g;
    }

    public int getHCost() {
        return this.hCost;
    }

    public void setHCost(int h) {
        this.hCost = h;
    }

    public void setFCostPrio(int fCostPrio) {
        this.fCostPrio = fCostPrio;
    }

    public int getFCostPrio() {
        return this.fCostPrio;
    }

    public void setGCostPrio(int gCostPrio) {
        this.gCostPrio = gCostPrio;
    }

    public int getGCostPrio() {
        return this.gCostPrio;
    }

    public void setHcostPrio(int hcostPrio) {
        this.hcostPrio = hcostPrio;
    }

    public int getHcostPrio() {
        return this.hcostPrio;
    }

    public int fetchSurfaceExit() {
        return this.ramp.getSurfaceExit();
    }

    public int fetchUndergroundExit() {
        return this.ramp.getUndergroundExit();
    }

    public int getNumOfActiveAgents() {
        return this.activeAgents.size();
    }

    public int getNumOfActivePrioAgents() {
        int count = 0;
        for (Agent agent : this.activeAgents) {
            if (agent.higherPrio) {
                count++;
            }
        }
        return count;
    }

    public int getNumOfInactiveAgents() {
        return this.inactiveAgents.size();
    }

    public void setParent(MAPFState parent) {
        this.parent = parent;
    }

    public void addActiveAgents(ArrayList<Agent> newActiveAgents) {
        this.activeAgents.addAll(newActiveAgents);
    }

    public int getTimeStep() {
        return this.timeStep;
    }

    public void setTimeStep(int timeStep) {
        this.timeStep = timeStep;
    }
}

