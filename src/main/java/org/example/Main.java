package org.example;

/*
* TODO: With current scenario, A* gets non-optimal solution. Debug and find out why.
* */

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        //long startTime = System.nanoTime();

        int[] passBays = {1};
        Ramp myRamp = new Ramp(3, 5, 5, passBays);

        // This section should be equivalent to the section after (now commented)
        // Add initial agents
        // Every other agent goes the same direction
        HashMap<Integer, Agent> agentList = new HashMap<>();
        AgentEntries agentEntries = new AgentEntries();
        for(int i = 0; i < 4; i++) {
            Agent agent;
            if(i % 2 == 0) {
                agent = new Agent(i, 1, Constants.DOWN, true);
                agentList.put(agent.id, agent);
                agentEntries.addEntry(0, agent);
            }
            else {
                agent = new Agent(i, 1, Constants.UP, true);
                agentList.put(agent.id, agent);
                agentEntries.addEntry(0, agent);
            }
        }
        Agent agent = new Agent(4, 1, Constants.UP, true);
        agentList.put(agent.id, agent);
        agentEntries.addEntry(1, agent);

        agent = new Agent(5, 1, Constants.DOWN, true);
        agentList.put(agent.id, agent);
        agentEntries.addEntry(3, agent);

        agent = new Agent(6, 1, Constants.DOWN, true);
        agentList.put(agent.id, agent);
        agentEntries.addEntry(17, agent);

        // Duration specifies the latest timeStep at which new agents can enter
        MAPFScenario scenario = new MAPFScenario(myRamp, agentEntries, 20);

        // A*
        /*
        MAPFSolver solverAStar = new MAPFSolver(scenario, "astar");
        solverAStar.solve();

        long endTimeAStar = System.nanoTime();
        long duration = endTimeAStar - startTime;
        System.out.println("\nExecution time: " + (duration / 1000000.0) + " ms");
        */

        // ICTS
        HashMap<Integer, Agent> agentList2 = new HashMap<>();
        AgentEntries agentEntries2 = new AgentEntries();
        for(int i = 0; i < 3; i++) {
            Agent agent2;
            if(i % 2 == 0) {
                agent2 = new Agent(i, 1, Constants.DOWN, true);
            }
            else {
                agent2 = new Agent(i, 1, Constants.UP, false);
            }
            agentList2.put(agent2.id, agent2);
            agentEntries2.addEntry(0, agent2);
        }
        Agent agent2 = new Agent(3, 1, Constants.UP, true);
        agentList2.put(agent2.id, agent);
        agentEntries2.addEntry(2, agent2);

        MAPFScenario scenario2 = new MAPFScenario(myRamp, agentEntries2, 20);
        MAPFScenario scenario3 = new MAPFScenario(myRamp, agentEntries2, 20);

//        long startTimeICTS = System.nanoTime();
//
//        MAPFSolver solverICTS = new MAPFSolver(scenario2, "ICTS");
//        solverICTS.solve();
//
//        long endTimeICTS = System.nanoTime();
//        long duration = endTimeICTS - startTimeICTS;
//        System.out.println("\nExecution time ICTS: " + (duration / 1000000.0) + " ms");
        long startTimeAstar = System.nanoTime();

        MAPFSolver solverAstar = new MAPFSolver(scenario3, "astar");
        solverAstar.solve();

        long endTimeAstar = System.nanoTime();
        long duration = endTimeAstar - startTimeAstar;
        System.out.println("\nExecution time A*: " + (duration / 1000000.0) + " ms");


    }
}