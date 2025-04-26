package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Solution {

    // Data members
    private ArrayList<MAPFState> solutionSet;
    private int cost;
    private int generatedStates;
    private int expandedStates;

    // Constructors
    public Solution(ArrayList<MAPFState> solution, int generatedStates, int expandedStates) {
        this.solutionSet = solution;
        this.generatedStates = generatedStates;
        this.expandedStates = expandedStates;
    }

    // Methods
    public void printSolution() {
        // Task: Print the paths of each agent

        HashMap<Agent, ArrayList<Integer>> agentPaths = new HashMap<>();

        System.out.println("\nSolution:");
        for (MAPFState state : this.solutionSet) {
            HashMap<Agent, Integer> agentLocations = state.getAgentLocations();

            for (Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
                Agent agent = entry.getKey();
                Integer location = entry.getValue();

                if(!agentPaths.containsKey(agent)) {
                    agentPaths.put(agent, new ArrayList<>());
                }
                agentPaths.get(agent).add(location);
            }
        }

        int surfaceExit = solutionSet.getLast().fetchSurfaceExit();
        int undergroundExit = solutionSet.getLast().fetchUndergroundExit();

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

    public ArrayList<MAPFState> getSolutionSet() {
        return this.solutionSet;
    }

    public void setSolutionSet(ArrayList<MAPFState> solutionSet) {
        this.solutionSet = solutionSet;
    }
}
