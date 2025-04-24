package org.example.algorithms;

import org.example.*;

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

    private static boolean isGoal(MAPFState state) {
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

    private static void buildSolution(MAPFState currentState, ArrayList<MAPFState> solution) {
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

    private static boolean isStateEqual(MAPFState state1, MAPFState state2) {
        // Task: Check if two states are equal in terms of the identical agents occupying the identical vertices
        // Thus, only compare their agentLocations
        // Agent class overrides equals() by comparing their id:s

        HashMap<Agent, Integer> agentLocations1 = state1.getAgentLocations();
        HashMap<Agent, Integer> agentLocations2 = state2.getAgentLocations();

        return agentLocations1.equals(agentLocations2);
    }

    private static void generateCartesianProductHelper(
            ArrayList<HashMap.Entry<Agent, ArrayList<Integer>>> entries,    // List of all agents possible moves (neighbours)
            int index,      // Tracks what agent we're currently at
            HashMap<Agent, Integer> current,    // Current recursion branch move combinations
            ArrayList<HashMap<Agent, Integer>> result) {
        // Task: Perform the actual recursion

        // Base case, if we have reached the final agent for this recursion branch
        if(index == entries.size()) {
            result.add(new HashMap<>(current));
            return;
        }

        // Get the current agent and its possible moves (neighbours)
        HashMap.Entry<Agent, ArrayList<Integer>> entry = entries.get(index);
        Agent agent = entry.getKey();
        ArrayList<Integer> possibleMoves = entry.getValue();

        for (Integer move : possibleMoves) {
            current.put(agent, move);
            generateCartesianProductHelper(entries, index + 1, current, result);
            current.remove(agent);
        }
    }

    private static ArrayList<HashMap<Agent, Integer>> generateCartesianProduct(
            ArrayList<HashMap.Entry<Agent, ArrayList<Integer>>> entries) {
        // Task: Generate the cartesian product of all agent moves with recursion

        ArrayList<HashMap<Agent, Integer>> result = new ArrayList<>();
        generateCartesianProductHelper(entries, 0, new HashMap<>(), result);
        return result;
    }

    private static HashMap<Agent, ArrayList<Integer>> getPossibleMoves(
            HashMap<Agent, Integer> agentLocations,
            HashMap<Integer, UpDownNeighbourList> adjList) {
        // Task: With agents and their locations, get all possible agent moves

        HashMap<Agent, ArrayList<Integer>> agentMoves = new HashMap<>();

        // Go through each agent, its location, and get all neighbours from adjList
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            Integer vertex = entry.getValue();

            ArrayList<Integer> moves;
            if(agent.direction == Constants.DOWN) {
                moves = adjList.get(vertex).getDownNeighbours();
                agentMoves.put(agent, moves);
            }
            else if(agent.direction == Constants.UP) {
                moves = adjList.get(vertex).getUpNeighbours();
                agentMoves.put(agent, moves);
            }
            else {
                System.out.println("A*->solve: UNKNOWN AGENT DIRECTION!");
            }
        }

        return agentMoves;
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
        HashMap<Integer, UpDownNeighbourList> adjList = scenario.fetchAdjList();
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

            // For each agent actions combination, generate a new state and add to frontier if not in explored

            ArrayList<Integer> prohibitedVertices = new ArrayList<>();
            ArrayList<int[]> prohibitedMoves = new ArrayList<>();

            HashMap<Agent, Integer> agentLocations = currentState.getAgentLocations();
            HashMap<Agent, Integer> agentNeighbourLocations;
            // TODO: ITERATE THROUGH ALL ACTION COMBINATIONS

            // Get all possible agent moves
            HashMap<Agent, ArrayList<Integer>> agentMoves = getPossibleMoves(agentLocations, adjList);

            // Generate all move combinations
            // See https://www.baeldung.com/java-cartesian-product-sets and
            // https://chatgpt.com/c/68094965-3324-800b-9735-a243367c446f
            // for cartesian product of sets. In this case, convert agentMoves to a list.
            // Then do recursion.
            // NOTE! Make sure to update prohibitedVertices+Moves and do NOT generate
            // MAPFStates that violate these prohibitions.

            ArrayList<HashMap<Agent, Integer>> moveCombinations =
                    generateCartesianProduct(new ArrayList<>(agentMoves.entrySet()));
            System.out.println("All movement combinations: " + moveCombinations);

            // Generate new states from the moveCombinations
            
        }

        System.out.println("A* COULD NOT FIND A SOLUTION!");
        return null;    // Return set of failed-solution states?
    }
}
