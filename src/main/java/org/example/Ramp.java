package org.example;

import java.sql.Array;
import java.util.ArrayList;
import java.util.HashMap;

/*
* NOTES!!
* passBays holds the ACTUAL RAMP vertex IDs. In other words, if passBay[x] = 2, then we mean adjacent to the second
* vertex in the ACTUAL ramp (disregarding the queues)
*
*
* */


public class Ramp {

    // Data members
    private int rampLength;             // length of actual ramp
    private int surfaceQLength;
    private int undergroundQLength;
    private int[] passBays;         // list of vertexIDs (only considering the actual ramp) that the passing bays are adjacent to
    private final int rampStart = surfaceQLength;
    private final int rampEnd = undergroundQLength;

    private HashMap<Integer, ArrayList<Integer>> adjList;      // adjacency list to keep track of edges

    // Constructors
    public Ramp(int rampLength, int surfaceQLength, int undergroundQLength, int[] passBays) {
        // Initialise the adjacency list which represents the ramp
        this.adjList = new HashMap<>();
        initialiseAdjList(rampLength, surfaceQLength, undergroundQLength, passBays);
    }

    // Methods
    void addVertexToRamp(int vertexId) {
        this.adjList.put(vertexId, new ArrayList<Integer>());
    }

    void addEdge(int fromVertex, int toVertex) {
        this.adjList.get(fromVertex).add(toVertex);
    }

    void initialiseAdjList(int rampLength, int surfaceQLength, int undergroundQLength, int[] passBays) {
        // Task: Given length of the ramp, initialise the adjacency list

        int verticesInRamp = 0;

        // Add the first vertex separately
        addVertexToRamp(0);
        verticesInRamp++;

        // Add the surface queue to the adjacency list. Note: directed subgraph!
        for(int i = verticesInRamp; i < surfaceQLength; i++) {
            addVertexToRamp(i);
            addEdge(i - 1, i);
        }

        // Add the actual ramp to the adjacency list. Note: undirected subgraph!
        for(int i = verticesInRamp; i < rampLength; i++) {
            addVertexToRamp(i);
            addEdge(i - 1, i);
            if(i != surfaceQLength) {           // Don't create backwards edge in the first ramp vertex
                addEdge(i, i - 1);
            }
        }

        // Add the underground queue to the adjacency list. Note: directed subgraph!
        for(int i = verticesInRamp; i < undergroundQLength; i++) {
            addVertexToRamp(i);
            addEdge(i, i - 1);
        }

        // Add passing bays. Add edge to corresponding edges in the ramp
        int currentPassBay = 0;
        for(int i = verticesInRamp; i < this.adjList.size() + passBays.length; i++) {
            addVertexToRamp(i);
            addEdge(surfaceQLength + passBays[currentPassBay] - 2, i);      // -2 needed for adjustment
            addEdge(i, surfaceQLength + passBays[currentPassBay] + 1);                       // +1 needed for adjustment
            currentPassBay++;
        }

        // Add the surface exit vertex to the adjacency list
        addVertexToRamp(verticesInRamp);
        addEdge(surfaceQLength, verticesInRamp);    // Add edge from first ramp vertex to the surface exit vertex
        verticesInRamp++;

        // Add the underground exit vertex to the adjacency list
        addVertexToRamp(verticesInRamp);
        addEdge(surfaceQLength + rampLength - 1, verticesInRamp);
        verticesInRamp++;
    }

    void printAdjList() {
        System.out.println(this.adjList);
    }
}
