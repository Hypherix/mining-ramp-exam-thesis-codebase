package org.example.ICTSclasses;

import java.util.*;

public class JointMDDNode {

    // Data members
    public ArrayList<Integer> vertices;
    public ArrayList<JointMDDNode> children;

    // Constructors
    public JointMDDNode(ArrayList<Integer> vertices) {
        this.vertices = vertices;
        this.children = new ArrayList<>();
    }

    // Methods
}
