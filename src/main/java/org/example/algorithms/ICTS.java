package org.example.algorithms;

import org.example.*;

import java.util.*;

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

    private boolean isGoalNode(ICTNode node) {
        // Task: Given an ICT node, check its agent MDDs for a solution

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

        // Check if root is goal node, i.e. if a join solution can be found from MDDs
        if(isGoalNode(root)) {
            // TODO NEXT: As it stands now, isGoalNode() returns true/false. However, as it determines this
            //  it must generate joint solutions if they exist. We want to retrieve these solutions.
            //  Thus: perhaps change the return type of isGoalNode(). But having it as a boolean looks good
            //  so that the if check can be made
            // Return solution etc (see Astar)
        }

        // If not a goal node, create ICT child nodes
        generateChildren(root);

        return null;
    }
}
