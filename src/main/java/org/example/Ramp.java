package org.example;

import java.util.ArrayList;
import java.util.HashMap;

/*
* NOTES!!
* passBays holds the ACTUAL RAMP vertex IDs. In other words, if passBay[x] = 2, then we mean adjacent to the second
* vertex in the ACTUAL ramp (i.e. disregarding the queues)
*
*
* */


public class Ramp {

    // Data members
    private int rampLength;                     // length of actual ramp
    private int surfaceQLength;
    private int undergroundQLength;
    private int[] passBays;                     // list of vertexIDs (only considering the actual ramp) that the passing bays are adjacent to
    private int surfaceStart;                      // needed?
    private int undergroundStart;                        // needed?
    private int surfaceExit;
    private int undergroundExit;
    private int verticesInRamp;
    private int surfaceQFree;
    private int undergroundQFree;

    private HashMap<Integer, ArrayList<Integer>> adjList;      // adjacency list to keep track of edges

    // Constructors
    public Ramp(int rampLength, int surfaceQLength, int undergroundQLength, int[] passBays) {
        // Update data members
        this.rampLength = rampLength;
        this.surfaceQLength = surfaceQLength;
        this.undergroundQLength = undergroundQLength;
        this.passBays = passBays;
        this.surfaceStart = surfaceQLength;
        this.undergroundStart = surfaceQLength + rampLength - 1;

        // Initialise the adjacency list which represents the ramp
        this.adjList = new HashMap<>();
        initialiseAdjList(rampLength, surfaceQLength, undergroundQLength, passBays);
    }

    // Methods
    void printAdjList() {
        // Task: Print the adjacency list
        System.out.println(this.adjList);
    }

    private void addVertexToRamp(int vertexId) {
        // Task: Add a vertex to the ramp
        this.adjList.put(vertexId, new ArrayList<Integer>());
    }

    private void addEdge(int fromVertex, int toVertex) {
        // Task: Add an edge to the ramp
        this.adjList.get(fromVertex).add(toVertex);
    }

    private void initialiseAdjList(int rampLength, int surfaceQLength, int undergroundQLength, int[] passBays) {
        // Task: Given length of the ramp, initialise the adjacency list

        verticesInRamp = 0;

        // Add the first vertex separately
        addVertexToRamp(0);
        verticesInRamp++;

        // Add the surface queue to the adjacency list. Note: directed subgraph!
        for (int i = 1; i < surfaceQLength; i++) {
            addVertexToRamp(verticesInRamp);
            addEdge(verticesInRamp - 1, verticesInRamp);
            verticesInRamp++;
        }

        // Add the actual ramp to the adjacency list. Note: undirected subgraph!
        for (int i = 0; i < rampLength; i++) {
            addVertexToRamp(verticesInRamp);
            addEdge(verticesInRamp - 1, verticesInRamp);
            if (i != 0) {                // Don't create backwards edge in the first ramp vertex
                addEdge(verticesInRamp, verticesInRamp - 1);
            }
            verticesInRamp++;
        }

        // Add the underground queue to the adjacency list. Note: directed subgraph!
        for (int i = 0; i < undergroundQLength; i++) {
            addVertexToRamp(verticesInRamp);
            addEdge(verticesInRamp, verticesInRamp - 1);
            verticesInRamp++;
        }

        // Add passing bays. Add edge to corresponding edges in the ramp
        int currentPassBay = 0;
        for (int i = 0; i < passBays.length; i++) {
            addVertexToRamp(verticesInRamp);
            addEdge(surfaceQLength + passBays[currentPassBay] - 2, verticesInRamp);      // -2 needed for adjustment
            addEdge(verticesInRamp, surfaceQLength + passBays[currentPassBay] - 2);
            addEdge(surfaceQLength + passBays[currentPassBay], verticesInRamp);
            addEdge(verticesInRamp, surfaceQLength + passBays[currentPassBay]);
            currentPassBay++;
            verticesInRamp++;
        }

        // Add the surface exit vertex to the adjacency list
        addVertexToRamp(verticesInRamp);
        addEdge(surfaceQLength, verticesInRamp);    // Add edge from first ramp vertex to the surface exit vertex
        this.surfaceExit = verticesInRamp;
        verticesInRamp++;

        // Add the underground exit vertex to the adjacency list
        addVertexToRamp(verticesInRamp);
        addEdge(surfaceQLength + rampLength - 1, verticesInRamp);
        this.undergroundExit = verticesInRamp;
        verticesInRamp++;

        // Set first free slot in surface queue and underground queue
        surfaceQFree = surfaceStart;
        undergroundQFree = undergroundStart;
    }


    HashMap<Integer, ArrayList<Integer>> getAdjList() {
        return this.adjList;
    }

    public int getSurfaceStart() {
        return this.surfaceStart;
    }

    public int getUndergroundStart() {
        return this.undergroundStart;
    }

    public int getVerticesInActualRamp() {
        return this.rampLength;
    }

    public int getSurfaceQFree() {
        return this.surfaceQFree;
    }

    public void setSurfaceQFree(int vertex) {
        this.surfaceQFree = vertex;
    }

    public int getUndergroundQFree() {
        return this.undergroundQFree;
    }

    public void setUndergroundQFree(int vertex) {
        this.undergroundQFree = vertex;
    }

}
