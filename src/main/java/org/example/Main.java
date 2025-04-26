package org.example;

/*
* TODO NEXT: With new agents entering later, check that solution is correct. Seems to print incorrect sort of
*  Check also that new gcost of the new initialState is correct
* TODO NEW: Are new agents added one step too early? If new enter at timeStep 2, time steps 0 and 1 should be
*  free from these new agents --> the first two time steps should only have the original agents
* */

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        long startTime = System.nanoTime();

        int[] passBays = {2, 6};
        Ramp myRamp = new Ramp(10, 5, 10, passBays);

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
        agentEntries.addEntry(4, agent);

        agent = new Agent(5, 1, Constants.DOWN);
        agentList.put(agent.id, agent);
        agentEntries.addEntry(4, agent);


//        HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocityDirection = new HashMap<Integer, ArrayList<int[]>>();
//        int[] locationVelocity1 = new int[]{3, 1, 0};
//        int[] locationVelocity2 = new int[]{7, 1, 1};
//        ArrayList<int[]> list = new ArrayList<>();
//        list.add(locationVelocity1);
//        list.add(locationVelocity2);
//        newAgentLocationVelocityDirection.put(0, list);
//        int[] locationVelocity3 = new int[]{3, 1, 0};
//        int[] locationVelocity4 = new int[]{7, 1, 1};
//        ArrayList<int[]> list2 = new ArrayList<>();
//        list2.add(locationVelocity3);
//        list2.add(locationVelocity4);
//        newAgentLocationVelocityDirection.put(2, list2);

        // Duration specifies the latest timeStep at which new agents can enter
        MAPFScenario scenario = new MAPFScenario(myRamp, agentEntries, 20);
        MAPFSolver solver = new MAPFSolver(scenario, "astar");
        solver.solve();

        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        System.out.println("\nExecution time: " + (duration / 1000000.0) + " ms");
    }
}