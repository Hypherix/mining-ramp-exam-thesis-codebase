package org.example.ICTSclasses;

public class MDD {

    // Data members
    public MDDNode root;

    // Constructors
    public MDD(MDDNode root) {
        this.root = root;
    }

    // Copy constructor
    public MDD(MDD other) {
        this.root = new MDDNode(other.root);
    }

    // Methods
    public int getRootVertex() {
        return this.root.vertex;
    }

}
