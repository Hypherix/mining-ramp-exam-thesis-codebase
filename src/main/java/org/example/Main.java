package org.example;

/*
* TODO LATER: Increase passing bay node count to simulate it being more costly
* */

public class Main {
    public static void main(String[] args) {
        int[] passBays = {2, 4};
        Ramp myRamp = new Ramp(5, 3, 3, passBays);
        myRamp.printAdjList();

        // This section should be equivalent to the section after (now commented)
        // Add initial agents
        // Every other agent goes the same direction
        AgentEntries agentEntries = new AgentEntries();
        for(int i = 0; i < 3; i++) {
            if(i % 2 == 0) {
                agentEntries.addEntry(0, new Agent(i, 1, Constants.DOWN));
            }
            else {
                agentEntries.addEntry(0, new Agent(i, 1, Constants.UP));
            }
        }

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

        MAPFScenario scenario = new MAPFScenario(myRamp, agentEntries, 5);
        MAPFSolver solver = new MAPFSolver(scenario, "astar");
        solver.solve();
    }
}