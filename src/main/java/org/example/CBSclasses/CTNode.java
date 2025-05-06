package org.example.CBSclasses;

/*
* Comparable to ICTNode for ICTS
* */

import org.example.Agent;
import org.example.MAPFState;

import java.util.*;

public class CTNode {

    // Data members
    public int cost;
    public Agent newlyConstrainedAgent;
    public ArrayList<CTNode> children;

    // Stores all agent paths
    public HashMap<Agent, ArrayList<Integer>> agentPaths;

    // <Agent, <timeStep, prohibitedVertexSet>>
    public HashMap<Agent, HashMap<Integer, Set<Integer>>> vertexConstraints;

    // <Agent, <timeStep, set of (fromVertex, toVertex)>>
    public HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> edgeConstraints;



    // Constructors
    public CTNode() {
        this.cost = 0;
        this.agentPaths = new HashMap<>();
        this.vertexConstraints = new HashMap<>();
        this.edgeConstraints = new HashMap<>();
        this.children = new ArrayList<>();
    }

    public CTNode(HashMap<Agent, HashMap<Integer, Set<Integer>>> vertexConstraints,
                  HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> edgeConstraints) {
        // Used for generating children where constraints are inherited

        this.cost = 0;
        this.agentPaths = new HashMap<>();
        this.vertexConstraints = vertexConstraints;
        this.edgeConstraints = edgeConstraints;
        this.children = new ArrayList<>();
    }


    // Methods
    public void addAgentPath(Agent agent, ArrayList<Integer> path) {
        this.agentPaths.put(agent, path);
    }

    public void addVertexConstraint(Agent agent, int vertex, int timeStep) {
        this.vertexConstraints
                .computeIfAbsent(agent, k -> new HashMap<>())
                .computeIfAbsent(timeStep, k -> new HashSet<>())
                .add(vertex);
    }

    public HashMap<Agent, HashMap<Integer, Set<Integer>>> getVertexConstraints() {
        return this.vertexConstraints;
    }

    public void addEdgeConstraint(Agent agent, int fromVertex, int toVertex, int timeStep) {
        ArrayList<Integer> edge = new ArrayList<>();
        edge.add(fromVertex);
        edge.add(toVertex);

        this.edgeConstraints
                .computeIfAbsent(agent, k -> new HashMap<>())
                .computeIfAbsent(timeStep, k -> new HashSet<>())
                .add(edge);
    }

    public HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> getEdgeConstraints() {
        return this.edgeConstraints;
    }


}
