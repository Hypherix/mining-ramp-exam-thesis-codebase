package org.rampTraffic;

import org.rampTraffic.visualiser.MAPFVisualiser;

/*
 * The way it works at the moment is that an agent must be created, then inserted
 * in to an agent entries map. The agent entries map, together with a ramp, is then
 * used to create a MAPFScenario. The MAPFScenario is fed to a MAPFAlgorithm.
*/

public class Main {
    public static void main(String[] args) {

        // Design the ramp
        int[] passBays = {2};       // the array numbers specify the ramp vertex that the pass bay will be adjacent to
        Ramp myRamp = new Ramp(7, 5, 5, passBays);

        // Set up agents and their arrivals
        AgentEntries agentEntries = new AgentEntries();
        int agentCount = 3;
        for(int i = 0; i < agentCount; i++) {
            Agent agent;
            if(i % 2 == 0) {
                agent = new Agent(i, 1, Constants.DOWN, true, false);
            }
            else {
                agent = new Agent(i, 1, Constants.UP, true, false);
            }
            agentEntries.addEntry(0, agent);
        }
        Agent agent2 = new Agent(agentCount++, 1, Constants.DOWN, true, false);
        agentEntries.addEntry(1, agent2);
        agent2 = new Agent(agentCount++, 1, Constants.UP, true, true);
        agentEntries.addEntry(5, agent2);
        agent2 = new Agent(agentCount++, 1, Constants.DOWN, true, false);
        agentEntries.addEntry(18, agent2);

        // Instantiate scenarios
        MAPFScenario scenarioICTS = new MAPFScenario(myRamp, agentEntries, 20);
        MAPFScenario scenarioAstar = new MAPFScenario(myRamp, agentEntries, 20);
        MAPFScenario scenarioCBS = new MAPFScenario(myRamp, agentEntries, 20);
        MAPFScenario scenarioCBSwP = new MAPFScenario(myRamp, agentEntries, 20);

        // Invoke algorithms

        boolean prioritise = false;         // toggle algorithms to prioritise

        System.out.println();

        // CBSwP
        System.out.println("#################### CBSwP ####################");
        MAPFSolver solverCBSwP = new MAPFSolver(scenarioCBSwP, "CBSwP");
        long startTimeCBSwP = System.nanoTime();
        MAPFSolution cbswpSolution = solverCBSwP.solve(prioritise);
        long endTimeCBSwP = System.nanoTime();
        long cbswpDuration = endTimeCBSwP - startTimeCBSwP;
        cbswpSolution.setObtainTime(cbswpDuration);
        System.out.println("\nExecution time CBSw/P: " + cbswpSolution.getObtainTime() + " ms");

        System.out.println();

        // A*
        System.out.println("#################### A* ####################");
        MAPFSolver solverAstar = new MAPFSolver(scenarioAstar, "astar");
        long startTimeAstar = System.nanoTime();
        MAPFSolution astarSolution = solverAstar.solve(prioritise);
        long endTimeAstar = System.nanoTime();
        long astarDuration = endTimeAstar - startTimeAstar;
        astarSolution.setObtainTime(astarDuration);
        System.out.println("\nExecution time A*: " + astarSolution.getObtainTime() + " ms");

        System.out.println();

        // ICTS
        System.out.println("#################### ICTS ####################");
        MAPFSolver solverICTS = new MAPFSolver(scenarioICTS, "ICTS");
        long startTimeICTS = System.nanoTime();
        MAPFSolution ictsSolution = solverICTS.solve(true);
        long endTimeICTS = System.nanoTime();
        long ictsDuration = endTimeICTS - startTimeICTS;
        ictsSolution.setObtainTime(ictsDuration);
        System.out.println("\nExecution time ICTS: " + ictsSolution.getObtainTime() + " ms");

        System.out.println();

        // CBS
        System.out.println("#################### CBS ####################");
        MAPFSolver solverCBS = new MAPFSolver(scenarioCBS, "CBS");
        long startTimeCBS = System.nanoTime();
        MAPFSolution cbsSolution = solverCBS.solve(prioritise);
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