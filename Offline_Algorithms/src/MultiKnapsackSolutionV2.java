package com.multiKnapsackAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;

/// This entity is created to be used in MultiKnapsackExhaustiveSearchV2, previous version is being used in
// first generation of knapsack solvers
public class MultiKnapsackSolutionV2{
    public MultiKnapsackSolutionV2() {
        totalProfit=0.0;
        knapsackSolutions=new ArrayList<>();
    }

    public Double totalProfit;

    public Double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }
    ArrayList<KnapsackSolution> knapsackSolutions;

    public ArrayList<KnapsackSolution> getKnapsackSolutions() {
        return knapsackSolutions;
    }

    public void setKnapsackSolutions(ArrayList<KnapsackSolution> knapsackSolutions) {
        knapsackSolutions = knapsackSolutions;
    }
    public void addKnapsackSolution(KnapsackSolution oKnapsackSolution){
        knapsackSolutions.add(oKnapsackSolution);
        totalProfit+=oKnapsackSolution.getTotalProfit();
    }

    @Override
    public String toString() {
        return "MultiKnapsackSolutionV2{" +
                "totalProfit=" + totalProfit +
                ", knapsackSolutions=" + knapsackSolutions  +
                "}\n";
    }
}
