package org.example.algorithms;

/*
* The MAPFAlgorithm interface provides a solve method to all algorithms,
* which is called whenever a MAPFSolver wants to solve the MAPF scenario
*
* */

import org.example.MAPFScenario;
import org.example.MAPFState;
import java.util.ArrayList;

public interface MAPFAlgorithm {

    // Methods
    public ArrayList<MAPFState> solve(MAPFScenario scenario);
}
