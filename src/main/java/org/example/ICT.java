package org.example;


/*
* Contains a root ICTNode
* */

public class ICT {

    // Data members
    private ICTNode root;


    // Constructors
    public ICT(ICTNode root) {
        this.root = root;
    }

    public ICT() {
        root = new ICTNode();
    }

    // Methods
    public ICTNode getRoot() {
        return this.root;
    }

    public void setRoot(ICTNode root) {
        this.root = root;
    }
}
