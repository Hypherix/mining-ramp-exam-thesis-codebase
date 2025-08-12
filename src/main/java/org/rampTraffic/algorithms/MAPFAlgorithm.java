package org.rampTraffic.algorithms;

/*
* The MAPFAlgorithm interface provides a solve method to all algorithms,
* which is called whenever a MAPFSolver wants to solve the MAPF scenario
*
* */

import org.rampTraffic.MAPFScenario;
import org.rampTraffic.MAPFSolution;

public interface MAPFAlgorithm {

    // Methods
    public MAPFSolution solve(MAPFScenario scenario, boolean prioritise);
}
