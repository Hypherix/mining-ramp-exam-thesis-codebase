package org.rampTraffic.CBSclasses;

/*
* Groups together vertex and edge conflicts for CTNode (and MAPFScenario)
* */

import org.rampTraffic.*;

public class Conflict {

    // Data members
    public Agent agent1;
    public Agent agent2;
    public int timeStep;
    public int vertex;      // Used for vertex conflicts
    public int fromVertex;  // Used for edge conflicts
    public int toVertex;    // Used for edge conflicts
    public boolean isEdgeConflict;
    public ConflictType type;

    public enum ConflictType {
        VERTEX,
        EDGE_OR_DIFF_DIRECTION_PASSBAY,
        PASSBAY_SAME_DIRECTION,
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
    // or passing bay conflict in different directions
    public Conflict(Agent agent1, Agent agent2, int timeStep, int fromVertex, int toVertex, boolean edgeConflict) {
        this.agent1 = agent1;
        this.agent2 = agent2;
        this.timeStep = timeStep;
        this.fromVertex = fromVertex;
        this.toVertex = toVertex;
        this.isEdgeConflict = edgeConflict;
        this.type = ConflictType.EDGE_OR_DIFF_DIRECTION_PASSBAY;
    }

    // Passing bay conflict in same direction constructor
    public Conflict(Agent laterAgent, int timeStep, int vertex) {
        this.agent1 = laterAgent;
        this.timeStep = timeStep;
        this.vertex = vertex;
        this.type = ConflictType.PASSBAY_SAME_DIRECTION;
    }


    // Methods
}
