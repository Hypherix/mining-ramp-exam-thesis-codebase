package org.example;

import java.util.*;

/*
* NOTES!!
* passBaysAdjVertex holds the ACTUAL RAMP vertex IDs. In other words, if passBay[x] = 2, then we mean adjacent to the second
* vertex in the ACTUAL ramp (i.e. disregarding the queues)
*
*
* */


public class Ramp {

    // Data members
    private int rampLength;                     // length of actual ramp
    private int surfaceQLength;
    private int undergroundQLength;
    private int[] passBaysAdjVertex;                     // list of vertexIDs (only considering the actual ramp) that the passing bays are adjacent to

    private int verticesInRamp;
    private int surfaceQFree;
    private int undergroundQFree;

    // Data members to keep track of which vertices are what in the ramp
    private ArrayList<Integer> verticesInSurfaceQ;
    private ArrayList<Integer> verticesInActualRamp;
    private ArrayList<Integer> verticesInUndergroundQ;
    private ArrayList<Integer> passingBays;
    private int surfaceStart;
    private int undergroundStart;
    private int surfaceExit;
    private int undergroundExit;

    private HashMap<Integer, UpDownNeighbourList> adjList;      // adjacency list to keep track of edges

    // Information about f, g and h values for each vertex in the ramp
//    HashMap<Integer, int[]> fghUpgoing;
//    HashMap<Integer, int[]> fghDowngoing;
    HashMap<Integer, Integer> hUpgoing;
    HashMap<Integer, Integer> hDowngoing;


    // Constructors
    public Ramp(int rampLength, int surfaceQLength, int undergroundQLength, int[] passBays) {
        // Update data members
        this.rampLength = rampLength;
        this.surfaceQLength = surfaceQLength;
        this.undergroundQLength = undergroundQLength;
        this.passBaysAdjVertex = passBays;
        this.surfaceStart = surfaceQLength;
        this.undergroundStart = surfaceQLength + rampLength - 1;

        // Initialise the adjacency list which represents the ramp
        this.adjList = new HashMap<>();
        initialiseAdjList(rampLength, surfaceQLength, undergroundQLength, passBaysAdjVertex);

        // Categorise vertices depending on what part of the ramp they are in
        categoriseVertices();

        // Calculate and store fgh values for the vertices
//        fghUpgoing = new HashMap<>();
//        fghDowngoing = new HashMap<>();
        hUpgoing = new HashMap<>();
        hDowngoing = new HashMap<>();
        seth();
    }

    // Methods
    public void printAdjList() {
        // Task: Print the adjacency list
        System.out.print("{");
        for(Integer key : this.adjList.keySet()) {
            System.out.print(key + "=");
            adjList.get(key).printNeighbourLists();
            System.out.print(", ");
        }
        System.out.print("}");
    }

    private void addVertexToRamp(int vertexId) {
        // Task: Add a vertex to the ramp
        this.adjList.put(vertexId, new UpDownNeighbourList());
    }

    private void addUpEdge(int fromVertex, int toVertex) {
        // Task: Add an edge to the ramp
        this.adjList.get(fromVertex).getUpNeighbours().add(toVertex);
    }

    private void addDownEdge(int fromVertex, int toVertex) {
        // Task: Add an edge to the ramp
        this.adjList.get(fromVertex).getDownEdges().add(toVertex);
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
            addDownEdge(verticesInRamp - 1, verticesInRamp);
            verticesInRamp++;
        }

        // Add the actual ramp to the adjacency list. Note: undirected subgraph!
        for (int i = 0; i < rampLength; i++) {
            addVertexToRamp(verticesInRamp);
            addDownEdge(verticesInRamp - 1, verticesInRamp);    // Connect surface queue to surface start
            if (i != 0) {                // Don't create backwards edge in the first ramp vertex
                addUpEdge(verticesInRamp, verticesInRamp - 1);
            }
            verticesInRamp++;
        }

        // Add the underground queue to the adjacency list. Note: directed subgraph!
        for (int i = 0; i < undergroundQLength; i++) {
            addVertexToRamp(verticesInRamp);
            addUpEdge(verticesInRamp, verticesInRamp - 1);
            verticesInRamp++;
        }

        // Add passing bays. Add edge to corresponding edges in the ramp
        int currentPassBay = 0;
        for (int i = 0; i < passBays.length; i++) {
            addVertexToRamp(verticesInRamp);
            addDownEdge(surfaceQLength + passBays[currentPassBay] - 2, verticesInRamp);      // -2 needed for adjustment
            addUpEdge(verticesInRamp, surfaceQLength + passBays[currentPassBay] - 2);
            addUpEdge(surfaceQLength + passBays[currentPassBay], verticesInRamp);
            addDownEdge(verticesInRamp, surfaceQLength + passBays[currentPassBay]);
            addUpEdge(verticesInRamp, verticesInRamp);            // Agents can wait in the passing bay
            addDownEdge(verticesInRamp, verticesInRamp);
            currentPassBay++;
            verticesInRamp++;
        }

