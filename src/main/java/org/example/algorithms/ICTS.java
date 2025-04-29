package org.example.algorithms;

import org.example.*;

import java.util.*;
import java.util.stream.Collectors;

/*
*
* */

public class ICTS implements MAPFAlgorithm {

    // Data members
    ICT tree;

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

        // Build the MDD by going through the solution path locations
        for(int i = 1; i < solutionSet.size(); i++) {
            MAPFState currentState = solutionSet.get(i);
            agentLocations = currentState.getAgentLocations();
            int location = getLocationFromAgentLocations(agentLocations);
            currentNode.children.add(new MDDNode(location));

            currentNode = currentNode.children.getFirst();
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
                                       HashSet<String> visited) {
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
                }
            }

            return;
        }

        for (MDDNode child : childrenLists.get(depth)) {    // For the current agent, try next child
            partial.add(child);
            generateJointChildren(childrenLists, depth + 1, partial, previous, queue, visited);
            partial.remove(partial.size() - 1);     // Remove latest child to backtrack and continue
        }
    }

    private boolean isGoalNode(ICTNode ictNode, int surfaceExit, int undergroundExit) {
        // Task: Given an ICT node, check its agent MDDs for a solution

        // Get all start vertices and create the root JointMDDNode
//        ArrayList<Integer> startVertices = new ArrayList<>();
//        for(MDD path : node.agentPaths) {
//            startVertices.add(path.getRootVertex());
//        }
//        JointMDDNode jointRoot = new JointMDDNode(startVertices);
//        JointMDDNode currentJointNode = jointRoot;
//
//        // Go through all possible JointMDDNode children
//        int gen = 0;
//        ArrayList<ArrayList<MDD>> children = new ArrayList<>();
//        while(true) {       // Change to something more befitting
//            for(int i = 0; i < node.agentPaths.size(); i++) {
//
//            }
//        }

        // TODO NEXT: Add parent to each MDDNode so that we can retrieve the solution from the final MDD node?
        //  Prob yes! Then parent must be assigned whenever a new child is generated

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
        queue.add(rootNodes);
        visited.add(encodeVertexList(rootVertices));

        while(!queue.isEmpty()) {
            ArrayList<MDDNode> currentNodes = queue.poll();

            // Check if all MDDNodes are in exit vertices --> goal node
            boolean goalNode = false;
            for(MDDNode mddNode : currentNodes) {
                if(mddNode.vertex == surfaceExit || mddNode.vertex == undergroundExit) {
                    goalNode = true;
                }
            }
            if(goalNode) {      // Could be moved to the if statement in the for each loop above
                return true;
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
            generateJointChildren(childrenLists, 0, new ArrayList<>(), currentNodes, queue, visited);
        }


        // Create joint MDD. Use JointMDD and JointMDDNode classes. Read ICTS paper
        return false;
    }

    private void generateChildren(ICTNode parent) {
        // Task: Given a parent node, generate child nodes

        // TODO: https://chatgpt.com/c/680f84ab-e578-800b-9db8-7c3913d06f51
    }

    @Override
    public MAPFSolution solve(MAPFScenario scenario) {

        ArrayList<MAPFState> solution = new ArrayList<>();
        ICTNode root = this.tree.getRoot();

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

            // Create agentEntries for the scenario
//            AgentEntries initialAgentEntries = new AgentEntries();
//            initialAgentEntries.addEntry(0, agent);

            MAPFScenario initialScenario = new MAPFScenario(
                    initialState.getRamp(), singleInitialState, 1);
            MAPFAlgorithm aStarSingle = AlgorithmFactory.getAlgorithm("astar");
            MAPFSolution initialSolution = aStarSingle.solve(initialScenario);
            initialSolution.printSolution(true);
            initialSolutions.add(initialSolution);
            initialOptimalCosts.add(initialSolution.getCost());
        }

        root.costVector = initialOptimalCosts;

        // Generate MDDs from the independent solutions
        for(MAPFSolution initialSolution : initialSolutions) {
            root.agentPaths.addLast(createMDDFromPath(initialSolution));
        }

        int surfaceExit = initialState.getRamp().getSurfaceExit();
        int undergroundExit = initialState.getRamp().getUndergroundExit();

        // Check if root is goal node, i.e. if a join solution can be found from MDDs
        if(isGoalNode(root, surfaceExit, undergroundExit)) {
            // TODO NEXT: As it stands now, isGoalNode() returns true/false. However, as it determines this
            //  it must generate joint solutions if they exist. We want to retrieve these solutions.
            //  Thus: perhaps change the return type of isGoalNode(). But having it as a boolean looks good
            //  so that the if check can be made
            // Return solution etc (see Astar)
            System.out.println("Goal node found!");
        }
        System.out.println("Current node is not a goal node");

        // If not a goal node, create ICT child nodes
        generateChildren(root);

        return null;
    }
}
