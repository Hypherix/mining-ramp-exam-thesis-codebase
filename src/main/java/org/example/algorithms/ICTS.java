package org.example.algorithms;

import org.example.*;

import java.util.*;
import java.util.stream.Collectors;

/*
*
* */

public class ICTS implements MAPFAlgorithm {

    // Data members
    private ICT tree;
    private int numOfAgents;

    // Constructors
    public ICTS() {
        this.tree = new ICT();
    }

    // Methods
    private int getLocationFromAgentLocations(HashMap<Agent, Integer> agentLocations) {
        // Task: From agentLocations with ONE entry, get the location

        Collection<Integer> location = agentLocations.values();
        return location.iterator().next();
    }

    private MDD createMDDFromPath(MAPFSolution solution) {
        // Task: From a solution path, generate an MDD from it

        // Get the start vertex location.
        ArrayList<MAPFState> solutionSet = solution.getSolutionSet();
        MAPFState startState = solutionSet.getFirst();
        HashMap<Agent, Integer> agentLocations = startState.getAgentLocations();
        int startVertex = getLocationFromAgentLocations(agentLocations);     // Works since startValue always only holds one value

        MDDNode root = new MDDNode(startVertex);
        MDDNode currentNode = root;
        currentNode.parent = null;

        // Build the MDD by going through the solution path locations
        for(int i = 1; i < solutionSet.size(); i++) {
            MAPFState currentState = solutionSet.get(i);
            agentLocations = currentState.getAgentLocations();
            int location = getLocationFromAgentLocations(agentLocations);
            currentNode.children.add(new MDDNode(location));

            MDDNode temp = currentNode.children.getFirst();
            temp.parent = currentNode;
            currentNode = temp;
        }

        return new MDD(root);
    }

