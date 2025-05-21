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
*
* TODO: Visualisation
*/

import java.util.HashMap;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        //long startTime = System.nanoTime();

//        int[] passBays = {2, 4, 6};
//        Ramp myRamp = new Ramp(10, 5, 5, passBays);

        Ramp my5x5 = new Ramp(5);

        HashMap<Agent, Integer> agentLocations = new HashMap<>();
        // ALL ALGORITHMS TEST
        HashMap<Integer, Agent> agentList2 = new HashMap<>();
        AgentEntries agentEntries2 = new AgentEntries();
        for(int i = 0; i < 6; i++) {
            Agent agent2;
            Random rand = new Random();
            int start = rand.nextInt(25);
            if(i % 2 == 0) {
                agent2 = new Agent(i, 1, Constants.DOWN, true);
                if (i == 0) {
                    agentLocations.put(agent2, start);
                }
                if (i == 2) {
                    agentLocations.put(agent2, start);
                }
                if (i == 4) {
                    agentLocations.put(agent2, start);
                }
            }
            else {
                agent2 = new Agent(i, 1, Constants.UP, false);

                if (i == 1) {
                    agentLocations.put(agent2, start);
                }
                if (i == 3) {
                    agentLocations.put(agent2, start);
                }
                if (i == 5) {
                    agentLocations.put(agent2, start);
                }
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


        MAPFState initialState = new MAPFState(my5x5, agentLocations, 0, 0);

        MAPFScenario scenarioICTS = new MAPFScenario(my5x5, initialState, 20);
        scenarioICTS.setAgentEntries(agentEntries2);
        MAPFScenario scenarioAstar = new MAPFScenario(my5x5, initialState, 20);
        scenarioAstar.setAgentEntries(agentEntries2);
        MAPFScenario scenarioCBS = new MAPFScenario(my5x5, initialState, 20);
        scenarioCBS.setAgentEntries(agentEntries2);
        MAPFScenario scenarioCBSwP = new MAPFScenario(my5x5, initialState, 20);
        scenarioCBSwP.setAgentEntries(agentEntries2);

//        MAPFScenario scenarioICTS = new MAPFScenario(my5x5, agentEntries2, 20);
//        MAPFScenario scenarioAstar = new MAPFScenario(my5x5, agentEntries2, 20);
//        MAPFScenario scenarioCBS = new MAPFScenario(my5x5, agentEntries2, 20);
//        MAPFScenario scenarioCBSwP = new MAPFScenario(my5x5, agentEntries2, 20);

        long duration;
        long ictsTotalDuration = 0;
        long astarTotalDuration = 0;
        long cbsTotalDuration = 0;
        long cbswpTotalDuration = 0;

        int iterations = 10;

        for (int i = 0; i < iterations; i++) {
            // ICTS
//            System.out.println("#################### ICTS ####################");
//            long startTimeICTS = System.nanoTime();
//            MAPFSolver solverICTS = new MAPFSolver(scenarioICTS, "ICTS");
//            solverICTS.solve();
//            long endTimeICTS = System.nanoTime();
//            duration = endTimeICTS - startTimeICTS;
//            ictsTotalDuration += duration;
//            System.out.println("\nExecution time ICTS: " + (duration / 1000000.0) + " ms");

            System.out.println();

            // CBS
            System.out.println("#################### CBS ####################");
            long startTimeCBS = System.nanoTime();
            MAPFSolver solverCBS = new MAPFSolver(scenarioCBS, "CBS");
            solverCBS.solve();
            long endTimeCBS = System.nanoTime();
            duration = endTimeCBS - startTimeCBS;
            cbsTotalDuration += duration;
//            System.out.println("\nExecution time CBS: " + (duration / 1000000.0) + " ms");

            // CBSwP
            System.out.println("#################### CBSwP ####################");
            long startTimeCBSwP = System.nanoTime();
            MAPFSolver solverCBSwP = new MAPFSolver(scenarioCBSwP, "CBSwP");
            solverCBSwP.solve();
            long endTimeCBSwP = System.nanoTime();
            duration = endTimeCBSwP - startTimeCBSwP;
            cbswpTotalDuration += duration;
//            System.out.println("\nExecution time CBSwP: " + (duration / 1000000.0) + " ms");
        }

        System.out.println();

//        System.out.println("Average ICTS execution time: " + ((ictsTotalDuration / 1000000.0) / iterations));
//        System.out.println("Average A* execution time: " + ((astarTotalDuration / 1000000.0) / iterations));
        System.out.println("Average CBS execution time: " + ((cbsTotalDuration / 1000000.0) / iterations));
        System.out.println("Average CBSw/P execution time: " + ((cbswpTotalDuration / 1000000.0) / iterations));

        for (int i = 0; i < iterations; i++) {
            // ICTS
            System.out.println("#################### ICTS ####################");
            long startTimeICTS = System.nanoTime();
            MAPFSolver solverICTS = new MAPFSolver(scenarioICTS, "ICTS");
            solverICTS.solve();
            long endTimeICTS = System.nanoTime();
            duration = endTimeICTS - startTimeICTS;
            ictsTotalDuration += duration;
//            System.out.println("\nExecution time A*: " + (duration / 1000000.0) + " ms");

            System.out.println();
        }

        System.out.println("Average ICTS execution time: " + ((ictsTotalDuration / 1000000.0) / iterations));

        for (int i = 0; i < iterations; i++) {
            // A*
            System.out.println("#################### A* ####################");
            long startTimeAstar = System.nanoTime();
            MAPFSolver solverAstar = new MAPFSolver(scenarioAstar, "astar");
            solverAstar.solve();
            long endTimeAstar = System.nanoTime();
            duration = endTimeAstar - startTimeAstar;
            astarTotalDuration += duration;
//            System.out.println("\nExecution time A*: " + (duration / 1000000.0) + " ms");

            System.out.println();
        }

        System.out.println("Average A* execution time: " + ((astarTotalDuration / 1000000.0) / iterations));
    }
}