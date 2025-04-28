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
        for(int i = 1; i < solution.getSolutionSet().size(); i++) {
            MAPFState currentState = solutionSet.get(i);
            int location = getLocationFromAgentLocations(agentLocations);
            currentNode.children.add(new MDDNode(location));

            currentNode = currentNode.children.getFirst();
        }
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

            // Create agentLocations for the scenario
            HashMap<Agent, Integer> initialAgentLocation = new HashMap<>();
            initialAgentLocation.put(agent, location);

            // Create agentEntries for the scenario
            AgentEntries initialAgentEntries = new AgentEntries();
            initialAgentEntries.addEntry(0, agent);

            MAPFScenario initialScenario = new MAPFScenario(
                    initialState.getRamp(), initialAgentEntries, 1);
            MAPFAlgorithm aStarSingle = AlgorithmFactory.getAlgorithm("astar");
            MAPFSolution initialSolution = aStarSingle.solve(initialScenario);
            initialSolutions.add(initialSolution);
            initialOptimalCosts.add(initialSolution.getCost());
            initialSolution.printSolution(true);
        }

        root.costVector = initialOptimalCosts;

        // Generate MDDs from the independent solutions
        for(MAPFSolution initialSolution : initialSolutions) {
            root.agentPaths.addLast(createMDDFromPath(initialSolution));
        }

        // Create ICT child nodes
        generateChildren(root);

        return null;
    }
}
