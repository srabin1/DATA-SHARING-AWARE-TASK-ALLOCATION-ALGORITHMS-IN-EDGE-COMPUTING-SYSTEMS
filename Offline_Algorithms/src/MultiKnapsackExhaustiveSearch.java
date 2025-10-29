package com.multiKnapsackAlgorithm;

        import java.util.*;

public class MultiKnapsackExhaustiveSearch extends GreedyAlgorithmBase {
    public MultiKnapsackExhaustiveSearch(String inputFilePath){
        super(inputFilePath);
        knapsackSolutionList=new ArrayList<>();
    }
    public MultiKnapsackExhaustiveSearch(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {
        super(taskDataTypeMatrix, requestItems, profitItems);
    }

    public MultiKnapsackExhaustiveSearch(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems, Knapsack[] knapsackItems) {
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
        System.out.println(knapsackSolutionList.size());
    }
    private List<KnapsackSolution>  findKnapsackSolutionForPowerSetItems(CustomSet.PowerSet powerSet){
        Knapsack currentKnapsack=getKnapsackItems()[0];
        powerSet.forEach(current->{
            for(Knapsack cKnapsack: getKnapsackItems()){
                KnapsackSolution oKnapsackSolution=null;
                oKnapsackSolution= solveKnapsack((ArrayList<Task>) current,cKnapsack);
                System.out.println(oKnapsackSolution.toString());
                System.out.println("--------------------------------------------");
                knapsackSolutionList.add(oKnapsackSolution);
            }
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
