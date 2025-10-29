package com.multiKnapsackAlgorithm;

import com.multiKnapsackAlgorithm.SetUtility.PartitionSetBuilder;

import java.util.*;

public class MultiKnapsackExhaustiveSearchV2 extends GreedyAlgorithmBase{

    public MultiKnapsackExhaustiveSearchV2(String inputFilePath){
        super(inputFilePath);
        knapsackSolutionList=new ArrayList<>();
    }
    public MultiKnapsackExhaustiveSearchV2(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {
        super(taskDataTypeMatrix, requestItems, profitItems);
    }

    public MultiKnapsackExhaustiveSearchV2(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems, Knapsack[] knapsackItems) {
        super(taskDataTypeMatrix, requestItems, profitItems, knapsackItems);
    }

    List<MultiKnapsackSolution> multiKnapsackSolutionList;
    List<KnapsackSolution> knapsackSolutionList;
    /// This method is gonna solve the exhaustive Search paradigm
    @Override
    public void run(){
        CustomSet oCustomSet=new CustomSet<Task>(getTaskItems());
        oCustomSet.setPowerSet();
//        System.out.println( oCustomSet.getPowerSet().size());
        for(Object currentSubsetObj: oCustomSet.getPowerSet()){
            ArrayList<Task> currentSubset=(ArrayList<Task>)currentSubsetObj;
            Set<Task> taskBaseSet = new HashSet<Task>();
            taskBaseSet.addAll(currentSubset);
            PartitionSetBuilder<Task> oTaskPartSetBuilder = new PartitionSetBuilder<Task>(taskBaseSet);
            Set<Set<Set<Task>>> taskPartitionSets = oTaskPartSetBuilder.findAllPartitions();
//            System.out.println("BaseSet: " + taskBaseSet);
//            System.out.println("Result:  " + taskPartitionSets);
//            System.out.println("Base-Size: " + taskBaseSet.size() + " Result-Size: " + taskPartitionSets.size());
            Set<List<Set<Task>>>  preparedTaskPartitionSets=preparePartitionSetsForCurrentKnapsackProblem(
                    taskPartitionSets,getKnapsackItems().length);
//            System.out.println( preparedTaskPartitionSets.toString());

            Set<List<Set<Task>[]>> oAllPossiblePartitionPermutations= getAllPossiblePermutations(preparedTaskPartitionSets);
            try{
                List<MultiKnapsackSolutionV2> oAllPossibleSolutions= solveExhaustively(oAllPossiblePartitionPermutations);
                System.out.println(oAllPossibleSolutions);
            }catch(Exception oException){
                System.out.println("HM: MultiKnapsack Exhaustive Search v2 -> run ->: "+oException.getMessage());
            }
        }

    }

    //Finds all possible solutions
    private List<MultiKnapsackSolutionV2> solveExhaustively(Set<List<Set<Task>[]>> oAllPossiblePartitionPermutations) throws Exception{
        List<MultiKnapsackSolutionV2> oAllPossibleSolutions=new ArrayList<>();
        for(List<Set<Task>[]> oCurrentPartition : oAllPossiblePartitionPermutations){
            for(Set<Task>[] oCurrentPermutation: oCurrentPartition){
                //System.out.println( oCurrentPermutation);
                MultiKnapsackSolutionV2 oMultiKnapsackSolutionV2= solveMultiKnapsack(oCurrentPermutation,getKnapsackItems());
                oAllPossibleSolutions.add(oMultiKnapsackSolutionV2);
                //System.out.println(oMultiKnapsackSolutionV2);
            }
        }
        return oAllPossibleSolutions;
    }


    private MultiKnapsackSolutionV2 solveMultiKnapsack(Set<Task>[] oCurrentPermutation, Knapsack[] knapsackItems) throws Exception {
        MultiKnapsackSolutionV2 oMultiKnapsackSolutionV2=new MultiKnapsackSolutionV2();
        if(oCurrentPermutation.length!=knapsackItems.length){
            //throw new Exception("HM: The length of current permutation does not match with knapsack array length");
            return oMultiKnapsackSolutionV2;
        }

        for(int index=0;index<knapsackItems.length;index++){
            KnapsackSolution oCurrentKnapsackSolution = solveKnapsack(oCurrentPermutation[index],knapsackItems[index]);
            oMultiKnapsackSolutionV2.addKnapsackSolution(oCurrentKnapsackSolution);
        }
        return oMultiKnapsackSolutionV2;
    }


    KnapsackSolution solveKnapsack(Set<Task> taskItems,Knapsack currentKnapsack){
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
    private Set<List<Set<Task>[]>> getAllPossiblePermutations(Set<List<Set<Task>>> preparedTaskPartitionSets) {
        Set<List<Set<Task>[]>> oTaskPartitionPermutations=new HashSet<>();
        for(List<Set<Task>> oCurrentTaskList: preparedTaskPartitionSets){

            //List<Set<Task>[]> oCurrentTaskListPermutations=new ArrayList<>();

            Set<Task>[] taskSetsArray = new HashSet[oCurrentTaskList.size()];
            taskSetsArray=oCurrentTaskList.toArray(taskSetsArray);
            TaskSetPermutationBuilder genericPerm=new TaskSetPermutationBuilder(taskSetsArray);
            List<Set<Task>[]> permutationArrayList= genericPerm.getAllPermutations();

//            //Logging: Begin
//            for (Set<Task>[] currentPerm:permutationArrayList) {
//            System.out.println( Arrays.deepToString(currentPerm));
//            }
//            System.out.println(permutationArrayList.size());
//            //Logging: End
            oTaskPartitionPermutations.add(permutationArrayList);
        }
        return oTaskPartitionPermutations;
    }

    /// This method is supposed to prepare the partition for solving MultiKnapSack
    private Set<List<Set<Task>>> preparePartitionSetsForCurrentKnapsackProblem(
            Set<Set<Set<Task>>> taskPartitionSets, int knapsackArrayLength) {

        Set<List<Set<Task>>> oTempTaskPartitionSets=new HashSet<>();
        for (Set<Set<Task>> oTaskPartition : taskPartitionSets) {
            int remainedRequiredPartitions=knapsackArrayLength- oTaskPartition.size();
            List<Set<Task>> oTempTaskPartition=new ArrayList<Set<Task>>();
            oTempTaskPartition.addAll(oTaskPartition);
//            oTaskPartition.forEach(current->{
//                oTempTaskPartition.add(current);
//            });
            if(remainedRequiredPartitions>0){
                for (int i = 0; i < remainedRequiredPartitions; i++) {
                    oTempTaskPartition.add(new HashSet<Task>());
                }

            }
            oTempTaskPartitionSets.add(oTempTaskPartition);
        }
        return oTempTaskPartitionSets;
    }
}
