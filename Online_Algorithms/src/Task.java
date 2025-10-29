import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Task {
    // Properties of the Task class
    private int index;
    private Double request;
    private Double arrivalTime;
    private Double durationTime;
    private Double terminationTime;
    private Data[] dataItems;
    private boolean isCandid;
    private boolean isDataNominated;
    private boolean isUsed;


    // Constructor with all parameters
    public Task(int index, Double request, Double arrivalTime, Double durationTime, Data[] dataItems) {
        this(request, arrivalTime, durationTime, dataItems);
        this.index = index;
        this.terminationTime = arrivalTime + durationTime; // Set terminationTime based on arrivalTime and durationTime
    }

    // Constructor without index parameter
    public Task(Double request, Double arrivalTime, Double durationTime, Data[] dataItems) {
        this.request = request;
        this.arrivalTime = arrivalTime;
        this.durationTime = durationTime;
        this.dataItems = dataItems;
        this.terminationTime = arrivalTime + durationTime; // Set terminationTime based on arrivalTime and durationTime
    }
    // Default constructor
    public Task() {
    }


    public Task(int index){
        this();
        this.index=index;
    }


    // Getter methods
    public int getIndex() {
        return index;
    }

    public Double getArrivalTime() {
        return arrivalTime;
    }

    public Double getDurationTime() {
        return durationTime;
    }

    public Double getRequest() {
        return request;
    }

    public Data[] getDataItems() {
        return dataItems;
    }

    public Double getTerminationTime() {
        return terminationTime;
    }

    public boolean isDataNominated() {
        return isDataNominated;
    }

    //This property is implemented to check if the task is in candid task items or not
    public boolean isCandid() {
        return isCandid;
    }

    public boolean isUsed() {
        return isUsed;
    }


    // Setter methods
    public void setDataItems(Data[] dataItems) {
        this.dataItems = dataItems;
    }

    public void setDurationTime(Double durationTime) {
        this.durationTime = durationTime;
    }

    public void setArrivalTime(Double arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setDataNominated(boolean dataNominated) {
        isDataNominated = dataNominated;
    }

    public void setRequest(Double request) {
        this.request = request;
    }

    public void setTerminationTime(Double terminationTime) {
        this.terminationTime = terminationTime;
    }

    public void setCandid(boolean candid) {
        isCandid = candid;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    // Helper methods
    // Check if the task belongs to a given task set
    public boolean belongsTo(ArrayList<Task> taskSet){
        for (Task taskItem:taskSet) {
            if(taskItem.equals(this)){
                return true;
            }
        }
        return false;
    }

    // Union of new arrival task with all previously allocated tasks on server
    public static ArrayList<Task> union(ArrayList<Task> currentTaskItems,Task newTaskItem){
        for (Task currentTask:currentTaskItems) {
            if(currentTask.equals(newTaskItem)){
                return currentTaskItems;
            }
        }
        currentTaskItems.add(newTaskItem);
        return currentTaskItems;
    }

    // Subtract a task from a taskset (and consequently from a server)
    public static ArrayList<Task> subtract(ArrayList<Task> currentTaskItems,Task newTaskItem)
            throws ArrayIndexOutOfBoundsException
    {
        for (Task currentTaskItem:currentTaskItems) {
            if(currentTaskItem.equals(newTaskItem)){
                currentTaskItems.remove(currentTaskItem);
                break;
            }
        }
        return currentTaskItems;
    }



    // Comparators for sorting tasks based on request time
    public static Comparator<Task> TaskRequestComparator=new Comparator<Task>() {
        @Override
        public int compare(Task task1, Task task2) {
            Double task1Request=task1.getRequest();
            Double task2Request=task2.getRequest();
            //descending order
            return task2Request.compareTo(task1Request);
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

    // ToString method for representing the Task as a string
    @Override
    public String toString() {
        return "Task [index= "+ index +
                ", request=" + request +
                ", arrivalTime=" + arrivalTime +
                ", durationTime="+ durationTime +
                ", dataItems=" + Arrays.toString(dataItems) +
                ", isCandid=" + isCandid+
                "]\n";
    }
}
