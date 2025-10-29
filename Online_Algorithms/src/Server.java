import java.util.*;

public class Server {

    // private List<Double> memoryItems;
    private List<Double> cpuItems;

    private List<List<Double>> memoryItemsArrays;
    private double memoryUsed;
    private double cpuUsed;
    private double serverCapacityMemory;
    private double serverCapacityCpu;

    public Server(double serverCapacityMemory, double serverCapacityCpu) {
        //this.memoryItems = new ArrayList<>();
        this.memoryItemsArrays = new ArrayList<>();
        this.cpuItems = new ArrayList<>();
        this.memoryUsed = 0.0;
        this.cpuUsed = 0.0;
        this.serverCapacityMemory = serverCapacityMemory;
        this.serverCapacityCpu = serverCapacityCpu;
    }

    public Server(Server server) {
        //  this.memoryItems = new ArrayList<>(server.memoryItems);
        this.memoryItemsArrays = new ArrayList<>(server.memoryItemsArrays);
        this.cpuItems = new ArrayList<>(server.cpuItems);
        this.memoryUsed = server.memoryUsed;
        this.cpuUsed = server.cpuUsed;
        this.serverCapacityMemory = server.serverCapacityMemory;
        this.serverCapacityCpu = server.serverCapacityCpu;
    }

    // Add constructor to initialize Server from OnlineSharingAlgorithms
    public Server(Bin bin) {
        this(bin.getBinMemorySize(), bin.getBinCPUSize());
    }

    public boolean canAddItem(double memoryItem, double cpuItem) {
        return (memoryUsed + memoryItem <= serverCapacityMemory) && (cpuUsed + cpuItem <= serverCapacityCpu);
    }


    // Method to add the entire memory array along with its CPU item
    public void addItem(List<Double> memoryArray, double cpuItem) {
        memoryItemsArrays.add(memoryArray);
        cpuItems.add(cpuItem);
        memoryUsed += memoryArray.stream().mapToDouble(Double::doubleValue).sum();
        cpuUsed += cpuItem;
    }

    public void removeItem(int index) {
        memoryUsed -= memoryItemsArrays.get(index).stream().mapToDouble(Double::doubleValue).sum();
        cpuUsed -= cpuItems.get(index);
        memoryItemsArrays.remove(index);
        cpuItems.remove(index);
    }


    public boolean canSwapItems(double currentMemoryItem, double newMemoryItem) {
        return (memoryUsed - currentMemoryItem + newMemoryItem <= serverCapacityMemory);
    }

    public boolean canSwapItems(double currentMemoryItem, double newMemoryItem, double currentCpuItem, double newCpuItem) {
        boolean memoryCheck = (memoryUsed - currentMemoryItem + newMemoryItem <= serverCapacityMemory);
        boolean cpuCheck = (cpuUsed - currentCpuItem + newCpuItem <= serverCapacityCpu);
        return memoryCheck && cpuCheck;
    }

    public void swapItems(int index, List<Double> newMemoryArray, double newCpuItem) {
        // Subtract the sum of the old memory array and add the new memory array's sum
        memoryUsed = memoryUsed - memoryItemsArrays.get(index).stream().mapToDouble(Double::doubleValue).sum()
                + newMemoryArray.stream().mapToDouble(Double::doubleValue).sum();
        // Update CPU usage
        cpuUsed = cpuUsed - cpuItems.get(index) + newCpuItem;

        // Update the memory and CPU lists
        memoryItemsArrays.set(index, newMemoryArray);
        cpuItems.set(index, newCpuItem);
    }





    //function for TabuSearch_Sharing
//    public double calculateUniqueMemoryUsed() {
//        Set<Double> uniqueElements = new HashSet<>();
//
//        // Iterate over memoryItemsArrays, which now holds List<Double>
//        for (List<Double> memoryArray : memoryItemsArrays) {
//            for (Double memory : memoryArray) {
//                if (memory != 0) {  // Ensure you're not counting zeros
//                    uniqueElements.add(memory);
//                }
//            }
//        }
//
//        // Sum the unique elements and return the total
//        return uniqueElements.stream().mapToDouble(Double::doubleValue).sum();
//    }

    public double calculateUniqueMemoryUsed() {
        Set<String> uniqueElements = new HashSet<>();

        // Iterate over memoryItemsArrays, which now holds List<List<Double>>
        for (int i = 0; i < memoryItemsArrays.size(); i++) {
            List<Double> memoryArray = memoryItemsArrays.get(i);
            for (int j = 0; j < memoryArray.size(); j++) {
                Double memory = memoryArray.get(j);
                if (memory != 0) {  // Ensure you're not counting zeros
                    String uniqueKey = j + ":" + memory;  // Create a unique key combining index and value
                    uniqueElements.add(uniqueKey);
                }
            }
        }

        // Calculate the sum of unique memory values
        return uniqueElements.stream()
                .mapToDouble(e -> Double.parseDouble(e.split(":")[1]))  // Extract the value part from each unique key
                .sum();
    }




    //function for TabuSearch_Sharing_Modified

    public double getAvailableMemorySpace() {
        double uniqueMemoryUsed = calculateUniqueMemoryUsed();
        return serverCapacityMemory - uniqueMemoryUsed; // Remaining memory capacity
    }



    public double getMemoryUsed() {
        return memoryUsed;
    }

    public double getCpuUsed() {
        return cpuUsed;
    }

    public double getServerCapacityMemory() {
        return serverCapacityMemory;
    }

    public double getServerCapacityCpu() {
        return serverCapacityCpu;
    }


    public List<List<Double>> getMemoryItemsArrays() {
        return memoryItemsArrays;
    }

    public List<Double> getCpuItems() {
        return cpuItems;
    }

    public boolean canAddCpu(double cpuItem) {
        return (this.cpuUsed + cpuItem <= this.serverCapacityCpu);
    }

    public double getRemainingMemoryCapacity() {
        return serverCapacityMemory - memoryUsed;
    }

    public double getRemainingCPUCapacity() {
        return serverCapacityCpu - cpuUsed;
    }


}