    private String encodeVertexList(List<Integer> vertices) {
        // Retrieve the list of vertices as a string
        return vertices.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    private boolean hasConflict(ArrayList<MDDNode> nodes, ArrayList<MDDNode> previousNodes) {
        int nodeSize = nodes.size();

        // Check if vertex conflict
        // If seenVertices already contains the vertex of the current mddNode, that means that another agent
        // has already occupied it --> vertex conflict
        HashSet<Integer> seenVertices = new HashSet<>();
        for (MDDNode mddNode : nodes) {
            if(seenVertices.contains(mddNode.vertex)) {
                return true;
            }
            else {
                seenVertices.add(mddNode.vertex);
            }
        }

        // Check if edge conflict
        // For each agent, compare its current vertex with every other agent's previous vertex, and then compare
        // its previous vertex with every other agent's current vertex. If match --> edge conflict
        for(int i = 0; i < nodeSize; i++) {
            for (int j = i + 1; j < nodeSize; j++) {
                if (nodes.get(i).vertex == previousNodes.get(j).vertex &&
                    nodes.get(j).vertex == previousNodes.get(i).vertex) {
                    return true;
                }
            }
        }

        return false;
    }

    private void generateJointChildren(ArrayList<ArrayList<MDDNode>> childrenLists,
                                       int depth,                       // Tracks recursion depth, i.e. what agent we're at
                                       ArrayList<MDDNode> partial,      // The joint child node we currently build on
                                       ArrayList<MDDNode> previous,     // The previous joint node for checking edge conflicts
                                       Queue<ArrayList<MDDNode>> queue,
                                       HashSet<String> visited,
                                       HashMap<String, ArrayList<MDDNode>> parentMap) {
        // Task: Generate every combination of child MDDNodes (one from each agent) and add to the queue
        // if they are conflict-free and not visited

        // Base case: our childrenLists size is full
        if(depth == childrenLists.size()) {             // If one child per agent has been chosen, base case is fulfilled
            if(!hasConflict(partial, previous)) {
                List<Integer> vertices = partial.stream().map(n -> n.vertex).collect(Collectors.toList());
                String key = encodeVertexList(vertices);
                if(!visited.contains(key)) {
                    visited.add(key);
                    queue.add(new ArrayList<>(partial));
                    parentMap.put(key, previous);
                }
            }

            return;
        }

        for (MDDNode child : childrenLists.get(depth)) {    // For the current agent, try next child
            partial.add(child);
            generateJointChildren(childrenLists, depth + 1, partial, previous, queue, visited, parentMap);
            partial.removeLast();     // Remove latest child to backtrack and continue
        }
    }

    private ArrayList<ArrayList<Integer>> MDDToSolutionPaths(ICTNode ictNode, int surfaceExit, int undergroundExit) {
        // Task: Given an ICT node, check its agent MDDs for a solution

        ArrayList<MDD> rootMDDs = ictNode.agentPaths;
        int numOfAgents = rootMDDs.size();

        // Initial joint MDD states
        ArrayList<MDDNode> rootNodes = new ArrayList<>();       // Used to access children
        ArrayList<Integer> rootVertices = new ArrayList<>();    // Used to encode visited joint MDD states
        for(MDD mdd : rootMDDs) {
            rootNodes.add(mdd.root);
            rootVertices.add(mdd.root.vertex);
        }

        // Initialise and add first entries to queue and visited
        Queue<ArrayList<MDDNode>> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();
        HashMap<String, ArrayList<MDDNode>> parentMap = new HashMap<>();
        queue.add(rootNodes);
        visited.add(encodeVertexList(rootVertices));

        while(!queue.isEmpty()) {
            ArrayList<MDDNode> currentNodes = queue.poll();

            // Check if all MDDNodes are in exit vertices --> goal node
            boolean goalNode = false;
            for(MDDNode mddNode : currentNodes) {
                if(mddNode.vertex == surfaceExit || mddNode.vertex == undergroundExit) {
                    goalNode = true;
                    break;
                }
            }

            if(goalNode) {      // Could be moved to the if statement in the for each loop above

                // Reconstruct the solution path
                ArrayList<ArrayList<MDDNode>> solutionPath = new ArrayList<>();
                String key = encodeVertexList(currentNodes.stream().map(n -> n.vertex).collect(Collectors.toList()));
                
                while(key != null) {
                    solutionPath.addFirst(currentNodes);
                    ArrayList<MDDNode> parent = parentMap.get(key);
                    if (parent == null) {
                        break;
                    }
                    currentNodes = parent;
                    key = encodeVertexList(currentNodes.stream().map(n -> n.vertex).collect(Collectors.toList()));
                }

                // Convert the solutionPath to agentPaths
                ArrayList<ArrayList<Integer>> agentPaths = new ArrayList<>();
                for (int i = 0; i < numOfAgents; i++) {
                    agentPaths.add(new ArrayList<>());
                }

                for(ArrayList<MDDNode> path : solutionPath) {
                    for (int i = 0; i < path.size(); i++) {
                        agentPaths.get(i).add(path.get(i).vertex);
                    }
                }
                return agentPaths;
            }

            // Generate child combinations
            ArrayList<ArrayList<MDDNode>> childrenLists = new ArrayList<>();
            for(MDDNode mddNode : currentNodes) {
                childrenLists.add(mddNode.children);
            }

            // Get the cartesian/cross product of the children
            // currentNodes is input as previous, so inside the method, previous will be the current
            // an empty arrayList is input as partial, so partial is empty and will be built on
            // this repeats for each recursive call where previous becomes current, and partial is empty and is
            // built on (which for that iteration is the current)
            generateJointChildren(childrenLists, 0, new ArrayList<>(), currentNodes, queue, visited, parentMap);
        }

        // If no goal node is found, return null as the agentPaths
        return null;
    }

    private ArrayList<MAPFState> buildSolution(MAPFScenario scenario, ArrayList<ArrayList<Integer>> solutionPaths) {
        // Task: From solutionPaths, build an ArrayList<MAPFState> with all solution paths

        MAPFState initialState = scenario.getInitialState();
        Ramp ramp = initialState.getRamp();
        int surfaceExit = ramp.getSurfaceExit();
        int undergroundExit = ramp.getUndergroundExit();
        int solutionLength = solutionPaths.get(0).size();
        ArrayList<MAPFState> solutionSet = new ArrayList<>();
        solutionSet.addFirst(initialState);

        // Retrieve all agents
        ArrayList<Agent> agents = new ArrayList<>();
        for (Map.Entry<Agent, Integer> entry : initialState.getAgentLocations().entrySet()) {
            Agent agent = entry.getKey();
            int location = entry.getValue();

            agents.addLast(agent);
        }
        int numOfAgents = agents.size();

        for (int i = 1; i < solutionLength; i++) {
            HashMap<Agent, Integer> agentLocations = new HashMap<>();

            int cost = 0;
            for (int j = 0; j < numOfAgents; j++) {
                int location = solutionPaths.get(j).get(i);
                Agent agent = agents.get(j);
                agentLocations.put(agent, location);

                if (location != surfaceExit && location != undergroundExit) {
                    cost++;
                }
            }
            
            MAPFState state = new MAPFState(ramp, agentLocations, cost);
            solutionSet.addLast(state);
        }

        // TODO: REMOVE BELOW

        return solutionSet;
    }

    private void generateChildren(ICTNode parent) {
        // Task: Given a parent node, generate child nodes

        ArrayList<Integer> costVector = parent.costVector;
        for (int i = 0; i < costVector.size(); i++) {
            ArrayList<Integer> childCostVector = new ArrayList<>(costVector);
            childCostVector.set(i, childCostVector.get(i) + 1);

            if(!this.tree.getAllCostVectors().contains(childCostVector)) {
                ICTNode child = new ICTNode();
                child.costVector = childCostVector;

                parent.children.add(child);

                this.tree.addCostVector(childCostVector);

                continue;
            }

            System.out.println("ICT child node with cost vector " + childCostVector + " was pruned");
        }
    }

    private ArrayList<Integer> getNeighbours(Ramp ramp, Agent agent, int location) {
        // Task: Return all eligible neighbour locations for a location/vertex

        ArrayList<Integer> neighbours = new ArrayList<>();
        HashMap<Integer, UpDownNeighbourList> adjList = ramp.getAdjList();

        if(agent.direction == Constants.DOWN) {
            neighbours = adjList.get(location).getDownNeighbours();
        }
        else {
            neighbours = adjList.get(location).getUpNeighbours();
        }

        return neighbours;
    }

    private ArrayList<MAPFSolution> getValidSolutions(Ramp ramp, Agent agent, int startLocation, int cost) {
        // Return all paths with the specified cost that leads to goal

        // Initialise with root
        BFSTreeNode root = new BFSTreeNode(startLocation);
        ArrayList<Integer> possibleLocations = new ArrayList<>();
        possibleLocations = getNeighbours(ramp, agent, startLocation);

        Queue<BFSTreeNode> leafGen = new LinkedList<>();
        ArrayList<BFSTreeNode> nextLeafGen = new ArrayList<>();

        for(Integer location : possibleLocations) {
            BFSTreeNode child = new BFSTreeNode(location);
            child.parent = root;
            root.children.add(child);
            leafGen.add(child);
        }

        // Repeat for children for cost number of moves
        for (int i = 1; i < cost + 1; i++) {
            nextLeafGen.clear();

            for(BFSTreeNode leaf : leafGen) {
                possibleLocations = getNeighbours(ramp, agent, leaf.location);

                for (Integer location : possibleLocations) {
                    BFSTreeNode child = new BFSTreeNode(location);
                    child.parent = leaf;
                    leaf.children.add(child);

                    nextLeafGen.add(child);
                }
            }

            leafGen.addAll(nextLeafGen);
        }

        // Get the leaf nodes after cost number of moves
        ArrayList<BFSTreeNode> finalLeafGen = new ArrayList<>(leafGen);

        // Get the goal location
        int goalLocation;
        if(agent.direction == Constants.DOWN) {
            goalLocation = ramp.getUndergroundExit();
        }
        else {
            goalLocation = ramp.getSurfaceExit();
        }

        // Remove all who do not end up in the goal location
        for (int i = finalLeafGen.size() - 1; i >= 0; i--) {
            if (finalLeafGen.get(i).location != goalLocation) {
                finalLeafGen.remove(i);
            }
        }

        // Generate a MAPFSolution for each path to goal found
        ArrayList<MAPFSolution> allSolutions = new ArrayList<>();

        for(int i = 0; i < finalLeafGen.size(); i++) {
            ArrayList<MAPFState> solutionStates = new ArrayList<>();

            BFSTreeNode current = finalLeafGen.get(i);

            int depth = cost;
            while(current != null) {
                // Create a MAPFState of the current situation
                HashMap<Agent, Integer> agentLocations = new HashMap<>();
                agentLocations.put(agent, current.location);
                int gcost = depth--;
                MAPFState state = new MAPFState(ramp, agentLocations, gcost);

                // Add the state to the solution set
                solutionStates.add(state);

                // Keep climbing
                current = current.parent;
            }

            // Reverse the solution set to get it from root to leaf
            Collections.reverse(solutionStates);

            // Create a MAPFSolution from the solutionStates
            MAPFSolution oneSolution = new MAPFSolution(solutionStates, 0, 0);

            // Add the solution to allSolutions
            allSolutions.add(oneSolution);
        }

        return allSolutions;
    }

    @Override
    public MAPFSolution solve(MAPFScenario scenario) {

        ICTNode root = new ICTNode();

        int accumulatedGeneratedStates = 0;
        int accumulatedExpandedStates = 0;
        int numOfExploredICTNodes = 0;

        // Run A* on each agent as if they were alone in the scenario
        MAPFState initialState = scenario.getInitialState();
        HashMap<Agent, Integer> agentLocations = initialState.getAgentLocations();
        ArrayList<Integer> initialOptimalCosts = new ArrayList<>();
        ArrayList<MAPFSolution> initialSolutions = new ArrayList<>();

        System.out.println("Initial independent solution paths:");

        // Create a scenario for each initial agent and get their independent optimal solutions
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            Integer location = entry.getValue();

            // Create initialState for the scenario
            HashMap<Agent, Integer> initialAgentLocation = new HashMap<>();
            initialAgentLocation.put(agent, location);
            MAPFState singleInitialState = new MAPFState(
                    initialState.getRamp(), initialAgentLocation, 0);

            MAPFScenario initialScenario = new MAPFScenario(
                    initialState.getRamp(), singleInitialState, 1);
            MAPFAlgorithm aStarSingle = AlgorithmFactory.getAlgorithm("astar");
            MAPFSolution initialSolution = aStarSingle.solve(initialScenario);
            initialSolution.printSolution(true);
            initialSolutions.add(initialSolution);
            initialOptimalCosts.add(initialSolution.getCost());
            accumulatedExpandedStates += initialSolution.getExpandedStates();
            accumulatedGeneratedStates += initialSolution.getGeneratedStates();
        }

        root.costVector = initialOptimalCosts;
        this.tree.addCostVector(root.costVector);
        this.tree.setRoot(root);


        // Generate MDDs from the independent solutions
        for(MAPFSolution initialSolution : initialSolutions) {
            root.agentPaths.addLast(createMDDFromPath(initialSolution));
        }

        int surfaceExit = initialState.getRamp().getSurfaceExit();
        int undergroundExit = initialState.getRamp().getUndergroundExit();

        // TODO Optimisation? Run MDDToSolutionPaths() on each pair of agents. If one of them do not return
        //  a solution, dont bother with generating a solution for the whole ICTNode

        // Check if root is goal node, i.e. if a joint solution can be found from the MDDs
        ArrayList<ArrayList<Integer>> solutionPaths = MDDToSolutionPaths(root, surfaceExit, undergroundExit);
        numOfExploredICTNodes++;

        if(solutionPaths != null) {
            ArrayList<MAPFState> solution = buildSolution(scenario, solutionPaths);

            MAPFSolution completeSolution = new MAPFSolution(solution, accumulatedGeneratedStates, accumulatedExpandedStates);

            System.out.println("Goal node found after " + numOfExploredICTNodes + " ICTNodes were explored!");

            return completeSolution;
        }

        // If not a goal node, create ICT child nodes
        generateChildren(root);

        // Create an ICT ictQueue from which nodes up for checking are retrieved
        Queue<ICTNode> ictQueue = new LinkedList<>(root.children);

        // Search through the ICT until a goal node is found
        while(!ictQueue.isEmpty()) {
            ICTNode currentNode = ictQueue.poll();
            int numOfAgents = currentNode.costVector.size();

            // Generate an MDD for each agent i, imposing all costVector.get(i) possible actions
            // Run BFS for x moves only and return the solution. Then call createMDDFromPath() to get MDD
            /*
            * TODO NEXT: Create a BFS method. It takes the ramp, agent start location, and goal location.
                Try all x action combinations and record the end location. If end location == goal location
                save the path, create a MAPFSolution for it, and run createMDDFromPath() to get MDD
            * */

            ArrayList<MAPFSolution> childSolutions;

            int iteration = 0;
            for(Map.Entry<Agent, Integer> entry : initialState.getAgentLocations().entrySet()) {
                Agent agent = entry.getKey();
                int location = entry.getValue();

                Ramp ramp = initialState.getRamp();
                int cost = currentNode.costVector.get(iteration);

                childSolutions = getValidSolutions(ramp, agent, location, cost);

                for(MAPFSolution solution : childSolutions) {
                    currentNode.agentPaths.addLast(createMDDFromPath(solution));
                }

                // Check if currentNode is a goal node, i.e. if a joint solution can be found from the MDDs
                solutionPaths = MDDToSolutionPaths(root, surfaceExit, undergroundExit);
                numOfExploredICTNodes++;

                // Return solution if goal node was found
                if(solutionPaths != null) {
                    ArrayList<MAPFState> solution = buildSolution(scenario, solutionPaths);

                    MAPFSolution completeSolution = new MAPFSolution(solution, accumulatedGeneratedStates, accumulatedExpandedStates);

                    System.out.println("Goal node found after " + numOfExploredICTNodes + " ICTNodes were explored!");

                    return completeSolution;
                }

                // Generate children to the non-goal node and enqueue to ictQueue
                generateChildren(currentNode);
                ictQueue.addAll(currentNode.children);
            }
        }

        return null;
    }
}
