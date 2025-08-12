package org.rampTraffic.ICTSclasses;

import java.util.ArrayList;

public class BFSTreeNode {

    // Data members
    public int location;
    public BFSTreeNode parent;
    public ArrayList<BFSTreeNode> children;

    // Constructors
    public BFSTreeNode(int location) {
        this.location = location;
        this.parent = null;
        this.children = new ArrayList<>();
    }

    // Methods

}
