package org.rampTraffic;

import org.rampTraffic.algorithms.AlgorithmFactory;
import org.rampTraffic.algorithms.MAPFAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MAPFSolver {

    // Data members
    private final MAPFScenario scenario;
    private final MAPFAlgorithm algorithm;
    private int timeStep;
    private final String algorithmString;


    // Constructors
    public MAPFSolver(MAPFScenario scenario, String algorithm) {
        this.scenario = scenario;
        this.timeStep = 0;
        this.algorithmString = algorithm;

        // Invoke the factory pattern to fetch the correct algorithm object
        this.algorithm = AlgorithmFactory.getAlgorithm(algorithm);
    }

    // Methods

    public MAPFSolution solve(boolean prioritise) {
        // Prompts the algorithm to solve the MAPF scenario
        // Note! Parameter prioritise does not affect ICTS since it inherently is unable to prioritise

        HashMap<Integer, ArrayList<Agent>> agentEntries = scenario.fetchAgentEntries();
        int endTime = this.scenario.getLifespan();

        // Invoke the algorithm to find a solution
        long startFirstTime = System.nanoTime();
        MAPFSolution currentSolution = this.algorithm.solve(this.scenario, prioritise);
        long endFirstTime = System.nanoTime();
        long firstDuration = endFirstTime - startFirstTime;
        System.out.println("Execution time " + this.algorithmString + ": " + (firstDuration / 1000000) + " ms");

        double totalReplanTime = 0;     // Accumulates all replan durations

        // Go through agent entries for every time step to see if new agents arrive --> replan
        for(timeStep = 1; timeStep < endTime; timeStep++) {
            if (agentEntries.containsKey(timeStep)) {   // If there are new agents entering this timeStep
                // Remove from solution states, those states that are now invalid

                ArrayList<MAPFState> currentSolutionStates = new ArrayList<>();
                if(currentSolution != null) {
                    currentSolutionStates = new ArrayList<>(currentSolution.getSolutionSet());
                }

                // If new agents arrive after all previous agents are in goal, pad the MAPFSolution with identical
                // MAPFStates of where the agents are in goal, up to the time step where the new agents arrive
                if(timeStep >= currentSolutionStates.size()) {
                    int difference = timeStep - currentSolutionStates.size() + 1;
                    MAPFState lastSolutionState = currentSolutionStates.getLast();
                    for (int i = 0; i < difference; i++) {
                        MAPFState paddedState = new MAPFState(lastSolutionState);
                        paddedState.setTimeStep(lastSolutionState.getTimeStep() + i);
                        paddedState.parent = currentSolutionStates.getLast();       // Link state to previous state
                        currentSolutionStates.add(paddedState);
                    }
                }
                currentSolutionStates.subList(timeStep + 1, currentSolutionStates.size()).clear();

                // Generate a new initialState and update MAPFScenario

                // Get the state we want to revert to
                MAPFState newCurrentState = currentSolutionStates.get(timeStep);

                scenario.setInitialState(newCurrentState);
                scenario.setTotalAgentCount(newCurrentState.getAgentLocations().size());
                scenario.setInitialState(scenario.generateState(timeStep, null));
                scenario.getInitialState().parent = newCurrentState.parent;

                // Invoke the algorithm anew
                long startReplanTime = System.nanoTime();
                currentSolution = this.algorithm.solve(this.scenario, prioritise);
                long endReplanTime = System.nanoTime();
                long replanDuration = endReplanTime - startReplanTime;
                System.out.println("Execution time " + this.algorithmString + " replan: " + (replanDuration / 1000000) + " ms");
                totalReplanTime += (double) replanDuration;

                // Start from goal of new A* run
                MAPFState goalState = currentSolution.getSolutionSet().getLast();
                LinkedList<MAPFState> fullPath = new LinkedList<>();

                MAPFState current = goalState;
                while (current != null) {
                    fullPath.addFirst(current);  // Prepend to reverse order
                    current = current.parent;
                }

                // Replace solution set with full reconstructed path
                currentSolution.setSolutionSet(new ArrayList<>(fullPath));
            }
        }
        System.out.println(this.algorithmString + " total replan time: " + Math.round(totalReplanTime / 1000000) + " ms");

        if(currentSolution != null) {
            currentSolution.calculateSolution(false);
            return currentSolution;
        }
        else {
            return null;
        }
    }
}
