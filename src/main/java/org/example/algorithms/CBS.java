package org.example.algorithms;

import org.example.*;
import org.example.CBSclasses.*;

import java.util.*;

/*
* TODO NEXT: Start here. Keep the algorithm and helper methods generic, to account for
*  CBSwP usage. Ideally only generateChildNodes will be overridden by CBSwP.
*  ALSO: (single-agent) A* shall be used as low-level search! Should be easier to implement
*  than ICTS since much of CBS builds on A* logic.
* */

public class CBS implements MAPFAlgorithm {

    // Data members
    CTNode root;
    int accumulatedGeneratedStates;
    int accumulatedExploredStates;

    // Constructors
    public CBS() {
        root = new CTNode();        // Needed? Yes
        this.accumulatedGeneratedStates = 0;
        this.accumulatedExploredStates = 0;
    }

    // Methods
    @Override
    public MAPFSolution solve(MAPFScenario scenario) {
        // Task: Generates a MAPFSolution from the MAPFScenario

        // Set the root CTNode agent paths and add it to the PrioQueue

        // Get the initial state
        MAPFState initialState = scenario.getInitialState();

        // Retrieve all agents in the scenario
        HashMap<Agent, Integer> agentLocations = initialState.getAgentLocations();

        // For each agent: generate a MAPFScenario, run A*, retrieve independent optimal paths
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            Integer location = entry.getValue();

            // Create an initial state for the scenario
            HashMap<Agent, Integer> initialAgentLocation = new HashMap<>();
            initialAgentLocation.put(agent, location);
            MAPFState singleInitialState = new MAPFState(
                    initialState.getRamp(), initialAgentLocation, 0, 0);
            MAPFScenario singleInitialScenario = new MAPFScenario(
                    initialState.getRamp(), singleInitialState, 1);

            // With the scenario, run A*
            MAPFAlgorithm aStarSingle = AlgorithmFactory.getAlgorithm("astar");
            MAPFSolution singleInitialSolution = aStarSingle.solve(singleInitialScenario);

            // Put the agent solution path in the root CT node
            root.addAgentPath(agent, singleInitialSolution.getSolutionSet());
        }


        // Create a PriorityQueue of CTNodes where the node with the lowest cost is prioritised
        PriorityQueue<CTNode> ctPrioQueue = new PriorityQueue<>(new CTNodeComparator());

        // Search through the CT until a goal node is found
        while (!ctPrioQueue.isEmpty()) {
            CTNode currentNode = ctPrioQueue.poll();
        }





        return null;
    }
}

