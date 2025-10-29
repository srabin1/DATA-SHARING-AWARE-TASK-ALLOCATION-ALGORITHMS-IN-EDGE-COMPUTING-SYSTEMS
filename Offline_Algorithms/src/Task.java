package com.multiKnapsackAlgorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class Task implements Comparable<Task>{
	private int index;
	private Double profit;
	private Double request;
	private DataType[] dataTypeItems;
	private Double efficiency;
	private boolean isCandid;


	// Helper property for DGreedy Algorithm
	private boolean isDataTypeNominated;

	public Task(int index, Double profit, Double request, DataType[] dataTypeItems){
		this( profit, request,  dataTypeItems);
		this.index=index;
	}
	public Task(Double profit, Double request, DataType[] dataTypeItems) {
		this.profit = profit;
		this.request = request;
		this.dataTypeItems = dataTypeItems;
		this.efficiency=Double.NaN;
	}
	public Task() {
	}
	public Task(int index){
		this();
		this.index=index;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * @return the profit
	 */
	public Double getProfit() {
		return profit;
	}
	/**
	 * @param profit the profit to set
	 */
	public void setProfit(Double profit) {
		this.profit = profit;
	}
	/**
	 * @return the request
	 */
	public Double getRequest() {
		return request;
	}

	public Double getEfficiency() {
		return efficiency;
	}
	public void setEfficiency(double efficiency){
		this.efficiency=efficiency;
	}
	public void setEfficiency(Knapsack[] knapsackItems) {
		if (knapsackItems==null)
			throw new NullPointerException("Knapsack Items has not been sent to input");
		Double efficiencyDbl=0.0;
		Double knapsackItemsCapacitySum=0.0;
		for (Knapsack knapsack:knapsackItems) {
			knapsackItemsCapacitySum+=knapsack.getCapacity();
		}
		efficiencyDbl= profit/ Math.sqrt(request/knapsackItemsCapacitySum);
		this.efficiency = efficiencyDbl;
	}

	public boolean isDataTypeNominated() {
		return isDataTypeNominated;
	}

	public void setDataTypeNominated(boolean dataTypeNominated) {
		isDataTypeNominated = dataTypeNominated;
	}
//	@Override
//	public String toString() {
//		return "\nTask [index= "+index+" ,profit=" + profit + ", request=" + request + ",\n dataTypeItems=" + Arrays.toString(dataTypeItems)
//				+", efficiency=" + efficiency
//				+", isUsed=" + isUsed +"]";
//	}
	@Override
	public String toString() {
		return "Task [index= "+index+" ,profit=" + profit + ", request=" + request
//				+ ",\n "
//				+"dataTypeItems=" + Arrays.toString(dataTypeItems)
//				+", efficiency=" + efficiency
//				+", isUsed=" + isUsed
				+"]\n";
	}
	/**
	 * @param request the request to set
	 */
	public void setRequest(Double request) {
		this.request = request;
	}
	
	public DataType[] getDataTypeItems() {
		return dataTypeItems;
	}
	public void setDataTypeItems(DataType[] dataTypeItems) {
		this.dataTypeItems = dataTypeItems;
	}

	///Helper methods
	public boolean belongsTo(ArrayList<Task> taskSet){
		for (Task taskItem:taskSet) {
			if(taskItem.equals(this)){
				return true;
			}
		}
		return false;
	}
	public static ArrayList<Task> union(ArrayList<Task> candidTaskItems,Task sortedTaskItem){
		for (Task currentTask:candidTaskItems) {
			if(currentTask.equals(sortedTaskItem)){
				return candidTaskItems;
			}
		}
		candidTaskItems.add(sortedTaskItem);
		return  candidTaskItems;
	}
	public static ArrayList<Task> subtract(ArrayList<Task> taskItems,Task newTaskItem)
			throws ArrayIndexOutOfBoundsException
	{
		for (Task currentTaskItem:taskItems) {
			if(currentTaskItem.equals(newTaskItem)){
				taskItems.remove(currentTaskItem);
				break;
			}
		}
		return taskItems;
	}
	@Override
	public int compareTo(Task o) {

		if(efficiency.isNaN() || o.efficiency.isNaN())
			try {
				throw new Exception("Task efficiency is not a number, therefore can not be comparable");
			} catch (Exception e) {
				e.printStackTrace();
			}
		if(efficiency<o.efficiency)
			return -1;
		else if (efficiency>o.efficiency)
			return 1;
		else
			return 0;
	}
	//This property is implemented to check if the the task is in candid task items or not
	public boolean isCandid() {
		return isCandid;
	}

	public void setCandid(boolean candid) {
		isCandid = candid;
	}
	//This property is implemented for exhaustive search purpose
	private boolean isUsed;

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean used) {
		isUsed = used;
	}

	public static Comparator<Task> TaskRequestComparator=new Comparator<Task>() {
		@Override
		public int compare(Task task1, Task task2) {
			Double task1Request=task1.getRequest();
			Double task2Request=task2.getRequest();
			//descending order
			return task2Request.compareTo(task1Request);

			//ascending order
			//return task1Request.compareTo(task2Request);
		}
	};
	public static Comparator<Task> TaskRequestComparatorAsc=new Comparator<Task>() {
		@Override
		public int compare(Task task1, Task task2) {
			Double task1Request=task1.getRequest();
			Double task2Request=task2.getRequest();


			//ascending order
			return task1Request.compareTo(task2Request);
		}
	};
}
