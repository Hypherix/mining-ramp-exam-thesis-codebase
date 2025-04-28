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
    private ArrayList<Integer> generateInitialSolutionCosts(
            MAPFState initialState, HashMap<Agent, Integer> agentLocations) {
        // Task: Given the initial agent locations, generate independent optimal paths
        // and return their costs

        ArrayList<Integer> initialOptimalCosts = new ArrayList<>();

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
            initialSolution.printSolution(true);
            initialOptimalCosts.add(initialSolution.getCost());
        }
        return initialOptimalCosts;
    }

    private void generateChildren(ICTNode parent) {
        // Task: Given a parent node, generate child nodes

        // TODO
    }

    @Override
    public MAPFSolution solve(MAPFScenario scenario) {

        ArrayList<MAPFState> solution = new ArrayList<>();
        ICTNode root = this.tree.getRoot();

        // Run A* on each agent as if they were alone in the scenario
        MAPFState initialState = scenario.getInitialState();
        HashMap<Agent, Integer> agentLocations = initialState.getAgentLocations();

        System.out.println("Initial independent solution paths:");

        // Create a scenario for each initial agent and get their independent optimal solutions
        root.costVector = generateInitialSolutionCosts(initialState, agentLocations);

        // Generate MDDs for each initial independent solution


        // Create children
        generateChildren(root);

        return null;
    }
}
