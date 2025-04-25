package org.example.algorithms;

/*
* The MAPFAlgorithm interface provides a solve method to all algorithms,
* which is called whenever a MAPFSolver wants to solve the MAPF scenario
*
* */

import org.example.MAPFScenario;
import org.example.Solution;

import java.util.ArrayList;

public interface MAPFAlgorithm {

    // Methods
    public Solution solve(MAPFScenario scenario);
}
