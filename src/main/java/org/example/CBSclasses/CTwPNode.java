package org.example.CBSclasses;

/*
* Extends CTNode. Only have priority as data member, the rest is inherited from CTNode
* */

import org.example.Agent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class CTwPNode extends CTNode {

    // Data members
    public HashMap<Agent, ArrayList<Agent>> partialOrderings;


    // Constructors
    public CTwPNode() {
        super();    // Call CTNode constructor
        this.partialOrderings = new HashMap<>();
    }

    public CTwPNode(HashMap<Agent, HashMap<Integer, Set<Integer>>> vertexConstraints,
                    HashMap<Agent, HashMap<Integer, Set<ArrayList<Integer>>>> edgeConstraints,
                    HashMap<Agent, ArrayList<Agent>> pairOrderings) {
        // Used for generating children where constraints and partialOrderings are inherited
        super(vertexConstraints, edgeConstraints);
        this.partialOrderings = pairOrderings;
    }


    // Methods

}
