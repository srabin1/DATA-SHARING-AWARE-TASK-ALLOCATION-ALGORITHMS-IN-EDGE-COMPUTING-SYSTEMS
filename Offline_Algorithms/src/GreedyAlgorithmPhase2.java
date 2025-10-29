package com.multiKnapsackAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GreedyAlgorithmPhase2 extends GreedyAlgorithmBase {
    public GreedyAlgorithmPhase2(String inputFilePath){
        super(inputFilePath);
    }
    public GreedyAlgorithmPhase2(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {
        super(taskDataTypeMatrix, requestItems, profitItems);
    }

    public GreedyAlgorithmPhase2(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems, Knapsack[] knapsackItems) {
        super(taskDataTypeMatrix, requestItems, profitItems, knapsackItems);
    }

    @Override
    public void run() {

        setCandidTaskItems(new ArrayList<Task>());
        setCandidDataTypeItems(new ArrayList<DataType>());
        setTotalProfit(0.0);
        System.out.println("MultiKnapsack Algorithm -> Run -> Set IsUsed Property: Begin");
        for (Knapsack knapsackItem : getKnapsackItems()) {
            knapsackItem.setUsed(false);
            System.out.println(knapsackItem.toString());
        }
        System.out.println("MultiKnapsack Algorithm -> Run -> Set IsUsed Property: End");

        System.out.println("MultiKnapsack Algorithm -> Run -> Set Sum Property: Initiative: Begin");
        Integer[] sum = getSum();
        Arrays.fill(sum, 0);
        setSum(sum);
        System.out.println(Arrays.toString(getSum()));
        System.out.println("MultiKnapsack Algorithm -> Run -> Set Sum Property: Initiative: End");

        System.out.println("MultiKnapsack Algorithm -> Run -> Set Sum Property: Begin");
        setSum();
        System.out.println(Arrays.toString(getSum()));
        System.out.println("MultiKnapsack Algorithm -> Run -> Set Sum Property: End");


        while (!isTaskItemsEmpty()) {
            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime Property: Initiative: Begin");
            setSumPrimeByZero();
            Byte[] sumPrime = getSumPrime();
            System.out.println(Arrays.toString(sumPrime));
            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime Property: Initiative: End");

//			System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Adding Mock candidTaskItems: Begin" );
//			candidTaskItems.add(taskItems.get(0));
//			candidTaskItems.add(taskItems.get(3));
//			System.out.println(Arrays.deepToString(candidTaskItems.toArray()));
//			System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Adding Mock candidTaskItems: End" );

            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime By CandidTaskItems: Begin");
            setSumPrimeByCandidTaskItems();
            System.out.println("SumPrim: " + Arrays.toString(getSumPrime()));
            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime By CandidTaskItems: End");

            int jTilda = 0;
            ArrayList<Integer> jTildaArray=new ArrayList<>();
            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrimeSupport: Begin");
            int sumPrimeSupport = getSumPrimeSupport();
            System.out.println("Sum Prime Support: " + sumPrimeSupport);
            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrimeSupport: End");

            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set jTilda: Begin");
            if (sumPrimeSupport > 0) {

                //jTilda = getArgMax(getSum(), getSumPrime());
                jTildaArray=getArgsMax(getSum(),getSumPrime());
            } else {
                //jTilda = getArgMax(getSum());
                jTildaArray=getArgsMax(getSum());
            }
            // Implement JTilda Array Support
            if (jTildaArray.size()>1){
                jTilda= getSelectedDataTypeItemsSupport(jTildaArray);
            }
                else{
                    jTilda=jTildaArray.get(0);
            }
            System.out.println("jTilda: " + jTilda);
            System.out.println("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set jTilda: End");

            System.out.println("MultiKnapsack Algorithm -> Run ->\tupdate TaskItems Efficiency: Begin");
            ArrayList<Task> updatedTaskItems = updateTaskItemsEfficiency(jTilda);
            System.out.println(Arrays.deepToString(updatedTaskItems.toArray()));
            System.out.println("MultiKnapsack Algorithm -> Run ->\tupdate TaskItems Efficiency: End");


            System.out.println("MultiKnapsack Algorithm -> Run ->\tsorting tasks in non-decreasing order" +
                    " by efficiency: Begin");
            Task[] sortedTaskItems = new Task[getTaskItems().size()];
            sortedTaskItems = getTaskItems().toArray(sortedTaskItems);
            Arrays.sort(sortedTaskItems, Collections.reverseOrder());
            System.out.println(Arrays.deepToString(sortedTaskItems));
            System.out.println("MultiKnapsack Algorithm -> Run ->\tsorting tasks in non-decreasing order" +
                    " by efficiency: End");

            System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment: Begin");
            for (Task sortedTaskItem : sortedTaskItems) {
                if (sortedTaskItem.getEfficiency() > 0) {
                	// The flag is for double checking purpose
                	boolean flag=true;
                    Arrays.sort(getKnapsackItems(), Collections.reverseOrder());
                    for (Knapsack knapsackItem : getKnapsackItems()) {
                        //First check if task efficiency is still non-zero
                        //After assigning taskItem to any knapsack set its efficiency to zero(To not let it be added again)
//                        if (knapsackItem.isTaskFeasibleToAssign(sortedTaskItem)
//                                && sortedTaskItem.getEfficiency() > 0
//                        )
                      if (knapsackItem.isTaskFeasibleToAssign(sortedTaskItem)
                    		  && flag)
                        {
                            System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Before Union");
                            //System.out.println( Arrays.deepToString(candidTaskItems.toArray()));
                            System.out.println(getCandidTaskItems().size());
                            setCandidTaskItems(Task.union(getCandidTaskItems(), sortedTaskItem));
                            System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: After Union");
                            //System.out.println( Arrays.deepToString(candidTaskItems.toArray()).length());
                            System.out.println(getCandidTaskItems().size());
                            System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Done Union");


                            knapsackItem.setCapacity(knapsackItem.getCapacity() - sortedTaskItem.getRequest());

                            knapsackItem.addTaskItem(sortedTaskItem);
                            sortedTaskItem.setEfficiency(0.0);
                            flag=false;
                            setTotalProfit(getTotalProfit() + sortedTaskItem.getProfit());
                        }
                        setTaskItems(Task.subtract(getTaskItems(), sortedTaskItem));
                    }
                }
            }
            System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment: End");
            setCandidDataTypeItems(DataType.union(getCandidDataTypeItems(), getDataTypeItems()[jTilda]));
            sum[jTilda] = 0;
            setSum(sum);
        }
        setCandidDataTypeItemsProportionValue();
    }

}
