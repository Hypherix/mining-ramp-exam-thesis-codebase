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
            HashMap<Integer, UpDownNeighbourList> adjList,
            int surfaceStart, int undergroundStart,
            int surfaceExit, int undergroundExit) {
        // Task: With agents and their locations, get all possible agent moves

        HashMap<Agent, ArrayList<Integer>> agentMoves = new HashMap<>();

        // Go through each agent, its location, and get all neighbours from adjList
        for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
            Agent agent = entry.getKey();
            Integer vertex = entry.getValue();

            ArrayList<Integer> moves;

            // If an upgoing agent has reached the surface start vertex, force next move to be to surface exit
            if(vertex == surfaceStart && agent.direction == Constants.UP) {
                moves = new ArrayList<>();
                moves.add(surfaceExit);
                agentMoves.put(agent, moves);
            }
            // If a downgoing agent has reached underground start vertex, force next move to be to underground exit
            else if(vertex == undergroundStart && agent.direction == Constants.DOWN) {
                moves = new ArrayList<>();
                moves.add(undergroundExit);
                agentMoves.put(agent, moves);
            }
            // If in surface exit, stay there indefinitely
            else if(vertex == surfaceExit) {
                moves = new ArrayList<>();
                moves.add(surfaceExit);
                agentMoves.put(agent, moves);
            }
            // If in underground exit, stay there indefinitely
            else if(vertex == undergroundExit) {
                moves = new ArrayList<>();
                moves.add(undergroundExit);
                agentMoves.put(agent, moves);
            }
            // If the agent is not in a start vertex, retrieve neighbours as usual
            else if(agent.direction == Constants.DOWN) {
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

    public void printMoveCombinations(ArrayList<HashMap<Agent, Integer>> moveCombinations) {
        // Task: Make a legible print of moveCombinations

        System.out.println("\nAll movement combinations:");

        int currentMoveCombination = 0;
        for(HashMap<Agent, Integer> moveCombination : moveCombinations) {
            System.out.print("{");

            int currentEntry = 0;
            for(Map.Entry<Agent, Integer> entry : moveCombination.entrySet()) {
                Agent agent = entry.getKey();
                int newLocation = entry.getValue();

                System.out.print("a" + agent.id + " = " + newLocation);

                if(++currentEntry != moveCombination.entrySet().size()) {
                    System.out.print(", ");
                }
            }

            System.out.println("}");
//            if(++currentMoveCombination != moveCombinations.size()) {
//                System.out.print(", ");
//            }
        }

        System.out.println("");
    }

    private static boolean isStateAllowed(
            HashMap<Agent, Integer> moveCombination, ArrayList<Integer> prohibitedVertices,
            ArrayList<ArrayList<Integer>> prohibitedMoves,
            HashMap<Agent, Integer> currentStateAgentLocations,
            int surfaceExit, int undergroundExit) {
        // Task: Check if the state is allowed in the frontier

        boolean stateAllowed = true;

        for(Map.Entry<Agent, Integer> entry : moveCombination.entrySet()) {
            Agent agent = entry.getKey();
            int newLocation = entry.getValue();
            int oldLocation = currentStateAgentLocations.get(agent);
            ArrayList<Integer> prohibitedMove = new ArrayList<>(Arrays.asList(newLocation, oldLocation));

            ArrayList<Integer> currentAgentMove = new ArrayList<>(Arrays.asList(oldLocation, newLocation));
            if (prohibitedVertices.contains(newLocation) || prohibitedMove.contains(currentAgentMove)) {
                stateAllowed = false;
            }

            // Exit vertices are never prohibited to enter, assuming direction is correct
            if(newLocation != surfaceExit && newLocation != undergroundExit) {
                prohibitedVertices.add(newLocation);
            }

            prohibitedMoves.add(prohibitedMove);
        }

        return stateAllowed;
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
        int surfaceExit = scenario.fetchSurfaceExit();
        int undergroundExit = scenario.fetchUndergroundExit();
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
                System.out.println(solution);
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

            HashMap<Agent, Integer> currentStateAgentLocations = currentState.getAgentLocations();

//            MAPFState testState = new MAPFState(currentState.getRamp(), currentStateAgentLocations, 0);
//
//            if(explored.contains(testState)) {
//                System.out.println("testState already exists in explored! Thus, correct!");
//            }
//            else {
//                explored.add(testState);
//                System.out.println("testState was added to explored, which should not happen!");
//            }



            // TODO: ITERATE THROUGH ALL ACTION COMBINATIONS

            // Get all possible agent moves
            HashMap<Agent, ArrayList<Integer>> agentMoves =
                    getPossibleMoves(currentStateAgentLocations, adjList,
                            surfaceStart, undergroundStart, surfaceExit, undergroundExit);

            // Generate all move combinations
            // See https://www.baeldung.com/java-cartesian-product-sets and
            // https://chatgpt.com/c/68094965-3324-800b-9735-a243367c446f
            // for cartesian product of sets. In this case, convert agentMoves to a list.
            // Then do recursion.
            // NOTE! Make sure to update prohibitedVertices+Moves and do NOT generate
            // MAPFStates that violate these prohibitions.

            ArrayList<HashMap<Agent, Integer>> moveCombinations =
                    generateCartesianProduct(new ArrayList<>(agentMoves.entrySet()));

            printMoveCombinations(moveCombinations);

            // Generate new states from moveCombinations

            ArrayList<Integer> prohibitedVertices;
            ArrayList<ArrayList<Integer>> prohibitedMoves;

            // Go through each move combination
            for (HashMap<Agent, Integer> moveCombination : moveCombinations) {
                prohibitedVertices = new ArrayList<>();
                prohibitedMoves = new ArrayList<>();

                // For each agent moveCombination, its new location can't be occupied by other agents
                // Likewise, no agent can make the opposite moveCombination as another agent
//                for(Map.Entry<Agent, Integer> entry : moveCombination.entrySet()) {
//                    Agent agent = entry.getKey();
//                    int newLocation = entry.getValue();
//                    int oldLocation = currentStateAgentLocations.get(agent);
//
//                    prohibitedVertices.add(newLocation);
//                    ArrayList<Integer> prohibitedMove = new ArrayList<>(Arrays.asList(newLocation, oldLocation));
//                    prohibitedMoves.add(prohibitedMove);
//
//                    ArrayList<Integer> currentAgentMove = new ArrayList<>(Arrays.asList(oldLocation, newLocation));
//                    if (prohibitedVertices.contains(newLocation) || prohibitedMove.contains(currentAgentMove)) {
//                        stateAllowed = false;
//                    }
//                }

                boolean stateAllowed = isStateAllowed(moveCombination, prohibitedVertices,
                        prohibitedMoves, currentStateAgentLocations, surfaceExit, undergroundExit);

                // If state is allowed, generate it
                if (stateAllowed) {
                    // This many agents have made an action --> gcost++ for each
                    int numOfActiveAgents = currentState.getNumOfActiveAgents();
                    int newGcost = currentState.getGcost() + numOfActiveAgents;

                    // Create the neighbourState and assign currentState as its parent
                    MAPFState neighbourState = new MAPFState(currentState.getRamp(), moveCombination, newGcost);
                    neighbourState.setParent(currentState);

                    // If neighbourState has not been encountered before, add to frontier
                    if(!frontier.contains(neighbourState) && !explored.contains(neighbourState)) {
                        frontier.add(neighbourState);
                    }
                    /*
                    * If an identical state to neighbourState, but with more expensive path-cost (g),
                    * replace with neighbourState.
                     */
                    else if(frontier.contains(neighbourState)) {
                        for(MAPFState state : frontier) {
                            if(state.equals(neighbourState) && neighbourState.getGcost() < state.getGcost()) {
                                frontier.remove(state);
                                frontier.add(neighbourState);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("A* COULD NOT FIND A SOLUTION!");
        return null;    // Return set of failed-solution states?
    }
}
