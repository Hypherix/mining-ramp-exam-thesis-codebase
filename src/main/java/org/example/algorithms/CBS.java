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
        accumulatedGeneratedStates = 0;
        accumulatedExpandedStates = 0;
    }

    private boolean addAgentPaths(CTNode node, HashMap<Agent, Integer> agentLocations, Ramp ramp,
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

            // Check if a single solution path was found, if not (i.e. null), return fail
            if (singleInitialSolution == null) {
                return false;
            }

            this.accumulatedGeneratedStates += singleInitialSolution.getGeneratedStates();
            this.accumulatedExpandedStates += singleInitialSolution.getExpandedStates();

            // With the MAPFSolution, retrieve each location in the path (solutionSet)
            ArrayList<Integer> agentPath = new ArrayList<>();
            ArrayList<MAPFState> singleSolutionSet = singleInitialSolution.getSolutionSet();
            for(MAPFState solutionState : singleSolutionSet) {
                int currentLocation = solutionState.getAgentLocations().get(agent); // Get the location of the agent
                agentPath.add(currentLocation);     // Add it to path
            }

            // Put the independent agent solution path in the root CT node, and update the node cost
            node.addAgentPath(agent, agentPath);
//            node.cost += agentPath.size() - 1;
        }

        // Go through all paths and calculate the SIC cost
        Collection<ArrayList<Integer>> allPaths = node.agentPaths.values();
        int cost = 0;
        for(ArrayList<Integer> path : allPaths) {
            cost += path.size() - 1;
        }
        node.cost = cost;

        return true;
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

        // Retrieve solution length
        HashMap<Agent, ArrayList<Integer>> agentPaths = goalNode.agentPaths;
        int solutionLength = 0;
        for (ArrayList<Integer> path : agentPaths.values()) {
            solutionLength = Math.max(solutionLength, path.size());
        }

        // For each timeStep, add the locations of each agent to agentLocation and create a MAPFState
        // Keep track of the cost. Don't add cost if prevLocation and current location are exit vertices
        ArrayList<MAPFState> solutionSet = new ArrayList<>();
        solutionSet.addFirst(initialState);

        // First, pad the shorter paths with the exit vertex until all path sizes = solutionLength (i.e. the longest path size)
        goalNode.agentPaths = padAgentpaths(goalNode.agentPaths);

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

    private HashMap<Agent, HashMap<Integer, Set<Integer>>> copyParentAndAddVertexConstraint(
            HashMap<Agent, HashMap<Integer, Set<Integer>>> parentConstraints,
            Agent agent,
            int timeStep,
            int prohibitedVertex
    ) {
        // Step 1: Shallow copy of the outer map
        HashMap<Agent, HashMap<Integer, Set<Integer>>> newConstraints = new HashMap<>(parentConstraints);

        // Step 2: Deep copy the specific agent's constraint map (or start fresh)
        HashMap<Integer, Set<Integer>> agentConstraints = parentConstraints.get(agent);
        HashMap<Integer, Set<Integer>> agentConstraintsCopy = (agentConstraints != null)
                ? new HashMap<>(agentConstraints)
                : new HashMap<>();

        // Step 3: Deep copy the vertex set for this time step (or start fresh)
        Set<Integer> vertexSet = agentConstraints != null ? agentConstraints.get(timeStep) : null;
        Set<Integer> vertexSetCopy = (vertexSet != null) ? new HashSet<>(vertexSet) : new HashSet<>();
        vertexSetCopy.add(prohibitedVertex);

        // Step 4: Put back into agent's map, then into top-level map
        agentConstraintsCopy.put(timeStep, vertexSetCopy);
        newConstraints.put(agent, agentConstraintsCopy);

        return newConstraints;
    }

    private HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> copyParentAndAddEdgeConstraint(
            HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> parentConstraints,
            Agent agent,
            int timeStep,
            int fromVertex,
            int toVertex,
            boolean firstChild
    ) {
        // Step 1: Shallow copy of the outer map
        HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> newConstraints = new HashMap<>(parentConstraints);

        // Step 2: Deep copy the agent's edge constraint map (or start fresh)
        HashMap<Integer, Set<ArrayList<Integer>>> agentConstraints = parentConstraints.get(agent);
        HashMap<Integer, Set<ArrayList<Integer>>> agentConstraintsCopy = (agentConstraints != null)
                ? new HashMap<>(agentConstraints)
                : new HashMap<>();

        // Step 3: Deep copy the edge set for this time step (or start fresh)
        Set<ArrayList<Integer>> edgeSet = agentConstraints != null ? agentConstraints.get(timeStep) : null;
        Set<ArrayList<Integer>> edgeSetCopy = (edgeSet != null) ? new HashSet<>(edgeSet) : new HashSet<>();

        // Step 4: Add new edge constraint
        ArrayList<Integer> constrainedEdge = new ArrayList<>();
        // If second child, create a constraint for the reverse move
        if(firstChild) {
            constrainedEdge.add(fromVertex);
            constrainedEdge.add(toVertex);
        }
        else {
            constrainedEdge.add(toVertex);
            constrainedEdge.add(fromVertex);
        }

        edgeSetCopy.add(constrainedEdge);

        // Step 5: Put back into agent's map, then into top-level map
        agentConstraintsCopy.put(timeStep, edgeSetCopy);
        newConstraints.put(agent, agentConstraintsCopy);

        return newConstraints;
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
            leftChild.vertexConstraints = copyParentAndAddVertexConstraint(
                    parent.vertexConstraints,
                    agent1,
                    conflict.timeStep,
                    conflict.vertex);

        }
        else if (conflict.type == Conflict.ConflictType.EDGE) {
            // If an edge conflict, add edge constraint where agent1
            // can't move from fromVertex to toVertex at the conflict's timestep
            leftChild.edgeConstraints = copyParentAndAddEdgeConstraint(
                    parent.edgeConstraints,
                    agent1,
                    conflict.timeStep,
                    conflict.fromVertex,
                    conflict.toVertex,
                    true);

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
            rightChild.vertexConstraints = copyParentAndAddVertexConstraint(
                    parent.vertexConstraints,
                    agent2,
                    conflict.timeStep,
                    conflict.vertex
            );

        } else if (conflict.type == Conflict.ConflictType.EDGE) {
            rightChild.edgeConstraints = copyParentAndAddEdgeConstraint(
                    parent.edgeConstraints,
                    agent2,
                    conflict.timeStep,
                    conflict.fromVertex,
                    conflict.toVertex,
                    false
            );

        }
        parent.children.add(rightChild);

        numOfGeneratedCTNodes += 2;
    }

    private HashMap<Agent, ArrayList<Integer>> padAgentpaths(HashMap<Agent, ArrayList<Integer>> agentPaths) {
        // Task: Make the agent paths of equal length

        // Retrieve solution length
        int solutionLength = 0;
        for (ArrayList<Integer> path : agentPaths.values()) {
            solutionLength = Math.max(solutionLength, path.size());
        }

        for (Map.Entry<Agent, ArrayList<Integer>> entry : agentPaths.entrySet()) {
            ArrayList<Integer> path = entry.getValue();

            int pathExitVertex = path.getLast();

            while(path.size() < solutionLength) {
                path.addLast(pathExitVertex);
            }
        }

        return agentPaths;
    }

    private HashMap<Agent, ArrayList<Integer>> copyAgentPaths(HashMap<Agent, ArrayList<Integer>> agentPaths) {
        // Task: Return a deep copy of the input agentPaths

        HashMap<Agent, ArrayList<Integer>> agentPathsCopy = new HashMap<>();

        for (Map.Entry<Agent, ArrayList<Integer>> entry : agentPaths.entrySet()) {
            Agent agent = entry.getKey();
            ArrayList<Integer> path = entry.getValue();

            Agent agentCopy = new Agent(agent);
            ArrayList<Integer> pathCopy = new ArrayList<>(path);
            agentPathsCopy.put(agentCopy, pathCopy);
        }

        return agentPathsCopy;
    }

    private boolean isQueueBehaviorValid(HashMap<Agent, ArrayList<Integer>> agentPaths, Ramp ramp) {

        ArrayList<Integer> verticesInSurfaceQ = ramp.getVerticesInSurfaceQ();
        ArrayList<Integer> verticesInUndergroundQ = ramp.getVerticesInUndergroundQ();

        int pathLength = agentPaths.values().iterator().next().size();

        for (int t = 0; t < pathLength - 1; t++) {
            boolean inSurfaceQ = true;

            // Go through one queue at a time
            for (ArrayList<Integer> queue : List.of(verticesInSurfaceQ, verticesInUndergroundQ)) {

                // Build a hashmap from vertex -> agent at time t
                HashMap<Integer, Agent> locationAgent = new HashMap<>();

                for (Map.Entry<Agent, ArrayList<Integer>> entry : agentPaths.entrySet()) {
                    Agent agent = entry.getKey();
                    ArrayList<Integer> path = entry.getValue();

                    locationAgent.put(path.get(t), agent);
                }

                if(inSurfaceQ && queue.size() >= 2) {
                    inSurfaceQ = false;

                    for (int i = queue.size() - 1; i > 0; i--) {
                        int frontVertex = queue.get(i);
                        int backVertex = queue.get(i - 1);

                        Agent backAgent = locationAgent.get(backVertex);
                        Agent frontAgent = locationAgent.get(frontVertex);

                        if (frontAgent != null && backAgent != null) {
                            ArrayList<Integer> frontPath = agentPaths.get(frontAgent);
                            ArrayList<Integer> backPath = agentPaths.get(backAgent);

                            if (t + 1 < pathLength) {
                                int frontMove = frontPath.get(t + 1);
                                int backMove = backPath.get(t + 1);

                                // If front agent moves and back agent stays, the node is invalid
                                if(frontMove != frontVertex && backMove == backVertex) {
                                    return false;
                                }
                            }
                        }
                    }
                }

                else if(!inSurfaceQ && queue.size() >= 2) {
                    for (int i = 0; i < queue.size() - 1; i++) {
                        int frontVertex = queue.get(i);
                        int backVertex = queue.get(i + 1);

                        Agent backAgent = locationAgent.get(backVertex);
                        Agent frontAgent = locationAgent.get(frontVertex);

                        if (frontAgent != null && backAgent != null) {
                            ArrayList<Integer> frontPath = agentPaths.get(frontAgent);
                            ArrayList<Integer> backPath = agentPaths.get(backAgent);

                            int frontMove = frontPath.get(t + 1);
                            int backMove = backPath.get(t + 1);

                            // If front agent moves and back agent stays, the node is invalid
                            if(frontMove != frontVertex && backMove == backVertex) {
                                return false;
                            }
                        }
                    }
                }
            }
        }

        return true;
    }
    // TODO: Check if method above works!!!


    // Methods
    @Override
    public MAPFSolution solve(MAPFScenario scenario) {
        // Task: Generates a MAPFSolution from the MAPFScenario

        int numOfGeneratedCTNodes = 0;
        int numOfExpandedCTNodes = 0;

        // Set the root CTNode agent paths and add it to the PrioQueue if not a goal node

        // Get the initial state
        MAPFState initialState = scenario.getInitialState();

        // Retrieve all agentLocations in the scenario
        HashMap<Agent, Integer> agentLocations = initialState.getAgentLocations();

        // Add independent agent paths to the root CT node
        boolean success = addAgentPaths(this.root, agentLocations, initialState.getRamp(),
                accumulatedGeneratedStates, accumulatedExpandedStates);

        if(!success) {
            System.out.println("CBS: Could not find a solution at root CT level");
        }


        // Create a PriorityQueue of CTNodes where the node with the lowest cost is prioritised
        PriorityQueue<CTNode> ctPrioQueue = new PriorityQueue<>(new CTNodeComparator());

        // Enqueue the root's children
        ctPrioQueue.add(root);


        // In case we are here due to a rollback. Add all initialState's ctPrioQueue's nodes
        boolean prioQueuePrefilled = false;
        if(!prioQueuePrefilled && !initialState.getConcurrentNodesInCTPrioQueue().isEmpty()) {

            ArrayList<CTNode> nodesToRemove = new ArrayList<>();
            // First, give the nodes new agentPaths (with the new agents included)
            for (CTNode node : initialState.getConcurrentNodesInCTPrioQueue()) {
                // Clear the node's agentPaths
                node.agentPaths.clear();

                // Then add with updated agentLocations
                success = addAgentPaths(node, agentLocations, initialState.getRamp(),
                        accumulatedGeneratedStates, accumulatedExpandedStates);

                // If no valid agentPaths can be procured with the new agents included, remove the CTNode later
                if(!success) {
                    nodesToRemove.add(node);
                }
            }

            ctPrioQueue.addAll(initialState.getConcurrentNodesInCTPrioQueue());

            ctPrioQueue.removeAll(nodesToRemove);
        }
        // Don't try to prefill the queue with initialState concurrent nodes again
        prioQueuePrefilled = true;


        // Search through the CT until a goal node is found
        while (!ctPrioQueue.isEmpty()) {
            CTNode currentNode = ctPrioQueue.poll();
            numOfExpandedCTNodes++;
//            System.out.println(currentNode.cost);

            // Check for conflicts in the currentNode agentPaths
            Conflict currentNodeConflict = getPathConflict(currentNode, initialState.getRamp());

            // currentNodeConflict is null if no conflicts were found --> goal node
            if(currentNodeConflict == null) {
                ArrayList<MAPFState> solutionStates = buildSolution(scenario, currentNode);

                MAPFSolution completeSolution = new MAPFSolution(solutionStates,
                        accumulatedGeneratedStates, accumulatedExpandedStates);

                System.out.println("CBS: Goal node found after " + numOfGeneratedCTNodes + " were generated (possibly added to queue)" +
                        ", and " + numOfExpandedCTNodes + " CTNodes were expanded (polled from the queue)!");

                return completeSolution;
            }

            // Generate children to the non-goal node and enqueue to ctPrioQueue
            generateChildren(currentNode, currentNodeConflict, numOfGeneratedCTNodes);
            numOfGeneratedCTNodes += 2;

            // For each child, generate a path for the agent affected by the new constraint
            // First, get the agentLocation of the constrained agent
            for (CTNode child : currentNode.children) {
                Agent constrainedAgent = child.newlyConstrainedAgent;
                HashMap<Agent, Integer> constrainedAgentLocation = new HashMap<>();

                if(agentLocations.containsKey(constrainedAgent)) {
                    constrainedAgentLocation.put(constrainedAgent, agentLocations.get(constrainedAgent));
                }

                // Get a new path for the constrained agent
                success = addAgentPaths(child, constrainedAgentLocation, initialState.getRamp(),
                        accumulatedGeneratedStates, accumulatedExpandedStates);

                // Pad the shorter paths with the exit vertex until all path sizes = solutionLength (i.e. the longest path size)
                // This is needed for checking if queue behaviour is valid
                HashMap<Agent, ArrayList<Integer>> agentPathsCopy = copyAgentPaths(child.agentPaths);

                agentPathsCopy = padAgentpaths(agentPathsCopy);

                // Only add the child to the queue if it has a set of solution paths that does not
                // violate normal queue behaviour
                boolean valid = isQueueBehaviorValid(agentPathsCopy, initialState.getRamp());
                if(success && valid) {
                    ctPrioQueue.add(child);
                }
            }
        }

        return null;
    }
}

// TODO: Add a snapshot of the ctPrioQueue to children right before they are enqueued, similar to in A*!
//  Then, make sure all other rollback-related things are in place and work. Then test CBS rollback
//  Check how the concurrentNodesInPrioQueue for a rollback state is handled, to ensure it is in the MAPFSolution
//  in MAPFSolver when looking for new agents and prompting a rollback, and thus also is in the new initialState
//  for that rollback.