package org.example;

import java.util.ArrayList;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        int[] passBays = {2, 4};
        Ramp myRamp = new Ramp(5, 3, 3, passBays);
        myRamp.printAdjList();

        HashMap<Integer, Integer> agentLocations = new HashMap<>();
        HashMap<Integer, Integer> agentVelocities = new HashMap<>();

        agentLocations.put(0, 3);       // agent starting from the surface
        agentVelocities.put(0, 1);
        agentLocations.put(1, 7);       // agent starting from the underground
        agentVelocities.put(1, 1);

        MAPFState initialState = new MAPFState(myRamp, agentLocations, agentVelocities);

        MAPFState initialState2 = new MAPFState(myRamp);

        HashMap<Integer, ArrayList<int[]>> newAgentLocationVelocity = new HashMap<Integer, ArrayList<int[]>>();
        int[] locationVelocity1 = new int[]{3, 1};
        int[] locationVelocity2 = new int[]{7, 1};
        ArrayList<int[]> list = new ArrayList<>();
        list.add(locationVelocity1);
        list.add(locationVelocity2);
        newAgentLocationVelocity.put(0, list);
        int[] locationVelocity3 = new int[]{3, 1};
        int[] locationVelocity4 = new int[]{7, 1};
        ArrayList<int[]> list2 = new ArrayList<>();
        list2.add(locationVelocity3);
        list2.add(locationVelocity4);
        newAgentLocationVelocity.put(2, list2);

        MAPFScenario scenario = new MAPFScenario(myRamp, newAgentLocationVelocity, 5);
        MAPFSolver solver = new MAPFSolver(scenario, "astar");
        solver.solve();
    }
}