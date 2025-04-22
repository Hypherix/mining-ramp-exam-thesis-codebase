package org.example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

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

    // Information about f, g and h values for each vertex in the ramp
    HashMap<Integer, int[]> fghUpgoing;
    HashMap<Integer, int[]> fghDowngoing;

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

        // Calculate and store fgh values for the vertices
        fghUpgoing = new HashMap<>();
        fghDowngoing = new HashMap<>();
        setfgh();
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


    public void setfgh() {
        // Task: Set the fgh values of all vertices

        Queue<Integer> frontierVertex = new LinkedList<>();      // Keeps track of vertices in frontier
        HashMap<Integer, ArrayList<Integer>> frontierNeighbours = new HashMap<>();  // Maps frontier vertices to neighbours
        ArrayList<Integer> explored = new ArrayList<>();            // Keeps track of explored vertices
        HashMap<Integer, Integer> vertexGeneration = new HashMap<>();   // Keeps track of each vertex's generation


        // It is enough to start from the surface start vertex. fgh in downgoing and upgoing direction can be attained
        // from this

        // Add surface vertex to frontier and explored
        frontierVertex.add(surfaceStart);
        frontierNeighbours.put(surfaceStart, adjList.get(surfaceStart));
        explored.add(surfaceStart);
        vertexGeneration.put(surfaceStart, 0);      // surface vertex is the first generation

        int round = 0;
        int currentVertex;
        ArrayList<Integer> currentNeighbours = new ArrayList<>();

        // Get costs of all vertices
        while(!frontierVertex.isEmpty()){
            currentVertex = frontierVertex.poll();              // Dequeue vertex first in queue
            currentNeighbours = frontierNeighbours.get(currentVertex);  // Get its neighbours

            int currentGeneration = vertexGeneration.get(currentVertex);    // Get current

            for(Integer neighbour : currentNeighbours) {
                // Only add neighbour to frontier if it hasn't already been explored or is already in the frontier
                if (!explored.contains(neighbour) && !frontierVertex.contains(neighbour)) {
                    frontierVertex.add(neighbour);
                    frontierNeighbours.put(neighbour, adjList.get(neighbour));

                    // Set the generation of the neighbour (+ 1 from the parent)
                    vertexGeneration.put(neighbour, currentGeneration + 1);
                }
            }
        }

        // With the generations set to each vertex, assign fgh values
        // Go through all actual ramp vertices. Get their cost and assign
        for(int i = surfaceStart; i < undergroundStart; i++) {
            int gDowngoing = vertexGeneration.get(i);
            fghDowngoing.get(i)[1] = gDowngoing;            // g is the second value array element

            // Since g in downgoing direction is the same as h in the upgoing direction, assign that too
            fghUpgoing.get(i)[2] = gDowngoing;

            // For every vertex in the actual ramp; if g in downgoing direction is 1 and the actual ramp length
            // is 5, then g in upgoing direction is always (actualRampLength - 1) - gDowngoing.
            fghUpgoing.get(i)[1] = rampLength - 1 - gDowngoing;
            fghDowngoing.get(i)[2] = rampLength - 1 - gDowngoing;

            // Assign f value as sum of g and h
            fghDowngoing.get(i)[0] = fghDowngoing.get(i)[1] + fghDowngoing.get(i)[2];
            fghUpgoing.get(i)[0] = fghUpgoing.get(i)[1] + fghUpgoing.get(i)[2];
        }
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
