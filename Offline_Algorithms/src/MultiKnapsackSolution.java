package com.multiKnapsackAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;

public class MultiKnapsackSolution {
    public Double totalProfit;
    public Knapsack[] knapsacks;
    public ArrayList<Task> taskItems;
    {
        totalProfit=0.0;
    }
    MultiKnapsackSolution(Knapsack[] knapsacks){
        this.knapsacks=knapsacks;
    }
    MultiKnapsackSolution(Knapsack[] knapsacks,ArrayList<Task> taskItems){
        this(knapsacks);
        this.taskItems=taskItems;
    }
    public Knapsack[] getKnapsacks() {
        return knapsacks;
    }

    public void setKnapsacks(Knapsack[] knapsacks) {
        this.knapsacks = knapsacks;
    }

    public ArrayList<Task> getTaskItems() {
        return taskItems;
    }

    public void setTaskItems(ArrayList<Task> taskItems) {
        this.taskItems = taskItems;
    }

    public Double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }

    @Override
    public String toString() {
        return "KnapsackSolution{" +
                "totalProfit=" + totalProfit +
                ", knapsacks=" + Arrays.deepToString(knapsacks) +
                ", taskItems=" + taskItems +
                '}';
    }
}
