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

    private boolean isGoal(MAPFState state) {
        // Task: Check if state is a goal state, i.e. if all agents have reached the exits

        // Get agent locations and exit locations
        Collection<Integer> agentLocations = state.getAgentLocations().values();
        int surfaceExit = state.fetchSurfaceExit();
        int undergroundExit = state.fetchUndergroundExit();
        Set<Integer> exits = Set.of(surfaceExit, undergroundExit);

        // Check if any agent is not in an exit
        for(int location : agentLocations) {
            if(!exits.contains(location)) {
                return false;
            }
        }
        return true;
    }

    private void buildSolution(MAPFState currentState, ArrayList<MAPFState> solution) {
        // Task: Starting from the currentState, add parent states to the stack to build a solution

        Stack<MAPFState> solutionStack = new Stack<>();
        solutionStack.push(currentState);

        while(currentState.parent != null){
            currentState = currentState.parent;
            solutionStack.push(currentState);
        }

        while(!solutionStack.isEmpty()) {
            solution.add(solutionStack.pop());
        }
    }

    @Override
    public ArrayList<MAPFState> solve(MAPFScenario scenario) {
        /*
        * Notes to self
        * - For each neighbour state, the g cost is that of the current state's g + num of all active agents (each action is g++)
        * - h is calculated by the state upon construction by looking at the ramp and the agentLocations
        * */

        ArrayList<MAPFState> solution = new ArrayList<>();

        // Fetch the scenario adjacency list and other key numbers
        HashMap<Integer, ArrayList<Integer>> adjList = scenario.fetchAdjList();
        int surfaceStart = scenario.fetchSurfaceStart();
        int undergroundStart = scenario.fetchUndergroundStart();
        int actualRampLength = scenario.fetchRampLength();

        // Get initialState and initialise its g and f cost to 0 (h is set in MAPFState constructor)
        MAPFState initialState = scenario.getInitialState();
        initialState.setGcost(0);
        initialState.setFcost(initialState.getGcost() + initialState.getHcost());

        // Initialise priority queues
        PriorityQueue<MAPFState> frontier = new PriorityQueue<MAPFState>(new StateComparator());
        PriorityQueue<MAPFState> explored = new PriorityQueue<MAPFState>(new StateComparator());
        frontier.add(initialState);

        while(!frontier.isEmpty()) {
            MAPFState currentState = frontier.poll();

            if(isGoal(currentState)) {
                buildSolution(currentState, solution);
                return solution;
            }

            explored.add(currentState);

            /*
            * TODO NEXT: generate neighbour states by letting each active agent make one action.
            *  Important: Need to prohibit collision-states from not being added to frontier.
            *  Furthermore: Have a list of active agents and another list of finished agents to know by how much
            *  each neighbour node should increment its g from its parent state? (g = g.parent + num of active agents)
            *  Additionally: if an upgoing agent is in surface start, automatically force to surface exit.
            *  Likewise: if a downgoing agent is in underground start, automatically force to underground exit.
            * */
        }

        System.out.println("A* COULD NOT FIND A SOLUTION!");
        return null;    // Return set of failed-solution states?
    }
}
