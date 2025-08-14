package org.rampTraffic;
import java.util.Comparator;

/*
* Enables comparison between MAPFStates based on their higher prio agent costs.
* If tie in f cost, look at h cost instead.
* If prioCosts are equal, go with all costs instead
*/

public class StatePrioComparator implements Comparator<MAPFState> {
    public int compare(MAPFState s1, MAPFState s2) {
        if (s1.getFCostPrio() < s2.getFCostPrio()) {
            return -1;
        }
        else if (s1.getFCostPrio() > s2.getFCostPrio()) {
            return 1;
        }
        else if (s1.getHcostPrio() < s2.getHcostPrio()) {
            return -1;
        }
        else if (s1.getHcostPrio() > s2.getHcostPrio()) {
            return 1;
        }
        else if (s1.getFCost() < s2.getFCost()) {
            return -1;
        }
        else if (s1.getFCost() > s2.getFCost()) {
            return 1;
        }
        else if (s1.getHCost() < s2.getHCost()) {
            return -1;
        }
        else if (s1.getHCost() > s2.getHCost()) {
            return 1;
        }
        return 0;
    }
}
