package org.example;

/*
* TODO NEXT: With new agents entering later, check that solution is correct. Seems to print incorrect sort of
*  Check also that new gcost of the new initialState is correct
* */

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();

        int[] passBays = {2};
        Ramp myRamp = new Ramp(5, 5, 5, passBays);

        // This section should be equivalent to the section after (now commented)
        // Add initial agents
        // Every other agent goes the same direction
        HashMap<Integer, Agent> agentList = new HashMap<>();
        AgentEntries agentEntries = new AgentEntries();
        for(int i = 0; i < 4; i++) {
            Agent agent;
            if(i % 2 == 0) {
                agent = new Agent(i, 1, Constants.DOWN);
                agentList.put(agent.id, agent);
                agentEntries.addEntry(0, agent);
            }
            else {
                agent = new Agent(i, 1, Constants.UP);
                agentList.put(agent.id, agent);
                agentEntries.addEntry(0, agent);
            }
        }
        Agent agent = new Agent(4, 1, Constants.UP);
        agentList.put(agent.id, agent);
        agentEntries.addEntry(1, agent);

        agent = new Agent(5, 1, Constants.DOWN);
        agentList.put(agent.id, agent);
        agentEntries.addEntry(3, agent);

        agent = new Agent(6, 1, Constants.DOWN);
        agentList.put(agent.id, agent);
        agentEntries.addEntry(17, agent);

        // Duration specifies the latest timeStep at which new agents can enter
        MAPFScenario scenario = new MAPFScenario(myRamp, agentEntries, 20);
        MAPFSolver solver = new MAPFSolver(scenario, "astar");
        solver.solve();

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("\nExecution time: " + (duration / 1000000.0) + " ms");
    }
}