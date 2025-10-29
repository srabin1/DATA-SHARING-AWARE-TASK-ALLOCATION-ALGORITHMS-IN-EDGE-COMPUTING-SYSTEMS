package com.multiKnapsackAlgorithm;


import com.multiKnapsackAlgorithm.hm.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class DGreedyAlgorithm extends GreedyAlgorithmBase{
    public DGreedyAlgorithm(String inputFilePath){
        super(inputFilePath);
    }
    public DGreedyAlgorithm(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {
        super(taskDataTypeMatrix, requestItems, profitItems);
    }

    public DGreedyAlgorithm(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems, Knapsack[] knapsackItems) {
        super(taskDataTypeMatrix, requestItems, profitItems, knapsackItems);
    }
    @Override
    public void run() {
        Arrays.sort(getKnapsackItems(), Collections.reverseOrder());
        setCandidTaskItems(new ArrayList<Task>());
        setCandidDataTypeItems(new ArrayList<DataType>());
        setTotalProfit(0.0);
        setTotalDataSize(0);
        Logger.message("MultiKnapsack Algorithm: DGreedy -> Run -> Set IsUsed Property: Begin -> Line 5" );
        for (Knapsack knapsackItem : getKnapsackItems()) {
            knapsackItem.setUsed(false);
            Logger.message( knapsackItem.toString());
        }
        Logger.message("MultiKnapsack Algorithm: DGreedy -> Run -> Set IsUsed Property: End" );

        Logger.message("MultiKnapsack Algorithm: DGreedy  -> Run -> Set Sum Property: Initiative: Begin -> Line 6" );
        Integer[] sum=getSum();
        Arrays.fill(sum,0);
        setSum(sum);
        Logger.message(Arrays.toString(getSum()));
        Logger.message("MultiKnapsack Algorithm: DGreedy  -> Run -> Set Sum Property: Initiative: End" );

        Logger.message("MultiKnapsack Algorithm: DGreedy -> Run -> Set Sum Property: Begin -> Line 7,8,9" );
        setSum();
        Logger.message(Arrays.toString(getSum()));
        Logger.message("MultiKnapsack Algorithm: DGreedy -> Run -> Set Sum Property: End" );

        while (!isTaskItemsAllCandid()){
            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tWhile Loop ->Set SumPrime Property: Initiative: Begin -> line 11" );
            setSumPrimeByZero();
            Byte[] sumPrime=getSumPrime();
            Logger.message(Arrays.toString(sumPrime));
            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tWhile Loop ->Set SumPrime Property: Initiative: End" );

            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tWhile Loop ->Set SumPrime By CandidTaskItems: Begin" );
            setSumPrimeByCandidTaskItems();
            Logger.message("SumPrim: "+ Arrays.toString(getSumPrime()));
            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tWhile Loop ->Set SumPrime By CandidTaskItems: End" );

            int jTilda=0;
            Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrimeSupport: Begin" );
            int sumPrimeSupport=getSumPrimeSupport();
            Logger.message("Sum Prime Support: "+sumPrimeSupport);
            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tWhile Loop ->Set SumPrimeSupport: End" );

            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tWhile Loop ->Set jTilda: Begin" );

            ArrayList<Integer> jTildaArray=new ArrayList<>();
            if (sumPrimeSupport > 0) {
                jTildaArray=getArgsMax(getSum(),getSumPrime());
            } else {
                jTildaArray=getArgsMax(getSum());
            }

            // Implement JTilda Array Support
            if (jTildaArray.size()>1){
                jTilda= getSelectedDataTypeItemsSupport(jTildaArray);
            }
            else{
                jTilda=jTildaArray.get(0);
            }

            Logger.message("jTilda: "+jTilda);
            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tWhile Loop ->Set jTilda: End" );

            Logger.message("MultiKnapsack Algorithm -> Run ->\tupdate TaskItems Efficiency: Begin");
            ArrayList<Task> updatedTaskItems= updateTaskItemsByDataTypeNomination(jTilda);
            Logger.message(Arrays.deepToString(updatedTaskItems.toArray()));
            Logger.message("MultiKnapsack Algorithm -> Run ->\tupdate TaskItems Efficiency: End");

            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tsorting tasks in non-decreasing order" +
                    " by request: Begin");
            ArrayList<Task> nonCandidTasks=new ArrayList<Task>();

            for (Task currentTask:getTaskItems()) {
                if(!currentTask.isCandid() && currentTask.isDataTypeNominated()){
                    nonCandidTasks.add(currentTask);
                }
            }

            Task[] sortedTaskItems=new Task[nonCandidTasks.size()];
            sortedTaskItems= nonCandidTasks.toArray(sortedTaskItems) ;

            Arrays.sort(sortedTaskItems, Task.TaskRequestComparatorAsc);
            Logger.message(Arrays.deepToString( sortedTaskItems));
            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tsorting tasks in non-decreasing order" +
                    " by request: End");

            Logger.message("MultiKnapsack Algorithm: DGreedy -> Run ->\tknapsack task assignment: Begin");
            for (Task sortedTaskItem:sortedTaskItems) {
                for (Knapsack knapsackItem:getKnapsackItems()) {
                    // Greedy Algorithm: DGreedy -> line 28
                    if(knapsackItem.isTaskFeasibleToAssignCapacityWise(sortedTaskItem)) {
                        Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Before Union");
                        Logger.message(getCandidTaskItems().size());
                        setCandidTaskItems (Task.union(getCandidTaskItems(),sortedTaskItem));
                        Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: After Union");
                        Logger.message(getCandidTaskItems().size());
                        Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Done Union");

                        knapsackItem.setCapacity(knapsackItem.getCapacity()-sortedTaskItem.getRequest());

                        knapsackItem.addTaskItem(sortedTaskItem);

                        setTotalProfit(getTotalProfit()+sortedTaskItem.getProfit());

                        knapsackItem.setUsed(true);
                        //remove duplicated data assignments on servers
                       break;

                    }

                }
                sortedTaskItem.setCandid(true);
                for (Task currentTask:getTaskItems()) {
                    if( currentTask.getIndex()== sortedTaskItem.getIndex() ){
                        currentTask.setCandid(true);
                    }
                }
            }
            //Consider data assignment for each individual server: issue 19
            int candidDataTypeItemCounter=0;
            for (Knapsack knapsackItem : getKnapsackItems()) {
                candidDataTypeItemCounter++;
            }

            Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment: End");
            setCandidDataTypeItems(DataType.union(getCandidDataTypeItems(),getDataTypeItems()[jTilda]));
           // setTotalDataSize(getTotalDataSize() + getDataTypeItems()[jTilda].getSize());
            setTotalDataSize(getTotalDataSize() + getDataTypeItems()[jTilda].getSize()*candidDataTypeItemCounter);

            sum[jTilda]=0;
            setSum(sum);
            for (Knapsack currentKnapsackItem : getKnapsackItems()) {
                currentKnapsackItem.setUsed(false);
            }
        }


        setCandidDataTypeItemsByCheckingFinalStates();
        ArrayList<DataType>  finalCandidDataTypeItems= getCandidDataTypeItems();
        setCandidDataTypeItemsProportionValue();
    }

}
