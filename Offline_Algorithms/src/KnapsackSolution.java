package com.multiKnapsackAlgorithm;

import java.util.ArrayList;

public class KnapsackSolution {
    public Double totalProfit;
    private Knapsack knapsack;
    private ArrayList<Task> taskItems;
    {
        totalProfit=0.0;
    }
    KnapsackSolution(Knapsack knapsacks){
        this.knapsack=knapsacks;
    }
    KnapsackSolution(Knapsack knapsack,ArrayList<Task> taskItems){
        this(knapsack);
        this.taskItems=taskItems;
    }

    public Double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Knapsack getKnapsack() {
        return knapsack;
    }

    public void setKnapsack(Knapsack knapsack) {
        this.knapsack = knapsack;
    }

    public ArrayList<Task> getTaskItems() {
        return taskItems;
    }

    public void setTaskItems(ArrayList<Task> taskItems) {
        this.taskItems = taskItems;
    }

    @Override
    public String toString() {
        return "KnapsackSolution{" +
                "totalProfit=" + totalProfit +
                ", knapsack=" + knapsack.toString() +
                '}';
    }
}
