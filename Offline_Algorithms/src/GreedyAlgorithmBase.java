package com.multiKnapsackAlgorithm;

import com.multiKnapsackAlgorithm.hm.Logger;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public abstract class GreedyAlgorithmBase {
    private ArrayList<Task> taskItems;
    private Double[] requestItems;
    private Double[] profitItems;
    private Integer taskCount;
    private Integer[][] taskDataTypeMatrix;
    private Integer knapsackCount;
    private Knapsack[] knapsackItems;
    private DataType[] dataTypeItems;
    private int dataTypeItemsCount;
    private Integer[] sum;
    private Byte[] sumPrime;
    private ArrayList<Task> candidTaskItems;
    private ArrayList<DataType> candidDataTypeItems;
    private Double totalProfit;
    private Integer totalDataSize;


    /**
	 * @return the totalDataSize
	 */
	public Integer getTotalDataSize() {
		return totalDataSize;
	}

	/**
	 * @param totalDataSize the totalDataSize to set
	 */
	public void setTotalDataSize(Integer totalDataSize) {
		this.totalDataSize = totalDataSize;
	}
	
	public void setTotalDataSize() {
		Integer totalDataSize = 0;
		for (int i=0; i<candidDataTypeItems.size(); i++) {
			//if (dataTypeItems.)
			totalDataSize+= candidDataTypeItems.get(i).getSize();
			
		}
		this.totalDataSize = totalDataSize;
	}
	
	private Double candidDataTypeItemsProportionValue;
    {
        taskItems=new ArrayList<Task>();
        taskCount=Integer.MIN_VALUE;
        knapsackCount=Integer.MIN_VALUE;
        candidDataTypeItems=new ArrayList<DataType>();
    }

    public Double[] getProfitItems() {
        return profitItems;
    }

    public void setProfitItems(Double[] profitItems) {
        this.profitItems = profitItems;
    }

    public Double[] getRequestItems() {
        return requestItems;
    }

    public void setRequestItems(Double[] requestItems) {
        this.requestItems = requestItems;
    }

    public Knapsack[] getKnapsackItems() {
        return knapsackItems;
    }

    public void setKnapsackItems(Knapsack[] knapsackItems) {
        this.knapsackItems = knapsackItems;
    }
    public void setKnapsackItems(String line,String splitter){
        Double[] knapsackItemsCapacity= Helper.readDoubleArrayFromString(line,0,",");
        List<Knapsack> knapsackList=new ArrayList<>();
//        Arrays.asList(knapsackItemsCapacity).forEach(current->{
//            knapsackList.add(new Knapsack(index, current));
//            index++;
//        });
        List<Double> knapsackItemsCapacityArrayList=new ArrayList<>();
        knapsackItemsCapacityArrayList= Arrays.asList(knapsackItemsCapacity);

        for (int index=0;index<knapsackItemsCapacityArrayList.size();index++){
            knapsackList.add(new Knapsack(index, knapsackItemsCapacity[index]));
        }
        this.knapsackItems=knapsackList.toArray(new Knapsack[knapsackList.size()]);
    }
    public ArrayList<Task> getCandidTaskItems() {
        return candidTaskItems;
    }

    public ArrayList<DataType> getCandidDataTypeItems() {
        return candidDataTypeItems;
    }

    public void setCandidDataTypeItems(ArrayList<DataType> candidDataTypeItems) {
        this.candidDataTypeItems = candidDataTypeItems;
    }

    public Double getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(Double totalProfit) {
        this.totalProfit = totalProfit;
    }

    public Integer[] getSum() {
        return sum;
    }

    public void setSum(Integer[] sum) {
        this.sum = sum;
    }
    public void setSum(){
        for (int index = 0; index < dataTypeItems.length; index++) {
            DataType currentDataType= dataTypeItems[index];
            sum[index]= setSum(currentDataType.getDataTypeName());
        }
    }

    public Byte[] getSumPrime() {
        return sumPrime;
    }

    public ArrayList<Task> getTaskItems() {
        return taskItems;
    }

    public void setTaskItems(ArrayList<Task> taskItems) {
        this.taskItems = taskItems;
    }

    public DataType[] getDataTypeItems() {
        return dataTypeItems;
    }

    public void setDataTypeItems(DataType[] dataTypeItems) {
        this.dataTypeItems = dataTypeItems;
    }
    public GreedyAlgorithmBase(String inputFilePath){
        try{
            int currentLine=0;
            List<String> lines= Helper.readLinesFromInputFile(inputFilePath);
            int taskDataTypeMatrixSize=Integer.valueOf(  lines.get(currentLine));
            Logger.message(taskDataTypeMatrixSize);

            this.taskDataTypeMatrix=Helper.read2DIntegerArrayFromStringList(lines,taskDataTypeMatrixSize," ");
            currentLine+=taskDataTypeMatrixSize+1;
            Logger.message(Arrays.deepToString( taskDataTypeMatrix));

            this.requestItems=Helper.readDoubleArrayFromString(lines.get(currentLine),taskDataTypeMatrixSize,",");
            currentLine++;
            Logger.message(Arrays.deepToString(requestItems));

            this.profitItems=Helper.readDoubleArrayFromString(lines.get(currentLine),taskDataTypeMatrixSize,",");
            currentLine++;
            Logger.message(Arrays.deepToString(profitItems));

            this.initialize();

            this.setKnapsackItems(lines.get(currentLine),",");
            Logger.message(Arrays.deepToString( knapsackItems));

        }catch (IOException ioException){
            ioException.printStackTrace();
        }catch (SecurityException securityException){
            securityException.printStackTrace();
        }
    }


    public void initialize(){
        this.dataTypeItems=extractDataTypeItemsFromMatrixRow(taskDataTypeMatrix[0]);
        this.dataTypeItemsCount=this.dataTypeItems.length;
        this.sum=new Integer[this.dataTypeItemsCount];

        for (int row=0;row<taskDataTypeMatrix.length;row++) {
            Task oTask=new Task(row);

            Integer[] taskRow= taskDataTypeMatrix[row];
            DataType[] dataTypeItems= extractDataTypeItemsFromMatrixRow(taskRow);
            oTask.setDataTypeItems(dataTypeItems);

            Double profit=profitItems[row];
            oTask.setProfit(profit);

            Double request=requestItems[row];
            oTask.setRequest(request);

            addTaskItem(oTask);
        }
    }
    public GreedyAlgorithmBase(Integer[][] taskDataTypeMatrix, Double[] requestItems, Double[] profitItems) {

        this.taskDataTypeMatrix = taskDataTypeMatrix;
        this.requestItems=requestItems;
        this.profitItems=profitItems;
        this.initialize();
        

    }
    public GreedyAlgorithmBase(Integer[][] taskDataTypeMatrix,Double[] requestItems,Double[] profitItems, Knapsack[] knapsackItems) {
        this(taskDataTypeMatrix,requestItems,profitItems);
        this.knapsackItems = knapsackItems;
    }
    private DataType[] extractDataTypeItemsFromMatrixRow(Integer[] taskRow) {
        //DataType[] dataTypeItems=null;
        ArrayList<DataType> dataTypeItemsArrayList= new ArrayList<DataType>();
        for (int index=0;index<taskRow.length;index++) {
            String dataTypeName=String.valueOf(index) ;
            DataType oDataType=new DataType(dataTypeName,taskRow[index]);
            dataTypeItemsArrayList.add(oDataType);
        }

        DataType[] dataTypeItems=new DataType[dataTypeItemsArrayList.size()];
        dataTypeItems=	dataTypeItemsArrayList.toArray(dataTypeItems);
        return dataTypeItems;
    }
    private void addTaskItem(Task oTask) {
        taskItems.add(oTask);
    }

    public void setCandidTaskItems(ArrayList<Task> candidTaskItems) {
        this.candidTaskItems = candidTaskItems;
    }
    public ArrayList<Task> updateTaskItemsEfficiency(int jTilda){
        for (Task taskItem:taskItems) {
            taskItem.setEfficiency(0.0);
            if (isTaskFeasibleForEfficiencyUpdate(taskItem,jTilda)){
                taskItem.setEfficiency(knapsackItems);
            }
        }
        return taskItems;
    }
    boolean isTaskFeasibleForEfficiencyUpdate(Task taskItem,int jTilda){
        boolean isFeasible= taskItem.getDataTypeItems()[jTilda].getSize()!=0;
        return isFeasible;
    }

    public int getArgMax(Integer[] product){
        int max = Arrays.stream(product).max(Integer::compareTo).get();
        //return max;
        for (int i=0;i<product.length;i++){
            if (product[i]==max){
                return i;
            }
        }

        return 0;
    }
    /*Helper methods for DGreedyAlgorithm*/
    public ArrayList<Task> updateTaskItemsByDataTypeNomination(int jTilda){
        for (Task taskItem:taskItems) {
            taskItem.setDataTypeNominated(false);
            if (isTaskFeasibleForNomiationUpdate(taskItem,jTilda)){
                taskItem.setDataTypeNominated(true);
            }
        }
        return taskItems;
    }
    boolean isTaskFeasibleForNomiationUpdate(Task taskItem,int jTilda){
        boolean isNominated= taskItem.getDataTypeItems()[jTilda].getSize()!=0;
        return isNominated;
    }
    /*End: Helper methods for DGreedyAlgorithm*/
    ///Greedy Algorithm 3: without sharing Data
    public  ArrayList<Integer> getArgsMax(Double[] product){
        double max = Arrays.stream(product).max(Double::compareTo).get();
        ArrayList<Integer> maxArray=new ArrayList<>();
        for (int i=0;i<product.length;i++){
            if (product[i]==max){
                maxArray.add(i);
            }
        }
        return maxArray;
    }
    ///End: Greedy Algorithm 3: without sharing Data
/// Revision for getting args max in set format
    /// Greedy Algorithm: Version 2: line 20
public  ArrayList<Integer> getArgsMax(Integer[] product){
    int max = Arrays.stream(product).max(Integer::compareTo).get();
    ArrayList<Integer> maxArray=new ArrayList<>();
    for (int i=0;i<product.length;i++){
        if (product[i]==max){
            maxArray.add(i);
        }
    }
    return maxArray;
}
    /// Greedy Algorithm: Version 2: line 18
    public ArrayList<Integer> getArgsMax(Integer[] sum, Byte[] sumPrime ){
        Integer[] product=getProductArray( sum,sumPrime);
        Logger.message("productArray(sum,sumPrime): "+ Arrays.toString(product));
        return getArgsMax(product);
    }
/// End: Revision for getting args max in set format

    public int getArgMax(Integer[] sum, Byte[] sumPrime){
        Integer[] product=getProductArray( sum,sumPrime);
        Logger.message("productArray(sum,sumPrime): "+ Arrays.toString(product));
        return getArgMax(product);

    }
    private Integer[] getProductArray(Integer[] sum,Byte[] sumPrime){
        Integer[] product=new Integer[this.dataTypeItemsCount];
        Arrays.setAll(product,current->sum[current]*sumPrime[current]);
        return product;
    }
    public int getSumPrimeSupport(){
        int sum= 0;
        for (byte item:sumPrime) {
            sum+=item;
        }

        return sum;
    }
    /// Greedy Algorithm: Version 2: line 22
    public int getSelectedDataTypeItemsSupport(ArrayList<Integer> jTildaArray){
            Map<Integer,Integer> supports=new TreeMap<Integer,Integer>();

            for (Integer jTildaArrayItem:jTildaArray) {
                supports.put(jTildaArrayItem, getSelectedDataTypeItemSupport(jTildaArrayItem));
            }
            int max= supports.values().stream().max(Integer::compareTo).get();

            for(Integer item:supports.keySet()){
                if(supports.get(item)==max)
                    return item;
            }
            return  max;
    }
    /// Greedy Algorithm: Version 2: line 22
    public int getSelectedDataTypeItemSupport(int dataTypeItemIndex){

            DataType currentDataType= dataTypeItems[dataTypeItemIndex];
            int nonZeroElementCount=0;
            for (Task taskItem:taskItems) {
                for (DataType dataTypeItem : taskItem.getDataTypeItems()) {
                    if(  dataTypeItem.getDataTypeName().equals(currentDataType.getDataTypeName())){
                        nonZeroElementCount++;
                    }
                }
            }
        return nonZeroElementCount;
    }
    public void setSumPrimeByCandidTaskItems(){
        for (Task taskItem:taskItems) {
            if (taskItem.belongsTo(candidTaskItems)){
                setSumPrime(candidDataTypeItems,taskItem);
            }
        }
    }

    public boolean isTaskItemsEmpty(){
        if(taskItems.size()==0)
            return true;
        return false;
    }
    protected boolean isTaskItemsAllCandid(){
        for (Task current:taskItems) {
            if (!current.isCandid())
                return false;
        }
        return true;
    }
    private boolean isCandidTaskItemsEmpty() {
        if(candidTaskItems.size()==0)
            return true;
        return false;
    }



    public abstract void run();

    public void setSumPrimeByZero() {
        this.sumPrime=new Byte[dataTypeItemsCount];
        Arrays.fill(this.sumPrime, (byte)0);
    }

    ///Calculate DataType Summation by getting datatype name
    private Integer setSum(String dataTypeName){
        int sum=0;
        for (Task taskItem:taskItems) {
            for (DataType dataTypeItem : taskItem.getDataTypeItems()) {
                if(  dataTypeItem.getDataTypeName().equals(dataTypeName)){
                    sum+= dataTypeItem.getSize();
//					break;
                }
            }
        }
        return sum;
    }

    private void setSumPrime(ArrayList<DataType> candidDataTypeItems,Task  currentTaskItem){
        for (int index = 0; index < dataTypeItemsCount; index++) {
            setSumPrimeByIndex(index,candidDataTypeItems,currentTaskItem);
        }
    }
    private void setSumPrimeByIndex(int index, ArrayList<DataType> candidDataTypeItems,Task  currentTaskItem){
        if (!currentTaskItem.getDataTypeItems()[index].belongsTo(candidDataTypeItems)
                && currentTaskItem.getDataTypeItems()[index].getSize()!=0)
            sumPrime[index]=(byte)1;
    }
    public Double getCandidDataTypeItemsProportionValue() {
        return candidDataTypeItemsProportionValue;
    }

    public void setCandidDataTypeItemsProportionValue() {
        ArrayList<DataType> candidDataTypeItems= getCandidDataTypeItems();

        for (Task current:getTaskItems()) {
            setDataTypeItemsDegreeByTaskItem(current);
        }
        for (Task current:getCandidTaskItems()) {
            setDataTypeItemsDegreeByTaskItem(current);
        }
//        getTaskItems().forEach(current->{
//            DataType[] currentTaskDataTypeItems= current.getDataTypeItems();
//            for (int dataTypeIndex=0; dataTypeIndex<currentTaskDataTypeItems.length;dataTypeIndex++) {
//                if (currentTaskDataTypeItems[dataTypeIndex].getSize()>0){
//                    Integer degree= getDataTypeItems()[dataTypeIndex].getDegree()+1;
//                    getDataTypeItems()[dataTypeIndex]
//                                    .setDegree(degree);
//                }
//            }
//        });
        double dataTypeItemsDegreeSum=0.0;
        double numberOfSharedCandidDataTypeItems=0.0;
        for (int dataTypeIndex = 0; dataTypeIndex <dataTypeItems.length ; dataTypeIndex++) {

            if (dataTypeItems[dataTypeIndex].getDegree()>1){
                dataTypeItemsDegreeSum+=1.0;
                if (isSharedCandidDataType(dataTypeItems[dataTypeIndex])){
                    numberOfSharedCandidDataTypeItems+=1;
                }
            }
        }

        //double candidDataTypeItemsSize= getCandidDataTypeItems().size();
        //this.candidDataTypeItemsProportionValue =  candidDataTypeItemsSize/dataTypeItemsDegreeSum;
        this.candidDataTypeItemsProportionValue =  numberOfSharedCandidDataTypeItems/dataTypeItemsDegreeSum;
    }

    private boolean isSharedCandidDataType(DataType currentDataTypeItem){
        ArrayList<DataType> candidDataTypeItems=getCandidDataTypeItems();
        for (DataType currentCandidDataTypeItem:candidDataTypeItems) {
            if(currentDataTypeItem.getDataTypeName().equals(currentCandidDataTypeItem.getDataTypeName()))
                return true;
        }
        return false;
    }

    private void setDataTypeItemsDegreeByTaskItem(Task current){
        DataType[] currentTaskDataTypeItems= current.getDataTypeItems();
        for (int dataTypeIndex=0; dataTypeIndex<currentTaskDataTypeItems.length;dataTypeIndex++) {
            if (currentTaskDataTypeItems[dataTypeIndex].getSize()>0){
                Integer degree= getDataTypeItems()[dataTypeIndex].getDegree()+1;
                getDataTypeItems()[dataTypeIndex]
                        .setDegree(degree);
            }
        }
    }
    /*
        This method is for the last iteration, when the final task is assigned to corresponding knapsack while
        its shared data is not assigned to the Candid Data Type array
    */
    public void setCandidDataTypeItemsByCheckingFinalStates() {
        for (Task taskItem:taskItems) {
            if (taskItem.belongsTo(candidTaskItems)){
                updateCandidDataTypeItemIfIsNotIncluded( candidDataTypeItems, taskItem);
            }
        }
    }
    public void updateCandidDataTypeItemIfIsNotIncluded(ArrayList<DataType> candidDataTypeItems,Task  currentTaskItem){
        for (int index = 0; index < dataTypeItemsCount; index++) {
            updateCandidDataTypeItemIfIsNotIncluded(index,candidDataTypeItems,currentTaskItem);

        }
    }
    public void updateCandidDataTypeItemIfIsNotIncluded(int index, ArrayList<DataType> candidDataTypeItems,Task  currentTaskItem){
        if (!currentTaskItem.getDataTypeItems()[index].belongsTo(candidDataTypeItems)
                && currentTaskItem.getDataTypeItems()[index].getSize()!=0)
            setCandidDataTypeItems(DataType.union(getCandidDataTypeItems(),getDataTypeItems()[index]));
    }

    @Override
    public String toString() {
        DataType[] candidDataTypeArray = new DataType[candidDataTypeItems.size()];
        candidDataTypeArray = candidDataTypeItems.toArray(candidDataTypeArray);

        return "GreedyAlgorithm [\ntaskItems=" + taskItems + ",\n requestItems=" + Arrays.toString(requestItems)
                + ",\n profitItems=" + Arrays.toString(profitItems) + ",\n taskCount=" + taskCount + ",\n taskDataTypeMatrix="
                + taskDataTypeMatrixToString() + ",\n knapsackCount=" + knapsackCount + "\n, knapsackItems="
                + knapsackItemsToString()  +
                ",\n dataTypeItems=" + Arrays.toString(dataTypeItems) +
                ",\n candidDataTypeItems=" + Arrays.toString(candidDataTypeArray) +
                ",\n dataTypeItemsSum=" + Arrays.toString(sum) +
                ",\n totalProfit="+totalProfit+
                ",\n candidDataTypeItemsProportionValue="+candidDataTypeItemsProportionValue+
                ",\n totalDataSize="+totalDataSize+
                '}';
    }

    private String taskDataTypeMatrixToString() {
        StringBuilder oStringBuilder=new StringBuilder();
        oStringBuilder.append("[");
        for(int row=0;row<taskDataTypeMatrix.length;row++)
        {
            oStringBuilder.append("[");
            for (int colVal:taskDataTypeMatrix[row]) {
                oStringBuilder.append(colVal+", ");
            }
            oStringBuilder.append("]\n");
        }
        oStringBuilder.append("]\n");
        return oStringBuilder.toString();
    }
    private String knapsackItemsToString() {
        StringBuilder oStringBuilder=new StringBuilder();
        oStringBuilder.append("[");
        for (Knapsack knapsack : knapsackItems) {
            oStringBuilder.append(knapsack.toString());
            oStringBuilder.append("\n");
        }
        oStringBuilder.append("],\n");
        return oStringBuilder.toString();
    }
}
