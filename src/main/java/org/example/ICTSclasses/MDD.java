package org.example.ICTSclasses;

public class MDD {

    // Data members
    public MDDNode root;

    // Constructors
    public MDD(MDDNode root) {
        this.root = root;
    }

    // Methods
    public int getRootVertex() {
        return this.root.vertex;
    }

}
