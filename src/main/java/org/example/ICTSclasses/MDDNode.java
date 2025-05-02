package org.example.ICTSclasses;

import java.util.ArrayList;

public class MDDNode {

    // Data members
    public int vertex;
    public ArrayList<MDDNode> children;
    public MDDNode parent;

    // Constructors
    public MDDNode(int vertex) {
        this.vertex = vertex;
        this.children = new ArrayList<>();
    }

    // Copy constructor
    public MDDNode(MDDNode other) {
        this.vertex = other.vertex;
        this.children = other.children;
    }

    // Methods

}
