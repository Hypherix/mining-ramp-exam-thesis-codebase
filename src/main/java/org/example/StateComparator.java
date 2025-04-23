package org.example;
import java.util.Comparator;

/*
* Enables comparison between MAPFStates based on their costs.
* Needed for implementation of PriorityQueues.
* Note! Equal cost states can be polled in any order, regardless of when they
* were enqueued. If tie-breaker is needed, create a counter data member for the
* MAPFState that keeps track of when it was enqueued, and bake it in to the comparator
* */

public class StateComparator implements Comparator<MAPFState> {
    public int compare(MAPFState s1, MAPFState s2) {
        if(s1.getFcost() < s2.getFcost()) {
            return -1;
        }
        else if (s1.getFcost() > s2.getFcost()) {
            return 1;
        }
        return 0;
    }
}
