package com.multiKnapsackAlgorithm.PGreedy;

import com.multiKnapsackAlgorithm.DataType;
import com.multiKnapsackAlgorithm.GreedyAlgorithmBase;
import com.multiKnapsackAlgorithm.Knapsack;
import com.multiKnapsackAlgorithm.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/// AKA PGreedy V2
public class PGreedyAlgorithmV2 extends GreedyAlgorithmBase {

    public PGreedyAlgorithmV2(String inputFilePath) {
        super(inputFilePath);
    }

    public PGreedyAlgorithmV2(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {
        super(taskDataTypeMatrix, requestItems, profitItems);
    }

    public PGreedyAlgorithmV2(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems, Knapsack[] knapsackItems) {
        super(taskDataTypeMatrix, requestItems, profitItems, knapsackItems);
    }

    @Override
    public void run() {
        Arrays.sort(getKnapsackItems(), Collections.reverseOrder());
        setCandidTaskItems(new ArrayList<Task>());
        setCandidDataTypeItems(new ArrayList<DataType>());
        setTotalProfit(0.0);
        setTotalDataSize(0);
        System.out.println("MultiKnapsack Algorithm -> Run -> Set IsUsed Property: Begin" );
        for (Knapsack knapsackItem : getKnapsackItems()) {
            knapsackItem.setUsed(false);
            System.out.println( knapsackItem.toString());
        }
        System.out.println("MultiKnapsack Algorithm -> Run -> Set IsUsed Property: End" );
        while (!isTaskItemsAllCandid()){
            ArrayList<Integer> jTildaArray=new ArrayList<>();
            ArrayList<Double> profitArrayList=new ArrayList<>();
            getTaskItems().forEach(current->{
                if (!current.isCandid())
                    profitArrayList.add(current.getProfit());
                else
                    profitArrayList.add(0.0);
            });
            Double[] profitArray=new Double[profitArrayList.size()];
            profitArray=profitArrayList.toArray(profitArray);
            jTildaArray=getArgsMax(profitArray);

            // Implement JTilda Array Support
            int jTilda=0;
            if (jTildaArray.size()>1){
                jTilda= getRequestsArgMin(jTildaArray);
            }
            else{
                jTilda=jTildaArray.get(0);
            }
            //Greedy Algorithm without sharing data -> line 12
            Task currentTaskItem =getTaskItems().get(jTilda);
            for (Knapsack knapsackItem:getKnapsackItems()) {

                //Greedy Algorithm without sharing data -> line 13
                if(knapsackItem.isTaskFeasibleToAssign(currentTaskItem)	)
                {
                    //Greedy Algorithm without sharing data -> line 14
                    System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Before Union");
                    System.out.println(getCandidTaskItems().size());
                    setCandidTaskItems (Task.union(getCandidTaskItems(),currentTaskItem));
                    System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: After Union");
                    System.out.println(getCandidTaskItems().size());
                    System.out.println("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Done Union");

                    knapsackItem.setCapacity(knapsackItem.getCapacity()-currentTaskItem.getRequest());

                    knapsackItem.addTaskItem(currentTaskItem);

                    setTotalProfit(getTotalProfit()+currentTaskItem.getProfit());

//                    currentTaskItem.setCandid(true);
//                    getTaskItems().get(jTilda).setCandid(true);
                    // long startTime = System.currentTimeMillis();
                    //comment out line 80-86 to get a correct execution time for P-Greedy
                    for (DataType currentDataType:currentTaskItem.getDataTypeItems()) {
                        if(currentDataType.getSize()!=0)
                            //Greedy Algorithm without sharing data -> line 21
                            setCandidDataTypeItems( DataType.union(getCandidDataTypeItems(),currentDataType));
                        setTotalDataSize(getTotalDataSize() + currentDataType.getSize());

                    }
                    //long endTime = System.currentTimeMillis();
                    // System.out.println("That took " + (endTime - startTime) + " milliseconds");
                    break;
                }
            }
            currentTaskItem.setCandid(true);
            getTaskItems().get(jTilda).setCandid(true);

        }
    }
    private int getRequestsArgMin(ArrayList<Integer> jTildaArray){
        ArrayList<Task> jTildaArrayTaskItems=new ArrayList<>();
        //extracting task items according to jTildaArray input
        for (int i = 0; i <jTildaArray.size() ; i++) {
            jTildaArrayTaskItems.add(getTaskItems().get(jTildaArray.get(i)));
        }

        ArrayList<Double> requestArrayList=new ArrayList<>();
        jTildaArrayTaskItems.forEach(current->{
            requestArrayList.add(current.getRequest());
        });

        Double[] requestArray=new Double[requestArrayList.size()];
        requestArray=requestArrayList.toArray(requestArray);

        double min=Arrays.stream(requestArray).min(Double::compareTo).get();
        ArrayList<Integer> minArray=new ArrayList<>();
        for (int i=0;i<requestArray.length;i++){
            if (requestArray[i]==min){
                Task oTask=jTildaArrayTaskItems.get(i);
                int minTaskIndex=oTask.getIndex();
                return minTaskIndex;
            }
        }
        return 0;

    }
}
