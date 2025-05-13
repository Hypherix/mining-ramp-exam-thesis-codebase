package org.example.algorithms;

/*
* The MAPFAlgorithm interface provides a solve method to all algorithms,
* which is called whenever a MAPFSolver wants to solve the MAPF scenario
*
* */

import org.example.MAPFScenario;
import org.example.MAPFSolution;

public interface MAPFAlgorithm {

    // Methods
    public MAPFSolution solve(MAPFScenario scenario, boolean prioritise);
}
