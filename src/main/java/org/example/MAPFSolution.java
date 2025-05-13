package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MAPFSolution {

    // Data members
    private ArrayList<MAPFState> solutionSet;
    private int cost;
    private int prioCost;
    private int generatedStates;
    private int expandedStates;
    private double obtainTime;

    // Constructors
    public MAPFSolution(ArrayList<MAPFState> solution, int generatedStates, int expandedStates) {
        this.solutionSet = solution;
        this.generatedStates = generatedStates;
        this.expandedStates = expandedStates;
    }

    // Methods

    public void printSolution(boolean initial) {
        // Task: Print the paths of each agent and calculates the costs in the same go

        HashMap<Agent, ArrayList<String>> agentPaths = new HashMap<>();

        // Get a set of all agents throughout the scenario
        ArrayList<MAPFState> solutionStates = this.getSolutionSet();
        Set<Agent> allAgents = solutionStates.getLast().getAgentLocations().keySet();

        // Initialise empty paths for all agents
        for (Agent agent : allAgents) {
            agentPaths.put(agent, new ArrayList<>());
        }

        // Build the paths
        for (MAPFState state : solutionStates) {
            HashMap<Agent, Integer> agentLocations = state.getAgentLocations();

            // Go through all agents for each state
            for (Agent agent : allAgents) {
                // If the agent existed in that state, add its location
                if(agentLocations.containsKey(agent)) {
                    agentPaths.get(agent).add(String.valueOf(agentLocations.get(agent)));
                }
                // Else, just write "-"
                else {
                    agentPaths.get(agent).add("-");
                }
            }
        }

        int surfaceExit = solutionSet.getLast().fetchSurfaceExit();
        int undergroundExit = solutionSet.getLast().fetchUndergroundExit();

        // Print the paths
        System.out.println("\nSolution:");
        for (Map.Entry<Agent, ArrayList<String>> entry : agentPaths.entrySet()) {
            Agent agent = entry.getKey();
            ArrayList<String> path = entry.getValue();

            System.out.print("a" + agent.id + ": ");
            for(String location : path) {
                if(!location.equals("-")) {
                    if(agent.direction == Constants.DOWN && Integer.parseInt(location) == undergroundExit) {
                        System.out.print(location + "\t");
                        break;
                    }
                    else if(agent.direction == Constants.UP && Integer.parseInt(location) == surfaceExit) {
                        System.out.print(location + "\t");
                        break;
                    }
                    else {
                        System.out.print(location + "\t");
                    }
                }
                else {
                    System.out.print(location + "\t");
                }
            }
            System.out.println();


            // For each action, increment cost. The starting locations do not cost anything
            // For each duplicate (hence cost++ in the end) surfaceExit or undergroundExit, decrement cost
            cost += path.size() - 1;
            if (agent.higherPrio) {
                prioCost += path.size() - 1;
            }

            for(String location : path) {
                if(!location.equals("-")) {
                    int locationInt = Integer.parseInt(location);
                    if(locationInt == surfaceExit || locationInt == undergroundExit) {
                        cost--;
                        if(agent.higherPrio) {
                            prioCost--;
                        }
                    }
                }
                else {
                    cost--;
                    if(agent.higherPrio) {
                        prioCost--;
                    }
                }
            }
            cost++;
        }

        if(!initial) {
            System.out.println("Solution cost: " + cost);
            System.out.println("Priority agents SIC: " + prioCost);
            System.out.println("Generated states (possibly added to frontier): " + generatedStates);
            System.out.println("Expanded states (polled from frontier): " + expandedStates);
        }
    }

    public ArrayList<MAPFState> getSolutionSet() {
        return this.solutionSet;
    }

    public void setSolutionSet(ArrayList<MAPFState> solutionSet) {
        this.solutionSet = solutionSet;
    }

    public int getCost() {
        return this.cost;
    }

    public int getPrioCost() {
        return this.prioCost;
    }

    public int getGeneratedStates() {
        return this.generatedStates;
    }

    public int getExpandedStates() {
        return this.expandedStates;
    }

    public void setObtainTime(long time) {
        this.obtainTime = time / 1000000.0;
    }

    public double getObtainTime() {
        return this.obtainTime;
    }
}
