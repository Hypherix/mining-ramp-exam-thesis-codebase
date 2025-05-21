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
    private ArrayList<Integer> passingBays;         // number of passing bays
    private ArrayList<Integer> verticesInPassingBays;   // ALL vertices that are in passing bays
    private ArrayList<ArrayList<Integer>> passingBayVertices;   // all passing bays and their respective vertices
    private ArrayList<Integer> secondPassBayVertices;       // all vertices closest to the underground in passing bays
    private int surfaceStart;
    private int undergroundStart;
    private int surfaceExit;
    private int undergroundExit;

    private HashMap<Integer, UpDownNeighbourList> adjList;      // adjacency list to keep track of edges

    // Information about f, g and h values for each vertex in the ramp
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
        this.verticesInPassingBays = new ArrayList<>();
        this.passingBayVertices = new ArrayList<>();
        this.secondPassBayVertices = new ArrayList<>();


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

        // Print ramp information
        printRampParts();
        System.out.println();
        printAdjList();
        System.out.println();
        printRampStructure();
    }

    // 5x5 grid constructor
    public Ramp(int gridLength) {
        this.adjList = new HashMap<>();
        this.hUpgoing = new HashMap<>();
        this.hDowngoing = new HashMap<>();
        this.verticesInSurfaceQ = new ArrayList<>();
        this.verticesInUndergroundQ = new ArrayList<>();
        this.verticesInPassingBays = new ArrayList<>();
        this.secondPassBayVertices = new ArrayList<>();
        this.passingBayVertices = new ArrayList<>();

        verticesInRamp = 0;

        // h values hard coded as if 5x5! gridLength must always be 5

        for (int i = 0; i < (gridLength * gridLength); i++) {
            addVertexToRamp(verticesInRamp++);
        }

        int verticesHandled = 0;
        for (int i = 0; i < gridLength; i++) {
            for (int j = 0; j < gridLength; j++) {

                // Upper left corner
                if(i == 0 && j == 0) {
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);
                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);
                    hDowngoing.put(verticesHandled, 3 + j);
                    hUpgoing.put(verticesHandled, 3 + j);

                    verticesHandled++;
                }

                // Upper edge
                else if(i == 0 && j < gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 3 + j);
                    hUpgoing.put(verticesHandled, 3 + j);

                    verticesHandled++;
                }

                // Upper right corner
                else if(i == 0 && j == gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 3 + j);
                    hUpgoing.put(verticesHandled, 3 + j);

                    verticesHandled++;
                }

                // Second row
                else if (i == 1 && j == 0) {
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 2 + j);
                    hUpgoing.put(verticesHandled, 2 + j);

                    verticesHandled++;
                }

                else if (i == 1 && j < gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 2 + j);
                    hUpgoing.put(verticesHandled, 2 + j);

                    verticesHandled++;
                }

                else if(i == 1 && j == gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 2 + j);
                    hUpgoing.put(verticesHandled, 2 + j);

                    verticesHandled++;
                }

                // Third row
                else if (i == 2 && j == 0) {
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 1 + j);
                    hUpgoing.put(verticesHandled, 1 + j);

                    verticesHandled++;
                }

                else if (i == 2 && j < gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 1 + j);
                    hUpgoing.put(verticesHandled, 1 + j);

                    verticesHandled++;
                }

                else if(i == 2 && j == gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 1 + j);
                    hUpgoing.put(verticesHandled, 1 + j);

                    verticesHandled++;
                }

                // Fourth row
                else if (i == 3 && j == 0) {
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 2 + j);
                    hUpgoing.put(verticesHandled, 2 + j);

                    verticesHandled++;
                }

                else if (i == 3 && j < gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 2 + j);
                    hUpgoing.put(verticesHandled, 2 + j);

                    verticesHandled++;
                }

                else if(i == 3 && j == gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addDownEdge(verticesHandled, verticesHandled + gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled + gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 2 + j);
                    hUpgoing.put(verticesHandled, 2 + j);

                    verticesHandled++;
                }

                // Fifth row
                // Lower left corner
                if(i == 4 && j == 0) {
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 3 + j);
                    hUpgoing.put(verticesHandled, 3 + j);

                    verticesHandled++;
                }

                // Upper edge
                else if(i == 4 && j < gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled + 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled + 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 3 + j);
                    hUpgoing.put(verticesHandled, 3 + j);

                    verticesHandled++;
                }

                // Lower right corner
                else if(i == 4 && j == gridLength - 1) {
                    addDownEdge(verticesHandled, verticesHandled - 1);
                    addDownEdge(verticesHandled, verticesHandled - gridLength);

                    addUpEdge(verticesHandled, verticesHandled - 1);
                    addUpEdge(verticesHandled, verticesHandled - gridLength);

                    addDownEdge(verticesHandled, verticesHandled);
                    addUpEdge(verticesHandled, verticesHandled);

                    hDowngoing.put(verticesHandled, 3 + j);
                    hUpgoing.put(verticesHandled, 3 + j);

                    verticesHandled++;
                }

                if (verticesHandled == verticesInRamp) {
                    // "Surface queue" of length 2
                    addVertexToRamp(verticesInRamp++);
                    addVertexToRamp(verticesInRamp++);
                    addDownEdge(verticesInRamp - 1, verticesInRamp - 2);
                    addDownEdge(verticesInRamp - 2, (gridLength * 2) - 1);

                    hDowngoing.put(verticesInRamp - 2, 8);
                    hUpgoing.put(verticesInRamp - 2, 8);
                    hDowngoing.put(verticesInRamp - 1, 7);
                    hUpgoing.put(verticesInRamp - 1, 7);

                    surfaceQFree = verticesInRamp - 2;
                    verticesInSurfaceQ.add(verticesInRamp - 2);
                    verticesInSurfaceQ.add(verticesInRamp - 1);

                    // "Underground queue" of length 2
                    addVertexToRamp(verticesInRamp++);
                    addVertexToRamp(verticesInRamp++);
                    addUpEdge(verticesInRamp - 1, verticesInRamp - 2);
                    addUpEdge(verticesInRamp - 2, (gridLength * 4) - 1);

                    hDowngoing.put(verticesInRamp - 2, 8);
                    hUpgoing.put(verticesInRamp - 2, 8);
                    hDowngoing.put(verticesInRamp - 1, 7);
                    hUpgoing.put(verticesInRamp - 1, 7);

                    undergroundQFree = verticesInRamp - 2;
                    verticesInUndergroundQ.add(verticesInRamp - 2);
                    verticesInUndergroundQ.add(verticesInRamp - 1);

                    // Add one universal exit node
                    addVertexToRamp(verticesInRamp++);
                    addDownEdge(gridLength * 2, verticesInRamp - 1);
                    addUpEdge(gridLength * 2, verticesInRamp - 1);
                    surfaceExit = verticesInRamp - 1;
                    undergroundExit = verticesInRamp - 1;
                    surfaceStart = (gridLength * 2) - 1;
                    undergroundStart = (gridLength * 4) - 1;

                    hDowngoing.put(verticesInRamp - 1, 0);
                    hUpgoing.put(verticesInRamp - 1, 0);
                }
            }
        }
    }

    // Copy constructor
    public Ramp(Ramp other) {
        // Copy primitives
        this.rampLength = other.rampLength;
        this.surfaceQLength = other.surfaceQLength;
        this.undergroundQLength = other.undergroundQLength;
        this.verticesInRamp = other.verticesInRamp;
        this.surfaceQFree = other.surfaceQFree;
        this.undergroundQFree = other.undergroundQFree;
        this.surfaceStart = other.surfaceStart;
        this.undergroundStart = other.undergroundStart;
        this.surfaceExit = other.surfaceExit;
        this.undergroundExit = other.undergroundExit;

        // Copy arrays
        this.passBaysAdjVertex = Arrays.copyOf(other.passBaysAdjVertex, other.passBaysAdjVertex.length);

        // Copy ArrayLists
        this.verticesInSurfaceQ = new ArrayList<>(other.verticesInSurfaceQ);
        this.verticesInActualRamp = new ArrayList<>(other.verticesInActualRamp);
        this.verticesInUndergroundQ = new ArrayList<>(other.verticesInUndergroundQ);
        this.passingBays = new ArrayList<>(other.passingBays);
        this.verticesInPassingBays = new ArrayList<>(other.verticesInPassingBays);

        // Special: ArrayList of ArrayLists
        this.passingBayVertices = new ArrayList<>();
        for (ArrayList<Integer> innerList : other.passingBayVertices) {
            this.passingBayVertices.add(new ArrayList<>(innerList));
        }

        // Copy HashMaps
        this.adjList = new HashMap<>();
        for (Map.Entry<Integer, UpDownNeighbourList> entry : other.adjList.entrySet()) {
            this.adjList.put(entry.getKey(), new UpDownNeighbourList(entry.getValue()));
        }

        this.hUpgoing = new HashMap<>(other.hUpgoing);
        this.hDowngoing = new HashMap<>(other.hDowngoing);
    }


    // Methods
    private void printAdjList() {
        // Task: Print the adjacency list
        System.out.println("Ramp adjacency list:");
        System.out.print("{");

        int currentKey = 0;
        for(Integer key : this.adjList.keySet()) {
            System.out.print(key + "=");
            adjList.get(key).printNeighbourLists();

            if(++currentKey != this.adjList.size()) {
                System.out.print(", ");
            }
        }
        System.out.println("}");
    }

    private void printRampParts() {
        // Task: Print out the ramp structure
        System.out.println("Surface queue: " + verticesInSurfaceQ);
        System.out.println("Ramp: " + verticesInActualRamp);
        System.out.println("Surface queue: " + verticesInSurfaceQ);
        System.out.println("Underground queue: " + verticesInUndergroundQ);
        System.out.println("Passing bays: " + passingBayVertices);
        System.out.println("Surface start: " + surfaceStart);
        System.out.println("Underground start: " + undergroundStart);
        System.out.println("Surface exit: " + surfaceExit);
        System.out.println("Underground exit: " + undergroundExit);
    }

    private void printRampStructure() {
        // Task: Print the actual ramp

        System.out.println("The ramp structure\n");

        // Passing bays
        for (int i = 0; i < this.passBaysAdjVertex.length; i++) {
            if(i == 0) {
                for (int j = 0; j < this.passBaysAdjVertex[i]; j++) {
                    System.out.print("\t");
                }
                System.out.print(passingBayVertices.get(i).getFirst() + "\t" + passingBayVertices.get(i).getLast());
            }
            else {
                for (int j = 0; j < (this.passBaysAdjVertex[i] - this.passBaysAdjVertex[i - 1] - 1); j++) {
                    System.out.print("\t");
                }
                System.out.print(passingBayVertices.get(i).getFirst() + "\t" + passingBayVertices.get(i).getLast());
            }
        }
        System.out.println();

        // Ramp and exits
        System.out.print(surfaceExit + "\t");
        for (Integer vertex : verticesInActualRamp) {
            System.out.print(vertex + "\t");
        }
        System.out.println(undergroundExit);

        // Queues
        int surfaceQLength = verticesInSurfaceQ.size();
        int undergroundQLength = verticesInUndergroundQ.size();
        ArrayList<Integer> longestQueue;
        if(verticesInSurfaceQ.size() > verticesInUndergroundQ.size()) {
            longestQueue = verticesInSurfaceQ;
        }
        else {
            longestQueue = verticesInUndergroundQ;
        }

        ArrayList<Integer> verticesInSurfaceQReverse = new ArrayList<>(verticesInSurfaceQ);
        Collections.reverse(verticesInSurfaceQReverse);

        for (int i = 0; i < longestQueue.size(); i++) {
            System.out.print("\t");

            if(i < surfaceQLength) {
                System.out.print(verticesInSurfaceQReverse.get(i));
            }
            for(int j = 0; j < rampLength - 1; j++) {
                System.out.print("\t");
            }
            if(i < undergroundQLength) {
                System.out.println(verticesInUndergroundQ.get(i));
            }
        }
        System.out.println();
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
        this.adjList.get(fromVertex).getDownNeighbours().add(toVertex);
    }

    private void initialiseAdjList(int rampLength, int surfaceQLength, int undergroundQLength, int[] passBays) {
        // Task: Given length of the ramp, initialise the adjacency list

        verticesInRamp = 0;

        // Add the first vertex separately
        addVertexToRamp(verticesInRamp);
        addDownEdge(verticesInRamp, verticesInRamp);  // Agents must be able to wait in its queue vertex
        verticesInRamp++;

        // Add the surface queue to the adjacency list. Note: directed subgraph!
        for (int i = 1; i < surfaceQLength; i++) {
            addVertexToRamp(verticesInRamp);
            addDownEdge(verticesInRamp - 1, verticesInRamp);
            addDownEdge(verticesInRamp, verticesInRamp);    // Agents must be able to wait in its queue vertex
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
            addUpEdge(verticesInRamp, verticesInRamp);    // Agents must be able to wait in its queue vertex
            verticesInRamp++;
        }

        // Add passing bays. Add edge to corresponding edges in the ramp
        // A passing bay consists of two nodes. The first node is the one closest to the surface
        int currentPassBay = 0;
        for(int i = 0; i< passBays.length; i++) {
            // PassBay vertex closest to surface
            addVertexToRamp(verticesInRamp);
            addUpEdge(verticesInRamp, verticesInRamp);      // Agents can wait in the passing bay
            addDownEdge(surfaceQLength + passBays[currentPassBay] - 1, verticesInRamp);     // -1 needed for adjustment
            addUpEdge(verticesInRamp, surfaceQLength + passBays[currentPassBay] - 1);

            // PassBay vertex closest to underground
            addVertexToRamp(verticesInRamp + 1);
            addDownEdge(verticesInRamp + 1, verticesInRamp + 1);    // Agents can wait in the passing bay
            addDownEdge(verticesInRamp, verticesInRamp + 1);
            addUpEdge(verticesInRamp + 1, verticesInRamp);
            addDownEdge(verticesInRamp + 1, surfaceQLength + passBays[currentPassBay]);
            addUpEdge(surfaceQLength + passBays[currentPassBay], verticesInRamp + 1);   // Only used by upgoing agents able to enter passing bays

            this.verticesInPassingBays.add(verticesInRamp);
            this.verticesInPassingBays.add(verticesInRamp + 1);
            this.secondPassBayVertices.add(verticesInRamp + 1);

            ArrayList<Integer> thisPassingBay = new ArrayList<>();
            thisPassingBay.add(verticesInRamp);
            thisPassingBay.add(verticesInRamp + 1);
            this.passingBayVertices.add(thisPassingBay);

            currentPassBay++;
            verticesInRamp += 2;
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
        // Agents always start in queue before entering the ramp, hence +/- 1
        surfaceQFree = surfaceStart - 1;
        undergroundQFree = undergroundStart + 1;
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
                hDowngoing.put(vertex, undergroundStart - vertex + 1); // h is the distance from the vertex to underground start + 1
            }
            // If any ramp vertex
            else {
                int gDowngoing = vertexGeneration.get(vertex);       // Get current vertex's downgoing g
                hDowngoing.put(vertex, rampLength - 1 - gDowngoing + 1);
            }
            // Passing bay vertices must have their h costs corrected since they are detours
            if(verticesInPassingBays.contains(vertex)) {
                // Go through every passing bay
                int passingBayNr = 0;
                for(ArrayList<Integer> passingBay : passingBayVertices) {
                    // Adjust the h value of each passing bay's vertices accordingly
                    if(passingBay.getFirst() == vertex) {
                        int adjVertex = this.passBaysAdjVertex[passingBayNr] + this.surfaceQLength - 1;
                        hDowngoing.put(vertex, hDowngoing.get(adjVertex - 1));
                    }
                    else if(passingBay.getLast() == vertex) {
                        int adjVertex = this.passBaysAdjVertex[passingBayNr] + this.surfaceQLength;
                        hDowngoing.put(vertex, hDowngoing.get(adjVertex - 1));
                    }
                    passingBayNr++;
                }
            }
        }
        // If an underground queue vertex, g is always 0, h is h, and f = h, since each of these could be the start vertex
        else if (direction == Constants.UP) {

            if(verticesInUndergroundQ.contains(vertex)) {
                hUpgoing.put(vertex, vertex - surfaceStart + 1);    // h is the distance from the vertex to surface start
            }
            else {
                int gUpgoing = vertexGeneration.get(vertex);       // Get current vertex's downgoing g
                hUpgoing.put(vertex, rampLength - 1 - gUpgoing + 1);
            }
            // Passing bay vertices must have their h costs corrected since they are detours
            if(verticesInPassingBays.contains(vertex)) {
                // Go through every passing bay
                int passingBayNr = 0;
                for(ArrayList<Integer> passingBay : passingBayVertices) {
                    // Adjust the h value of each passing bay's vertices accordingly
                    if(passingBay.getFirst() == vertex) {
                        int adjVertex = this.passBaysAdjVertex[passingBayNr] + this.surfaceQLength - 1;
                        hUpgoing.put(vertex, hUpgoing.get(adjVertex + 1));
                    }
                    else if(passingBay.getLast() == vertex) {
                        int adjVertex = this.passBaysAdjVertex[passingBayNr] + this.surfaceQLength;
                        hUpgoing.put(vertex, hUpgoing.get(adjVertex + 1));
                    }
                    passingBayNr++;
                }
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

    public HashMap<Integer, UpDownNeighbourList> getAdjList() {
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

    public ArrayList<Integer> getVerticesInPassingBays() {
        return this.verticesInPassingBays;
    }

    public ArrayList<ArrayList<Integer>> getPassingBayVertices() {
        return this.passingBayVertices;
    }

    public ArrayList<Integer> getSecondPassBayVertices() {
        return this.secondPassBayVertices;
    }
}
