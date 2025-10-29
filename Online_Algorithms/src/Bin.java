import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Bin implements Comparable <Bin>{

    // Properties of the Bin (Server) class
    private Double binCPUSize;
    private Double binMemorySize;
    private int index;
    private ArrayList<Task> taskList;
    private Double[] unionDataSizes;
    private boolean isUsed;


    //implementation for tabusearch

    private List<Double> memoryItems;
    private List<Double> cpuItems;
    private double memoryUsed;
    private double cpuUsed;
    private double binCapacityMemory;
    private double binCapacityCpu;

    public Bin(){
        taskList = new ArrayList<>();
        //assignedDataTasks = new ArrayList<>();

    }
    public Bin(int index, Double binCPUSize, Double binMemorySize) {
        this.binCPUSize = binCPUSize;
        this.binMemorySize = binMemorySize;
        this.index = index;
        //this.assignedDataTasks = new ArrayList<>();
    }

    public Bin(double binCapacityMemory, double binCapacityCpu) {
        this.memoryItems = new ArrayList<>();
        this.cpuItems = new ArrayList<>();
        this.memoryUsed = 0.0;
        this.cpuUsed = 0.0;
        this.binCapacityMemory = binCapacityMemory;
        this.binCapacityCpu = binCapacityCpu;
    }

    public Bin(Bin bin) {
        this.memoryItems = new ArrayList<>(bin.memoryItems);
        this.cpuItems = new ArrayList<>(bin.cpuItems);
        this.memoryUsed = bin.memoryUsed;
        this.cpuUsed = bin.cpuUsed;
        this.binCapacityMemory = bin.binCapacityMemory;
        this.binCapacityCpu = bin.binCapacityCpu;
    }

    // Getter methods
    public Double getBinMemorySize() {
        return binMemorySize;
    }

    public ArrayList<Task> getTaskList() {
        return taskList;
    }

    public int getIndex() {
        return index;
    }

    public Double[] getUnionDataSizes() {
        return unionDataSizes;
    }

    public Double getBinCPUSize() {

        return binCPUSize;
    }
    public boolean isUsed() {
        return !taskList.isEmpty();
    }




    // Setter mehtods
    public void setBinMemorySize(Double binMemorySize) {
        this.binMemorySize = binMemorySize;
    }

    public void setBinCPUSize(Double binCPUSize) {
        this.binCPUSize = binCPUSize;
    }

    {
        taskList = new ArrayList<Task>();
        isUsed = false;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public void setUnionDataSizes(Double[] unionDataSizes) {
        this.unionDataSizes = unionDataSizes;
    }


    public void setIndex(int index) {
        this.index = index;
    }

    public void setTaskList(ArrayList<Task> taskList) {
        this.taskList = taskList;
    }


    // This function adds a new arrival task to the taskList (all previously allocated task on the same server)
   public void addTask (Task newTask){
        this.taskList= Task.union(this.taskList, newTask);
    }


    // This function deallocates a task after finishing its job from taskList
    public void removeTask(Task taskToRemove) {
        // Create a copy of the current taskList
        ArrayList<Task> updatedTaskList = new ArrayList<>(taskList);

        // Find the task to remove by iterating through the taskList
        for (Task task : taskList) {
            if (task.equals(taskToRemove)) {
                updatedTaskList.remove(task);
                break; // Assuming you want to remove only the first occurrence
            }
        }
        // Update the taskList with the modified list
        taskList = updatedTaskList;
    }





    @Override
    public int compareTo(Bin b){
        Bin bin = (Bin) b;
        if(this.binCPUSize<bin.binCPUSize) {
            return -1;
        }else if(this.binCPUSize>bin.binCPUSize) {
            return 1;
        }else {
            return 0;
        }
    }

//    @Override
//    public String toString() {
//        return "Bin{" +
//                "index=" + index +
//                ", binResidualCPUSize=" + binCPUSize +
//                ", binResidualMemorySize=" + binMemorySize +
//                ", taskList=" + taskList +
//                ", isUsed=" + isUsed +
//                ", unionDataSizes=" + java.util.Arrays.toString(unionDataSizes) +
//                // ", totalTaskSize=" + getValue() +
//                '}' + '\n';
//    }


}


