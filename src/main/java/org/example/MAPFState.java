package org.example;

import org.example.ICTSclasses.ICTNode;

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
    private int timeStep;
    public MAPFState parent;
    public ArrayList<Agent> activeAgents;      // Agents not in an exit
    public ArrayList<Agent> inactiveAgents;    // Agents in an exit
    private PriorityQueue<MAPFState> concurrentStatesInFrontier;    // Needed for A* rollback to MAPFState
    private PriorityQueue<MAPFState> concurrentStatesInExplored;    // Needed for A* rollback to MAPFState
    private Queue<ICTNode> ictQueueAtRollback;      // Needed for ICTS rollback to MAPFState

    // Constructors
    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocations, int gcost, int timeStep) {
        this.ramp = ramp;
        this.agentLocations = agentLocations;
        this.gcost = gcost;
        this.hcost = calculateHcost();
        this.fcost = this.gcost + this.hcost;
        this.parent = null;
        this.timeStep = timeStep;
        this.concurrentStatesInFrontier = new PriorityQueue<>(new StateComparator());
        this.concurrentStatesInExplored = new PriorityQueue<>(new StateComparator());
        this.ictQueueAtRollback = new LinkedList<>();

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

        // Deep copy of concurrentStatesInFrontier
        this.concurrentStatesInFrontier = new PriorityQueue<>(new StateComparator());
        for (MAPFState state : other.concurrentStatesInFrontier) {
            this.concurrentStatesInFrontier.add(new MAPFState(state));
        }

        // Deep copy of concurrentStatesInExplored
        this.concurrentStatesInExplored = new PriorityQueue<>(new StateComparator());
        for (MAPFState state : other.concurrentStatesInExplored) {
            this.concurrentStatesInExplored.add(new MAPFState(state));
        }

        this.ictQueueAtRollback = new LinkedList<>();
        for(ICTNode node : other.ictQueueAtRollback) {
            this.ictQueueAtRollback.add(new ICTNode(node));
        }

    }



    // Methods
    private int calculateHcost() {
        // Task: Calculate the total h of all agents

        int h = 0;
        HashMap<Integer, Integer> hUpgoing = ramp.hUpgoing;
        HashMap<Integer, Integer> hDowngoing = ramp.hDowngoing;

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

    private int calculateCost() {
        // Task: Go through each agent's location and calculate the sum of their f values
        /*
        *   TODO when A*: MAPFState cannot calculate its fcost, only h. g is equal to the agents'
        *    total travel time. Thus, g and f are attained at simulation-time. THEREFORE:
        *    calculateCost() is not to be called in the constructor. Change this method when A*
        *    is progressed on to only work with h, and find a way to get g.
        * */
        // CURRENTLY NOT IN USE

        int cost = 0;
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

        return cost;
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

    public int fetchSurfaceExit() {
        return this.ramp.getSurfaceExit();
    }

    public int fetchUndergroundExit() {
        return this.ramp.getUndergroundExit();
    }

    public int getNumOfActiveAgents() {
        return this.activeAgents.size();
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

    public PriorityQueue<MAPFState> getConcurrentStatesInFrontier() {
        return this.concurrentStatesInFrontier;
    }

    public void setConcurrentStatesInFrontier(PriorityQueue<MAPFState> concurrentStatesInFrontier) {
        this.concurrentStatesInFrontier = concurrentStatesInFrontier;
    }

    public PriorityQueue<MAPFState> getConcurrentStatesInExplored() {
        return this.concurrentStatesInExplored;
    }

    public void setConcurrentStatesInExplored(PriorityQueue<MAPFState> concurrentStatesInExplored) {
        this.concurrentStatesInExplored = concurrentStatesInExplored;
    }

    public void setIctQueueAtRollback(Queue<ICTNode> queue) {
        this.ictQueueAtRollback = queue;
    }

    public Queue<ICTNode> getIctQueueAtRollback() {
        return this.ictQueueAtRollback;
    }
}

