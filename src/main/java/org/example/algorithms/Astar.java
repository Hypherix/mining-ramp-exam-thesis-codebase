package org.example.algorithms;

import org.example.MAPFScenario;
import org.example.Vertex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

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

        PriorityQueue<Integer> frontier = new PriorityQueue<>();
        PriorityQueue<Integer> explored = new PriorityQueue<>();

        // Start with having surface vertex
        // First as target vertex
        frontier.addAll(adjList.get(surfaceStart));
        int round = 0;
        while(!frontier.isEmpty()){
            //currentVertex = frontier.poll()
            /*
            * frontier and explored need not be priority queues, we must go through all of them regardless
            * need a way to keep track of cost. perhaps have hashmap with vertexID : round(=cost)? yes!!
            * */
        }
    }
}
