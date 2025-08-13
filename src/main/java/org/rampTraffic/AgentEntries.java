package org.rampTraffic;

import java.util.ArrayList;
import java.util.HashMap;

public class AgentEntries {

    // Data members
    HashMap<Integer, ArrayList<Agent>> entries;     // timeStep, agents entering

    // Constructors
    public AgentEntries() {
        entries = new HashMap<>();
    }

    // Methods
    public void addEntry(int timeStep, Agent agent) {
        if(!entries.containsKey(timeStep)) {
            entries.put(timeStep, new ArrayList<>());
        }
        entries.get(timeStep).add(agent);
    }

    public HashMap<Integer, ArrayList<Agent>> getEntries() {
        return this.entries;
    }
}
