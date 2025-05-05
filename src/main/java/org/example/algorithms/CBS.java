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
    int accumulatedExpandedStates;

    // Constructors
    public CBS() {
        root = new CTNode();        // Needed? Yes
        this.accumulatedGeneratedStates = 0;
        this.accumulatedExpandedStates = 0;
    }

    private void addAgentPaths(CTNode node, HashMap<Agent, Integer> agentLocations, MAPFState initialState) {
        // Task: Given a CTNode and the agentLocations of the MAPFScenario, add independent agent paths to the CTNode

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
            this.accumulatedGeneratedStates += singleInitialSolution.getGeneratedStates();
            this.accumulatedExpandedStates += singleInitialSolution.getExpandedStates();

            // With the MAPFSolution, retrieve each location in the path (solutionSet)
            ArrayList<Integer> agentPath = new ArrayList<>();
            ArrayList<MAPFState> singleSolutionSet = singleInitialSolution.getSolutionSet();
            for(MAPFState solutionState : singleSolutionSet) {
                int currentLocation = solutionState.getAgentLocations().get(agent); // Get the location of the agent
                agentPath.add(currentLocation);     // Add it to path
            }

            // Put the independent agent solution path in the root CT node
            root.addAgentPath(agent, agentPath);
        }
    }

    public boolean isAnExitVertex(int vertex, Ramp ramp) {
        // Task: Return true if the input vertex is either the surface- or underground exit

        int surfaceExit = ramp.getSurfaceExit();
        int undergroundExit = ramp.getUndergroundExit();

        return vertex == surfaceExit || vertex == undergroundExit;
    }

    public Conflict getPathConflict(CTNode node, Ramp ramp) {
        // Task: Given a CTNode, return the first conflict amongst agents, else null.
        // TODO: Check if vertex is an exit vertex, in which case it should not count as a conflict
        //  Perhaps even implement that if both agent locations are at exits, continue to next pair for optimisation
        // TODO ALSO: Perhaps isAnExitVertex is not needed in all if statements since the first if statement
        //  rules out any of the vertices being exit vertices

        // Get exit vertices. Two agents located in an exit vertex should not count as a conflict

        ArrayList<Agent> agents = new ArrayList<>(node.agentPaths.keySet());

        // Go through the agent paths pair-wise and look for conflicts
        for (int i = 0; i < agents.size(); i++) {
            Agent firstAgent = agents.get(i);
            ArrayList<Integer> firstPath = node.agentPaths.get(firstAgent);

            for (int j = i + 1; j < agents.size(); j++) {
                Agent secondAgent = agents.get(j);
                ArrayList<Integer> secondPath = node.agentPaths.get(secondAgent);

                int minTime = Math.min(firstPath.size(), secondPath.size());

                for (int t = 0; t < minTime; t++) {
                    int firstPosition = firstPath.get(t);
                    int secondPosition = secondPath.get(t);

                    // If one of the agents have reached an exit, no future conflicts exist between the two
                    if(isAnExitVertex(firstPosition, ramp) || isAnExitVertex(secondPosition, ramp)) {
                        continue;
                    }

                    // Check for a vertex conflict
                    if(firstPosition == secondPosition && !isAnExitVertex(firstPosition, ramp)) {
                        return new Conflict(firstAgent, secondAgent, t, firstPosition);
                    }

                    // Check for an edge conflict
                    int nextFirstPosition = firstPath.get(t + 1);
                    int nextSecondPosition = secondPath.get(t + 1);

                    if (firstPosition == nextSecondPosition && secondPosition == nextFirstPosition
                        && !isAnExitVertex(firstPosition, ramp)) {

                        // The move in the edge conflict is the one that will be in the first agent's constraint.
                        // To get the second agent's constraint, simply reverse the vertices.
                        return new Conflict(firstAgent, secondAgent, t + 1, firstPosition, nextFirstPosition);
                    }
                }
            }
        }

        // If no detections found, i.e. a goal CT node
        return null;
    }

    public MAPFSolution buildSolution(CTNode goalNode) {
        // Task: Given a goal CTNode, construct a MAPFSolution

        // TODO NEXT: generated/expanded state counts are data member of CBS. Needed is solution cost
        //  calculation.
        //  DO NOT FORGET TO SET PARENTS TO THE STATES!
        //  Find a fitting MAPFState constructor and ensure that all costs and such are correct
    }

    // Methods
    @Override
    public MAPFSolution solve(MAPFScenario scenario) {
        // Task: Generates a MAPFSolution from the MAPFScenario

        // Set the root CTNode agent paths and add it to the PrioQueue

        // Get the initial state
        MAPFState initialState = scenario.getInitialState();

        // Retrieve all agentLocations in the scenario
        HashMap<Agent, Integer> agentLocations = initialState.getAgentLocations();

        // Add independent agent paths to the root CT node
        addAgentPaths(this.root, agentLocations, initialState);


        // Create a PriorityQueue of CTNodes where the node with the lowest cost is prioritised
        PriorityQueue<CTNode> ctPrioQueue = new PriorityQueue<>(new CTNodeComparator());

        // Search through the CT until a goal node is found
        while (!ctPrioQueue.isEmpty()) {
            CTNode currentNode = ctPrioQueue.poll();

            // Check for conflicts in the currentNode agent paths

            Conflict conflict = getPathConflict(currentNode, initialState.getRamp());

            // conflict is null if no conflicts were found --> goal node
            if(conflict == null) {
                MAPFSolution solution = buildSolution(currentNode);
            }
        }

        return null;
    }
}

