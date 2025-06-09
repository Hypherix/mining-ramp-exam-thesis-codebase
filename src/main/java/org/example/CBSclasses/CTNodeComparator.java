package org.example.CBSclasses;
import java.util.Comparator;

/*
* Enables comparison between CTNodes based on their costs.
* Needed for implementation of CBS priority queue
* Lowest cost CT node is polled first --> best-first search
* */

public class CTNodeComparator implements Comparator<CTNode> {
    public int compare(CTNode n1, CTNode n2) {
        if (n1.cost < n2.cost) {
            return -1;
        }
        else if (n1.cost > n2.cost) {
            return 1;
        }
        return 0;
    }
}
