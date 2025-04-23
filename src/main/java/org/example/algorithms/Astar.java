package org.example.algorithms;

import org.example.MAPFScenario;
import org.example.MAPFState;
import org.example.StateComparator;

import java.util.*;

/*
* NOTES!
* Surface and underground exit nodes are not handled in a special way when assigning fgh values.
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

    @Override
    public ArrayList<MAPFState> solve(MAPFScenario scenario) {  // TODO: SHALL RETURN SET OF SOLUTION STATES

        // Fetch the scenario adjacency list and other key numbers
        HashMap<Integer, ArrayList<Integer>> adjList = scenario.fetchAdjList();
        int surfaceStart = scenario.fetchSurfaceStart();
        int undergroundStart = scenario.fetchUndergroundStart();
        int verticesInActualRamp = scenario.fetchRampLength();

        PriorityQueue<MAPFState> frontier = new PriorityQueue<MAPFState>(new StateComparator());
        PriorityQueue<MAPFState> explored = new PriorityQueue<MAPFState>(new StateComparator());
        return null;
    }
}
