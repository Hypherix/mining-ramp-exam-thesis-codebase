package org.rampTraffic.algorithms;
import org.rampTraffic.*;
import java.util.*;


public class Astar implements MAPFAlgorithm {

    // Data members
    private int generatedStates = 0;
    private int expandedStates = 0;

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

    private static void generateCartesianProductHelper(
            ArrayList<HashMap.Entry<Agent, ArrayList<Integer>>> entries,    // List of all agents possible moves (neighbours)
            int index,                                                      // Tracks what agent we are currently at
            HashMap<Agent, Integer> current,                                // Current recursion branch move combinations
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

    private HashMap<Agent, ArrayList<Integer>> getPossibleMoves(
            HashMap<Agent, Integer> agentLocations,
            Ramp ramp) {
        // Task: With agents and their locations, get all possible agent moves

        int surfaceStart = ramp.getSurfaceStart();
        int surfaceExit = ramp.getSurfaceExit();
        int undergroundStart = ramp.getUndergroundStart();
        int undergroundExit = ramp.getUndergroundExit();
        HashMap<Integer, UpDownNeighbourList> adjList = ramp.getAdjList();
        ArrayList<Integer> firstPassBayVertices = ramp.getFirstPassBayVertices();
        ArrayList<Integer> secondPassBayVertices = ramp.getSecondPassBayVertices();

        HashMap<Agent, ArrayList<Integer>> agentMoves = new HashMap<>();

        // Go through each agent, its location, and get all neighbours from adjList IN THE RAMP
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

                // If downgoing agent can't enter passing bays, remove the move entering a passing bay
                if(!agent.passBayAble) {
                    ArrayList<Integer> tempMoves = new ArrayList<>(moves);
                    for(Integer move : tempMoves) {
                        if(firstPassBayVertices.contains(move)) {
                            moves.remove(move);
                        }
                    }
                }

                agentMoves.put(agent, moves);
            }
            else if(agent.direction == Constants.UP) {
                moves = adjList.get(vertex).getUpNeighbours();

                // If upgoing agent can't enter passing bays, remove the move entering a passing bay
                if(!agent.passBayAble) {
                    ArrayList<Integer> tempMoves = new ArrayList<>(moves);
                    for(Integer move : tempMoves) {
                        if(secondPassBayVertices.contains(move)) {
                            moves.remove(move);
                        }
                    }
                }

                agentMoves.put(agent, moves);
            }
            else {
                System.out.println("A*->solve: UNKNOWN AGENT DIRECTION!");
            }
        }

