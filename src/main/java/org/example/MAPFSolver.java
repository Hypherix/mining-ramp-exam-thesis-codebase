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
    public void generateInitialState(MAPFScenario scenario, int timeStep) {
        // Task: From the MAPFScenario, generate the first initial MAPFState
        // The MAPFState only contains the ramp, agent location and velocity

        Ramp ramp = scenario.getRamp();
        HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocity = scenario.getNewAgentLocationVelocity();
        HashMap<Integer, Integer> newAgentLocations = new HashMap<>();
        HashMap<Integer, Integer> newAgentVelocities = new HashMap<>();

        // Get the number of new agents at this timeStep
        int nrOfNewAgentsThisTimeStep = newAgentLocationVelocity.get(timeStep).size();

        // Extract the [location, velocity] of every new agent this TimeStep
        ArrayList<int[]> newAgentLocationVelocityThisTimeStep = newAgentLocationVelocity.get(timeStep);

        // Add each new agent's location and velocity in respective HashMap
        // Use totalAgentCount to ensure new agents have the correct key (id) in the hashmap
        for (int i = scenario.getTotalAgentCount(); i < nrOfNewAgentsThisTimeStep + scenario.getTotalAgentCount(); i++) {
            newAgentLocations.put(i, newAgentLocationVelocityThisTimeStep.get(i)[0]);
            newAgentVelocities.put(i, newAgentLocationVelocityThisTimeStep.get(i)[1]);
        }

        // If multiple starting agents, they will occupy the same start vertex --> put in queue instead
        putAgentsInQueue(scenario.getRamp(), newAgentLocations);

        // Update scenario's totalAgentCount
        scenario.addTotalAgentCount(nrOfNewAgentsThisTimeStep);

        // Finally, add new newAgentLocations to the already existing newAgentLocations
        HashMap<Integer, Integer> finalAgentLocations = scenario.fetchAgentLocations();
        finalAgentLocations.putAll(newAgentLocations);

        // Do the same for velocities
        HashMap<Integer, Integer> finalAgentVelocities = scenario.fetchAgentVelocities();
        finalAgentVelocities.putAll(newAgentVelocities);

        scenario.setInitialState(new MAPFState(ramp, finalAgentLocations, finalAgentVelocities));
    }

    public void putAgentsInQueue(Ramp ramp, HashMap<Integer, Integer> newAgentLocations) {
        // Task: If multiple agents in the same start vertex, put them in queue instead
        // newAgentLocations is a hashmap of the agent id and its start vertex (either surface or underground)
        // of ONLY the new agents that are joining the ramp.

        int surfaceQFree = ramp.getSurfaceQFree();
        int undergroundQFree = ramp.getUndergroundQFree();
        int surfaceStart = ramp.getSurfaceStart();
        int undergroundStart = ramp.getUndergroundStart();

        // Put excessive starting agents in their corresponding queues
        int surfaceCount = 0;
        int undergroundCount = 0;
        for (Map.Entry<Integer, Integer> entry : newAgentLocations.entrySet()) {
            // Agents starting from the surface
            if(entry.getValue() == surfaceStart) {
                newAgentLocations.put(entry.getKey(), surfaceQFree);
                surfaceQFree--;
            }
            // Agents starting from the underground
            else if (entry.getValue() == undergroundStart) {
                newAgentLocations.put(entry.getKey(), undergroundQFree);
                undergroundQFree++;
            }
        }

        // Update the surface and underground queue free statuses
        ramp.setSurfaceQFree(surfaceQFree);
        ramp.setUndergroundQFree(undergroundQFree);
    }

    public void solve() {
        // Task: Prompts the algorithm to solve the MAPF scenario
        // As of now, the solve methods return void. When A* is implemented,
        // return a representation of a solution

        HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocity = scenario.getNewAgentLocationVelocity();
        int endTime = this.scenario.getDuration();
        ArrayList<MAPFState> currentSolution;

        // Need to implement so that for every time step, MAPFSolver checks its scenario if
        // new agents enter. In that case, run t

        //currentSolution = this.algorithm.solve(this.scenario);

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
                generateInitialState(scenario, timeStep);

                // Invoke the algorithm anew
                //currentSolution = this.algorithm.solve(this.scenario);

                timeStep++;
            }
        }

        //this.solution = currentSolution;
    }
}
