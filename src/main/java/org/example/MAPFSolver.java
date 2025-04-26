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
    private Solution solution;
    private int timeStep;
    private int solutionCost;
    private int solutionGeneratedStates;
    private int solutionExpandedStates;


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
        Solution currentSolution;
        Solution newSolution;

        // Need to implement so that for every time step, MAPFSolver checks its scenario if
        // new agents enter. In that case, run t

        currentSolution = this.algorithm.solve(this.scenario);

        for(timeStep = 1; timeStep < endTime; timeStep++) {
            if (agentEntries.containsKey(timeStep)) {   // If there are new agents entering this timeStep
                // Remove from solution states those states that are now invalid

                ArrayList<MAPFState> solutionSet = currentSolution.getSolutionSet();

                solutionSet.subList(timeStep, solutionSet.size()).clear();

                // Generate a new initialState and update MAPFScenario

                // Get the state we want to revert to
                MAPFState newCurrentState = solutionSet.get(timeStep - 1);
                // Assign the scenario newCurrentState
                scenario.setInitialState(newCurrentState);
                // Update totalAgentCount to reflect currentState's
                scenario.setTotalAgentCount(newCurrentState.getAgentLocations().size());

                // Update MAPFScenario's initialState
                scenario.generateInitialState(timeStep);

                // Invoke the algorithm anew
                currentSolution = this.algorithm.solve(this.scenario);

                newSolution = this.algorithm.solve(this.scenario);

                solutionSet.addAll(newSolution.getSolutionSet());

                currentSolution.setSolutionSet(solutionSet);

                timeStep++;
            }
        }

        this.solution = currentSolution;

        this.solution.printSolution();
    }

    public void setSolutionGeneratedStates(int generatedStates) {
        this.solutionGeneratedStates = generatedStates;
    }

    public void setSolutionExpandedStates(int expandedStates) {
        this.solutionExpandedStates = expandedStates;
    }

    public ArrayList<MAPFState> fetchSolutionSet() {
        return this.solution.getSolutionSet();
    }
}
