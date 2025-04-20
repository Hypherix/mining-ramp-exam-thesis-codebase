package org.example;
import java.util.Comparator;

/*
* Enables comparison between MAPFStates based on their costs.
* Needed for implementation of PriorityQueues
* */

public class StateComparator implements Comparator<MAPFState> {
    public int compare(MAPFState s1, MAPFState s2) {
        if(s1.getCost() < s2.getCost()) {
            return 1;
        }
        else if (s1.getCost() > s2.getCost()) {
            return -1;
        }
        return 0;
    }
}
