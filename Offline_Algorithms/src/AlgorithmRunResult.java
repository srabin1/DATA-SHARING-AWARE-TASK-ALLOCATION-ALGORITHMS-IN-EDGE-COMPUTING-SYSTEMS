package com.multiKnapsackAlgorithm;

public class AlgorithmRunResult {
    private int instanceNo;
    private String testCaseName;
    private String algorithmName;
    private long elapsedTime;
    private Double totalProfit;
    private Integer totalDataSize;
    public AlgorithmRunResult(){

    }
    public int getInstanceNo() {
        return instanceNo;
    }

    public void setInstanceNo(int instanceNo) {
        this.instanceNo = instanceNo;
    }
    public String getTestCaseName() {
        return testCaseName;
    }

    public void setTestCaseName(String testCaseName) {
        this.testCaseName = testCaseName;
    }
    public String getAlgorithmName() {
        return algorithmName;
    }

    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Integer getTotalDataSize() {
        return totalDataSize;
    }

    public void setTotalDataSize(Integer totalDataSize) {
        this.totalDataSize = totalDataSize;
    }
}
