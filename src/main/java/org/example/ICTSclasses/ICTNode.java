package org.example.ICTSclasses;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/*
* An ICT node contains the following:
*  - A vector (ArrayList) of all agent path costs
*  - An MDD for each possible path with the vector-specified cost, for each agent
*  - An ArrayList of child ICT nodes
* */

public class ICTNode {

    // Data members
    // Change to private?
    public ArrayList<Integer> costVector;
    public ArrayList<ArrayList<MDD>> agentPaths;
    public ArrayList<ICTNode> children;
    private Queue<ICTNode> ictQueue;

    // Constructors
    public ICTNode() {
        this.costVector = new ArrayList<>();
        this.agentPaths = new ArrayList<>();
        this.children = new ArrayList<>();
        this.ictQueue = new LinkedList<>();
    }

    // Copy constructor
    public ICTNode(ICTNode other) {
        this.costVector = new ArrayList<>(other.costVector);

        this.agentPaths = new ArrayList<>();
        for (ArrayList<MDD> pathList : other.agentPaths) {
            ArrayList<MDD> copiedList = new ArrayList<>();
            for (MDD mdd : pathList) {
                copiedList.add(new MDD(mdd));
            }
            this.agentPaths.add(copiedList);
        }

        this.children = new ArrayList<>();
        for (ICTNode child : other.children) {
            this.children.add(new ICTNode(child));
        }

        this.ictQueue = new LinkedList<>();
        for(ICTNode node : other.ictQueue) {
            this.ictQueue.add(new ICTNode(node));
        }
    }

    // Methods
    public void setIctQueue(Queue<ICTNode> ictQueue) {
        this.ictQueue = ictQueue;
    }

    public Queue<ICTNode> getIctQueue() {
        return this.ictQueue;
    }

    public int costVectorSum() {
        int sum = 0;
        for(Integer cost : this.costVector) {
            sum += cost;
        }
        return sum;
    }
}
