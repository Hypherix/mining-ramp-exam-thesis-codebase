package org.example;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
    private int cost;

    // Constructors
    public MAPFState(Ramp ramp, HashMap<Agent, Integer> agentLocation) {
        this.ramp = ramp;
        this.agentLocations = agentLocation;
        this.cost = calculateCost();
    }

    public MAPFState(Ramp ramp) {
        this.ramp = ramp;
        this.agentLocations = new HashMap<Agent, Integer>();
    }

    // Methods
    public int calculateCost() {
        // Task: Go through each agent's location and calculate the sum of their f values
        /*
        *   TODO when A*: MAPFState cannot calculate its cost, only h. g is equal to the agents'
        *    total travel time. Thus, g and f are attained at simulation-time. THEREFORE:
        *    calculateCost() is not to be called in the constructor. Change this method when A*
        *    is progressed on to only work with h, and find a way to get g.
        * */


        int cost = 0;
        int h = 0;

        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();
            if (agent.direction == Constants.DOWN) {
                // If downgoing, add the agent location's downgoing f value to the cost
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

    Ramp getRamp() {
        return this.ramp;
    }

    HashMap<Agent, Integer> getAgentLocations() {
        return this.agentLocations;
    }

    public int getCost() {
        return this.cost;
    }
}

