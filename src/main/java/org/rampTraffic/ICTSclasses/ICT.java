package org.rampTraffic.ICTSclasses;


/*
* Contains a root ICTNode
* */

import java.util.*;

public class ICT {

    // Data members
    private ICTNode root;
    private ArrayList<ArrayList<Integer>> allCostVectors;   // Used to check for duplicate child pruning


    // Constructors
    public ICT(ICTNode root) {
        this.root = root;
    }

    public ICT() {
        root = new ICTNode();
        allCostVectors = new ArrayList<>();
    }

    // Methods
    public ICTNode getRoot() {
        return this.root;
    }

    public void setRoot(ICTNode root) {
        this.root = root;
    }

    public void addCostVector(ArrayList<Integer> costVector) {
        this.allCostVectors.add(costVector);
    }

    public ArrayList<ArrayList<Integer>> getAllCostVectors() {
        return this.allCostVectors;
    }
}
