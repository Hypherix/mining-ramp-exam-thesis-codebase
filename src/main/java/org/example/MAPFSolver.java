package org.example;

import org.example.algorithms.AlgorithmFactory;
import org.example.algorithms.MAPFAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


/*
 * Activates an algorithm to solve a given MAPFScenario
 * Contents:
 *   - MAPFScenario
 *   - Algorithm to use
 *
 * */


public class MAPFSolver {

    // Data members
    private MAPFScenario scenario;
    private MAPFAlgorithm algorithm;
    private MAPFSolution solution;
    private int timeStep;
    private String algorithmString;


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
        // Task: Prompts the algorithm to solve the MAPF scenario
        // As of now, the solve methods return void. When A* is implemented,
        // return a representation of a solution
        // Note! Parameter prioritise does not affect ICTS since it cannot prioritise

        HashMap<Integer, ArrayList<Agent>> agentEntries = scenario.fetchAgentEntries();
        int endTime = this.scenario.getDuration();
        MAPFSolution currentSolution;

        // Need to implement so that for every time step, MAPFSolver checks its scenario if
        // new agents enter. In that case, run t

        long startFirstTime = System.nanoTime();
        currentSolution = this.algorithm.solve(this.scenario, prioritise);
        long endFirstTime = System.nanoTime();
        long firstDuration = endFirstTime - startFirstTime;
        System.out.println("Execution time " + this.algorithmString + ": " + (firstDuration / 1000000) + " ms");

        double totalReplanTime = 0;

        for(timeStep = 1; timeStep < endTime; timeStep++) {
            if (agentEntries.containsKey(timeStep)) {   // If there are new agents entering this timeStep
                // Remove from solution states those states that are now invalid

                ArrayList<MAPFState> currentSolutionStates = new ArrayList<>();
                if(currentSolution != null) {
                    currentSolutionStates = new ArrayList<>(currentSolution.getSolutionSet());
                }

                // TODO: Check if timeStep > currentSolutionStates.size(). If so, create as many identical states as the last
                //  state until currentSolutionStates.size() == timeStep

                if(timeStep >= currentSolutionStates.size()) {
                    int difference = timeStep - currentSolutionStates.size() + 1;
                    MAPFState lastSolutionState = currentSolutionStates.getLast();
                    for (int i = 0; i < difference; i++) {
                        MAPFState paddedState = new MAPFState(lastSolutionState);
                        paddedState.setTimeStep(lastSolutionState.getTimeStep() + i);  // Advance time step
                        paddedState.parent = currentSolutionStates.getLast();   // Link to previous state
                        currentSolutionStates.add(paddedState);
                    }
                }
                currentSolutionStates.subList(timeStep + 1, currentSolutionStates.size()).clear();

                // Generate a new initialState and update MAPFScenario

                // Get the state we want to revert to
                MAPFState newCurrentState = currentSolutionStates.get(timeStep);
                // Assign the scenario newCurrentState as initial state
                scenario.setInitialState(newCurrentState);
                // Update totalAgentCount to reflect currentState's
                scenario.setTotalAgentCount(newCurrentState.getAgentLocations().size());

                // Update MAPFScenario's initialState and manually set parent to newCurrentState.parent
                scenario.setInitialState(scenario.generateState(timeStep, null));
                scenario.getInitialState().parent = newCurrentState.parent;


                // Invoke the algorithm anew
                long startReplanTime = System.nanoTime();
                currentSolution = this.algorithm.solve(this.scenario, prioritise);
                long endReplanTime = System.nanoTime();
                long replanDuration = endReplanTime - startReplanTime;
                System.out.println("Execution time " + this.algorithmString + " replan: " + (replanDuration / 1000000) + " ms");
                totalReplanTime += (double) replanDuration;

                // Since currentSolution's initial solution set state is the same as the last state
                // in currentSolutionStates, remove it from currentSolutionStates
                //currentSolutionStates.removeLast();

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


                // Set the solution set of newSolution as newCurrentSolutionStates
                //currentSolution.setSolutionSet(newCurrentSolutionStates);
            }
        }
        System.out.println(this.algorithmString + " total replan time: " + Math.round(totalReplanTime / 1000000) + " ms");

        this.solution = currentSolution;

        if(this.solution != null) {
            this.solution.printSolution(false);
            return solution;
        }
        else {
            return null;
        }
    }
}
