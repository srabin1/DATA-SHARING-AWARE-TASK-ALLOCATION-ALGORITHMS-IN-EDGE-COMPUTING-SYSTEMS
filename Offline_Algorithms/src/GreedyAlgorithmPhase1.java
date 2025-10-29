package com.multiKnapsackAlgorithm;

import com.multiKnapsackAlgorithm.hm.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class GreedyAlgorithmPhase1 extends GreedyAlgorithmBase{
	public GreedyAlgorithmPhase1(String inputFilePath){
		super(inputFilePath);
	}
	public GreedyAlgorithmPhase1(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {
		super(taskDataTypeMatrix, requestItems, profitItems);
	}

	public GreedyAlgorithmPhase1(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems, Knapsack[] knapsackItems) {
		super(taskDataTypeMatrix, requestItems, profitItems, knapsackItems);
	}

	@Override
	public void run() {
		Arrays.sort(getKnapsackItems(), Collections.reverseOrder());
		setCandidTaskItems(new ArrayList<Task>());
		setCandidDataTypeItems(new ArrayList<DataType>());
		setTotalProfit(0.0);
		setTotalDataSize(0);
		Logger.message("MultiKnapsack Algorithm -> Run -> Set IsUsed Property: Begin" );
		for (Knapsack knapsackItem : getKnapsackItems()) {
			knapsackItem.setUsed(false);
			Logger.message( knapsackItem.toString());
		}
		Logger.message("MultiKnapsack Algorithm -> Run -> Set IsUsed Property: End" );

		Logger.message("MultiKnapsack Algorithm -> Run -> Set Sum Property: Initiative: Begin" );
		Integer[] sum=getSum();
		Arrays.fill(sum,0);
		setSum(sum);
		Logger.message(Arrays.toString(getSum()));
		Logger.message("MultiKnapsack Algorithm -> Run -> Set Sum Property: Initiative: End" );

		Logger.message("MultiKnapsack Algorithm -> Run -> Set Sum Property: Begin" );
		setSum();
		Logger.message(Arrays.toString(getSum()));
		Logger.message("MultiKnapsack Algorithm -> Run -> Set Sum Property: End" );

		//!isTaskItemsEmpty()
		while ( !isTaskItemsAllCandid()){
			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime Property: Initiative: Begin" );
			setSumPrimeByZero();
			Byte[] sumPrime=getSumPrime();
			Logger.message(Arrays.toString(sumPrime));
			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime Property: Initiative: End" );

//			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Adding Mock candidTaskItems: Begin" );
//			candidTaskItems.add(taskItems.get(0));
//			candidTaskItems.add(taskItems.get(3));
//			Logger.message(Arrays.deepToString(candidTaskItems.toArray()));
//			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Adding Mock candidTaskItems: End" );

			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime By CandidTaskItems: Begin" );
			setSumPrimeByCandidTaskItems();
			Logger.message("SumPrim: "+ Arrays.toString(getSumPrime()));
			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrime By CandidTaskItems: End" );

			int jTilda=0;
			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrimeSupport: Begin" );
			int sumPrimeSupport=getSumPrimeSupport();
			Logger.message("Sum Prime Support: "+sumPrimeSupport);
			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set SumPrimeSupport: End" );

			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set jTilda: Begin" );
/*
JTilda first implementation without considering that for calculating
jTilda, we need an array first
* */
//			if(sumPrimeSupport>0){
//
//				jTilda=getArgMax(getSum(),getSumPrime());
//			}else{
//				jTilda=getArgMax(getSum());
//			}
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
			Logger.message("MultiKnapsack Algorithm -> Run ->\tWhile Loop ->Set jTilda: End" );

			Logger.message("MultiKnapsack Algorithm -> Run ->\tupdate TaskItems Efficiency: Begin");
			ArrayList<Task> updatedTaskItems= updateTaskItemsEfficiency(jTilda);
			Logger.message(Arrays.deepToString(updatedTaskItems.toArray()));
			Logger.message("MultiKnapsack Algorithm -> Run ->\tupdate TaskItems Efficiency: End");


			Logger.message("MultiKnapsack Algorithm -> Run ->\tsorting tasks in non-decreasing order" +
					" by efficiency: Begin");
			ArrayList<Task> nonCandidTasks=new ArrayList<Task>() ;
			for (Task currentTask:getTaskItems()) {
				if(!currentTask.isCandid()){
					nonCandidTasks.add(currentTask);
				}
			}
			Task[] sortedTaskItems=new Task[nonCandidTasks.size()];
			sortedTaskItems= nonCandidTasks.toArray(sortedTaskItems) ;

			Arrays.sort(sortedTaskItems, Collections.reverseOrder());
			Logger.message(Arrays.deepToString( sortedTaskItems));
			Logger.message("MultiKnapsack Algorithm -> Run ->\tsorting tasks in non-decreasing order" +
					" by efficiency: End");

			Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment: Begin");
			for (Task sortedTaskItem:sortedTaskItems) {
				if(sortedTaskItem.getEfficiency()>0){
					//The flag is for double checking purpose
					boolean flag=true;
					for (Knapsack knapsackItem:getKnapsackItems()) {
						//First check if task efficiency is still non-zero
						//After assigning taskItem to any knapsack set its efficiency to zero(To not let it be added again)
//						if(knapsackItem.isTaskFeasibleToAssign(sortedTaskItem)
//								&& sortedTaskItem.getEfficiency()>0
//						)
						// Greedy Algorithm: Version 1 -> line 34
						if(knapsackItem.isTaskFeasibleToAssign(sortedTaskItem)	)
						{

							Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Before Union");

							Logger.message(getCandidTaskItems().size());
							setCandidTaskItems (Task.union(getCandidTaskItems(),sortedTaskItem));
							Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: After Union");
							Logger.message(getCandidTaskItems().size());
							Logger.message("MultiKnapsack Algorithm -> Run ->\tknapsack task assignment->\t\tcandidTaskItem: Done Union");


							knapsackItem.setCapacity(knapsackItem.getCapacity()-sortedTaskItem.getRequest());

							knapsackItem.addTaskItem(sortedTaskItem);
							sortedTaskItem.setEfficiency(0.0);
							flag=false;
							setTotalProfit(getTotalProfit()+sortedTaskItem.getProfit());

							knapsackItem.setUsed(true);

						}
//						sortedTaskItem.setCandid(true);
////						setTaskItems(Task.subtract(getTaskItems(),sortedTaskItem));
//						for (Task currentTask:getTaskItems()) {
//							if( currentTask.getIndex()== sortedTaskItem.getIndex() ){
//								currentTask.setCandid(true);
//							}
//						}
						if (!flag)
						break;
					}
					sortedTaskItem.setCandid(true);
//						setTaskItems(Task.subtract(getTaskItems(),sortedTaskItem));
					for (Task currentTask:getTaskItems()) {
						if( currentTask.getIndex()== sortedTaskItem.getIndex() ){
							currentTask.setCandid(true);
						}
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
