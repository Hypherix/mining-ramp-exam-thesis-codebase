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

    // Constructors
    public CBS() {
        root = new CTNode();        // Needed? Yes
    }

    private void addAgentPaths(CTNode node, HashMap<Agent, Integer> agentLocations, Ramp ramp,
                               int accumulatedGeneratedStates, int accumulatedExpandedStates) {
        // Task: Given a CTNode and the agentLocations of the agents that need a path,
        // add independent agent paths to the CTNode

        // For each agent: generate a MAPFScenario, run A*, retrieve an independent optimal path
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            Integer location = entry.getValue();

            // Create an initial state for the scenario
            HashMap<Agent, Integer> initialAgentLocation = new HashMap<>();
            initialAgentLocation.put(agent, location);
            MAPFState singleInitialState = new MAPFState(
                    ramp, initialAgentLocation, 0, 0);
            // Fill the scenario with the node's constraints that A* must consider
            MAPFScenario singleInitialScenario = new MAPFScenario(
                    ramp, singleInitialState, 1,
                    node.vertexConstraints, node.edgeConstraints);

            // With the scenario, run A*
            MAPFAlgorithm aStarSingle = AlgorithmFactory.getAlgorithm("astar");
            MAPFSolution singleInitialSolution = aStarSingle.solve(singleInitialScenario);
            accumulatedGeneratedStates += singleInitialSolution.getGeneratedStates();
            accumulatedExpandedStates += singleInitialSolution.getExpandedStates();

            // With the MAPFSolution, retrieve each location in the path (solutionSet)
            ArrayList<Integer> agentPath = new ArrayList<>();
            ArrayList<MAPFState> singleSolutionSet = singleInitialSolution.getSolutionSet();
            for(MAPFState solutionState : singleSolutionSet) {
                int currentLocation = solutionState.getAgentLocations().get(agent); // Get the location of the agent
                agentPath.add(currentLocation);     // Add it to path
            }

            // Put the independent agent solution path in the root CT node, and update the node cost
            node.addAgentPath(agent, agentPath);
            node.cost += agentPath.size() - 1;
        }
    }

    public boolean isAnExitVertex(int vertex, Ramp ramp) {
        // Task: Return true if the input vertex is either the surface- or underground exit

        int surfaceExit = ramp.getSurfaceExit();
        int undergroundExit = ramp.getUndergroundExit();

        return vertex == surfaceExit || vertex == undergroundExit;
    }

    public Conflict getPathConflict(CTNode node, Ramp ramp) {
        // Task: Given a the root CTNode, return the first conflict amongst agents, else null.
        // TODO: Check if vertex is an exit vertex, in which case it should not count as a conflict
        //  Perhaps even implement that if both agent locations are at exits, continue to next pair for optimisation
        // TODO ALSO: Perhaps isAnExitVertex is not needed in all if statements since the first if statement
        //  rules out any of the vertices being exit vertices

        // Get exit vertices. Two agents located in an exit vertex should not count as a conflict

        ArrayList<Agent> agents = new ArrayList<>(node.agentPaths.keySet());

        // If less than two agents, no conflicts exist
        if(agents.size() < 2) {
            return null;
        }

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

        // If no detections found, i.e. a goal CT root
        return null;
    }

    public ArrayList<MAPFState> buildSolution(MAPFScenario scenario, CTNode goalNode) {
        // Task: Given a goal CTNode, construct an ArrayList<MAPFState> with all solution paths

        // TODO NEXT: generated/expanded state counts are data members of CBS. Needed is solution cost
        //  calculation.
        //  DO NOT FORGET TO SET PARENTS TO THE STATES!
        //  Find a fitting MAPFState constructor and ensure that all costs and such are correct

        MAPFState initialState = scenario.getInitialState();
        Ramp ramp = initialState.getRamp();
        int surfaceExit = ramp.getSurfaceExit();
        int undergroundExit = ramp.getUndergroundExit();

        // Retrieve all agents
        ArrayList<Agent> agents = new ArrayList<>(initialState.getAgentLocations().keySet());

        // Retrieve solution length
        HashMap<Agent, ArrayList<Integer>> agentPaths = goalNode.agentPaths;
        int solutionLength = agentPaths.get(agents.getFirst()).size();

        // For each timeStep, add the locations of each agent to agentLocation and create a MAPFState
        // Keep track of the cost. Don't add cost if prevLocation and current location are exit vertices
        ArrayList<MAPFState> solutionSet = new ArrayList<>();
        solutionSet.addFirst(initialState);

        for (int i = 1; i < solutionLength; i++) {
            HashMap<Agent, Integer> agentLocations = new HashMap<>();

            int cost = 0;
            for(Map.Entry<Agent, ArrayList<Integer>> entry : agentPaths.entrySet()) {
                Agent agent = entry.getKey();
                int prevLocation = entry.getValue().get(i - 1);
                int location = entry.getValue().get(i);

                agentLocations.put(agent, location);

                // If current location is not an exit vertex, cost++
                if(!isAnExitVertex(location, ramp)) {
                    cost++;
                }
                // If current location is an exit vertex, but previous was not, cost++
                else if(!isAnExitVertex(prevLocation, ramp)) {
                    cost++;
                }
            }

            MAPFState state = new MAPFState(ramp, agentLocations, cost, i);
            if(!solutionSet.isEmpty()) {
                state.parent = solutionSet.getLast();
            }
            solutionSet.addLast(state);
        }

        return solutionSet;
    }

    private void addVertexConstraint(CTNode node, Agent agent, Conflict conflict) {
        // Task: Add a vertex constraint to a CTNode

        node.vertexConstraints
                .computeIfAbsent(agent, k -> new HashMap<>())
                .computeIfAbsent(conflict.timeStep, k -> new HashSet<>())
                .add(conflict.vertex);
    }

    private void addEdgeConstraint(CTNode node, Agent agent, Conflict conflict, boolean leftChild) {
        // Task: Add an edge constraint to a CTNode
        // If firstChild == true, constrained edge is fromVertex->toVertex. Vice versa if false

        ArrayList<Integer> constrainedEdge = new ArrayList<>();

        if(leftChild) {
            constrainedEdge.add(conflict.fromVertex);
            constrainedEdge.add(conflict.toVertex);
        }
        else {
            constrainedEdge.add(conflict.toVertex);
            constrainedEdge.add(conflict.fromVertex);
        }

        node.edgeConstraints
                .computeIfAbsent(agent, k -> new HashMap<>())
                .computeIfAbsent(conflict.timeStep, k -> new HashSet<>())
                .add(constrainedEdge);
    }

    private void generateChildren(CTNode parent, Conflict conflict, int numOfGeneratedCTNodes) {
        // Generate two children to parent based on the conflict

        // TODO: Currently, children get shallow copy of parent constraints. Editing child constraints
        //  affect the parent also. Maybe not a problem since we dont work with parents anymore? Or
        //  does the sibling get affected by its other sibling?

        // Get the agents affected by the conflict
        Agent agent1 = conflict.agent1;
        Agent agent2 = conflict.agent2;

        // First (left) child
        CTNode leftChild = new CTNode(parent.vertexConstraints, parent.edgeConstraints);

        // Left child agentPaths is identical to its parent, with the newly constrained agent (agent1) removed
        HashMap<Agent, ArrayList<Integer>> leftChildAgentPaths = new HashMap<>();
        for(Map.Entry<Agent, ArrayList<Integer>> entry : parent.agentPaths.entrySet()) {
            // Copy each entry from parent's agentPaths to leftChild's agentPaths (deep copy)
            leftChildAgentPaths.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        leftChildAgentPaths.remove(agent1);
        leftChild.agentPaths = leftChildAgentPaths;
        leftChild.newlyConstrainedAgent = agent1;

        if(conflict.type == Conflict.ConflictType.VERTEX) {
            // If a vertex conflict, add vertex constraint where agent1
            // can't occupy the vertex at the conflict's timestep
            addVertexConstraint(leftChild, agent1, conflict);
        }
        else if (conflict.type == Conflict.ConflictType.EDGE) {
            // If an edge conflict, add edge constraint where agent1
            // can't move from fromVertex to toVertex at the conflict's timestep
            addEdgeConstraint(leftChild, agent1, conflict, true);
        }
        parent.children.add(leftChild);

        // Right (second) child
        CTNode rightChild = new CTNode(parent.vertexConstraints, parent.edgeConstraints);

        // Right child agentPaths is identical to its parent, with the newly constrained agent (agent2) removed
        HashMap<Agent, ArrayList<Integer>> rightChildAgentPaths = new HashMap<>();
        for(Map.Entry<Agent, ArrayList<Integer>> entry : parent.agentPaths.entrySet()) {
            // Copy each entry from parent's agentPaths to rightChild's agentPaths (deep copy)
            rightChildAgentPaths.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }

        rightChildAgentPaths.remove(agent2);
        rightChild.agentPaths = rightChildAgentPaths;
        rightChild.newlyConstrainedAgent = agent2;

        if(conflict.type == Conflict.ConflictType.VERTEX) {
            addVertexConstraint(rightChild, agent2, conflict);
        } else if (conflict.type == Conflict.ConflictType.EDGE) {
            addEdgeConstraint(rightChild, agent2, conflict, false);
        }
        parent.children.add(rightChild);

        numOfGeneratedCTNodes += 2;
    }


    // Methods
    @Override
    public MAPFSolution solve(MAPFScenario scenario) {
        // Task: Generates a MAPFSolution from the MAPFScenario

        int accumulatedGeneratedStates = 0;
        int accumulatedExpandedStates = 0;
        int numOfGeneratedCTNodes = 0;
        int numOfExpandedCTNodes = 0;

        // Set the root CTNode agent paths and add it to the PrioQueue if not a goal node

        // Get the initial state
        MAPFState initialState = scenario.getInitialState();

        // Retrieve all agentLocations in the scenario
        HashMap<Agent, Integer> agentLocations = initialState.getAgentLocations();

        // Add independent agent paths to the root CT node
        addAgentPaths(this.root, agentLocations, initialState.getRamp(),
                accumulatedGeneratedStates, accumulatedExpandedStates);


        // Create a PriorityQueue of CTNodes where the node with the lowest cost is prioritised
        PriorityQueue<CTNode> ctPrioQueue = new PriorityQueue<>(new CTNodeComparator());

        // Enqueue the root's children
        ctPrioQueue.add(root);

        // Search through the CT until a goal node is found
        while (!ctPrioQueue.isEmpty()) {
            CTNode currentNode = ctPrioQueue.poll();
            numOfExpandedCTNodes++;

            // Check for conflicts in the currentNode agentPaths
            Conflict currentNodeConflict = getPathConflict(currentNode, initialState.getRamp());

            // currentNodeConflict is null if no conflicts were found --> goal node
            if(currentNodeConflict == null) {
                ArrayList<MAPFState> solutionStates = buildSolution(scenario, currentNode);

                MAPFSolution completeSolution = new MAPFSolution(solutionStates,
                        accumulatedGeneratedStates, accumulatedExpandedStates);

                System.out.println("CBS: Goal node found after " + numOfExpandedCTNodes + " CTNodes we explored!");

                return completeSolution;
            }

            // Generate children to the non-goal node and enqueue to ctPrioQueue
            generateChildren(currentNode, currentNodeConflict, numOfGeneratedCTNodes);

            // For each child, generate a path for the agent affected by the new constraint
            // First, get the agentLocation of the constrained agent
            for (CTNode child : currentNode.children) {
                Agent constrainedAgent = child.newlyConstrainedAgent;
                HashMap<Agent, Integer> constrainedAgentLocation = new HashMap<>();

                if(agentLocations.containsKey(constrainedAgent)) {
                    constrainedAgentLocation.put(constrainedAgent, agentLocations.get(constrainedAgent));
                }

                // Get a new path for the constrained agent
                addAgentPaths(child, constrainedAgentLocation, initialState.getRamp(),
                        accumulatedGeneratedStates, accumulatedExpandedStates);
            }

            ctPrioQueue.addAll(currentNode.children);
        }

        return null;
    }
}

