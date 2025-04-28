package org.example;

public class MDD {

    // Data members
    private MDDNode root;

    // Constructors
    public MDD(MDDNode root) {
        this.root = root;
    }

    // Methods
    public void printNode(MDDNode node) {

    }

    public void printMDD() {
        // Task: Print the MD

        MDDNode currentNode = this.root;
        while(currentNode != null) {
            System.out.print("[" + currentNode.vertex + "]: " + currentNode.children + ", ");

            for(MDDNode child : currentNode.children) {
                // TODO: Create a method that prints the node and its children.
                //  Have the method under MDD or MDDNode(perhaps better in node?)
                //  Then call that method here
            }
        }
    }
}
