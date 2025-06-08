package org.example;

import org.example.CBSclasses.CTNode;
import org.example.CBSclasses.CTNodeComparator;

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
    private int fcost;      // = gcost + hcost
    private int gcost;      // sum of agent actions
    private int hcost;      // sum of agent location h values
    private int fcostPrio;
    private int gcostPrio;
    private int hcostPrio;
    private int timeStep;
    public MAPFState parent;
    public ArrayList<Agent> activeAgents;      // Agents not in an exit
    public ArrayList<Agent> inactiveAgents;    // Agents in an exit
    private PriorityQueue<MAPFState> concurrentStatesInFrontier;    // Needed for rollback to MAPFState (A*)
    private PriorityQueue<MAPFState> concurrentStatesInExplored;    // Needed for rollback to MAPFState (A*)
    private PriorityQueue<CTNode> concurrentNodesInCTPrioQueue;    // Needed for CBS rollback


    // Constructors
    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocations, int gcost, int timeStep) {
        // Used by ICTS since it does not care about higher priority agents. It is based on all agents' costs

        this.ramp = ramp;
        this.agentLocations = agentLocations;
        this.gcost = gcost;
        this.hcost = calculateHcost();
        this.fcost = this.gcost + this.hcost;
        this.parent = null;
        this.timeStep = timeStep;

        // Categorise the agents as active or inactive
        this.activeAgents = new ArrayList<>();
        this.inactiveAgents = new ArrayList<>();
        int surfaceExit = fetchSurfaceExit();
        int undergroundExit = fetchUndergroundExit();
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (location == surfaceExit || location == undergroundExit) {
                this.inactiveAgents.add(agent);
            }
            else {
                this.activeAgents.add(agent);
            }
        }
    }

    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocations, int gcost, int gcostPrio, int timeStep) {
        // Used by everything but ICTS since they should be able to prioritise based on higherPrio agents

        this.ramp = ramp;
        this.agentLocations = agentLocations;
        this.gcost = gcost;
        this.hcost = calculateHcost();
        this.fcost = this.gcost + this.hcost;
        this.gcostPrio = gcostPrio;
        this.hcostPrio = calculateHcostPrio();
        this.fcostPrio = this.gcostPrio + this.hcostPrio;
        this.parent = null;
        this.timeStep = timeStep;
        this.concurrentStatesInFrontier = new PriorityQueue<>(new StateComparator());
        this.concurrentStatesInExplored = new PriorityQueue<>(new StateComparator());
        this.concurrentNodesInCTPrioQueue = new PriorityQueue<>(new CTNodeComparator());

        // Categorise the agents as active or inactive
        this.activeAgents = new ArrayList<>();
        this.inactiveAgents = new ArrayList<>();
        int surfaceExit = fetchSurfaceExit();
        int undergroundExit = fetchUndergroundExit();
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (location == surfaceExit || location == undergroundExit) {
                this.inactiveAgents.add(agent);
            }
            else {
                this.activeAgents.add(agent);
            }
        }
    }

    // Constructor with concurrentStatesInFrontier/Explored and parent set (for A* rollback)
    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocations, int gcost, int gcostPrio, int timeStep,
                     MAPFState parent) {
        this.ramp = ramp;
        this.agentLocations = agentLocations;
        this.gcost = gcost;
        this.hcost = calculateHcost();
        this.fcost = this.gcost + this.hcost;
        this.gcostPrio = gcostPrio;
        this.hcostPrio = calculateHcostPrio();
        this.fcostPrio = this.gcostPrio + this.hcostPrio;
        this.parent = parent;
        this.timeStep = timeStep;


        // Categorise the agents as active or inactive
        this.activeAgents = new ArrayList<>();
        this.inactiveAgents = new ArrayList<>();
        int surfaceExit = fetchSurfaceExit();
        int undergroundExit = fetchUndergroundExit();
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (location == surfaceExit || location == undergroundExit) {
                this.inactiveAgents.add(agent);
            }
            else {
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
        this.fcost = other.fcost;
        this.gcost = other.gcost;
        this.hcost = other.hcost;
        this.fcostPrio = other.fcostPrio;
        this.gcostPrio = other.gcostPrio;
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
    private int calculateHcost() {
        // Task: Calculate the total h of all agents

        int h = 0;

        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (agent.direction == Constants.DOWN) {
                // If downgoing, add the agent location's downgoing f value to the fcost
                h += ramp.hDowngoing.get(location);
            }
            else if (agent.direction == Constants.UP) {
                // If upgoing, add upgoing f value
                h += ramp.hUpgoing.get(location);
            }
            else {
                System.out.println("UNKNOWN DIRECTION WHEN CALCULATING STATE COST!");
            }
        }
        return h;
    }

    private int calculateHcostPrio() {
        // Task: Calculate the total h of all agents with higherPrio

        int h = 0;
        for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (agent.higherPrio) {
                if (agent.direction == Constants.DOWN) {
                    h += ramp.hDowngoing.get(location);
                }
                else if (agent.direction == Constants.UP) {
                    h += ramp.hUpgoing.get(location);
                }
                else {
                    System.out.println("UNKNOWN DIRECTION WHEN CALCULATING STATE COST!");
                }
            }
        }
        return h;
    }


    @Override
    public boolean equals(Object o) {
        // MAPFState equality is based on agentLocations and gcost

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MAPFState otherState = (MAPFState) o;

        return agentLocations.equals(otherState.agentLocations) && gcost == otherState.gcost;

//        return agentLocations.equals(otherState.agentLocations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(agentLocations, gcost);
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

    public int getFcost() {
        return this.fcost;
    }

    public void setFcost(int f) {
        this.fcost = f;
    }

    public int getGcost() {
        return this.gcost;
    }

    public void setGcost(int g) {
        this.gcost = g;
    }

    public int getHcost() {
        return this.hcost;
    }

    public void setHcost(int h) {
        this.hcost = h;
    }

    public void setFcostPrio(int fcostPrio) {
        this.fcostPrio = fcostPrio;
    }

    public int getFcostPrio() {
        return this.fcostPrio;
    }

    public void setGcostPrio(int gcostPrio) {
        this.gcostPrio = gcostPrio;
    }

    public int getGcostPrio() {
        return this.gcostPrio;
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
        for(Agent agent : this.activeAgents) {
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

