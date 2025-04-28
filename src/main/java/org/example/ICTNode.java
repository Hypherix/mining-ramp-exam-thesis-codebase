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
    // Change to private?
    public ArrayList<Integer> costVector;
    public ArrayList<MDD> agentPaths;
    public ArrayList<ICTNode> children;


    // Constructors
    public ICTNode() {
        this.agentPaths = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    // Methods

}
