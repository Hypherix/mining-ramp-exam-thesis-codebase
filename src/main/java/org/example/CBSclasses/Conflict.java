package org.example.CBSclasses;

/*
* Groups together vertex and edge conflicts for CTNode (and MAPFScenario)
* */

import org.example.*;

public class Conflict {

    // Data members
    public Agent agent1;
    public Agent agent2;
    public int timeStep;
    public int vertex;      // Used for vertex conflicts
    public int fromVertex;  // Used for edge conflicts
    public int toVertex;    // Used for edge conflicts
    public ConflictType type;

    public enum ConflictType {
        VERTEX,
        EDGE
    }


    // Vertex conflict constructor
    public Conflict(Agent agent1, Agent agent2, int timeStep, int vertex) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.timeStep = timeStep;
        this.vertex = vertex;
        this.type = ConflictType.VERTEX;
    }

    // Edge conflict constructor
    public Conflict(Agent agent1, Agent agent2, int timeStep, int fromVertex, int toVertex) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.timeStep = timeStep;
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.type = ConflictType.EDGE;
    }


    // Methods
}
