package com.multiKnapsackAlgorithm;

import java.util.ArrayList;

public class Knapsack extends KnapsackComparator implements Comparable<Knapsack> {
	private Double capacity;
	private int index;
    private ArrayList<Task> taskItems;
    private boolean isUsed;
    private Double value;
    //This property is just being used by PGreedyAlgorithmV2
    private Double totalDataSize;
	//This property is designed to use in PGreedyAlgorithm
	ArrayList<DataType> assignedDataTypeItems;

	public ArrayList<DataType> getAssignedDataTypeItems() {
		return assignedDataTypeItems;
	}

	public void setAssignedDataTypeItems(ArrayList<DataType> assignedDataTypeItems) {
		this.assignedDataTypeItems = assignedDataTypeItems;
	}
	public boolean existInDataTypeItems(DataType currentDataType){
		for (DataType dataTypeItem:this.assignedDataTypeItems) {
			if(dataTypeItem.getDataTypeName().equals(currentDataType.getDataTypeName())){
				return true;
			}
		}
		return false;
	}
	public Double getValue() {
		if(taskItems.size()==0)
			return 0.0;
		double val=0.0;
		for(Task current:taskItems){
			val+= current.getProfit();
		}
		return val;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	{
    	taskItems=new ArrayList<Task>();
    	isUsed=false;
    }

	public Double getTotalDataSize() {
		return totalDataSize;
	}

	public void setTotalDataSize(Double totalDataSize) {
		this.totalDataSize = totalDataSize;
	}

	public Knapsack(int index, Double capacity){
    	this(capacity);
    	this.index=index;
    	this.assignedDataTypeItems=new ArrayList<>();
	}
    public Knapsack(Double capacity) {
		this.capacity = capacity;
	}

	/**
	 * @return the capacity
	 */
	public Double getCapacity() {
		return capacity;
	}

	/**
	 * @param capacity the capacity to set
	 */
	public void setCapacity(Double capacity) {
		this.capacity = capacity;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean used) {
		isUsed = used;
	}

	//Helper methods
//	public boolean isTaskFeasibleToAssign(Task sortedTaskItem){
//		boolean isFeasibleCapacity= this.capacity-sortedTaskItem.getRequest()>=0;
//		if(isFeasibleCapacity && !sortedTaskItem.isCandid())
//			return true;
//		return false;
//	}
	public boolean isTaskFeasibleToAssign(Task sortedTaskItem){
		boolean isFeasibleCapacity= isTaskFeasibleToAssignCapacityWise(sortedTaskItem);
		if(isFeasibleCapacity && !sortedTaskItem.isCandid())
			return true;
		return false;
	}
	public boolean isTaskFeasibleToAssignCapacityWise(Task sortedTaskItem){
		boolean isFeasibleCapacity= this.capacity-sortedTaskItem.getRequest()>=0;
		return isFeasibleCapacity;
	}
	public  void addTaskItem(Task taskItem){
		this.taskItems= Task.union(this.taskItems,taskItem);
	}

	public ArrayList<Task> getTaskItems() {
		return taskItems;
	}

	@Override
	public int compareTo(Knapsack o) {
		Knapsack knapsack=(Knapsack) o;
		if(this.capacity<knapsack.capacity) {
			return -1;
		}else if(this.capacity>knapsack.capacity) {
			return 1;
		}else {
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Knapsack{" +
				"index=" + index +
				", capacity=" + capacity +
				", taskItems=" + taskItems +
				", isUsed=" + isUsed +
				'}';
	}


}
