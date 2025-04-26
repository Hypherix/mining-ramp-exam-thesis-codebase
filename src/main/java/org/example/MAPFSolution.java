package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MAPFSolution {

    // Data members
    private ArrayList<MAPFState> solutionSet;
    private int cost;
    private int generatedStates;
    private int expandedStates;

    // Constructors
    public MAPFSolution(ArrayList<MAPFState> solution, int generatedStates, int expandedStates) {
        this.solutionSet = solution;
        this.generatedStates = generatedStates;
        this.expandedStates = expandedStates;
    }

    // Methods
    public void printSolution() {
        // Task: Print the paths of each agent

        HashMap<Agent, ArrayList<Integer>> agentPaths = new HashMap<>();

        System.out.println("\nSolution:");

        // Iterate through each state
        for (MAPFState state : this.solutionSet) {
            HashMap<Agent, Integer> agentLocations = state.getAgentLocations();

            for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
                Agent agent = entry.getKey();
                Integer location = entry.getValue();

                // Add the location of the path of this agent. Create a key-value entry if first location for the agent
                if(!agentPaths.containsKey(agent)) {
                    agentPaths.put(agent, new ArrayList<>());
                }
                agentPaths.get(agent).add(location);
            }
        }

        int surfaceExit = solutionSet.getLast().fetchSurfaceExit();
        int undergroundExit = solutionSet.getLast().fetchUndergroundExit();

        // Print the path of each agent
        for (Map.Entry<Agent, ArrayList<Integer>> entry : agentPaths.entrySet()) {
            Agent agent = entry.getKey();
            ArrayList<Integer> path = entry.getValue();

            System.out.println("a" + agent.id + ": " + path);

            // For each action, increment cost. The starting locations do not cost anything
            // For each duplicate (hence cost++ in the end) surfaceExit or undergroundExit, decrement cost
            cost += path.size() - 1;
            for(Integer location : path) {
                if(location == surfaceExit || location == undergroundExit) {
                    cost--;
                }
            }
            cost++;
        }
        System.out.println("Solution cost: " + cost);
        System.out.println("Generated states (possibly added to frontier): " + generatedStates);
        System.out.println("Expanded states (polled from frontier): " + expandedStates);
    }

    public void printSolutionV2() {
        // Task: Print the paths of each agent

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
                System.out.print(location + "\t");
            }
            System.out.println();


            // For each action, increment cost. The starting locations do not cost anything
            // For each duplicate (hence cost++ in the end) surfaceExit or undergroundExit, decrement cost
            cost += path.size() - 1;
            for(String location : path) {
                if(!location.equals("-")) {
                    int locationInt = Integer.parseInt(location);
                    if(locationInt == surfaceExit || locationInt == undergroundExit) {
                        cost--;
                    }
                }
                else {
                    cost--;
                }
            }
            cost++;
        }

        System.out.println("Solution cost: " + cost);
        System.out.println("Generated states (possibly added to frontier): " + generatedStates);
        System.out.println("Expanded states (polled from frontier): " + expandedStates);
    }

    public ArrayList<MAPFState> getSolutionSet() {
        return this.solutionSet;
    }

    public void setSolutionSet(ArrayList<MAPFState> solutionSet) {
        this.solutionSet = solutionSet;
    }
}
