package org.example;

import java.util.ArrayList;

public class MDDNode {

    // Data members
    public int vertex;
    public ArrayList<MDDNode> children;

    // Constructors
    public MDDNode(int vertex) {
        this.vertex = vertex;
        this.children = new ArrayList<>();
    }

    // Methods

}
