package org.example.algorithms;

import org.example.MAPFScenario;
import org.example.Vertex;

import java.util.*;

public class Astar implements MAPFAlgorithm {

    // Data members


    // Constructors


    // Methods

    @Override
    public void solve(MAPFScenario scenario) {

        // Fetch the scenario adjacency list and other key numbers
        HashMap<Integer, ArrayList<Integer>> adjList = scenario.fetchAdjList();
        int surfaceStart = scenario.fetchSurfaceStart();
        int undergroundStart = scenario.fetchUndergroundStart();
        int verticesInScenario = scenario.fetchVerticesInRamp();

        // fgh stores all vertices' f, g and h values
        // Create and initialise them
        HashMap<Integer, int[]> fghUpgoing = new HashMap<>();
        for (int i = 0; i < verticesInScenario; i++) {
            fghUpgoing.put(i, new int[]{0, 0, 0});
        }

        HashMap<Integer, int[]> fghDowngoing = new HashMap<>();
        for (int i = 0; i < verticesInScenario; i++) {
            fghDowngoing.put(i, new int[]{0, 0, 0});
        }


        PriorityQueue<Integer> frontier = new PriorityQueue<>();
        PriorityQueue<Integer> explored = new PriorityQueue<>();

        // Set fgh values of all vertices

    }

    public void setfgh(HashMap<Integer, int[]> fghUpgoing, HashMap<Integer, int[]> fghDowngoing,
                       HashMap<Integer, ArrayList<Integer>> adjList,
                       int surfaceStart, int undergroundStart, int verticesInRamp) {
        // Task: Set the fgh values of all vertices

        Queue<Integer> frontierVertex = new LinkedList<>();      // Keeps track of vertices in frontier
        HashMap<Integer, ArrayList<Integer>> frontierNeighbours = new HashMap<>();  // Maps frontier vertices to neighbours
        ArrayList<Integer> explored = new ArrayList<>();

        // Start with having surface vertex

        // First as target vertex
        // Add surface vertex to frontier and explored
        frontierVertex.add(surfaceStart);
        frontierNeighbours.put(surfaceStart, adjList.get(surfaceStart));
        explored.add(surfaceStart);
        int round = 0;
        int currentVertex;
        ArrayList<Integer> currentNeighbours = new ArrayList<>();

        while(!frontierVertex.isEmpty()){
            currentVertex = frontierVertex.poll();              // Dequeue vertex first in queue
            currentNeighbours = frontierNeighbours.get(currentVertex);  // Get its neighbours
            for(Integer neighbour : currentNeighbours) {
                // Only add neighbour to frontier if it hasn't already been explored or is already in the frontier
                if (!explored.contains(neighbour) && !frontierVertex.contains(neighbour)) {
                    frontierVertex.add(neighbour);
                    frontierNeighbours.put(neighbour, adjList.get(neighbour));
                    // HITTA ETT SÄTT ATT FÅ FRAM VILKEN GENERATION/COST SOM SKA LÄGGAS TILL
                    // SE https://chatgpt.com/c/68000a63-aea0-800b-95a3-7514364f5cee för förslag på hur!1
                }
            }
        }
    }
}
