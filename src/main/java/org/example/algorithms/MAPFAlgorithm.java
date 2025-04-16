package org.example.algorithms;

/*
* The MAPFAlgorithm interface provides a solve method to all algorithms,
* which is called whenever a MAPFSolver wants to solve the MAPF scenario
*
* */

import org.example.MAPFScenario;

public interface MAPFAlgorithm {

    // Methods
    public void solve(MAPFScenario scenario);
}
