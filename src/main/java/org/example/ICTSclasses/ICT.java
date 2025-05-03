package org.example.ICTSclasses;


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

    // Copy constructor
    public ICT(ICT other) {
        this.root = new ICTNode(other.root);

        // Deep copy of allCostVectors
        this.allCostVectors = new ArrayList<>();
        for (ArrayList<Integer> vector : other.allCostVectors) {
            this.allCostVectors.add(new ArrayList<>(vector));
        }
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

    public void resetAllCostVectors() {
        this.allCostVectors.clear();
    }
}