        // Add the surface exit vertex to the adjacency list
        addVertexToRamp(verticesInRamp);
        addUpEdge(surfaceQLength, verticesInRamp);    // Add edge from first ramp vertex to the surface exit vertex
        this.surfaceExit = verticesInRamp;
        verticesInRamp++;

        // Add the underground exit vertex to the adjacency list
        addVertexToRamp(verticesInRamp);
        addDownEdge(surfaceQLength + rampLength - 1, verticesInRamp);
        this.undergroundExit = verticesInRamp;
        verticesInRamp++;

        // Set first free slot in surface queue and underground queue
        surfaceQFree = surfaceStart;
        undergroundQFree = undergroundStart;
    }


    private void categoriseVertices() {
        // Task: Categorise vertices depending on what part of the ramp they are in

        int currentVertex = 0;

        // Surface queue
        verticesInSurfaceQ = new ArrayList<>();
        for(int i = 0; i < surfaceQLength; i++){
            verticesInSurfaceQ.add(i);
        }
        currentVertex += surfaceQLength;

        // The actual ramp
        verticesInActualRamp = new ArrayList<>();
        for (int i = 0; i < rampLength; i++) {
            verticesInActualRamp.add(currentVertex++);
        }

        // Underground queue
        verticesInUndergroundQ = new ArrayList<>();
        for (int i = 0; i < undergroundQLength; i++) {
            verticesInUndergroundQ.add(currentVertex++);
        }

        // Passing bays
        passingBays = new ArrayList<>();
        for (int i = 0; i < passBaysAdjVertex.length; i++) {
            passingBays.add(currentVertex++);
        }

        // Surface exit and underground exit
        surfaceExit = currentVertex++;
        undergroundExit = currentVertex++;
    }


    private HashMap<Integer, Integer> getVerticesCosts(int sourceVertex) {
        // Task: Get the vertices costs (= their generation from a starting vertex)

        HashMap<Integer, Integer> vertexGeneration = new HashMap<>();

        Queue<Integer> frontierVertex = new LinkedList<>();      // Keeps track of vertices in frontier
        HashMap<Integer, ArrayList<Integer>> frontierNeighbours = new HashMap<>();  // Maps frontier vertices to neighbours
        ArrayList<Integer> explored = new ArrayList<>();            // Keeps track of explored vertices

        // It is enough to start from the surface start vertex. fgh in downgoing and upgoing direction can be attained
        // from this

        // Add surface vertex to frontier and explored
        frontierVertex.add(sourceVertex);
        frontierNeighbours.put(sourceVertex, adjList.get(sourceVertex).getAllNeighbours());
        explored.add(sourceVertex);
        vertexGeneration.put(sourceVertex, 0);      // surface vertex is the first generation

        int currentVertex;
        ArrayList<Integer> currentNeighbours = new ArrayList<>();

        // Get costs of all vertices
        while(!frontierVertex.isEmpty()){
            currentVertex = frontierVertex.poll();              // Dequeue vertex first in queue
            explored.add(currentVertex);                        // Mark as explored
            currentNeighbours = frontierNeighbours.get(currentVertex);  // Get its neighbours

            int currentGeneration = vertexGeneration.get(currentVertex);    // Get current

            for(Integer neighbour : currentNeighbours) {
                // Only add neighbour to frontier if it hasn't already been explored or is already in the frontier
                if (!explored.contains(neighbour) && !frontierVertex.contains(neighbour)) {
                    frontierVertex.add(neighbour);

                    ArrayList<Integer> neighboursOfNeighbour = adjList.get(neighbour).getAllNeighbours();

                    frontierNeighbours.put(neighbour, neighboursOfNeighbour);

                    // Set the generation of the neighbour (+ 1 from the parent)
                    vertexGeneration.put(neighbour, currentGeneration + 1);
                }
            }
        }

        // Manually assign costs/generations to surface queue and underground queue
        // Surface queue
        for(int i = 0; i < surfaceStart; i++) {
            vertexGeneration.put(i, sourceVertex - i);
        }

        // Underground queue
        // Since assigning fgh values assumes the surface start vertex as the source vertex, this must be used
        // here as well
        for(int i = undergroundStart + 1; i < undergroundStart + undergroundQLength + 1; i++) {
            vertexGeneration.put(i, i - sourceVertex);
        }

        return vertexGeneration;
    }


    private void assignh(int vertex, HashMap<Integer, Integer> vertexGeneration, int direction) {
        // Task: Assign h values to a vertex based on its generation/cost
        // Note! Some fgh values for queue vertices will make no sense, but they will not be used in the program

        // Depending on direction, populate downgoing or upgoing fgh values
        if (direction == Constants.DOWN) {
//            fghDowngoing.put(vertex, new int[]{0, 0, 0});

            // If a surface queue vertex, g is always 0, h is h, and f = h, since each of these could be the start vertex
            if(verticesInSurfaceQ.contains(vertex)) {
//                fghDowngoing.get(vertex)[1] = 0;        // g is always 0 for surface queue vertices
//                fghDowngoing.get(vertex)[2] = undergroundStart - vertex;   // h is the distance from the vertex to underground start
//                fghDowngoing.get(vertex)[0] = fghDowngoing.get(vertex)[1] + fghDowngoing.get(vertex)[2];    // f = g + h
                hDowngoing.put(vertex, undergroundStart - vertex); // h is the distance from the vertex to underground start
            }
            else {
                int gDowngoing = vertexGeneration.get(vertex);       // Get current vertex's downgoing g
//                fghDowngoing.get(vertex)[1] = gDowngoing;            // g is the second value array element
//                fghDowngoing.get(vertex)[2] = rampLength - 1 - gDowngoing;  // from g we can get h
//                fghDowngoing.get(vertex)[0] = fghDowngoing.get(vertex)[1] + fghDowngoing.get(vertex)[2];    // f = g + h
                hDowngoing.put(vertex, rampLength - 1 - gDowngoing);
            }
        }
        // If an underground queue vertex, g is always 0, h is h, and f = h, since each of these could be the start vertex
        else if (direction == Constants.UP) {
//            fghUpgoing.put(vertex, new int[]{0, 0, 0});

            if(verticesInUndergroundQ.contains(vertex)) {
//                fghUpgoing.get(vertex)[1] = 0;      // g is always 0 for underground queue vertices
//                fghUpgoing.get(vertex)[2] = vertex - surfaceStart;     // h is the distance from the vertex to surface start
//                fghUpgoing.get(vertex)[0] = fghUpgoing.get(vertex)[1] + fghUpgoing.get(vertex)[2];    // f = g + h
                hUpgoing.put(vertex, vertex - surfaceStart);    // h is the distance from the vertex to surface start
            }
            else {
                int gUpgoing = vertexGeneration.get(vertex);       // Get current vertex's downgoing g
//                fghUpgoing.get(vertex)[1] = gUpgoing;            // g is the second value array element
//                fghUpgoing.get(vertex)[2] = rampLength - 1 - gUpgoing;  // from g we can get h
//                fghUpgoing.get(vertex)[0] = fghUpgoing.get(vertex)[1] + fghUpgoing.get(vertex)[2];    // f = g + h
                hUpgoing.put(vertex, rampLength - 1 - gUpgoing);
            }
        }
        else {
            System.out.println("UNKNOWN DIRECTION WHEN ASSIGNING FGH VALUES!");
        }
    }


    public void seth() {
        // Task: Set the h values of all vertices.
        // Note! h values are set to queue and exit vertices as well. Some of these might technically have
        // incorrect values, but they do not matter for the program to work. E.g. a surface queue's vertex's
        // upgoing h values are nonsensical since only downgoing agents will occupy such a vertex

        // First, get costs of all vertices (= their generation)
        // Keep track of each vertex's generation
        HashMap<Integer, Integer> vertexGenerationSurfaceSource = getVerticesCosts(surfaceStart);
        HashMap<Integer, Integer> vertexGenerationUndergroundSource = getVerticesCosts(undergroundStart);

        // With the generations set to each vertex, assign h values
        for(Map.Entry<Integer, Integer> entry : vertexGenerationSurfaceSource.entrySet()) {
            int vertex = entry.getKey();
            assignh(vertex, vertexGenerationSurfaceSource, Constants.DOWN);
            assignh(vertex, vertexGenerationUndergroundSource, Constants.UP);
        }
    }


    HashMap<Integer, UpDownNeighbourList> getAdjList() {
        return this.adjList;
    }

    public int getSurfaceStart() {
        return this.surfaceStart;
    }

    public int getUndergroundStart() {
        return this.undergroundStart;
    }

    public int getRampLength() {
        return this.rampLength;
    }

//    public int getSurfaceQFree() {
//        return this.surfaceQFree;
//    }
//
//    public void setSurfaceQFree(int vertex) {
//        this.surfaceQFree = vertex;
//    }
//
//    public int getUndergroundQFree() {
//        return this.undergroundQFree;
//    }
//
//    public void setUndergroundQFree(int vertex) {
//        this.undergroundQFree = vertex;
//    }

    public ArrayList<Integer> getVerticesInSurfaceQ() {
        return this.verticesInSurfaceQ;
    }

    public ArrayList<Integer> getVerticesInUndergroundQ() {
        return this.verticesInUndergroundQ;
    }

    public int getSurfaceExit() {
        return this.surfaceExit;
    }

    public int getUndergroundExit() {
        return this.undergroundExit;
    }

}
