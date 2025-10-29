package com.multiKnapsackAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NaiveKnapsack {
    // A utility function that returns
    // maximum of two integers
    static Double max(Double a, Double b)
    {
        return (a > b) ? a : b;
    }


    // Returns the maximum value that
    // can be put in a knapsack of
    // capacity W
    static Double knapSack(
            Double W, Double wt[],
            Double val[], int n)
    {
        // Base Case
        if (n == 0 || W == 0)
             return 0.0;

        // If weight of the nth item is
        // more than Knapsack capacity W,
        // then this item cannot be included
        // in the optimal solution
        if (wt[n - 1] > W)
            return knapSack(W, wt, val, n - 1);

            // Return the maximum of two cases:
            // (1) nth item included
            // (2) not included
        else
            return max(
                    val[n - 1] + knapSack(W - wt[n - 1],
                            wt, val, n - 1),
                    knapSack(W, wt, val, n - 1));
    }
    static List<Task> remainedTaskItems;
    static Knapsack knapsackByTaskItems(Knapsack knapsack, List<Task> tasks){
        // Base Case
        if (tasks.size() == 0 || knapsack.getCapacity() == 0){
            return new Knapsack(-1,0.0);
        }
        // If weight of the nth item is
        // more than Knapsack capacity W,
        // then this item cannot be included
        // in the optimal solution
        if (tasks.get(tasks.size()-1).getRequest() > knapsack.getCapacity()){
            remainedTaskItems.add(tasks.get(tasks.size()-1));
            tasks.remove(tasks.size()-1);
            return knapsackByTaskItems(knapsack,tasks);
        }
        // Return the maximum of two cases:
        // (1) nth item included
        // (2) not included
        else{

            List<Task> tempTasks= tasks;
            tempTasks.remove(tasks.size()-1);

            Knapsack tempKnapsack=knapsack;
            tempKnapsack.setCapacity(tempKnapsack.getCapacity()-tasks.get(tasks.size()-1).getRequest());
            tempKnapsack.addTaskItem(tasks.get(tasks.size()-1));

            Knapsack knapsackWithLastTask= knapsackByTaskItems(tempKnapsack,tempTasks);


            Knapsack knapsackWithoutLastTask= knapsackByTaskItems(knapsack,tempTasks);

            Knapsack resultKnapsack= max(knapsackWithLastTask,knapsackWithoutLastTask);
            return resultKnapsack;
        }


    }
    // A utility function that returns
    // maximum of two integers
    static Knapsack max(Knapsack a, Knapsack b)
    {
        List<Knapsack> knapsackList=new ArrayList<>();
        knapsackList.add(a);
        knapsackList.add(b);
        Knapsack max=Collections.max(knapsackList,Knapsack::compareTo);
        return max;
    }
}
