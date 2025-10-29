package com.multiKnapsackAlgorithm;

import java.util.*;

public class ExhaustiveSearch extends GreedyAlgorithmBase {
    public ExhaustiveSearch(String inputFilePath){
        super(inputFilePath);
        knapsackSolutionList=new ArrayList<>();
    }
    public ExhaustiveSearch(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {
        super(taskDataTypeMatrix, requestItems, profitItems);
    }

    public ExhaustiveSearch(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems, Knapsack[] knapsackItems) {
        super(taskDataTypeMatrix, requestItems, profitItems, knapsackItems);
    }

    List<MultiKnapsackSolution> multiKnapsackSolutionList;
    List<KnapsackSolution> knapsackSolutionList;
    @Override
    public void run() {
        CustomSet oCustomSet=new CustomSet<Task>(getTaskItems());
        oCustomSet.setPowerSet();
//        oCustomSet.printPowerSet();
        System.out.println( oCustomSet.getPowerSet().size());
        List<KnapsackSolution>  knapsackSolutionLst= findKnapsackSolutionForPowerSetItems(oCustomSet.getPowerSet());
        for (KnapsackSolution oKnapsackSolution:knapsackSolutionLst){
             String str="Profit: "+oKnapsackSolution.getTotalProfit()+
                "\t\tCapacity: "+oKnapsackSolution.getKnapsack().getCapacity()+
                     "\t\tTaskCount: "+oKnapsackSolution.getKnapsack().getTaskItems().size();
            System.out.println(str);
        }
//        findBestSolution();
//        //Initializing the solution list
////        knapsackSolutionList=new ArrayList<KnapsackSolution>();
////        KnapsackSolution oKnapsackSolution= new KnapsackSolution(getKnapsackItems(),getTaskItems());
////        List<List<Task>> oTaskItemsPermutationList= buildAllPermutations(getTaskItems());
////        knapsackSolutionList= solveAllPermutations(getKnapsackItems(),oTaskItemsPermutationList);
////        KnapsackSolution oBestSolution =findBestSolution();
//
//
////        for (Knapsack currentKnapsack:oKnapsackSolution.getKnapsacks()) {
////            for(int index = 0; index <oKnapsackSolution.getTaskItems().size(); index++){
////                Task currentTask=oKnapsackSolution.getTaskItems().get(index);
////                if(currentKnapsack.getCapacity()>=currentTask.getRequest()){
////                    //assign this task to knapsack
////                    currentKnapsack.addTaskItem(currentTask);
////                    //set Knapsack capacity
////                    currentKnapsack.setCapacity(currentKnapsack.getCapacity()-currentTask.getRequest());
////
////                    currentKnapsack.setUsed(true);
////
////                    //setTotalProfit for the current knapsack solution
////                    oKnapsackSolution.setTotalProfit( oKnapsackSolution.getTotalProfit()+currentTask.getProfit());
////                }
////            }
////
////            System.out.println("---------Before removing ---------------");
////            System.out.println( oKnapsackSolution.getTaskItems().toString());
////            oKnapsackSolution.getTaskItems().removeIf(current->current.isUsed());
////            System.out.println("---------After removing ---------------");
////            System.out.println( oKnapsackSolution.getTaskItems().toString());
////            System.out.println("In Loop");
////        }
    }
    private List<KnapsackSolution>  findKnapsackSolutionForPowerSetItems(CustomSet.PowerSet powerSet){
        Knapsack currentKnapsack=getKnapsackItems()[0];
        powerSet.forEach(current->{
            KnapsackSolution oKnapsackSolution=null;
            oKnapsackSolution= solveKnapsack((ArrayList<Task>) current,currentKnapsack);
            System.out.println(oKnapsackSolution.toString());
            System.out.println("--------------------------------------------");
            knapsackSolutionList.add(oKnapsackSolution);
        });
        return knapsackSolutionList;
    }
    KnapsackSolution solveKnapsack(ArrayList<Task> taskItems,Knapsack currentKnapsack){
        Knapsack tempKnapsack = new Knapsack(0,currentKnapsack.getCapacity());
        Double profit=0.0;
        for (Task currentTask:taskItems){
            if (tempKnapsack.isTaskFeasibleToAssign(currentTask)){
                tempKnapsack.addTaskItem(currentTask);
                tempKnapsack.setCapacity(tempKnapsack.getCapacity()-currentTask.getRequest());
                profit+=currentTask.getProfit();
            }
        }
        KnapsackSolution oKnapsackSolution=new KnapsackSolution(tempKnapsack);
        oKnapsackSolution.setTotalProfit(profit);
        return oKnapsackSolution;
    }
    //Todo
    private KnapsackSolution findBestSolution() {
        return null;
    }

    private List<MultiKnapsackSolution> solveAllPermutations(Knapsack[] knapsackItems,List<List<Task>> oTaskItemsPermutationList) {
        for (List<Task> currentTaskItemsPermutation:oTaskItemsPermutationList) {
            MultiKnapsackSolution oKnapsackSolution= solveMultiKnapsack(currentTaskItemsPermutation);
            //Inserting new found solution in the solution array
            multiKnapsackSolutionList.add(oKnapsackSolution);
        }
        return multiKnapsackSolutionList;
    }

    private MultiKnapsackSolution solveMultiKnapsack(List<Task> oTaskItems) {
        MultiKnapsackSolution oKnapsackSolution=null;
        NaiveKnapsack.remainedTaskItems=new ArrayList<>();
        Knapsack[] knapsackArrayList=new Knapsack[getKnapsackItems().length];
        for(int index=0;index<knapsackArrayList.length;index++){
            System.out.println("Naive Knapsack: Begin ");
            Knapsack currentKnapsackSolution= NaiveKnapsack.knapsackByTaskItems(getKnapsackItems()[index],oTaskItems);
            knapsackArrayList[index]= currentKnapsackSolution;
            System.out.println("Naive Knapsack: End ");
        }
        oKnapsackSolution=new MultiKnapsackSolution(knapsackArrayList);
        System.out.println(oKnapsackSolution.toString());
        return oKnapsackSolution;
    }




}
