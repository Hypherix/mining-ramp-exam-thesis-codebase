package org.example;

import org.example.algorithms.AlgorithmFactory;
import org.example.algorithms.MAPFAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;


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


    // Constructors
    public MAPFSolver(MAPFScenario scenario, String algorithm) {
        this.scenario = scenario;
        timeStep = 0;

        // Invoke the factory pattern to fetch the correct algorithm object
        this.algorithm = AlgorithmFactory.getAlgorithm(algorithm);
    }

    // Methods

    public void solve() {
        // Task: Prompts the algorithm to solve the MAPF scenario
        // As of now, the solve methods return void. When A* is implemented,
        // return a representation of a solution

        HashMap<Integer, ArrayList<Agent>> agentEntries = scenario.fetchAgentEntries();
        int endTime = this.scenario.getDuration();
        MAPFSolution currentSolution;
        MAPFSolution newSolution;

        // Need to implement so that for every time step, MAPFSolver checks its scenario if
        // new agents enter. In that case, run t

        currentSolution = this.algorithm.solve(this.scenario);

        for(timeStep = 1; timeStep < endTime; timeStep++) {
            if (agentEntries.containsKey(timeStep)) {   // If there are new agents entering this timeStep
                // Remove from solution states those states that are now invalid

                ArrayList<MAPFState> currentSolutionStates = currentSolution.getSolutionSet();

                // TODO: Check if timeStep > currentSolutionStates.size(). If so, create as many identical states as the last
                //  state until currentSolutionStates.size() == timeStep

                int previousSolutionLength = currentSolutionStates.size();

                if(timeStep >= currentSolutionStates.size()) {
                    int difference = timeStep - currentSolutionStates.size() + 1;
                    MAPFState lastSolutionState = currentSolutionStates.getLast();
                    for (int i = 0; i < difference; i++) {
                        currentSolutionStates.add(new MAPFState(lastSolutionState));
                    }
                }
                currentSolutionStates.subList(timeStep + 1, currentSolutionStates.size()).clear();

                // Generate a new initialState and update MAPFScenario

                // Get the state we want to revert to
                MAPFState newCurrentState = currentSolutionStates.get(timeStep);
                // Assign the scenario newCurrentState
                scenario.setInitialState(newCurrentState);
                // Update totalAgentCount to reflect currentState's
                scenario.setTotalAgentCount(newCurrentState.getAgentLocations().size());

                // Update MAPFScenario's initialState
                scenario.generateInitialState(timeStep);

                // Invoke the algorithm anew
                currentSolution = this.algorithm.solve(this.scenario);

                // Since currentSolution's initial solution set state is the same as the last state
                // in currentSolutionStates, remove it from currentSolutionStates
                currentSolutionStates.removeLast();

                // Merge currentSolutionStates and currentSolution's solution set
                ArrayList<MAPFState> newCurrentSolutionStates = currentSolutionStates;
                newCurrentSolutionStates.addAll(currentSolution.getSolutionSet());

                // Set the solution set of newSolution as newCurrentSolutionStates
                currentSolution.setSolutionSet(newCurrentSolutionStates);
            }
        }

        this.solution = currentSolution;

        this.solution.printSolution(false);
    }
}
