package org.example.algorithms;

import org.example.MAPFScenario;

import java.util.*;

/*
* NOTES!
* surface and underground exit nodes are not handled in a special way when assigning fgh values.
* TODO: implement check for if an upgoing vehicle has reached the surface vertex, force it to move to
*  surface exit, vice versa for downgoing vehicles and underground exit
* TODO: Likewise as previous TODO, downgoing vehicles reaching surface start should not be allowed to
*  move to surface exit! The same goes for upgoing vehicles and underground exit!!
*
* */

public class Astar implements MAPFAlgorithm {

    // Data members


    // Constructors


    // Methods
    public void setfgh(HashMap<Integer, int[]> fghUpgoing, HashMap<Integer, int[]> fghDowngoing,
                       HashMap<Integer, ArrayList<Integer>> adjList,
                       int surfaceStart, int undergroundStart, int verticesInActualRamp) {
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

        // Get costs to all
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
            fghUpgoing.get(i)[1] = verticesInActualRamp - 1 - gDowngoing;
            fghDowngoing.get(i)[2] = verticesInActualRamp - 1 - gDowngoing;

            // Assign f value as sum of g and h
            fghDowngoing.get(i)[0] = fghDowngoing.get(i)[1] + fghDowngoing.get(i)[2];
            fghUpgoing.get(i)[0] = fghUpgoing.get(i)[1] + fghUpgoing.get(i)[2];
        }
    }

    @Override
    public void solve(MAPFScenario scenario) {

        // Fetch the scenario adjacency list and other key numbers
        HashMap<Integer, ArrayList<Integer>> adjList = scenario.fetchAdjList();
        int surfaceStart = scenario.fetchSurfaceStart();
        int undergroundStart = scenario.fetchUndergroundStart();
        int verticesInActualRamp = scenario.fetchVerticesInActualRamp();

        // fgh stores all vertices' f, g and h values
        // Create and initialise them
        HashMap<Integer, int[]> fghUpgoing = new HashMap<>();
        for (int i = 0; i < verticesInActualRamp; i++) {
            fghUpgoing.put(i, new int[]{0, 0, 0});
        }
        HashMap<Integer, int[]> fghDowngoing = new HashMap<>();
        for (int i = 0; i < verticesInActualRamp; i++) {
            fghDowngoing.put(i, new int[]{0, 0, 0});
        }

        // Set fgh values of all vertices
        setfgh(fghUpgoing, fghDowngoing, adjList, surfaceStart, undergroundStart, verticesInActualRamp);

        PriorityQueue<Integer> frontier = new PriorityQueue<>();
        PriorityQueue<Integer> explored = new PriorityQueue<>();
    }
}
