# Mining Ramp Traffic Simulator

## Introduction
This program simulates a mining ramp. A mining ramp is a ramp used by mining vehicles, either those from the surface wanting to reach the mine or those from the mine wanting to return to the surface. The peculiarity of the mining ramp is that it is heavily bottlenecked in that it only consists of one lane. To alleviate this bottleneck, passing bays may be placed throughout the ramp.

The mining vehicles can enter the scenario at different times throughout its lifespan. Whenever a vehicle tries to use the ramp, it must first join either on of the ramp's queues - the surface queue or the underground queue - depending on the vehicle's direction. The goal for each vehicle is to reach the opposite end of the ramp from where it starts. Depending on the properties of a vehicle, it may (or may not) occupy a passing bay to allow oncoming vehicles to pass.

As mentioned, the scenario has a lifespan. Within this lifespan duration, the scenario checks if new vehicles will enter the ramp, in which case it puts the vehicles in their respective queues. The scenario ends as soon as (1) all vehicles have reached their destinations, and (2) the lifespan duration has expired.

## User Controls

The software gives the user ability to control the features of the mining ramp, the vehicles and their properties, and the scenario lifespan duration. The mining ramp has the following adjustable properties:
+ length
+ passing bays and their locations
+ queue lengths

Each vehicle has the following adjustable properties:
+ direction of travel
+ ability to use passing bays
+ priority status
+ velocity (a bit misleading since only velocity = 1 works in this version)

As is explained later, the user decides what algorithms to apply on a scenario. Whenever the algorithms have run, a UI appears which allows the user to graphically see the solutions of the algorithms time step by time step.

## Algorithms

The algorithms implemented in this software closely follows those described in their respective original papers. Due to the extensive constraints and bottlenecks of the mining ramp scenario, however, the algorithm have been slightly modified to be compatible with the scenario.

### A*

The first algorithm is an ordinary A\* extended to work with multiple agents. There are two frequently seen extensions of multi-agent A\*: [Operator Decomposition and Independence Detection](	https://doi.org/10.1609/aaai.v24i1.7564 ). Neither of these are implemented due to the limited benefit they would add with this scenario.

### Increasing Cost Tree Search (ICTS)

The Increasing Cost Tree Search (ICTS) algorithm is an optimal two-level search algorithm. It works by trying to find an optimal conflict-free solution by iteratively increasing the allowed path cost of each agent. It starts off by computing the optimal path cost for each agent as if they were alone on the mining ramp. Starting with these acquired cost values, ICTS continues to increase the path costs until a solution is found.

#### Reference

- [The increasing cost tree search for optimal multi-agent pathfinding](https://doi.org/10.1016/j.artint.2012.11.006)

### Conflict-Based Search (CBS)

Conflict-Based Search (CBS) is also an optimal two-level algorithm. It works by treating the agents as if they are alone on the mining ramp, to find a path for them to their destinations. It then simulates the agents executing their plans and stops as soon as a conflict between two agents is encountered. Following a conflict, CBS imposes constraints on both involved agent and thus entertains both possibilities following the conflict: one where the first agent is prohibited from taking the conflict-inducing action, and one where the other agent is instead prohibited. With these constraints the affected agents now take slightly different paths in the hopes of avoiding any further conflicts. This process continues until all agents successfully reach their destinations.

#### Reference

- [Conflict-based search for optimal multi-agent pathfinding](https://doi.org/10.1016/j.artint.2014.11.006)

### Conflict-Based Search with Priorities (CBSw/P)

Conflict-Based Search with Priorities (CBSw/P) works very similar to the traditional CBS. Here, however, the way in which constraints are imposed on the agents are always consistent by constructing a priority ordering between the agents. Following a conflict, the constraints added to the agents imply a priority difference between the involved agents. If a later conflict occurs between the same agents, the constraint must be imposed on the same agent as in the first conflict between them that later resulted in this conflict happening. Because of this behaviour, CBSw/P does not always try to impose constraints on both involved agents (if it would violate the priority ordering), hence why all possibilities following a conflict are not always explored. Therefore, CBSw/P is not optimal but generally finds a solution faster than ordinary CBS since less possibilities are explored.

#### Reference

- [Searching with Consistent Prioritization for Multi-Agent Path Finding]( 	
https://doi.org/10.48550/arXiv.1812.06356)

## How to Run the Program (The Flow of Running the Software)
Gå igenom vad som behövs i main filen och i vilken ordning. Säg att main-filen innehåller ett exempelfall.
