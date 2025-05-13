package org.example.CBSclasses;
import java.util.Comparator;

/*
* Enables comparison between CTNodes based on their prioCosts (from higherPrio agents).
* If prioCosts are equal, go with all costs instead
* */

public class CTNodePrioComparator implements Comparator<CTNode> {
    public int compare(CTNode n1, CTNode n2) {
        if (n1.prioCost < n2.prioCost) {
            return -1;
        }
        else if (n1.prioCost > n2.prioCost) {
            return 1;
        }
        else if (n1.cost < n2.cost) {
            return -1;
        }
        else if (n1.cost > n2.cost) {
            return 1;
        }
        return 0;
    }
}