        return agentMoves;
    }

    private ArrayList<HashMap<Agent, Integer>> removeInvalidMoveCombinations(ArrayList<HashMap<Agent, Integer>> moveCombinations,
                                               HashMap<Agent, Integer> agentLocations,
                                               ArrayList<Integer> verticesInSurfaceQ,
                                               ArrayList<Integer> verticesInUndergroundQ) {
        // Task: Remove all combinations where if, in a queue, an agent in front moves but the agent behind stays.
        // If two agents are in a queue and the one in front moves, the one behind should always also move.
        // Note that this does not concern the first agent in either queue, since this agent must be allowed to stay
        // even though the vertex ahead is free. This is because an agent should only leave the queue when appropriate
        // (on the ramp, it cannot stay in place or go back). Therefore, verticesInQ will exclude the first vertex

        ArrayList<HashMap<Agent, Integer>> validCombinations = new ArrayList<>();

        for(HashMap<Agent, Integer> moveCombination : moveCombinations) {
            boolean isValid = true;
            boolean inSurfaceQ = true;

            for(ArrayList<Integer> queue : List.of(verticesInSurfaceQ, verticesInUndergroundQ)) {

                HashMap<Integer, Agent> queuePositionToAgent = new HashMap<>();
                for(Map.Entry<Agent, Integer> entry : agentLocations.entrySet()) {
                    Agent agent = entry.getKey();
                    int location = entry.getValue();

                    if(queue.contains(location)) {
                        queuePositionToAgent.put(location, agent);
                    }
                }

                if(inSurfaceQ && queue.size() >= 2) {
                    inSurfaceQ = false;
                    for(int i = queue.size() - 1; i > 0; i--) {
                        int frontVertex = queue.get(i);
                        int backVertex = queue.get(i - 1);

                        Agent frontAgent = queuePositionToAgent.get(frontVertex);
                        Agent backAgent = queuePositionToAgent.get(backVertex);

                        if (frontAgent != null && backAgent != null) {
                            Integer frontMove = moveCombination.get(frontAgent);
                            Integer backMove = moveCombination.get(backAgent);

                            if(frontMove != null && backMove != null) {
                                // If front agent moves but back agent stays, the move combination is invalid
                                if(!frontMove.equals(frontVertex) && backMove.equals(backVertex)) {
                                    isValid = false;
                                    break;
                                }
                            }
                        }
                    }

                    if(!isValid) {
                        break;
                    }
                }

                else if(!inSurfaceQ && queue.size() >= 2) {
                    for(int i = 0; i < queue.size() - 1; i++) {
                        int frontVertex = queue.get(i);
                        int backVertex = queue.get(i + 1);

                        Agent frontAgent = queuePositionToAgent.get(frontVertex);
                        Agent backAgent = queuePositionToAgent.get(backVertex);

                        if (frontAgent != null && backAgent != null) {
                            Integer frontMove = moveCombination.get(frontAgent);
                            Integer backMove = moveCombination.get(backAgent);

                            if(frontMove != null && backMove != null) {
                                // If front agent moves but back agent stays, the move combination is invalid
                                if(!frontMove.equals(frontVertex) && backMove.equals(backVertex)) {
                                    isValid = false;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            if(isValid) {
                validCombinations.add(moveCombination);
            }
        }

        return validCombinations;
    }

    private void printMoveCombinations(ArrayList<HashMap<Agent, Integer>> moveCombinations) {
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
        }

        System.out.println();
    }

    private static boolean isStateAllowed(
            HashMap<Agent, Integer> moveCombination,
            HashMap<Agent, Integer> currentStateAgentLocations,
            int surfaceExit, int undergroundExit,
            ArrayList<Integer> verticesInPassingBays,
            ArrayList<ArrayList<Integer>> passingBayVertices,
            MAPFScenario scenario, int timeStep) {
        // Task: Check if the state is allowed in the frontier

        ArrayList<Integer> prohibitedVertices = new ArrayList<>();
        ArrayList<ArrayList<Integer>> prohibitedMoves = new ArrayList<>();

        HashMap<Agent, HashMap<Integer, Set<Integer>>> vertexConstraints = scenario.getVertexConstraints();
        HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> edgeConstraints = scenario.getEdgeConstraints();

        // If A* invoked by CBS, check if any vertex constraints exist
        if (vertexConstraints != null) {
            // If vertexConstraints != null, we are here from CBS. CBS only invokes A* on one agent. Retrieve
            // the agent from scenario's agentLocations
            Set<Agent> agents = scenario.fetchAgentLocations().keySet();
            Agent agent = agents.iterator().next();

            // Get all prohibited vertices for the agent this timeStep if they exist.
            // Then add them to prohibitedVertices
            if (vertexConstraints.containsKey(agent)) {
                HashMap<Integer, Set<Integer>> agentVertexConstraints = vertexConstraints.get(agent);
                if (agentVertexConstraints.containsKey(timeStep)) {
                    prohibitedVertices.addAll(agentVertexConstraints.get(timeStep));
                }
            }
        }

        // If A* invoked by CBS, check if any edge constraints exist
        if (edgeConstraints != null) {
            // If vertexConstraints != null, we are here from CBS. CBS only invokes A* on one agent. Retrieve
            // the agent from scenario's agentLocations
            Set<Agent> agents = scenario.fetchAgentLocations().keySet();
            Agent agent = agents.iterator().next();

            // Do the same for prohibited moves
            if (edgeConstraints.containsKey(agent)) {
                HashMap<Integer, Set<ArrayList<Integer>>> agentEdgeConstraints = edgeConstraints.get(agent);
                if (agentEdgeConstraints.containsKey(timeStep)) {
                    prohibitedMoves.addAll(agentEdgeConstraints.get(timeStep));
                }
            }
        }

        // Check for vertex/edge conflicts
        for(Map.Entry<Agent, Integer> entry : moveCombination.entrySet()) {
            Agent agent = entry.getKey();
            int newLocation = entry.getValue();
            int oldLocation = currentStateAgentLocations.get(agent);
            ArrayList<Integer> prohibitedMove = new ArrayList<>(Arrays.asList(newLocation, oldLocation));

            // If an agent moves to an occupied vertex, or there is an edge conflict, state is not allowed
            ArrayList<Integer> currentAgentMove = new ArrayList<>(Arrays.asList(oldLocation, newLocation));
            if (prohibitedVertices.contains(newLocation) || prohibitedMoves.contains(currentAgentMove)) {
                return false;
            }

            // Upgoing agents are not allowed to enter passing bays
            if(agent.direction == Constants.UP && verticesInPassingBays.contains(newLocation)
                && !agent.passBayAble) {
                return false;
            }

            // Exit vertices are never prohibited to enter, assuming direction is correct
            if(newLocation != surfaceExit && newLocation != undergroundExit) {
                prohibitedVertices.add(newLocation);
            }

            // If the vertex is in a passing bay, prohibit the other vertex in the same passing bay from being occupied
            if (verticesInPassingBays.contains(oldLocation) || verticesInPassingBays.contains(newLocation)) {
                for(ArrayList<Integer> passingBay : passingBayVertices) {
                    if (passingBay.contains(newLocation)) {
                        prohibitedVertices.addAll(passingBay);
                        break;
                    }
                }
            }

            // If an agent has reached an exit, it will keep "moving" to the same exit.
            // If another agent reaches the same exit it will make the same move, which must not then be prohibited
            if(oldLocation == newLocation && (oldLocation == surfaceExit || oldLocation == undergroundExit)) {
                // Do nothing
            }
            else {
                prohibitedMoves.add(prohibitedMove);
            }
        }

        return true;
    }

    @Override
    public MAPFSolution solve(MAPFScenario scenario, boolean prioritise) {
        /*
        * - For each neighbour state, the g cost is that of the current state's g + num of all active agents (each action is g++)
        * - h is calculated by the state upon construction by looking at the ramp and the agentLocations
        * */

        ArrayList<MAPFState> solution = new ArrayList<>();

        // Fetch key ramp information
        int surfaceExit = scenario.fetchSurfaceExit();
        int undergroundExit = scenario.fetchUndergroundExit();
        ArrayList<Integer> verticesInSurfaceQ = scenario.fetchVerticesInSurfaceQ();
        ArrayList<Integer> verticesInUndergroundQ = scenario.fetchVerticesInUndergroundQ();
        ArrayList<Integer> verticesInPassingBays = scenario.fetchVerticesInPassingBays();
        ArrayList<ArrayList<Integer>> passingBayVertices = scenario.fetchPassingBayVertices();

        // Get initialState and initialise its f cost (h is set in MAPFState constructor)
        MAPFState initialState = scenario.getInitialState();
        initialState.setFCost(initialState.getGCost() + initialState.getHCost());

        // Initialise priority queues
        PriorityQueue<MAPFState> frontier = prioritise
                ? new PriorityQueue<>(new StatePrioComparator())
                : new PriorityQueue<>(new StateComparator());
        PriorityQueue<MAPFState> explored = prioritise
                ? new PriorityQueue<>(new StatePrioComparator())
                : new PriorityQueue<>(new StateComparator());
        frontier.add(initialState);


        while(!frontier.isEmpty()) {
            MAPFState currentState = frontier.poll();
            expandedStates++;

            if(isGoal(currentState)) {
                buildSolution(currentState, solution);

                return new MAPFSolution(solution, generatedStates, expandedStates);
            }

            explored.add(currentState);

            // For each agent actions combination, generate a new state and add to frontier if not in explored

            HashMap<Agent, Integer> currentStateAgentLocations = currentState.getAgentLocations();

            // Get all possible agent moves
            HashMap<Agent, ArrayList<Integer>> agentMoves =
                    getPossibleMoves(currentStateAgentLocations, currentState.getRamp());

            // Generate all move combinations
            ArrayList<HashMap<Agent, Integer>> moveCombinations =
                    generateCartesianProduct(new ArrayList<>(agentMoves.entrySet()));

            moveCombinations = removeInvalidMoveCombinations(moveCombinations, currentStateAgentLocations,
                    verticesInSurfaceQ, verticesInUndergroundQ);

//            printMoveCombinations(moveCombinations);

            // Generate new states from moveCombinations

            // Go through each move combination
            for (HashMap<Agent, Integer> moveCombination : moveCombinations) {

                boolean stateAllowed = isStateAllowed(moveCombination, currentStateAgentLocations,
                        surfaceExit, undergroundExit, verticesInPassingBays, passingBayVertices,
                        scenario, currentState.getTimeStep() + 1);

                // If state is allowed, generate it
                if (stateAllowed) {
                    // This many agents have made an action --> gcost++ for each
                    int numOfActiveAgents = currentState.getNumOfActiveAgents();
                    int newGcost = currentState.getGCost() + numOfActiveAgents;
                    int numOfActivePrioAgents = currentState.getNumOfActivePrioAgents();
                    int newGcostPrio = currentState.getgCostPrio() + numOfActivePrioAgents;

                    // Create the neighbourState and assign currentState as its parent
                    MAPFState neighbourState = new MAPFState(currentState.getRamp(), moveCombination, newGcost,
                            newGcostPrio, currentState.getTimeStep() + 1);
                    neighbourState.setParent(currentState);

                    // If neighbourState has not been encountered before, add it to frontier
                    if(!frontier.contains(neighbourState) && !explored.contains(neighbourState)) {
                        frontier.add(neighbourState);
                        generatedStates++;
                    }
                    /*
                    * If an identical state to neighbourState, but with more expensive path-cost (g),
                    * replace with neighbourState.
                    */
                    else if(frontier.contains(neighbourState)) {
                        for(MAPFState state : frontier) {
                            if(state.equals(neighbourState) && neighbourState.getGCost() < state.getGCost()) {
                                frontier.remove(state);
                                frontier.add(neighbourState);
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
