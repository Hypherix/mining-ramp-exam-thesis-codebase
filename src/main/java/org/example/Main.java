package org.example;

/*
* TODO: The algorithms tend to give different solutions, sometimes with different results.
*  This is especially the case with more agents, larger ramps and re-planning.
*  Of course, the nature of the rollback differs between the algorithms.
*  Honestly, not really sure what to make of it at the moment
*
* TODO ALSO: Test CBSwP with root constraints non-empty.
*
* TODO: Create 6x6 grid with 4 connections and try the algorithms to see if they deviate
*  DONE. Have not found any deviations --> passing bays causing issues?
*
* TODO: Visualisation
*/

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        //long startTime = System.nanoTime();

        int[] passBays = {2, 4, 6};
        Ramp myRamp = new Ramp(10, 5, 5, passBays);



        // ALL ALGORITHMS TEST
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
//        Agent agent2 = new Agent(2, 1, Constants.UP, true);
//        agentList2.put(agent2.id, agent2);
//        agentEntries2.addEntry(2, agent2);
//        agent2 = new Agent(4, 1, Constants.DOWN, true);
//        agentList2.put(agent2.id, agent2);
//        agentEntries2.addEntry(18, agent2);

        MAPFScenario scenarioICTS = new MAPFScenario(myRamp, agentEntries2, 20);
        MAPFScenario scenarioAstar = new MAPFScenario(myRamp, agentEntries2, 20);
        MAPFScenario scenarioCBS = new MAPFScenario(myRamp, agentEntries2, 20);
        MAPFScenario scenarioCBSwP = new MAPFScenario(myRamp, agentEntries2, 20);

        long duration;

        // ICTS
        System.out.println("#################### ICTS ####################");
        long startTimeICTS = System.nanoTime();
        MAPFSolver solverICTS = new MAPFSolver(scenarioICTS, "ICTS");
        solverICTS.solve();
        long endTimeICTS = System.nanoTime();
        duration = endTimeICTS - startTimeICTS;
        System.out.println("\nExecution time ICTS: " + (duration / 1000000.0) + " ms");

        System.out.println();

        // A*
        System.out.println("#################### A* ####################");
        long startTimeAstar = System.nanoTime();
        MAPFSolver solverAstar = new MAPFSolver(scenarioAstar, "astar");
        solverAstar.solve();
        long endTimeAstar = System.nanoTime();
        duration = endTimeAstar - startTimeAstar;
        System.out.println("\nExecution time A*: " + (duration / 1000000.0) + " ms");

        System.out.println();

        // CBS
        System.out.println("#################### CBS ####################");
        long startTimeCBS = System.nanoTime();
        MAPFSolver solverCBS = new MAPFSolver(scenarioCBS, "CBS");
        solverCBS.solve();
        long endTimeCBS = System.nanoTime();
        duration = endTimeCBS - startTimeCBS;
        System.out.println("\nExecution time CBS: " + (duration / 1000000.0) + " ms");

        // CBSwP
        System.out.println("#################### CBSwP ####################");
        long startTimeCBSwP = System.nanoTime();
        MAPFSolver solverCBSwP = new MAPFSolver(scenarioCBSwP, "CBSwP");
        solverCBSwP.solve();
        long endTimeCBSwP = System.nanoTime();
        duration = endTimeCBSwP - startTimeCBSwP;
        System.out.println("\nExecution time CBSwP: " + (duration / 1000000.0) + " ms");
    }
}