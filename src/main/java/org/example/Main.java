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
*  DONE
*
* TODO: Agent prio option
*  Left is adding prio cost prints and showing it on the visualiser
*/

import org.example.visualiser.MAPFVisualiser;

import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        //long startTime = System.nanoTime();

        int[] passBays = {2, 5};
        Ramp myRamp = new Ramp(8, 5, 5, passBays);


        // ALL ALGORITHMS TEST
        HashMap<Integer, Agent> agentList2 = new HashMap<>();
        AgentEntries agentEntries2 = new AgentEntries();
        for(int i = 0; i < 3; i++) {
            Agent agent2;
            if(i % 2 == 0) {
                agent2 = new Agent(i, 1, Constants.DOWN, true, false);
            }
            else {
                agent2 = new Agent(i, 1, Constants.UP, true, false);
            }
            agentList2.put(agent2.id, agent2);
            agentEntries2.addEntry(0, agent2);
        }
        Agent agent2 = new Agent(3, 1, Constants.UP, false, false);
        agentList2.put(agent2.id, agent2);
        agentEntries2.addEntry(8, agent2);
        agent2 = new Agent(4, 1, Constants.DOWN, true, true);
        agentList2.put(agent2.id, agent2);
        agentEntries2.addEntry(5, agent2);
//        agent2 = new Agent(5, 1, Constants.UP, true, true);
//        agentList2.put(agent2.id, agent2);
//        agentEntries2.addEntry(10, agent2);
//        agent2 = new Agent(6, 1, Constants.DOWN, true, true);
//        agentList2.put(agent2.id, agent2);
//        agentEntries2.addEntry(11, agent2);

        MAPFScenario scenarioICTS = new MAPFScenario(myRamp, agentEntries2, 20);
        MAPFScenario scenarioAstar = new MAPFScenario(myRamp, agentEntries2, 20);
        MAPFScenario scenarioCBS = new MAPFScenario(myRamp, agentEntries2, 20);
        MAPFScenario scenarioCBSwP = new MAPFScenario(myRamp, agentEntries2, 20);

        long duration;




        System.out.println();

        // CBSwP
        System.out.println("#################### CBSwP ####################");
        MAPFSolver solverCBSwP = new MAPFSolver(scenarioCBSwP, "CBSwP");
        long startTimeCBSwP = System.nanoTime();
        MAPFSolution cbswpSolution = solverCBSwP.solve(false);
        long endTimeCBSwP = System.nanoTime();
        long cbswpDuration = endTimeCBSwP - startTimeCBSwP;
        cbswpSolution.setObtainTime(cbswpDuration);
        System.out.println("\nExecution time CBSw/P: " + cbswpSolution.getObtainTime() + " ms");

        System.out.println();

        // A*
        System.out.println("#################### A* ####################");
        MAPFSolver solverAstar = new MAPFSolver(scenarioAstar, "astar");
        long startTimeAstar = System.nanoTime();
        MAPFSolution astarSolution = solverAstar.solve(false);
        long endTimeAstar = System.nanoTime();
        long astarDuration = endTimeAstar - startTimeAstar;
        astarSolution.setObtainTime(astarDuration);
        System.out.println("\nExecution time A*: " + astarSolution.getObtainTime() + " ms");

        System.out.println();

        // ICTS
        System.out.println("#################### ICTS ####################");
        MAPFSolver solverICTS = new MAPFSolver(scenarioICTS, "ICTS");
        long startTimeICTS = System.nanoTime();
        MAPFSolution ictsSolution = solverICTS.solve(false);
        long endTimeICTS = System.nanoTime();
        long ictsDuration = endTimeICTS - startTimeICTS;
        ictsSolution.setObtainTime(ictsDuration);
        System.out.println("\nExecution time ICTS: " + ictsSolution.getObtainTime() + " ms");

        System.out.println();

        // CBS
        System.out.println("#################### CBS ####################");
        MAPFSolver solverCBS = new MAPFSolver(scenarioCBS, "CBS");
        long startTimeCBS = System.nanoTime();
        MAPFSolution cbsSolution = solverCBS.solve(false);
        long endTimeCBS = System.nanoTime();
        long cbsDuration = endTimeCBS - startTimeCBS;
        cbsSolution.setObtainTime(cbsDuration);
        System.out.println("\nExecution time CBS: " + cbsSolution.getObtainTime() + " ms");


        System.out.println("\nExecution time ICTS: " + Math.round(ictsSolution.getObtainTime()) + " ms, and cost: " + ictsSolution.getCost());
        System.out.println("Execution time A*: " + Math.round(astarSolution.getObtainTime()) + " ms, and cost: " + astarSolution.getCost());
        System.out.println("Execution time CBS: " + Math.round(cbsSolution.getObtainTime()) + " ms, and cost: " + cbsSolution.getCost());
        System.out.println("Execution time CBSwP: " + Math.round(cbswpSolution.getObtainTime()) + " ms, and cost: " + cbswpSolution.getCost());


        // Visualiser
        MAPFVisualiser visualiser = new MAPFVisualiser(myRamp, astarSolution, ictsSolution, cbsSolution, cbswpSolution);
    }
}