package org.example;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Holds information regarding new agents entering a scenario.
 * Required as parameter for creating a MAPFScenario.
 * Format: (timeStep, ArrayList<{direction, velocity}>)
 * Direction = 1 means upgoing. Direction = 0 means downgoing.
 * */
public class AgentEntries {

    // Data members
    HashMap<Integer, ArrayList<int[]>> entries;

    // Constructors
    public AgentEntries() {
        entries = new HashMap<Integer, ArrayList<int[]>>();
    }

    // Methods
    public void addEntry(int timeStep, String direction, int velocity) {
        // Task: Add an entry to entries hashmap

        // Convert direction string to int
        int directionInt;
        direction = direction.toLowerCase();
        if (direction.equals("up")) {
            directionInt = Constants.UP;
        }
        else if (direction.equals("down")) {
            directionInt = Constants.DOWN;
        }
        else {
            System.out.println("UNKNOWN DIRECTION, CAN'T ADD TO AGENT ENTRIES!");
            directionInt = -1;
        }

        // If there is no entry for the specified timeStep, add it
        if(!entries.containsKey(timeStep)) {
            entries.put(timeStep, new ArrayList<>());
        }

        // Add the entry
        int[] agentInfo = new int[]{directionInt, velocity};
        entries.get(timeStep).add(agentInfo);
    }

    public HashMap<Integer, ArrayList<int[]>> getEntries() {
        return this.entries;
    }
}
