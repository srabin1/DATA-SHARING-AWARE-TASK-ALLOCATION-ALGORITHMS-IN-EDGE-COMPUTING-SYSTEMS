import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class LocalSearch {

    private List<Double> memoryItems;
    private List<List<Double>> inputItems;
    private List<Double> cpuItems;
    private List<Server> servers;
    private List<Server> bestSolution;
    private double bestServerCount;
    private List<List<Integer>> tabuList;
    private int maxIterations;
    private int nonImprovingIterationThreshold;
    private double totalDataTransferred = 0.0;

    private double currentIterationDataTransfer = 0.0; // Reset each iteration
    private final double transferThresholdRatio = 0.10; // 0.01, 0.1, ...


    // private double[] serverCapacityMemory = {10.0, 11.0, 9.0, 12.0, 10.0};
    //  private double[] serverCapacityCpu = {8.0, 7.0, 10.0, 11.0, 5.0};

    private int serverIndex;
    Map<Double, List<List<Double>>> sumToArraysMap;

    private Bin[] binItems; // This will store the server capacity data
    private static boolean isFirstCall = true;


    public LocalSearch(List<List<Double>> inputItems, List<Double> cpuItems, Bin[] binItems, int maxIterations, int nonImprovingThreshold) {

        this.inputItems = inputItems;
        this.cpuItems = cpuItems;
        this.binItems = binItems; // Set bin items (servers)
        this.servers = new ArrayList<>();
        this.bestSolution = new ArrayList<>();
        this.bestServerCount = Double.MAX_VALUE;
        this.tabuList = new ArrayList<>();
        this.maxIterations = maxIterations;
        this.nonImprovingIterationThreshold = nonImprovingThreshold;
        this.serverIndex = 0;
        initializeSumToArraysMap(inputItems);
    }

    private void initializeSumToArraysMap(List<List<Double>> inputItems) {
        sumToArraysMap = new HashMap<>();
        for (List<Double> item : inputItems) {
            // Calculate the sum of the values in the List<Double>
            double sum = item.stream().mapToDouble(Double::doubleValue).sum();

            // Store the item in the map, using the sum as the key
            sumToArraysMap.computeIfAbsent(sum, k -> new ArrayList<>()).add(item);
        }
    }



    // only for one iteration
//    public void execute() {
//
//        initialSolution();
//        System.out.println("Initial Solution:");
//        printSolution(servers);
//        System.out.println("Total servers used: " + servers.size());
//        System.out.println("Total memory used: " + calculateTotalUniqueMemoryUsed());
//
//        // Append initial values to CSV
//        appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed());
//
//        int iteration = 0;
//        int stableCounter = 0;
//
//        while (iteration < maxIterations && stableCounter < nonImprovingIterationThreshold) {
//            boolean improved = exploreNeighborhood();
//            consolidateServers();
//
//            if (servers.size() < bestServerCount) {
//                updateBestSolution();
//                bestServerCount = servers.size();  // Update the best server count if the current server count is lower
//                stableCounter = 0;
//            } else {
//                stableCounter++;
//            }
//
//            System.out.println();
//            System.out.println("Iteration " + (iteration + 1) + ":");
//            printSolution(servers);
//            System.out.println("Total servers used: " + servers.size()); // Print the number of servers currently used
//            System.out.println("Total memory used: " + calculateTotalUniqueMemoryUsed()); // Print the total memory used
//
//            // Append iteration values to CSV
//            appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed());
//
//            iteration++;
//            // Try one last time to merge the final two servers after all other operations
//            tryMergeFinalTwoServers();
//        }
//
//        System.out.println();
//        System.out.println("final solution:");
//        printSolution(bestSolution);
//        System.out.println("Total servers used: " + bestServerCount);
//        System.out.println("Total memory used: " + calculateTotalUniqueMemoryUsed());
//
//        // Append final values to CSV
//        appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed());
//
//    }



    // for all iterations without excluding time from csv file
    public void execute() {
        initialSolution();
       // System.out.println("Initial Solution:");
      //  printSolution(servers);
      //  System.out.println("Total servers used: " + servers.size());
       // System.out.println("Total memory used: " + calculateTotalUniqueMemoryUsed());

        // Append initial values to CSV
        appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed(),totalDataTransferred, false);

        int iteration = 0;
        int stableCounter = 0;

        while (iteration < maxIterations && stableCounter < nonImprovingIterationThreshold) {
            currentIterationDataTransfer = 0.0;  // Reset at start of iteration
            boolean improved = exploreNeighborhood();
            consolidateServers();

            if (servers.size() < bestServerCount) {
                updateBestSolution();
                bestServerCount = servers.size();  // Update the best server count if the current server count is lower
                stableCounter = 0;
            } else {
                stableCounter++;
            }

         //   System.out.println();
          //  System.out.println("Iteration " + (iteration + 1) + ":");
        //   printSolution(servers);
         //   System.out.println("Total servers used: " + servers.size()); // Print the number of servers currently used
          //  System.out.println("Total memory used: " + calculateTotalUniqueMemoryUsed()); // Print the total memory used

            // Append iteration values to CSV
            //System.out.println("Total data transferred so far: " + totalDataTransferred + " units.");
            appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed(), totalDataTransferred, iteration == maxIterations - 1);

         //   appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed(), iteration == maxIterations - 1);

            iteration++;
            // Try one last time to merge the final two servers after all other operations
            tryMergeFinalTwoServers();
        }

        //System.out.println();
       // System.out.println("final solution:");
       // printSolution(bestSolution);
        //System.out.println("Total servers used: " + bestServerCount);
        //System.out.println("Total memory used: " + calculateTotalUniqueMemoryUsed());

        // Append final values to CSV
        appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed(), totalDataTransferred,true); // Also pass true to add blank line after final values
    }

