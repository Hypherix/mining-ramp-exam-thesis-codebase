package org.example;

import org.example.algorithms.AlgorithmFactory;
import org.example.algorithms.MAPFAlgorithm;

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
    private int timeStep;


    // Constructors
    public MAPFSolver(MAPFScenario scenario, String algorithm) {
        this.scenario = scenario;
        this.timeStep = 0;

        // Invoke the factory pattern to fetch the correct algorithm object
        this.algorithm = AlgorithmFactory.getAlgorithm(algorithm);
    }

    // Methods
    public MAPFState generateMAPFState(MAPFScenario scenario) {
        // Task: From the MAPFScenario, generate a MAPFState
        // The MAPFState only contains the ramp, agent location and velocity

        Ramp ramp = scenario.getRamp();
        HashMap<Integer, int[]> agentList = scenario.getAgentList();
        HashMap<Integer, Integer> agentLocation = new HashMap<>();
        HashMap<Integer, Integer> agentVelocty = new HashMap<>();

        // Separate the agentList into agentLocation and agentVelocity for MAPFState
        for (Map.Entry<Integer, int[]> entry : agentList.entrySet()) {
            int key = entry.getKey();
            int[] values = entry.getValue();

            agentLocation.put(key, values[0]);
            agentVelocty.put(key, values[1]);
        }

        return new MAPFState(ramp, agentLocation, agentVelocty);
    }

    public void solve() {
        // Task: Prompts the algorithm to solve the MAPF scenario
        // As of now, the solve methods return void. When A* is implemented,
        // return a representation of a solution

        int duration = this.scenario.getDuration();
        // Need to implement so that for every time step, MAPFSolver checks its scenario if
        // new agents enter. In that case, run t

        // Should return set of solution states
        this.algorithm.solve(this.scenario);
    }
}
