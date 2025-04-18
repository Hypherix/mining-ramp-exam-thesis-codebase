package org.example;

import org.example.algorithms.AlgorithmFactory;
import org.example.algorithms.MAPFAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


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
    private ArrayList<MAPFState> solution;
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

        HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocity = scenario.getNewAgentLocationVelocity();
        int endTime = this.scenario.getDuration();
        ArrayList<MAPFState> currentSolution;

        // Need to implement so that for every time step, MAPFSolver checks its scenario if
        // new agents enter. In that case, run t

        currentSolution = this.algorithm.solve(this.scenario);

        for(timeStep = 1; timeStep < endTime; timeStep++) {
            if (newAgentLocationVelocity.containsKey(timeStep)) {   // If there are new agents entering this timeStep
                // Remove from solution states those states that are now invalid
                this.solution.subList(timeStep, this.solution.size()).clear();

                // Generate a new initialState and update MAPFScenario

                // Get the state we want to revert to
                MAPFState newCurrentState = this.solution.get(timeStep - 1);
                // Assign the scenario newCurrentState
                scenario.setInitialState(newCurrentState);
                // Update totalAgentCount to reflect currentState's
                scenario.setTotalAgentCount(newCurrentState.getAgentLocations().size());

                // Update MAPFScenario's initialState
                scenario.generateInitialState(timeStep);

                // Invoke the algorithm anew
                currentSolution = this.algorithm.solve(this.scenario);

                timeStep++;
            }
        }

        this.solution = currentSolution;
    }
}
