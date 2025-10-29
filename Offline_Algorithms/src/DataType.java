package com.multiKnapsackAlgorithm;

import java.util.ArrayList;

public class DataType implements Comparable<DataType>{
	private String dataTypeName;
	private Integer size;
	private Integer degree;
	public DataType(String dataTypeName, Integer size) {
		this.dataTypeName = dataTypeName;
		this.size = size;
		this.degree=0;
	}

	public String getDataTypeName() {
		return dataTypeName;
	}

	public Integer getSize() {
		return size;
	}

	public Integer getDegree() {
		return degree;
	}
	public void setDegree(Integer degree) {
		this.degree = degree;
	}

	//Helper Methods
	public static ArrayList<DataType> union(ArrayList<DataType> dataTypeItems,DataType newDataTypeItem){
		for (DataType dataTypeItem:dataTypeItems) {
			if(dataTypeItem.equals(newDataTypeItem)){
				return dataTypeItems;
			}
		}
		dataTypeItems.add(newDataTypeItem);
		return dataTypeItems;
	}
	@Override
	public String toString() {
		return "DataType [dataTypeName=" + dataTypeName + ", size=" + size + "]";
	}


	@Override
	public int compareTo(DataType o) {
		return dataTypeName.compareTo(o.dataTypeName);
	}
	///Helper methods
	public boolean belongsTo(ArrayList<DataType> dataTypeSet) {
		for (DataType dataTypeItem:dataTypeSet) {
			if(dataTypeItem.equals(this)){
				return true;
			}
		}
		return false;
	}
}