// To test which part of the execute function is the most time consuming part
//    public void execute() {
//
//        long totalInitialTime = 0;  // Variable to accumulate total CSV append time
//        long startTime_Initial, endTime_Initial;
//        startTime_Initial = System.nanoTime();
//        initialSolution();
//        endTime_Initial = System.nanoTime();
//        totalInitialTime = (endTime_Initial - startTime_Initial)/ 1_000_000;
//
//        // System.out.println("Initial Solution:");
//        // printSolution(servers);
//        // System.out.println("Total servers used: " + servers.size());
//        // System.out.println("Total memory used: " + calculateTotalUniqueMemoryUsed());
//
//        long totalCsvTime = 0;  // Variable to accumulate total CSV append time
//        long startTime_CSV, endTime_CSV;
//
//        long totalExeTime = 0;  // Variable to accumulate total CSV append time
//        long startTime_Exe, endTime_Exe;
//
//
//
//        // Append initial values to CSV with timing
//        startTime_CSV = System.nanoTime();
//        appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed(), false);
//        endTime_CSV = System.nanoTime();
//        long duration = (endTime_CSV - startTime_CSV) / 1_000_000; // Time in milliseconds
//       // System.out.println("Time to append initial CSV data: " + duration + " ms");
//        totalCsvTime += duration;
//
//
//
//        long totalExploreTime = 0;
//        long startTime_Explore, endTime_Explore;
//
//        long totalConsolidateTime = 0;
//        long startTime_Consolidate, endTime_Consolidate;
//
//        long totalBestSolutionTime = 0;
//        long startTime_best, endTime_best;
//
//        long totalmergeTime = 0;
//        long startTime_merge, endTime_merge;
//
//
//
//        int iteration = 0;
//        int stableCounter = 0;
//        startTime_Exe = System.nanoTime();
//        while (iteration < maxIterations && stableCounter < nonImprovingIterationThreshold) {
//            startTime_Explore = System.nanoTime();
//            boolean improved = exploreNeighborhood();
//            endTime_Explore = System.nanoTime();
//
//            totalExploreTime = (endTime_Explore -startTime_Explore) / 1_000_000;
//
//            startTime_Consolidate = System.nanoTime();
//            consolidateServers();
//            endTime_Consolidate = System.nanoTime();
//            totalConsolidateTime = (endTime_Consolidate- startTime_Consolidate) / 1_000_000;
//
//
//            startTime_best = System.nanoTime();
//            if (servers.size() < bestServerCount) {
//                updateBestSolution();
//                bestServerCount = servers.size();  // Update the best server count if the current server count is lower
//                stableCounter = 0;
//            } else {
//                stableCounter++;
//            }
//            endTime_best = System.nanoTime();
//            totalBestSolutionTime = (endTime_best - startTime_best) / 1_000_000;
//
//            // Append iteration values to CSV with timing
//            startTime_CSV = System.nanoTime();
//            appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed(), iteration == maxIterations - 1);
//            endTime_CSV = System.nanoTime();
//            duration = (endTime_CSV - startTime_CSV) / 1_000_000;
//           // System.out.println("Time to append iteration " + iteration + " CSV data: " + duration + " ms");
//            totalCsvTime += duration;
//
//            iteration++;
//            // Try one last time to merge the final two servers after all other operations
//
//            startTime_merge = System.nanoTime();
//            tryMergeFinalTwoServers();
//
//            endTime_merge = System.nanoTime();
//            totalmergeTime = (endTime_merge - startTime_merge) / 1_000_000;
//        }
//
//        // Append final values to CSV with timing
//        startTime_CSV = System.nanoTime();
//        appendToCsv(servers.size(), calculateTotalUniqueMemoryUsed(), true); // Also pass true to add blank line after final values
//        endTime_CSV = System.nanoTime();
//        duration = (endTime_CSV - startTime_CSV) / 1_000_000;
//       // System.out.println("Time to append final CSV data: " + duration + " ms");
//        totalCsvTime += duration;
//        endTime_Exe = System.nanoTime();
//        totalExeTime = (endTime_Exe - startTime_Exe) / 1_000_000;
//
//
//
//
//        // Print total time spent on CSV operations
//        System.out.println("Total time spent on initial operations: " + totalInitialTime + " ms");
//        System.out.println("Total time spent on CSV operations: " + totalCsvTime + " ms");
//        System.out.println("Total time spent on Explore operations: " + totalExploreTime + " ms");
//        System.out.println("Total time spent on Consolidate operations: " + totalConsolidateTime + " ms");
//        System.out.println("Total time spent on Best solution operations: " + totalBestSolutionTime + " ms");
//        System.out.println("Total time spent on Merge operations: " + totalmergeTime + " ms");
//        System.out.println("Total time spent on Execution: " + totalExeTime + " ms");
//    }

    private void initialSolution() {
        // Sort indices based on CPU items in descending order
        List<Integer> sortedIndices = IntStream.range(0, cpuItems.size())
                .boxed()
                .sorted((i, j) -> Double.compare(cpuItems.get(j), cpuItems.get(i)))
                .collect(Collectors.toList());

        // Sort servers based on CPU capacity in descending order
        servers.sort((s1, s2) -> Double.compare(s2.getServerCapacityCpu(), s1.getServerCapacityCpu()));

        for (int index : sortedIndices) {
            List<Double> memoryArray = inputItems.get(index);
            double cpuItem = cpuItems.get(index);
            double memoryItemSum = memoryArray.stream().mapToDouble(Double::doubleValue).sum();
            boolean placed = false;

            // Try placing the item in existing servers that have enough CPU and memory capacity
            for (Server server : servers) {
                double remainingMemory = server.getRemainingMemoryCapacity();
                double remainingCpu = server.getRemainingCPUCapacity();

                if (memoryItemSum <= remainingMemory && cpuItem <= remainingCpu) {
                    server.addItem(memoryArray, cpuItem);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                boolean canFitInNewServer = false;

                // Instead of using serverCapacityCpu, use binItems to get the CPU capacity from the bin
                for (Bin bin : binItems) {
                    if (cpuItem <= bin.getBinCPUSize()) {
                        // Create a new server using the bin data
                        Server newServer = createNewServer();
                        if (newServer.canAddItem(memoryItemSum, cpuItem)) {
                            newServer.addItem(memoryArray, cpuItem);
                            servers.add(newServer);
                            canFitInNewServer = true;
                            break;
                        }
                    }
                }

                if (!canFitInNewServer) {
                    System.out.println("Item with CPU: " + cpuItem + " and memory: " + memoryItemSum + " cannot be placed in any server.");
                }
            }
        }
    }


//    private void initialSolution() {
//        List<Integer> sortedIndices = IntStream.range(0, cpuItems.size())
//                .boxed()
//                .sorted((i, j) -> Double.compare(cpuItems.get(j), cpuItems.get(i)))
//                .collect(Collectors.toList());
//
//        servers.sort((s1, s2) -> Double.compare(s2.getServerCapacityCpu(), s1.getServerCapacityCpu()));
//
//        for (int index : sortedIndices) {
//            int[] memoryArray = inputItems.get(index);
//            double cpuItem = cpuItems.get(index);
//            double memoryItemSum = Arrays.stream(memoryArray).sum();
//            boolean placed = false;
//
//            // Try placing the item in servers that have enough CPU and memory capacity
//            for (Server server : servers) {
//                double remainingMemory = server.getRemainingMemoryCapacity();
//                double remainingCpu = server.getRemainingCPUCapacity();
//
//                if (memoryItemSum <= remainingMemory && cpuItem <= remainingCpu) {
//                    server.addItem(memoryArray, cpuItem);
//                    placed = true;
//                    break;
//                }
//            }
//
//            if (!placed) {
//                boolean canFitInNewServer = false;
//                for (int capacityIndex = 0; capacityIndex < serverCapacityCpu.length; capacityIndex++) {
//                    if (cpuItem <= serverCapacityCpu[capacityIndex]) {
//                        Server newServer = createNewServer();
//                        if (newServer.canAddItem(memoryItemSum, cpuItem)) {
//                            newServer.addItem(memoryArray, cpuItem);
//                            servers.add(newServer);
//                            canFitInNewServer = true;
//                            break;
//                        }
//                    }
//                }
//
//                if (!canFitInNewServer) {
//                    System.out.println("Item with CPU: " + cpuItem + " and memory: " + memoryItemSum + " cannot be placed in any server.");
//                }
//            }
//        }
//    }

//    private Server createNewServer() {
//        Integer[] indices = new Integer[serverCapacityCpu.length];
//        for (int i = 0; i < indices.length; i++) {
//            indices[i] = i;
//        }
//        Arrays.sort(indices, (a, b) -> Double.compare(serverCapacityCpu[b], serverCapacityCpu[a]));
//
//        if (serverIndex >= indices.length) {
//            serverIndex = 0;
//        }
//
//        int sortedIndex = indices[serverIndex];
//        Server newServer = new Server(serverCapacityMemory[sortedIndex], serverCapacityCpu[sortedIndex]);
//        serverIndex++;
//        return newServer;
//    }

    private Server createNewServer() {
        // Iterate over binItems (servers) to create new Server objects
        Integer[] indices = new Integer[binItems.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        Arrays.sort(indices, (a, b) -> Double.compare(binItems[b].getBinCPUSize(), binItems[a].getBinCPUSize()));  // Sort based on CPU capacity

        if (serverIndex >= indices.length) {
            serverIndex = 0;
        }

        int sortedIndex = indices[serverIndex];
        Bin bin = binItems[sortedIndex];
        Server newServer = new Server(bin);  // Create Server from the Bin object
        serverIndex++;
        return newServer;
    }


    public double calculateTotalUniqueMemoryUsed() {
        double totalUniqueMemoryUsed = 0;
        for (Server server : servers) {
            totalUniqueMemoryUsed += server.calculateUniqueMemoryUsed();
        }
        return totalUniqueMemoryUsed;
    }

    private boolean exploreNeighborhood() {
        boolean improved = false;
        for (int i = 0; i < servers.size(); i++) {
            Server serverI = servers.get(i);
            for (int j = 0; j < serverI.getMemoryItemsArrays().size(); j++) {
                List<Double> memoryArray = serverI.getMemoryItemsArrays().get(j);  // Access memory array as List<Double>
                double memoryItemSum = memoryArray.stream().mapToDouble(Double::doubleValue).sum();  // Sum the values of List<Double>
                double cpuItem = serverI.getCpuItems().get(j);

                for (int k = 0; k < servers.size(); k++) {
                    if (i != k) {
                        Server serverK = servers.get(k);

                        if (serverK.canAddItem(memoryItemSum, cpuItem) && tryMove(i, j, k)) {
                            improved = true;
                            return true;
                        }

                        for (int l = 0; l < serverK.getMemoryItemsArrays().size(); l++) {
                            if (trySwap(i, j, k, l)) {
                                improved = true;
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return improved;
    }

    private void consolidateServers() {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            for (int i = 0; i < servers.size(); i++) {
                Server server1 = servers.get(i);
                for (int j = i + 1; j < servers.size(); j++) {
                    Server server2 = servers.get(j);
                    if (canMergeServers(server1, server2)) {
                        mergeServers(server1, server2);
                        servers.remove(j);
                        improvement = true;
                        break;
                    }
                }
                if (improvement) {
                    break;
                }
            }
        }
    }



//    private boolean canMergeServers(Server server1, Server server2) {
//        for (int i = 0; i < server2.getMemoryItemsArrays().size(); i++) {
//            // Access memory array as List<Double> and sum the values using stream
//            List<Double> memoryArray = server2.getMemoryItemsArrays().get(i);
//            double memoryItemSum = memoryArray.stream().mapToDouble(Double::doubleValue).sum();
//
//            // Check if server1 can add the memory and CPU item from server2
//            if (!server1.canAddItem(memoryItemSum, server2.getCpuItems().get(i))) {
//                return false;
//            }
//        }
//        return true;
//    }

 //   private void mergeServers(Server target, Server source) {
//        for (int i = 0; i < source.getMemoryItemsArrays().size(); i++) {
//            target.addItem(source.getMemoryItemsArrays().get(i), source.getCpuItems().get(i));
//        }
//    }

    private boolean canMergeServers(Server server1, Server server2) {
        double totalCpuUsed = server1.getCpuUsed() + server2.getCpuUsed();
        double totalMemoryUsed = server1.calculateUniqueMemoryUsed() + server2.calculateUniqueMemoryUsed();

        // Ensure merging does not exceed CPU and memory limits
        return totalCpuUsed <= Math.max(server1.getServerCapacityCpu(), server2.getServerCapacityCpu()) &&
                totalMemoryUsed <= Math.max(server1.getServerCapacityMemory(), server2.getServerCapacityMemory());
    }

    private void mergeServers(Server target, Server source) {
        // Merge all items from source into target
        for (int i = 0; i < source.getMemoryItemsArrays().size(); i++) {
            target.addItem(source.getMemoryItemsArrays().get(i), source.getCpuItems().get(i));
        }
    }




    private void tryMergeFinalTwoServers() {
        if (servers.size() == 2) {
            Server server1 = servers.get(0);
            Server server2 = servers.get(1);
            if (canMergeFinalTwoServers(server1, server2)) {
                mergeServers(server1, server2);
                servers.remove(1);
            }
        }
    }


    private boolean canMergeFinalTwoServers(Server server1, Server server2) {
        double combinedUniqueMemoryUsed = server1.calculateUniqueMemoryUsed() + server2.calculateUniqueMemoryUsed();

        // Calculate total unique memory using HashSet<Double>
        double totalUniqueMemory = new HashSet<Double>() {{
            addAll(server1.getMemoryItemsArrays().stream()
                    .flatMap(List::stream)  // Flatten List<List<Double>> into List<Double>
                    .collect(Collectors.toList()));
            addAll(server2.getMemoryItemsArrays().stream()
                    .flatMap(List::stream)  // Flatten List<List<Double>> into List<Double>
                    .collect(Collectors.toList()));
        }}.stream().mapToDouble(Double::doubleValue).sum();  // Sum the unique values as double

        // Calculate total CPU usage
        double totalCpuUsed = server1.getCpuUsed() + server2.getCpuUsed();

        // Check if both memory and CPU constraints are satisfied
        return totalUniqueMemory <= Math.max(server1.getServerCapacityMemory(), server2.getServerCapacityMemory()) &&
                totalCpuUsed <= Math.max(server1.getServerCapacityCpu(), server2.getServerCapacityCpu());
    }


//    private boolean canMergeFinalTwoServers(Server server1, Server server2) {
//        // Calculate the combined unique memory used by both servers
//        double combinedUniqueMemoryUsed = server1.calculateUniqueMemoryUsed() + server2.calculateUniqueMemoryUsed();
//
//        // Calculate total unique memory by combining unique memory items from both servers
//        double totalUniqueMemory = new HashSet<Double>() {{
//            addAll(server1.getMemoryItemsArrays().stream()
//                    .flatMap(List::stream)  // Flatten the List<List<Double>> into List<Double>
//                    .collect(Collectors.toList()));
//            addAll(server2.getMemoryItemsArrays().stream()
//                    .flatMap(List::stream)  // Flatten the List<List<Double>> into List<Double>
//                    .collect(Collectors.toList()));
//        }}.stream().mapToDouble(Double::doubleValue).sum();  // Sum the unique values as double
//
//        // Check if total unique memory is within the maximum server capacity
//        return totalUniqueMemory <= Math.max(server1.getServerCapacityMemory(), server2.getServerCapacityMemory());
//    }


    private void updateBestSolution() {
        bestSolution.clear();
        for (Server server : servers) {
            bestSolution.add(new Server(server)); // Deep copy of servers
        }
        bestServerCount = servers.size();
    }
    //only tracks data transferred without threshold
 /*
    private boolean tryMove(int fromServer, int itemIndex, int toServer) {
        Server serverFrom = servers.get(fromServer);
        Server serverTo = servers.get(toServer);

        if (itemIndex >= serverFrom.getMemoryItemsArrays().size() || itemIndex >= serverFrom.getCpuItems().size()) {
            return false;
        }

        // Access memoryArray as List<Double> and calculate the sum
        List<Double> memoryArray = serverFrom.getMemoryItemsArrays().get(itemIndex);
        double cpuItem = serverFrom.getCpuItems().get(itemIndex);
        double itemMemorySum = memoryArray.stream().mapToDouble(Double::doubleValue).sum();

        // Track data transferred
        totalDataTransferred += itemMemorySum;

        if (!tabuList.contains(Arrays.asList(fromServer, itemIndex, toServer)) &&
                serverTo.getAvailableMemorySpace() + serverTo.calculateUniqueMemoryUsed() >= itemMemorySum + serverTo.calculateUniqueMemoryUsed() &&
                serverTo.canAddCpu(cpuItem)) {
            serverFrom.removeItem(itemIndex);
            serverTo.addItem(memoryArray, cpuItem);
            updateTabuList(Arrays.asList(fromServer, itemIndex, toServer));
            return true;
        }
        return false;
    }

  */

    //Allow a move or swap only if total data transferred in this
    // iteration is less than 10% of the total data stored (memory used).

    //tryMove function with data transferred and threshold
    private boolean tryMove(int fromServer, int itemIndex, int toServer) {
        Server serverFrom = servers.get(fromServer);
        Server serverTo = servers.get(toServer);

        if (itemIndex >= serverFrom.getMemoryItemsArrays().size() || itemIndex >= serverFrom.getCpuItems().size()) {
            return false;
        }

        List<Double> memoryArray = serverFrom.getMemoryItemsArrays().get(itemIndex);
        double cpuItem = serverFrom.getCpuItems().get(itemIndex);
        double itemMemorySum = memoryArray.stream().mapToDouble(Double::doubleValue).sum();

        double potentialNewTransfer = currentIterationDataTransfer + itemMemorySum;
        double memoryThreshold = transferThresholdRatio * calculateTotalUniqueMemoryUsed();

        //This rejects the move if it exceeds the threshold.
        if (potentialNewTransfer > memoryThreshold) return false; // Enforce threshold

        if (!tabuList.contains(Arrays.asList(fromServer, itemIndex, toServer)) &&
                serverTo.getAvailableMemorySpace() + serverTo.calculateUniqueMemoryUsed() >= itemMemorySum + serverTo.calculateUniqueMemoryUsed() &&
                serverTo.canAddCpu(cpuItem)) {
            serverFrom.removeItem(itemIndex);
            serverTo.addItem(memoryArray, cpuItem);
            updateTabuList(Arrays.asList(fromServer, itemIndex, toServer));

            totalDataTransferred += itemMemorySum;              // Cumulative over all iterations
            //how much data has been moved/swapped so far in this iteration.
            currentIterationDataTransfer += itemMemorySum;      // Cumulative only this iteration
            return true;
        }

        return false;
    }

    // trySwap with data transferred but no threshold
    /*
    private boolean trySwap(int fromServer, int fromIndex, int toServer, int toIndex) {
        Server serverFrom = servers.get(fromServer);
        Server serverTo = servers.get(toServer);

        if (fromIndex >= serverFrom.getMemoryItemsArrays().size() || fromIndex >= serverFrom.getCpuItems().size() ||
                toIndex >= serverTo.getMemoryItemsArrays().size() || toIndex >= serverTo.getCpuItems().size()) {
            return false;
        }

        // Access memory arrays as List<Double>
        List<Double> fromMemoryArray = serverFrom.getMemoryItemsArrays().get(fromIndex);
        List<Double> toMemoryArray = serverTo.getMemoryItemsArrays().get(toIndex);

        double fromCpuItem = serverFrom.getCpuItems().get(fromIndex);
        double toCpuItem = serverTo.getCpuItems().get(toIndex);

        // Calculate the sum of memory arrays using stream for List<Double>
        double fromMemoryItemSum = fromMemoryArray.stream().mapToDouble(Double::doubleValue).sum();
        double toMemoryItemSum = toMemoryArray.stream().mapToDouble(Double::doubleValue).sum();

        // Track data transferred
        totalDataTransferred += fromMemoryItemSum + toMemoryItemSum;

        List<Integer> swap = Arrays.asList(fromServer, fromIndex, toServer, toIndex);

        if (!tabuList.contains(swap) &&
                serverFrom.getAvailableMemorySpace() + fromMemoryItemSum >= toMemoryItemSum &&
                serverTo.getAvailableMemorySpace() + toMemoryItemSum >= fromMemoryItemSum &&
                serverFrom.canAddCpu(toCpuItem) && serverTo.canAddCpu(fromCpuItem)) {

            // Perform the swap by calling swapItems method with List<Double>
            serverFrom.swapItems(fromIndex, toMemoryArray, toCpuItem);
            serverTo.swapItems(toIndex, fromMemoryArray, fromCpuItem);

            updateTabuList(Arrays.asList(fromServer, fromIndex, toServer, toIndex));
            return true;
        }
        return false;
    }

     */

    //trySwap function with data transferred and threshold

    private boolean trySwap(int fromServer, int fromIndex, int toServer, int toIndex) {
        Server serverFrom = servers.get(fromServer);
        Server serverTo = servers.get(toServer);

        if (fromIndex >= serverFrom.getMemoryItemsArrays().size() || fromIndex >= serverFrom.getCpuItems().size() ||
                toIndex >= serverTo.getMemoryItemsArrays().size() || toIndex >= serverTo.getCpuItems().size()) {
            return false;
        }

        List<Double> fromMemoryArray = serverFrom.getMemoryItemsArrays().get(fromIndex);
        List<Double> toMemoryArray = serverTo.getMemoryItemsArrays().get(toIndex);

        double fromCpuItem = serverFrom.getCpuItems().get(fromIndex);
        double toCpuItem = serverTo.getCpuItems().get(toIndex);

        double fromMemoryItemSum = fromMemoryArray.stream().mapToDouble(Double::doubleValue).sum();
        double toMemoryItemSum = toMemoryArray.stream().mapToDouble(Double::doubleValue).sum();

        double potentialNewTransfer = currentIterationDataTransfer + fromMemoryItemSum + toMemoryItemSum;
        double memoryThreshold = transferThresholdRatio * calculateTotalUniqueMemoryUsed();

        if (potentialNewTransfer > memoryThreshold) return false;

        List<Integer> swap = Arrays.asList(fromServer, fromIndex, toServer, toIndex);

        if (!tabuList.contains(swap) &&
                serverFrom.getAvailableMemorySpace() + fromMemoryItemSum >= toMemoryItemSum &&
                serverTo.getAvailableMemorySpace() + toMemoryItemSum >= fromMemoryItemSum &&
                serverFrom.canAddCpu(toCpuItem) && serverTo.canAddCpu(fromCpuItem)) {

            serverFrom.swapItems(fromIndex, toMemoryArray, toCpuItem);
            serverTo.swapItems(toIndex, fromMemoryArray, fromCpuItem);

            updateTabuList(swap);
            totalDataTransferred += fromMemoryItemSum + toMemoryItemSum;
            currentIterationDataTransfer += fromMemoryItemSum + toMemoryItemSum;
            return true;
        }

        return false;
    }

    private void updateTabuList(List<Integer> move) {
        tabuList.add(move);
        if (tabuList.size() > 10) {
            tabuList.remove(0);
        }
    }



   // for all iterations
//    private void appendToCsv(int totalServersUsed, double totalMemoryUsed, boolean addBlankLine) {
//        String filename = "output.csv";
//
//        try (FileWriter fw = new FileWriter(filename, true);
//             PrintWriter pw = new PrintWriter(fw)) {
//            pw.println(totalServersUsed + "," + totalMemoryUsed);
//            if (addBlankLine) {
//                pw.println(); // This writes a blank line in the CSV
//            }
//        } catch (IOException e) {
//            System.err.println("Error writing to CSV file: " + e.getMessage());
//        }
//    }

    private void appendToCsv(int totalServersUsed, double totalMemoryUsed, double totalDataTransferred, boolean addBlankLine) {
        String filename = "output.csv";

        try (FileWriter fw = new FileWriter(filename, true);
             PrintWriter pw = new PrintWriter(fw)) {
            pw.println(totalServersUsed + "," + totalMemoryUsed + "," + totalDataTransferred);  // Include data transferred
            if (addBlankLine) {
                pw.println(); // This writes a blank line in the CSV
            }
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }

    public void copyCsvFile() {
        String inputFilename = "output.csv";
        String outputFilename = "BFS_Reallocation_equal.csv";
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename));
             FileWriter fw = new FileWriter(outputFilename);
             PrintWriter pw = new PrintWriter(fw)) {

            String line;
            while ((line = br.readLine()) != null) {
                pw.println(line);  // Write line exactly as-is
            }

        } catch (IOException e) {
            System.err.println("Error copying CSV file: " + e.getMessage());
        }
    }



    // for all iterations without data transferred
    /*
    public void calculateAndAppendMinimumsToCsv() {
        String inputFilename = "output.csv";
        String outputFilename = "BFS_Reallocation_equal.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename));
             FileWriter fw = new FileWriter(outputFilename);
             PrintWriter pw = new PrintWriter(fw)) {
            String line;
            int minServersUsed = Integer.MAX_VALUE;
            double minMemoryUsed = Double.MAX_VALUE;
            boolean firstBlock = true; // Flag to manage the first data block

            // Header for the output file
            pw.println("min_servers_used,min_memory_used");

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    // Check if the previous block had valid data to output
                    if (minServersUsed != Integer.MAX_VALUE && minMemoryUsed != Double.MAX_VALUE) {
                        pw.println(minServersUsed + "," + minMemoryUsed);
                    }
                    // Reset the minimums for the next block of data
                    minServersUsed = Integer.MAX_VALUE;
                    minMemoryUsed = Double.MAX_VALUE;
                    firstBlock = false;
                    continue;
                }

                String[] values = line.split(",");
                if (values.length == 2) {
                    int serversUsed = Integer.parseInt(values[0].trim());
                    double memoryUsed = Double.parseDouble(values[1].trim());

                    if (serversUsed < minServersUsed) {
                        minServersUsed = serversUsed;
                    }
                    if (memoryUsed < minMemoryUsed) {
                        minMemoryUsed = memoryUsed;
                    }
                }
            }

            // Ensure to capture the last block if it hasn't been output yet
            if (!firstBlock && minServersUsed != Integer.MAX_VALUE && minMemoryUsed != Double.MAX_VALUE) {
                pw.println(minServersUsed + "," + minMemoryUsed);
            }
        } catch (IOException e) {
            System.err.println("Error processing CSV files: " + e.getMessage());
        }
    }

     */

    //with data transferred

    public void calculateAndAppendMinimumsToCsv() {
        String inputFilename = "output.csv";
        String outputFilename = "BFS_Reallocation_equal.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename));
             FileWriter fw = new FileWriter(outputFilename);
             PrintWriter pw = new PrintWriter(fw)) {

            String line;
            int minServersUsed = Integer.MAX_VALUE;
            double minMemoryUsed = Double.MAX_VALUE;

            List<Double> candidateTransfers = new ArrayList<>();
            List<String[]> currentBlockRows = new ArrayList<>();
            boolean firstBlock = true;

            // Updated CSV header
            pw.println("min_servers_used,min_memory_used,data_transferred");

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    // Process block
                    if (!currentBlockRows.isEmpty()) {
                        // Find 10% of minMemoryUsed
                        double threshold = 0.10 * minMemoryUsed;

                        // Filter and find max dataTransferred ≤ threshold
                        double bestTransfer = 0.0;
                        for (String[] values : currentBlockRows) {
                            double dataTransferred = Double.parseDouble(values[2].trim());
                            if (dataTransferred <= threshold && dataTransferred > bestTransfer) {
                                bestTransfer = dataTransferred;
                            }
                        }

                        pw.println(minServersUsed + "," + minMemoryUsed + "," + bestTransfer);
                    }

                    // Reset for next block
                    minServersUsed = Integer.MAX_VALUE;
                    minMemoryUsed = Double.MAX_VALUE;
                    currentBlockRows.clear();
                    firstBlock = false;
                    continue;
                }

                String[] values = line.split(",");
                if (values.length == 3) {
                    int serversUsed = Integer.parseInt(values[0].trim());
                    double memoryUsed = Double.parseDouble(values[1].trim());

                    if (serversUsed < minServersUsed) {
                        minServersUsed = serversUsed;
                    }
                    if (memoryUsed < minMemoryUsed) {
                        minMemoryUsed = memoryUsed;
                    }

                    currentBlockRows.add(values); // Store row for later evaluation
                }
            }

            // Final block (if not yet written)
            if (!firstBlock && !currentBlockRows.isEmpty()) {
                double threshold = 0.10 * minMemoryUsed;

                double bestTransfer = 0.0;
                for (String[] values : currentBlockRows) {
                    double dataTransferred = Double.parseDouble(values[2].trim());
                    if (dataTransferred <= threshold && dataTransferred > bestTransfer) {
                        bestTransfer = dataTransferred;
                    }
                }

                pw.println(minServersUsed + "," + minMemoryUsed + "," + bestTransfer);
            }

        } catch (IOException e) {
            System.err.println("Error processing CSV files: " + e.getMessage());
        }
    }


    // data transfer with min value>0

//    public void calculateAndAppendMinimumsToCsv() {
//        String inputFilename = "output.csv";
//        String outputFilename = "BFS_Reallocation_equal.csv";
//
//        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename));
//             FileWriter fw = new FileWriter(outputFilename);
//             PrintWriter pw = new PrintWriter(fw)) {
//
//            String line;
//            int minServersUsed = Integer.MAX_VALUE;
//            double minMemoryUsed = Double.MAX_VALUE;
//            double minDataTransferredUsed = Double.MAX_VALUE;
//            boolean hasNonZeroTransfer = false;
//            boolean firstBlock = true;
//
//            // Updated CSV header
//            pw.println("min_servers_used,min_memory_used,min_nonzero_data_transferred");
//
//            while ((line = br.readLine()) != null) {
//                if (line.trim().isEmpty()) {
//                    // Write current block result
//                    if (minServersUsed != Integer.MAX_VALUE && minMemoryUsed != Double.MAX_VALUE) {
//                        double finalDataTransferred = hasNonZeroTransfer ? minDataTransferredUsed : 0.0;
//                        pw.println(minServersUsed + "," + minMemoryUsed + "," + finalDataTransferred);
//                    }
//
//                    // Reset for next block
//                    minServersUsed = Integer.MAX_VALUE;
//                    minMemoryUsed = Double.MAX_VALUE;
//                    minDataTransferredUsed = Double.MAX_VALUE;
//                    hasNonZeroTransfer = false;
//                    firstBlock = false;
//                    continue;
//                }
//
//                String[] values = line.split(",");
//                if (values.length == 3) {
//                    int serversUsed = Integer.parseInt(values[0].trim());
//                    double memoryUsed = Double.parseDouble(values[1].trim());
//                    double dataTransferred = Double.parseDouble(values[2].trim());
//
//                    if (serversUsed < minServersUsed) {
//                        minServersUsed = serversUsed;
//                    }
//                    if (memoryUsed < minMemoryUsed) {
//                        minMemoryUsed = memoryUsed;
//                    }
//
//                    if (dataTransferred > 0) {
//                        hasNonZeroTransfer = true;
//                        if (dataTransferred < minDataTransferredUsed) {
//                            minDataTransferredUsed = dataTransferred;
//                        }
//                    }
//                }
//            }
//
//            // Final block (if not yet written)
//            if (!firstBlock &&
//                    minServersUsed != Integer.MAX_VALUE &&
//                    minMemoryUsed != Double.MAX_VALUE) {
//
//                double finalDataTransferred = hasNonZeroTransfer ? minDataTransferredUsed : 0.0;
//                pw.println(minServersUsed + "," + minMemoryUsed + "," + finalDataTransferred);
//            }
//
//        } catch (IOException e) {
//            System.err.println("Error processing CSV files: " + e.getMessage());
//        }
//    }


//    public void calculateAndAppendMinimumsToCsv() {
//        String inputFilename = "output.csv";
//        String outputFilename = "BFS_Reallocation_equal.csv";
//
//        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename));
//             FileWriter fw = new FileWriter(outputFilename);
//             PrintWriter pw = new PrintWriter(fw)) {
//
//            String line;
//            int minServersUsed = Integer.MAX_VALUE;
//            double minMemoryUsed = Double.MAX_VALUE;
//
//            double totalDataTransferred = 0.0;  // For averaging
//            int dataTransferCount = 0;
//
//            boolean firstBlock = true;
//
//            // Write header
//            pw.println("min_servers_used,min_memory_used,avg_data_transferred");
//
//            while ((line = br.readLine()) != null) {
//                if (line.trim().isEmpty()) {
//                    // Write result of this block
//                    if (dataTransferCount > 0 &&
//                            minServersUsed != Integer.MAX_VALUE &&
//                            minMemoryUsed != Double.MAX_VALUE) {
//
//                        double avgDataTransferred = totalDataTransferred / dataTransferCount;
//                        pw.println(minServersUsed + "," + minMemoryUsed + "," + avgDataTransferred);
//                    }
//
//                    // Reset for next block
//                    minServersUsed = Integer.MAX_VALUE;
//                    minMemoryUsed = Double.MAX_VALUE;
//                    totalDataTransferred = 0.0;
//                    dataTransferCount = 0;
//                    firstBlock = false;
//                    continue;
//                }
//
//                String[] values = line.split(",");
//                if (values.length == 3) {
//                    int serversUsed = Integer.parseInt(values[0].trim());
//                    double memoryUsed = Double.parseDouble(values[1].trim());
//                    double dataTransferred = Double.parseDouble(values[2].trim());
//
//                    // Update minimums
//                    if (serversUsed < minServersUsed) {
//                        minServersUsed = serversUsed;
//                    }
//                    if (memoryUsed < minMemoryUsed) {
//                        minMemoryUsed = memoryUsed;
//                    }
//
//                    // Update running sum and count for average
//                    totalDataTransferred = totalDataTransferred+dataTransferred;
//                    dataTransferCount++;
//                }
//            }
//
//            // Final block (if not written yet)
//            if (!firstBlock && dataTransferCount > 0 &&
//                    minServersUsed != Integer.MAX_VALUE &&
//                    minMemoryUsed != Double.MAX_VALUE) {
//
//                double avgDataTransferred = totalDataTransferred / dataTransferCount;
//                pw.println(minServersUsed + "," + minMemoryUsed + "," + avgDataTransferred);
//            }
//
//        } catch (IOException e) {
//            System.err.println("Error processing CSV files: " + e.getMessage());
//        }
//    }




    //valid for one output according to maxIteration

//    private void appendToCsv(int totalServersUsed, double totalMemoryUsed) {
//        String filename = "output.csv";
//
//        try (FileWriter fw = new FileWriter(filename, true);
//             PrintWriter pw = new PrintWriter(fw)) {
//            pw.println(totalServersUsed + "," + totalMemoryUsed);
//        } catch (IOException e) {
//            System.err.println("Error writing to CSV file: " + e.getMessage());
//        }
//    }
//
//    public void calculateAndAppendMinimumsToCsv() {
//        String inputFilename = "output.csv";
//        String outputFilename = "output_min.csv";
//
//        int minServersUsed = Integer.MAX_VALUE;
//        double minMemoryUsed = Double.MAX_VALUE;
//
//        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] values = line.split(",");
//                if (values.length == 2) {
//                    int serversUsed = Integer.parseInt(values[0].trim());
//                    double memoryUsed = Double.parseDouble(values[1].trim());
//
//                    if (serversUsed < minServersUsed) {
//                        minServersUsed = serversUsed;
//                    }
//                    if (memoryUsed < minMemoryUsed) {
//                        minMemoryUsed = memoryUsed;
//                    }
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Error reading from CSV file: " + e.getMessage());
//            return;
//        }
//
//        try (FileWriter fw = new FileWriter(outputFilename);
//             PrintWriter pw = new PrintWriter(fw)) {
//            pw.println("min_servers_used,min_memory_used");
//            pw.println(minServersUsed + "," + minMemoryUsed);
//        } catch (IOException e) {
//            System.err.println("Error writing to CSV file: " + e.getMessage());
//        }
//    }

    private void printSolution(List<Server> solution) {
        for (int i = 0; i < solution.size(); i++) {
            Server server = solution.get(i);

            System.out.print("Server " + (i + 1) + ": ");
            System.out.print("Memory items: ");

            // Access memory arrays as List<List<Double>>
            List<List<Double>> memoryArrays = server.getMemoryItemsArrays();
            System.out.print("[");

            for (int j = 0; j < memoryArrays.size(); j++) {
                // Print each memory array (List<Double>)
                System.out.print(memoryArrays.get(j).toString());
                if (j < memoryArrays.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.print("], ");
            System.out.print("CPU items: " + server.getCpuItems() + ", ");
            System.out.print("Unique Memory used: " + server.calculateUniqueMemoryUsed() + ", ");
            System.out.print("CPU used: " + server.getCpuUsed() + ", ");
            System.out.print("Server capacity (Memory): " + server.getServerCapacityMemory() + ", ");
            System.out.print("Server capacity (CPU): " + server.getServerCapacityCpu());
            System.out.println();
        }
    }


}
