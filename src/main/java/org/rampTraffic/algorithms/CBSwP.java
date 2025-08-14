package org.rampTraffic.algorithms;

import org.rampTraffic.CBSclasses.*;
import org.rampTraffic.*;

import java.util.*;

/*
* Extends CBS. The idea is that all methods will be identical to CBS. Priorities
* however only should affect child node generation, so override generateChildNodes method.
*
* */

public class CBSwP extends CBS {

    // Data members


    // Constructors


    // Methods

    @Override
    protected CTNode createRootNode() {
        return new CTwPNode();
    }

    @Override
    protected void generateChildren(CTNode ctParent, Conflict conflict) {
        // Generates two children, only if they do not violate parent partial ordering

        CTwPNode parent = (CTwPNode) ctParent;

        if(conflict.type != Conflict.ConflictType.PASSBAY_SAME_DIRECTION) {
            // Get the agents affected by the conflict
            Agent agent1 = conflict.agent1;
            Agent agent2 = conflict.agent2;

            // The first child implies the pair ordering where agent2 has prio over agent1. Check if this is a violation
            if (!parent.partialOrderings.containsKey(agent1) || !parent.partialOrderings.get(agent1).contains(agent2)) {

                CTwPNode leftChild = new CTwPNode(parent.vertexConstraints, parent.edgeConstraints);

                // Left child partial ordering is identical to its parent, with the addition that
                // agent2 has prio over agent1. Add this if not already present
                HashMap<Agent, ArrayList<Agent>> leftChildOrderings = new HashMap<>();
                for (Map.Entry<Agent, ArrayList<Agent>> entry : parent.partialOrderings.entrySet()) {
                    // Copy each entry from parent's partial orderings to leftChild's partialOrderings
                    leftChildOrderings.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
                leftChildOrderings.computeIfAbsent(agent2, _ -> new ArrayList<>()).add(agent1);
                leftChild.partialOrderings = leftChildOrderings;

                generateChildHelper(parent, leftChild, conflict, agent1, true);

                numOfGeneratedCTNodes++;
            }

            // Vice versa for right child
            if (!parent.partialOrderings.containsKey(agent2) || !parent.partialOrderings.get(agent2).contains(agent1)) {

                CTwPNode rightChild = new CTwPNode(parent.vertexConstraints, parent.edgeConstraints);

                HashMap<Agent, ArrayList<Agent>> rightChildOrderings = new HashMap<>();
                for (Map.Entry<Agent, ArrayList<Agent>> entry : parent.partialOrderings.entrySet()) {
                    rightChildOrderings.put(entry.getKey(), new ArrayList<>(entry.getValue()));
                }
                rightChildOrderings.computeIfAbsent(agent1, _ -> new ArrayList<>()).add(agent2);
                rightChild.partialOrderings = rightChildOrderings;

                generateChildHelper(parent, rightChild, conflict, agent2, false);

                numOfGeneratedCTNodes++;
            }
        }

        // IF passbay conflict in same direction, create only one child for the later agent, with same partial ordering
        else {
            Agent agent = conflict.agent1;
            CTwPNode onlyChild = new CTwPNode(parent.vertexConstraints, parent.edgeConstraints);

            HashMap<Agent, ArrayList<Agent>> onlyChildOrderings = new HashMap<>();
            for (Map.Entry<Agent, ArrayList<Agent>> entry : parent.partialOrderings.entrySet()) {
                onlyChildOrderings.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
            onlyChild.partialOrderings = onlyChildOrderings;

            generateChildHelper(parent, onlyChild, conflict, agent, true);

            numOfGeneratedCTNodes++;
        }
    }
}
