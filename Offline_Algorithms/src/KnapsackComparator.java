package com.multiKnapsackAlgorithm;

import java.util.Comparator;

public class KnapsackComparator implements Comparator<Knapsack> {

    @Override
    public  int compare(Knapsack o1, Knapsack o2) {
        if(o1.getValue()<o2.getValue()) {
            return -1;
        }else if(o1.getValue()<o2.getValue()) {
            return 1;
        }else {
            return 0;
        }
    }
}
