package org.example;

import java.util.ArrayList;

/*
* An ICT node contains the following:
*  - A vector (ArrayList) of all agent path costs
*  - An MDD for each possible path with the vector-specified cost, for each agent
*  - An ArrayList of child ICT nodes
* */

public class ICTNode {

    // Data members
    ArrayList<Integer> costVector;
    ArrayList<MDD> agentPaths;
    ArrayList<ICTNode> children;


    // Constructors


    // Methods

}
