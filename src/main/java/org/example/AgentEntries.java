package org.example;

import java.util.ArrayList;
import java.util.HashMap;

/*
 * Holds information regarding new agents entering a scenario.
 * Required as parameter for creating a MAPFScenario.
 * Format: (timeStep, ArrayList<Agent>)
 * Direction = 1 means upgoing. Direction = 0 means downgoing.
 * */
public class AgentEntries {

    // Data members
    HashMap<Integer, ArrayList<Agent>> entries;

    // Constructors
    public AgentEntries() {
        entries = new HashMap<Integer, ArrayList<Agent>>();
    }

    // Methods
    public void addEntry(int timeStep, Agent agent) {
        // Task: Add an entry to entries hashmap

        // If there is no entry for the specified timeStep, add it
        if(!entries.containsKey(timeStep)) {
            entries.put(timeStep, new ArrayList<>());
        }

        // Add the entry
        entries.get(timeStep).add(agent);
    }

    public HashMap<Integer, ArrayList<Agent>> getEntries() {
        return this.entries;
    }
}
