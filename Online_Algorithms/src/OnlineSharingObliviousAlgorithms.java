import java.util.*;
import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;

public class OnlineSharingObliviousAlgorithms {
    private ArrayList<Task> taskItems;
    private Double[] requestItems;
    private Double[] arrivalTimeItems;
    private Double[] durationTimeItems;
    private Double[] terminationTimeItems;
    private Double[][] taskBinMatrix;
    private int totalServerCount;
    private Bin[] binItems;
    private Data[] dataItems;
    private Double totalDataSize;
    private Double[] unionOfDataSize;
    private List<Double[]> unionOfDataSizesList = new ArrayList<>();
    int taskBinMatrixSize;
    int taskBinDataSize;
    private Integer taskCount;
    private ArrayList<Bin> servers;

    int maxIterations = 100; // Maximum iterations

    int stableThreshold = 80;
    private List<String[]> allBinAndTaskDetails = new ArrayList<>();

    private List<List<Double>> inputItems= new ArrayList<>();
    private List<Double> cpuItems= new ArrayList<>();

    private List<List<Double>> cpuItemsList = new ArrayList<>();
    private List<List<List<Double>>> inputItemsList = new ArrayList<>();

    private final Random rng = new Random(2025); // seed for reproducibility

//    private int dataItemsCount;
//    private Integer[] sum;
//    private ArrayList<Task> candidateTasks;
//    private ArrayList<Data> candidateData;
//    int k_hat;
//    int j;

    public OnlineSharingObliviousAlgorithms(Double[] requestItems, Double[] arrivalTimeItems, Double[] terminationTimeItems, Double[][] taskBinMatrix) {
        this.requestItems = requestItems;
        this.arrivalTimeItems = arrivalTimeItems;
        this.terminationTimeItems = terminationTimeItems;
        this.taskBinMatrix = taskBinMatrix;
    }

    public OnlineSharingObliviousAlgorithms(ArrayList<Bin> servers) {
        this.servers = servers;
        this.totalServerCount = servers.size();
    }

    {
        taskItems = new ArrayList<Task>();
        taskCount = Integer.MIN_VALUE;
        totalServerCount = Integer.MIN_VALUE;
    }


    public void setBinItems(String line1, String line2, String splitter) {
        Double[][] result = Helper.readTwoDimensionalDoubleArrayFromString(line1, line2, 0, splitter);
        Double[] binItemsCPUCapacity = result[0];
        Double[] binItemsMemoryCapacity = result[1];
        List<Bin> binList = new ArrayList<>();
        for (int index = 0; index < binItemsCPUCapacity.length; index++) {
            binList.add(new Bin(index, binItemsCPUCapacity[index], binItemsMemoryCapacity[index]));
        }
        this.binItems = binList.toArray(new Bin[0]);
    }

    private Data[] extractDataTypeItemsFromMatrixRow(Double[] taskRow) {
        ArrayList<Data> dataTypeItemsArrayList = new ArrayList<Data>();
        for (int index = 0; index < taskRow.length; index++) {
            String dataTypeName = String.valueOf(index);
            Data oDataType = new Data(dataTypeName, taskRow[index]);
            dataTypeItemsArrayList.add(oDataType);
        }
        Data[] dataTypeItems = new Data[dataTypeItemsArrayList.size()];
        dataTypeItems = dataTypeItemsArrayList.toArray(dataTypeItems);
        return dataTypeItems;
    }

    private Double[] extractDataSizesFromMatrixRow(Double[] taskRow) {
        Double[] dataSizes = new Double[taskRow.length];
        for (int index = 0; index < taskRow.length; index++) {
            dataSizes[index] = taskRow[index];
        }
        return dataSizes;
    }

    public Double[] calculateUnionOfAssignedDataSizes(Bin bin) {
        ArrayList<Task> taskList = bin.getTaskList();
        if (taskList.isEmpty()) {
            // Handle the case where the task list is empty
            return new Double[0];
        }
        int dataSizeLength = taskList.get(0).getDataItems().length;
        // Initialize the result array with zeros
        Double[] unionDataSizes = new Double[dataSizeLength];
        for (int i = 0; i < dataSizeLength; i++) {
            unionDataSizes[i] = 0.0;
        }
        // Iterate through tasks and update the unionDataSizes array
        for (Task task : taskList) {
            Data[] dataItems = task.getDataItems();
            for (int i = 0; i < dataSizeLength; i++) {
                unionDataSizes[i] = Math.max(unionDataSizes[i], dataItems[i].getSize());
            }
        }
        return unionDataSizes;
    }

    public Double[] calculateSummationOfAssignedDataSizes(Bin bin) {
        ArrayList<Task> taskList = bin.getTaskList();
        if (taskList.isEmpty()) {
            // Handle the case where the task list is empty
            return new Double[0];
        }
        int dataSizeLength = taskList.get(0).getDataItems().length;
        // Initialize the result array with zeros
        Double[] summationDataSizes = new Double[dataSizeLength];
        for (int i = 0; i < dataSizeLength; i++) {
            summationDataSizes[i] = 0.0;
        }
        // Iterate through tasks and update the summationDataSizes array
        for (Task task : taskList) {
            Data[] dataItems = task.getDataItems();
            for (int i = 0; i < dataSizeLength; i++) {
                summationDataSizes[i] += dataItems[i].getSize();
            }
        }
        return summationDataSizes;
    }

    public Double[][] calculateUnionOfDataSizesForAllBins() {
        Double[][] unionOfDataSizesForBin = new Double[totalServerCount][];
        for (int k = 0; k < totalServerCount; k++) {
            Bin bin = binItems[k];
            unionOfDataSizesForBin[k] = calculateUnionOfAssignedDataSizes(bin);
        }
        return unionOfDataSizesForBin;
    }

    public Integer countActiveBins(Bin[] binItems) {
        Integer numberOfActiveBin = 0;

        for (Bin bin : binItems) {
            if (bin.isUsed()) {
                numberOfActiveBin++;
            }
        }
        return numberOfActiveBin;
    }

//    public Double sumOfDataOnServer(List<Double[]> unionData) {
//        Double sum = 0.0;
//        for (Double[] array : unionData) {
//            for (Double num : array) {
//                sum += num;
//            }
//        }
//        return sum;
//
//    }

    public Double sumOfDataOnServer(List<Double[]> unionData) {
        Double sum = 0.0;
        for (Double[] array : unionData) {
            if (array != null) {
                for (Double num : array) {
                    if (num != null) {
                        sum += num;
                    }
                }
            }
        }
        return sum;
    }

    public void printBinAndTaskDetails(List<Bin> bins) {
        for (int i = 0; i < bins.size(); i++) {
            Bin bin = bins.get(i);
            // System.out.println("Bin " + i + " (CPU: " + bin.getBinCPUSize() + ", Memory: " + bin.getBinMemorySize() + ", Used: " + bin.isUsed() + "):");
            List<Task> tasks = bin.getTaskList();
            if (tasks.isEmpty()) {
                // System.out.println("  No tasks are currently assigned to this bin.");
            } else {
                for (Task task : tasks) {
                    // Get the list of data sizes for the task
                    StringBuilder dataList = new StringBuilder("[");
                    Data[] dataItems = task.getDataItems();
                    for (int j = 0; j < dataItems.length; j++) {
                        dataList.append(dataItems[j].getSize()); // Assuming Data class has a getSize method
                        if (j < dataItems.length - 1) {
                            dataList.append(", "); // Add a comma if not the last element
                        }
                    }
                    dataList.append("]");

                    // Print the task index, request, and its data sizes
                    System.out.println("  Task " + task.getIndex() + " (cpuItems: " + task.getRequest() + ", inputItems: " + dataList + ")");

                }
            }
        }
        System.out.println("----------------------------");
    }

    private List<String[]> collectBinAndTaskDetails(List<Bin> bins) {
        List<String[]> details = new ArrayList<>();

        for (int i = 0; i < bins.size(); i++) {
            Bin bin = bins.get(i);
            String binDetail = "Bin " + i + " (CPU: " + bin.getBinCPUSize() + ", Memory: " + bin.getBinMemorySize() + ", Used: " + bin.isUsed() + ")";

            List<Task> tasks = bin.getTaskList();

            // Check if there are tasks, only add details if tasks exist
            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    StringBuilder dataList = new StringBuilder("[");
                    Data[] dataItems = task.getDataItems();
                    for (int j = 0; j < dataItems.length; j++) {
                        dataList.append(dataItems[j].getSize());
                        if (j < dataItems.length - 1) {
                            dataList.append(", ");
                        }
                    }
                    dataList.append("]");

                    // Combine bin detail and task details into a single line
                    details.add(new String[]{
                            //binDetail +
                            " Task " + task.getIndex() + " (cpuItems: " + task.getRequest() + ", inputItems: " + dataList.toString() + ")"
                    });
                }
            }
        }


        return details;
    }




    // for all iterations
    public void prepareItems() {
        List<Double> currentCpuItems = new ArrayList<>();
        List<List<Double>> currentInputItems = new ArrayList<>();
        boolean newIteration = true; // Flag to identify the start of a new iteration

        for (String[] block : allBinAndTaskDetails) {
            for (String detail : block) {
                // Check if the line is empty, indicating an iteration separator
                if (detail.trim().isEmpty()) {
                    if (!newIteration) {
                        // Store the completed iteration data
                        cpuItemsList.add(new ArrayList<>(currentCpuItems));
                        inputItemsList.add(new ArrayList<>(currentInputItems));
                        // Reset for the next iteration
                        currentCpuItems.clear();
                        currentInputItems.clear();
                    }
                    newIteration = true;
                    continue;
                }
                newIteration = false;

                // Process the current detail line
                try {
                    // Extract cpuItems
                    int cpuStart = detail.indexOf("cpuItems:") + 9;
                    int cpuEnd = detail.indexOf(",", cpuStart);
                    if (cpuStart < 9 || cpuEnd == -1) {
                        continue; // Skip if indices are incorrect
                    }
                    String cpuItemStr = detail.substring(cpuStart, cpuEnd).trim();
                    double cpuItem = Double.parseDouble(cpuItemStr);
                    currentCpuItems.add(cpuItem);

                    // Extract inputItems
                    int inputStart = detail.indexOf("[", cpuEnd) + 1;
                    int inputEnd = detail.indexOf("]", inputStart);
                    if (inputStart <= cpuEnd || inputEnd == -1) {
                        continue; // Skip if indices are incorrect
                    }
                    String inputItemsStr = detail.substring(inputStart, inputEnd).trim();
                    List<Double> inputItem = Arrays.stream(inputItemsStr.split(","))
                            .map(String::trim)
                            .map(Double::parseDouble)
                            .collect(Collectors.toList());
                    currentInputItems.add(inputItem);
                } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
                    System.err.println("Error parsing numbers in detail: " + detail);
                    e.printStackTrace();
                }
            }
        }

        // Add the last iteration if not added yet
        if (!currentCpuItems.isEmpty() && !currentInputItems.isEmpty()) {
            cpuItemsList.add(currentCpuItems);
            inputItemsList.add(currentInputItems);
        }
    }


    public List<List<Double>> getInputItems() {
        return inputItems;
    }

    public List<Double> getCpuItems() {
        return cpuItems;
    }

    public List<List<Double>> getCpuItemsList() {
        return cpuItemsList;
    }

    public List<List<List<Double>>> getInputItemsList() {
        return inputItemsList;
    }


    public void writeDetailsToCSV(List<String[]> details, String fileName) {

        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(fileName))) {
            for (String[] detail : details) {
                csvWriter.writeNext(detail);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

// for random implementation
    /** Pick a random feasible server under SHARING-OBLIVIOUS memory (delta_jk = 0). */
    private int pickRandomFeasibleServerOblivious(Task arrivalTask, double[] taskSizes) {
        ArrayList<Integer> feas = new ArrayList<>();
        for (int k = 0; k < totalServerCount; k++) {
            Bin bin = binItems[k];
            // CPU fits?
            if (bin.getBinCPUSize() < arrivalTask.getRequest()) continue;
            // Memory fits? (oblivious -> we subtract the full task size)
            double memInc = taskSizes[arrivalTask.getIndex()];
            if (bin.getBinMemorySize() >= memInc) feas.add(k);
        }
        if (feas.isEmpty()) return -1;
        return feas.get(rng.nextInt(feas.size()));
    }


    public OnlineSharingObliviousAlgorithms(String inputFilePath) {
        try {
            int currentLine = 0; //current line of input
            int currentLineNext =0;
            List<String> list = Helper.readInputFileLineByLine(inputFilePath);
            //calculate number of task (rows) in adjacency matrix
            taskBinMatrixSize = Integer.parseInt(list.get(currentLine));
            //System.out.println("taskBinMatrixSize: " + taskBinMatrixSize);
            currentLine++;
            //calculate number of data (columns) in adjacency matrix
            taskBinDataSize= Integer.parseInt(list.get(currentLine));
            //System.out.println("taskBinDataSize: " + taskBinDataSize);
            //fill out adjacency matrix with below function
            this.taskBinMatrix = Helper.createTaskBinMatrixFromStringList(list, taskBinMatrixSize, taskBinDataSize, " ");
            //System.out.println("print task-bin matrix: " + Arrays.deepToString(taskBinMatrix));
            //System.out.println("Data Items:");

//            for (int i = 0; i < taskBinMatrixSize; i++) {
//                System.out.print("Data Item " + (i) + ": ");
//                for (int col = 0; col < taskBinMatrix[j].length; col++) {
//                    System.out.print(taskBinMatrix[i][col] + " ");
//                }
//                System.out.println();
//            }
            currentLine += taskBinMatrixSize + 1;

            // Calculate task sizes
            double[] taskSizes = Helper.calculateTaskSizes(taskBinMatrix);
            // Print task sizes
//            for (int i = 0; i < taskSizes.length; i++) {
//                System.out.println("Size of Task " + (i + 1) + ": " + taskSizes[i]);
//            }

            //fill out computational requests
            this.requestItems = Helper.readDoubleTypeArrayFromString(list.get(currentLine), taskBinMatrixSize, ",");
            // System.out.println("\nprint task's request: " + Arrays.deepToString(requestItems));
            currentLine++;
            // Tasks' arrival time
            this.arrivalTimeItems = Helper.readDoubleTypeArrayFromString(list.get(currentLine), taskBinMatrixSize, ",");
         //   System.out.println("print task's arrival time: " + Arrays.deepToString(arrivalTimeItems));
            currentLine++;

            // Tasks' duration
            this.durationTimeItems = Helper.readDoubleTypeArrayFromString(list.get(currentLine), taskBinMatrixSize, ",");
         //   System.out.println("print task's duration time: " + Arrays.deepToString(durationTimeItems));

            // Tasks' termination time
            this.terminationTimeItems = new Double[taskBinMatrixSize];
            for (int i = 0; i < taskBinMatrixSize; i++) {
                terminationTimeItems[i]= arrivalTimeItems[i] + durationTimeItems[i];
            }
          //  System.out.println("print task's termination time: " + Arrays.deepToString(terminationTimeItems));

            currentLine++;
            currentLineNext = currentLine+1;

            // fill out server's CPU and memory capacity
            this.setBinItems(list.get(currentLine), list.get(currentLineNext),",");
            //System.out.println("print bin's items:" + Arrays.deepToString(binItems));

            for (int i = 0; i < taskBinMatrixSize; i++) {
                taskItems.add(new Task(i, requestItems[i], arrivalTimeItems[i], durationTimeItems[i], new Data[0]));
            }
//            for (Task task : taskItems) {
//                System.out.println("Task " + task.getIndex() + ": " + task.getRequest() + ", " + task.getArrivalTime()+ ", " + task.getDurationTime());
//            }

            // Number of servers
            totalServerCount = binItems.length;

            //for random implementation
            // Ensure per-server union list is indexable for all servers from the start
            unionOfDataSizesList = new ArrayList<>(totalServerCount);
            for (int i = 0; i < totalServerCount; i++) unionOfDataSizesList.add(null);


        } catch (IndexOutOfBoundsException e2) {
            e2.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }



    //******************** implementation of random online sharing oblivious algorithm

    /************************ Random Online Sharing-Oblivious ********************************/
    public void runOnline_RandomSO() {
        List<AbstractMap.SimpleEntry<Integer, Double>> pairList = new ArrayList<>();
        PriorityQueue<Task> arrivalQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));
        PriorityQueue<Task> terminationQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));
        for (Task t : taskItems) arrivalQueue.add(t);

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));
        for (Task t : taskItems) terminationQueue.add(t);

        List<Integer> numOfActiveBinsList = new ArrayList<>();
        List<Double> sumOfDataList = new ArrayList<>();
        int iteration = 0;

        double[] taskSizes = Helper.calculateTaskSizes(taskBinMatrix);

        int k_hat = 0;
        while (k_hat < taskBinMatrixSize) {
            Task arrivalTask = arrivalQueue.peek();
            Task terminationTask = terminationQueue.peek();
            if (arrivalTask == null) break;

            // --- CASE A: arrival before next termination ---
            if (terminationTask == null || arrivalTask.getArrivalTime() < terminationTask.getTerminationTime()) {

                int selectedServerIndex = pickRandomFeasibleServerOblivious(arrivalTask, taskSizes);

                if (selectedServerIndex != -1) {
                    Bin selectedServer = binItems[selectedServerIndex];

                    // CPU
                    selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                    // MEM (oblivious: subtract full task data size)
                    double memInc = taskSizes[arrivalTask.getIndex()];
                    selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - memInc);

                    // Attach task + data
                    selectedServer.addTask(arrivalQueue.poll());
                    dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                    arrivalTask.setDataItems(dataItems);

                    // Update per-server "union" under oblivious model: we use the SUMMATION array
                    unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);
                    unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);

                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                    selectedServer.setUsed(true);
                    numOfActiveBinsList.add(countActiveBins(binItems));

                    List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                    allBinAndTaskDetails.addAll(det);
                    allBinAndTaskDetails.add(new String[]{""});
                    iteration++;
                } else {
                    // no feasible server now -> drop this arrival (matches your FFS behavior)
                    arrivalTask.setCandid(false);
                    arrivalQueue.remove(arrivalTask);
                    terminationQueue.remove(arrivalTask);
                }

                k_hat++;

                // --- CASE B: arrival time == next termination time ---
            } else if (Objects.equals(arrivalTask.getArrivalTime(), terminationTask.getTerminationTime())) {

                // First free all tasks completing at this instant (oblivious adds back full size)
                for (int i = 0; i < binItems.length; i++) {
                    for (Task binTask : new ArrayList<>(binItems[i].getTaskList())) {
                        if (Objects.equals(binTask.getTerminationTime(), arrivalTask.getArrivalTime())) {
                            terminationQueue.poll();
                            binItems[i].removeTask(binTask);
                            binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                            if (binItems[i].getTaskList().isEmpty()) {
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + taskSizes[binTask.getIndex()]);
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]); // empty -> []
                                binItems[i].setUnionDataSizes(null);
                                unionOfDataSizesList.set(i, unionOfDataSize);
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                binItems[i].setUsed(false);
                                numOfActiveBinsList.add(countActiveBins(binItems));

                                List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(det);
                                allBinAndTaskDetails.add(new String[]{""});
                                iteration++;
                            } else {
                                binItems[i].setUsed(true);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(det);
                                allBinAndTaskDetails.add(new String[]{""});
                                iteration++;

                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(unionOfDataSize);
                                unionOfDataSizesList.set(i, unionOfDataSize);

                                // add back full size for the completed task (oblivious)
                                binItems[i].setBinMemorySize(
                                        binItems[i].getBinMemorySize() + taskSizes[binTask.getIndex()]
                                );
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                            }
                        }
                    }
                }

                // Now try to place arrival at same timestamp — RANDOM OBLIVIOUS
                int selectedServerIndex = pickRandomFeasibleServerOblivious(arrivalTask, taskSizes);
                if (selectedServerIndex != -1) {
                    Bin selectedServer = binItems[selectedServerIndex];

                    selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                    double memInc = taskSizes[arrivalTask.getIndex()];
                    selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - memInc);

                    selectedServer.addTask(arrivalQueue.poll());
                    dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                    arrivalTask.setDataItems(dataItems);

                    unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);
                    selectedServer.setUnionDataSizes(unionOfDataSize);
                    unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);

                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                    selectedServer.setUsed(true);
                    numOfActiveBinsList.add(countActiveBins(binItems));

                    List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                    allBinAndTaskDetails.addAll(det);
                    allBinAndTaskDetails.add(new String[]{""});
                    iteration++;
                } else {
                    arrivalTask.setCandid(false);
                    arrivalQueue.remove(arrivalTask);
                    terminationQueue.remove(arrivalTask);
                }

                k_hat++;

                // --- CASE C: we must process earlier terminations first ---
            } else { // arrival > termination
                List<Task> tasksToRemove = new ArrayList<>();
                for (Task tt : terminationQueue) {
                    if (Objects.equals(tt.getTerminationTime(), terminationTask.getTerminationTime())) {
                        tasksToRemove.add(tt);
                    }
                }

                for (int i = 0; i < binItems.length; i++) {
                    for (Task binTask : new ArrayList<>(binItems[i].getTaskList())) {
                        if (Objects.equals(binTask.getTerminationTime(), terminationTask.getTerminationTime())) {
                            binItems[i].removeTask(binTask);
                            binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                            if (binItems[i].getTaskList().isEmpty()) {
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + taskSizes[binTask.getIndex()]);
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(null);
                                unionOfDataSizesList.set(i, unionOfDataSize);
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                binItems[i].setUsed(false);
                                numOfActiveBinsList.add(countActiveBins(binItems));

                                List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(det);
                                allBinAndTaskDetails.add(new String[]{""});
                                iteration++;
                            } else {
                                binItems[i].setUsed(true);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(det);
                                allBinAndTaskDetails.add(new String[]{""});
                                iteration++;

                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(unionOfDataSize);
                                unionOfDataSizesList.set(i, unionOfDataSize);

                                binItems[i].setBinMemorySize(
                                        binItems[i].getBinMemorySize() + taskSizes[binTask.getIndex()]
                                );
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                            }
                        }
                    }
                }
                for (Task tr : tasksToRemove) terminationQueue.remove(tr);
                continue; // reconsider the same arrival vs next termination
            }

            // trailing clean-up (same shape as your FFS)
            if (k_hat >= taskBinMatrixSize && !terminationQueue.isEmpty()) {
                boolean flag1 = false;
                int i = 0;
                while (i < binItems.length) {
                    for (Task binTask : new ArrayList<>(binItems[i].getTaskList())) {
                        if (terminationQueue.peek() != null &&
                                Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())) {
                            flag1 = true;
                            terminationQueue.poll();
                            binItems[i].removeTask(binTask);
                            binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                            if (binItems[i].getTaskList().isEmpty()) {
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + taskSizes[binTask.getIndex()]);
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(null);
                                unionOfDataSizesList.set(i, unionOfDataSize);
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                binItems[i].setUsed(false);
                                numOfActiveBinsList.add(countActiveBins(binItems));

                                List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(det);
                                allBinAndTaskDetails.add(new String[]{""});
                                iteration++;
                            } else {
                                binItems[i].setUsed(true);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> det = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(det);
                                allBinAndTaskDetails.add(new String[]{""});
                                iteration++;

                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(unionOfDataSize);
                                unionOfDataSizesList.set(i, unionOfDataSize);

                                binItems[i].setBinMemorySize(
                                        binItems[i].getBinMemorySize() + taskSizes[binTask.getIndex()]
                                );
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                            }
                            break;
                        }
                    }
                    if (flag1) i = 0; else i++;
                    flag1 = false;
                }
            }
            if (k_hat >= taskBinMatrixSize && terminationTask == null) break;
            if (k_hat >= taskBinMatrixSize) break;
        }

        // CSV output like yours
//        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
//            try (com.opencsv.CSVWriter csvWriter =
//                         new com.opencsv.CSVWriter(new FileWriter("RandomSO-Online-Sharing-Oblivious.csv"))) {
//                for (int i = 0; i < numOfActiveBinsList.size(); i++) {
//                    String[] row = { String.valueOf(numOfActiveBinsList.get(i)),
//                            String.valueOf(sumOfDataList.get(i)) };
//                    csvWriter.writeNext(row);
//                }
//            } catch (IOException e) { e.printStackTrace(); }
//        } else {
//            System.err.println("Lists are of different sizes.");
//        }
//
//        writeDetailsToCSV(allBinAndTaskDetails, "BinAndTaskDetails_RandomSO.csv");

        // Append results to a single CSV across all datasets (Sharing-Oblivious case)
        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            final String outPath = "RandomSO-Online-Sharing-Oblivious.csv"; // change name for oblivious output
            try {
                boolean exists = java.nio.file.Files.exists(java.nio.file.Paths.get(outPath));

                try (com.opencsv.CSVWriter csvWriter =
                             new com.opencsv.CSVWriter(new FileWriter(outPath, true))) { // append = true

                    // Write header only once
                    if (!exists) {
                        csvWriter.writeNext(new String[] { "active_bins", "sum_of_data" });
                    }

                    for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                        csvWriter.writeNext(new String[] {
                                String.valueOf(numOfActiveBinsList.get(i)),
                                String.valueOf(sumOfDataList.get(i))
                        });
                    }

                    // Optional visual separation between datasets
                    csvWriter.writeNext(new String[] {});
                }
            } catch (IOException e) { e.printStackTrace(); }
        } else {
            System.err.println("Lists are of different sizes.");
        }

// Keep your existing detailed dump output:
        writeDetailsToCSV(allBinAndTaskDetails, "BinAndTaskDetails_RandomSO.csv");

    }



    /************************implementation of Online Best Fit Sharing-Oblivious ********************************/
    public void runOnline_BF() {
        List<AbstractMap.SimpleEntry<Integer, Double>> pairList = new ArrayList<>();
        PriorityQueue<Task> arrivalQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));
        PriorityQueue<Task> terminationQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));


        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));


        for (Task task : taskItems) {
            arrivalQueue.add(task);
        }

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));


        for (Task task : taskItems) {
            terminationQueue.add(task);
        }


        List<Integer> numOfActiveBinsList = new ArrayList<Integer>();
        List<Double> sumOfDataList = new ArrayList<Double>();

        int iteration = 0;

        double[] taskSizes = Helper.calculateTaskSizes(taskBinMatrix);

        // Initialize k_hat and j
        double efficiency;
        double alpha = 0.5;//1.0, 0.2, 0.8, 0.0002, 0.8, 0.97
        double beta = 0.5; //1.0, 0.2, 0.8, 0.9998, 1.2, 0.03
        int k_hat = 0;
        while (k_hat < taskBinMatrixSize) {
            Task arrivalTask = arrivalQueue.peek();
            Task terminationTask = terminationQueue.peek();
            totalDataSize = taskSizes[arrivalTask.getIndex()];
            //System.out.println("total data size for task " + arrivalTask.getIndex() + " is: " + totalDataSize);

            // Initialize max efficiency and selected server index
            double maxEfficiency = 0.0;
            int selectedServerIndex = -1; //this is k_tilde
            double delta_jk = 0.0;

            if (arrivalTask != null && terminationTask != null) {
                if (arrivalTask.getArrivalTime() < terminationTask.getTerminationTime()) {
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount; k++) {
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            //System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            //delta_jk = Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSize, j);
                            delta_jk = 0.0;
                            //System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }

                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            // Calculate efficiency
                            efficiency = 1.0 / Math.sqrt((alpha * (bin.getBinCPUSize() - arrivalTask.getRequest())) + (beta * (bin.getBinMemorySize() - (totalDataSize - delta_jk))));
                            //System.out.println("efficiency is: " + efficiency);
                            // Update max efficiency and selected server index if necessary
                            arrivalTask.setCandid(true);

                            if (efficiency > maxEfficiency) {
                                maxEfficiency = efficiency;
                                selectedServerIndex = k;
                                //System.out.println("selected server index is: " + selectedServerIndex);
                            }
                        } else {
                            efficiency = 0.0;
                            arrivalTask.setCandid(false);
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }

                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        //System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        //System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        //System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElements(taskBinMatrix, j, selectedServerIndex)));
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSize, j)));
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }


                        selectedServer.addTask(arrivalQueue.poll());
                        //System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);
//
//                        if (selectedServerIndex < unionOfDataSizesList.size()) {
//                            // If the index exists in the list, use set
//                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
//                        } else {
//                            // If the index is new, use add
//                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
//                        }

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, fill the list with nulls up to the index
                            while (unionOfDataSizesList.size() < selectedServerIndex) {
                                unionOfDataSizesList.add(null);
                            }

                            // Add the data at the specified index
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }

//                        System.out.println("\nPrint list: ");
//                        for (Integer[] array : unionOfDataSizesList) {
//                            System.out.println(Arrays.toString(array));
//                        }
                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        //System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        //System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());
                        List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                        allBinAndTaskDetails.addAll(binAndTaskDetails);
                        allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                        iteration++;

                    }
                }
                //else if is here
                else if (Objects.equals(arrivalTask.getArrivalTime(), terminationTask.getTerminationTime())){
                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), arrivalTask.getArrivalTime())) {
                                terminationQueue.poll();
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if(binItems[i].getTaskList().isEmpty()){
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                }
                                else{
                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));

                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

//                                    for (Integer[] array : unionOfDataSizesList) {
//                                        System.out.println(Arrays.toString(array));
//                                    }
//                                    System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }

                        }
                    }

                    //after removing offloaded task add new arrival task
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount ; k++) {
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            //System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            //delta_jk = Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSize, j);
                            delta_jk = 0.0;
                            //System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }

                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            // Calculate efficiency
                            efficiency = 1.0 / Math.sqrt((alpha * (bin.getBinCPUSize() - arrivalTask.getRequest())) + (beta * (bin.getBinMemorySize() - (totalDataSize - delta_jk))));
                            //System.out.println("efficiency is: " + efficiency);
                            arrivalTask.setCandid(true);
                            // Update max efficiency and selected server index if necessary
                            if (efficiency > maxEfficiency) {
                                maxEfficiency = efficiency;
                                selectedServerIndex = k;
                                //System.out.println("selected server index is: " + selectedServerIndex);
                            }
                        } else {
                            efficiency = 0.0;
                            arrivalTask.setCandid(false);
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }
                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        //System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        //System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        //System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }


                        selectedServer.addTask(arrivalQueue.poll());
                        //System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, use add
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }
                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

//                        System.out.println("\nPrint list: ");
//                        for (Integer[] array : unionOfDataSizesList) {
//                            System.out.println(Arrays.toString(array));
//                        }
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        //System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        //System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());
                        List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                        allBinAndTaskDetails.addAll(binAndTaskDetails);
                        allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                        iteration++;

                    }
                }
                else if(arrivalTask.getArrivalTime() > terminationTask.getTerminationTime()){
                    List<Task> tasksToRemove = new ArrayList<>();
                    for (Task terminationTask1 : terminationQueue) {
                        if (Objects.equals(terminationTask1.getTerminationTime(), terminationTask.getTerminationTime())){
                            tasksToRemove.add(terminationTask1);
                            //System.out.println("task is "+ terminationTask1.getId());
                        }
                    }

                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), terminationTask.getTerminationTime())) {
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if(binItems[i].getTaskList().isEmpty()){
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                }
                                else{

                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails); // Save to CSV file
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                    for (Integer[] array : unionOfDataSizesList) {
//                                        System.out.println(Arrays.toString(array));
//                                    }
//
//                                    System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }
                        }
                        // System.out.println(Arrays.deepToString(binItems));
                    }
                    for (Task taskToRemove : tasksToRemove) {
                        terminationQueue.remove(taskToRemove); // Remove from the terminationQueue as well
                        //System.out.println("Task " + taskToRemove.getIndex() + " is being offloaded on Server 1 (after).");
                    }
                    continue;
                }

            }
            k_hat++;


            if (k_hat>= taskBinMatrixSize  && terminationTask != null) {
                boolean flag = false;
                int i = 0;
                while (i< binItems.length){
                    for (Task binTask : binItems[i].getTaskList()) {
                        if (terminationQueue.peek() != null && Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())) {
                            flag = true;
                            terminationQueue.poll();
                            binItems[i].removeTask(binTask);
                            binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                            if(binItems[i].getTaskList().isEmpty()){
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(null);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                binItems[i].setUsed(false);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(binAndTaskDetails);
                                allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                iteration++;
                            }
                            else{

                                // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                binItems[i].setUsed(true);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(binAndTaskDetails); // Save to CSV file
                                allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                iteration++;
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                binItems[i].setUnionDataSizes(unionOfDataSize);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                for (Integer[] array : unionOfDataSizesList) {
//                                    System.out.println(Arrays.toString(array));
//                                }
//                                System.out.println("\nPrint unionOfDataSizesList: ");
                            }
                            break;

                        }

                    }
                    if (flag==true){
                        i=0;
                    }
                    else {
                        i++;
                    }
                    flag=false;

                }

            }


            if (k_hat >= taskBinMatrixSize && terminationTask == null) {
                break;
            }
            if (k_hat >= taskBinMatrixSize) {
                break;
            }



        }



        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                int numOfActiveBins = numOfActiveBinsList.get(i);
                double sumOfData = sumOfDataList.get(i);

                AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                pairList.add(pair);
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }

//        for (AbstractMap.SimpleEntry<Integer, Double> pair : pairList) {
//            System.out.println("Active Bins: " + pair.getKey() + ", Sum: " + pair.getValue());
//        }

        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter("BF-Online-Sharing-Oblivious-equal.csv"))) {
                for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                    int numOfActiveBins = numOfActiveBinsList.get(i);
                    double sumOfData = sumOfDataList.get(i);

                    AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                    pairList.add(pair);

                    // Write the data to the CSV file
                    String[] data = {String.valueOf(numOfActiveBins), String.valueOf(sumOfData)};
                    csvWriter.writeNext(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }

        writeDetailsToCSV(allBinAndTaskDetails, "BinAndTaskDetails.csv");


    }



    /************************implementation of Online Worst Fit Sharing-Oblivious********************************/
    public void runOnline_WF() {
        List<AbstractMap.SimpleEntry<Integer, Double>> pairList = new ArrayList<>();
        PriorityQueue<Task> arrivalQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));
        PriorityQueue<Task> terminationQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));


        for (Task task : taskItems) {
            arrivalQueue.add(task);
        }

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));


        for (Task task : taskItems) {
            terminationQueue.add(task);
        }

        List<Integer> numOfActiveBinsList = new ArrayList<Integer>();
        List<Double> sumOfDataList = new ArrayList<Double>();
        int iteration = 0;


        double[] taskSizes = Helper.calculateTaskSizes(taskBinMatrix);
        double efficiency;
        double alpha = 0.5;//1.0, 0.2, 0.8, 0.0002, 0.8, 0.97
        double beta = 0.5; //1.0, 0.2, 0.8, 0.9998, 1.2, 0.03
        int k_hat = 0;
        while (k_hat < taskBinMatrixSize) {
            Task arrivalTask = arrivalQueue.peek();
            Task terminationTask = terminationQueue.peek();
            totalDataSize = taskSizes[arrivalTask.getIndex()];
            //System.out.println("total data size for task " + arrivalTask.getIndex() + " is: " + totalDataSize);

            // Initialize max efficiency and selected server index
            double minEfficiency = Double.POSITIVE_INFINITY;
            int selectedServerIndex = -1; //this is k_tilde
            double delta_jk = 0.0;
            if (arrivalTask != null && terminationTask != null) {
                if (arrivalTask.getArrivalTime() < terminationTask.getTerminationTime()) {
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount ; k++) {
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            // System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            delta_jk = 0.0;
                            //  System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }
                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            // Calculate efficiency
                            //efficiency = 1.0 / Math.sqrt((bin.getBinCPUSize() - arrivalTask.getRequest()) + (bin.getBinMemorySize() - (totalDataSize - delta_jk)));
                            efficiency = 1.0 / Math.sqrt((alpha * (bin.getBinCPUSize() - arrivalTask.getRequest())) + (beta * (bin.getBinMemorySize() - (totalDataSize - delta_jk))));
                            //System.out.println("efficiency is: " + efficiency);
                            arrivalTask.setCandid(true);
                            // Update max efficiency and selected server index if necessary
                            if (efficiency < minEfficiency) {
                                minEfficiency = efficiency;
                                selectedServerIndex = k;
                                // System.out.println("selected server index is: " + selectedServerIndex);
                            }
                        } else {
                            efficiency = 0.0;
                            arrivalTask.setCandid(false);
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }
                    //System.out.println("program inside of if statement do something more");
                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        // System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        //System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElements(taskBinMatrix, j, selectedServerIndex)));
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSize, j)));
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }

                        selectedServer.addTask(arrivalQueue.poll());
                        //System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);

//                        if (selectedServerIndex < unionOfDataSizesList.size()) {
//                            // If the index exists in the list, use set
//                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
//                        } else {
//                            // If the index is new, use add
//                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
//                        }

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, fill the list with nulls up to the index
                            while (unionOfDataSizesList.size() < selectedServerIndex) {
                                unionOfDataSizesList.add(null);
                            }

                            // Add the data at the specified index
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }


                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

                        // System.out.println("\nPrint list: ");
//                            for (Integer[] array : unionOfDataSizesList) {
//                                System.out.println(Arrays.toString(array));
//                            }
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        // System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());
                        List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                        allBinAndTaskDetails.addAll(binAndTaskDetails);
                        allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                        iteration++;

                    }
                }
                //else if is here

                else if (Objects.equals(arrivalTask.getArrivalTime(), terminationTask.getTerminationTime())) {
                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), arrivalTask.getArrivalTime())) {
                                terminationQueue.poll();
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if (binItems[i].getTaskList().isEmpty()) {
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                } else {
                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    // System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));

                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                        for (Integer[] array : unionOfDataSizesList) {
//                                            System.out.println(Arrays.toString(array));
//                                        }
                                    // System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }

                        }
                    }
                    //after removing offloaded task add new arrival task
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount ; k++) {
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            //System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            delta_jk = 0.0;
                            // System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }

                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            // Calculate efficiency
                            //efficiency = 1.0 / Math.sqrt((bin.getBinCPUSize() - arrivalTask.getRequest()) + (bin.getBinMemorySize() - (totalDataSize - delta_jk)));
                            efficiency = 1.0 / Math.sqrt((alpha * (bin.getBinCPUSize() - arrivalTask.getRequest())) + (beta * (bin.getBinMemorySize() - (totalDataSize - delta_jk))));
                            //System.out.println("efficiency is: " + efficiency);
                            arrivalTask.setCandid(true);
                            // Update max efficiency and selected server index if necessary
                            if (efficiency < minEfficiency) {
                                minEfficiency = efficiency;
                                selectedServerIndex = k;
                                //System.out.println("selected server index is: " + selectedServerIndex);
                            }
                        } else {
                            efficiency = 0.0;
                            arrivalTask.setCandid(false);
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }

                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        // System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        // System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }


                        selectedServer.addTask(arrivalQueue.poll());
                        // System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        // System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, use add
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }
                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

                        // System.out.println("\nPrint list: ");
//                            for (Integer[] array : unionOfDataSizesList) {
//                                System.out.println(Arrays.toString(array));
//                            }
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        // System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());
                        List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                        allBinAndTaskDetails.addAll(binAndTaskDetails);
                        allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                        iteration++;

                    }
                } else if (arrivalTask.getArrivalTime() > terminationTask.getTerminationTime()) {
                    List<Task> tasksToRemove = new ArrayList<>();
                    for (Task terminationTask1 : terminationQueue) {
                        if (Objects.equals(terminationTask1.getTerminationTime(), terminationTask.getTerminationTime())) {
                            tasksToRemove.add(terminationTask1);
                            //System.out.println("task is "+ terminationTask1.getId());
                        }
                    }

                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), terminationTask.getTerminationTime())) {
                                //if (terminationQueue.peek()!=null && Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())){
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if (binItems[i].getTaskList().isEmpty()) {
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));

                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                } else {

                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                        for (Integer[] array : unionOfDataSizesList) {
//                                            System.out.println(Arrays.toString(array));
//                                        }

                                    //System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }
                        }
                        // System.out.println(Arrays.deepToString(binItems));
                    }
                    for (Task taskToRemove : tasksToRemove) {
                        terminationQueue.remove(taskToRemove); // Remove from the terminationQueue as well
                        //System.out.println("Task " + taskToRemove.getIndex() + " is being offloaded on Server 1 (after).");
                    }
                    continue;
                }
            }
            //increment k_hat here
            k_hat++;


            if (k_hat >= taskBinMatrixSize && !terminationQueue.isEmpty()) {
                boolean flag = false;
                int i = 0;
                while (i < binItems.length) {
                    for (Task binTask : binItems[i].getTaskList()) {
                        if (terminationQueue.peek() != null && Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())) {
                            flag = true;
                            terminationQueue.poll();
                            binItems[i].removeTask(binTask);
                            binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                            if (binItems[i].getTaskList().isEmpty()) {
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(null);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                binItems[i].setUsed(false);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(binAndTaskDetails);
                                allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                iteration++;
                            } else {
                                // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                binItems[i].setUsed(true);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(binAndTaskDetails);
                                allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                iteration++;

                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                binItems[i].setUnionDataSizes(unionOfDataSize);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                //                                    for (Integer[] array : unionOfDataSizesList) {
                                //                                        System.out.println(Arrays.toString(array));
                                //                                    }
                                //System.out.println("\nPrint unionOfDataSizesList: ");
                            }
                            break;
                        }
                    }
                    if (flag == true) {
                        i = 0;
                    } else {
                        i++;
                    }
                    flag = false;
                }

            }
            if (k_hat >= taskBinMatrixSize && terminationTask == null) {
                break;
            }
            if (k_hat >= taskBinMatrixSize) {
                break;
            }

        }


        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                int numOfActiveBins = numOfActiveBinsList.get(i);
                double sumOfData = sumOfDataList.get(i);

                AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                pairList.add(pair);
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }


//        for (AbstractMap.SimpleEntry<Integer, Double> pair : pairList) {
//            System.out.println("Active Bins: " + pair.getKey() + ", Sum: " + pair.getValue());
//        }

        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter("WF-Online-Sharing-Oblivious-equal.csv"))) {
                for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                    int numOfActiveBins = numOfActiveBinsList.get(i);
                    double sumOfData = sumOfDataList.get(i);

                    AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                    pairList.add(pair);

                    // Write the data to the CSV file
                    String[] data = {String.valueOf(numOfActiveBins), String.valueOf(sumOfData)};
                    csvWriter.writeNext(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }

        writeDetailsToCSV(allBinAndTaskDetails, "BinAndTaskDetails.csv");


    }

    /************************implementation of Online First Fit Sharing-Oblivious ********************************/
    public void runOnline_FF() {
        List<AbstractMap.SimpleEntry<Integer, Double>> pairList = new ArrayList<>();
        PriorityQueue<Task> arrivalQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));
        PriorityQueue<Task> terminationQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));


        for (Task task : taskItems) {
            arrivalQueue.add(task);
        }

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));


        for (Task task : taskItems) {
            terminationQueue.add(task);
        }

        List<Integer> numOfActiveBinsList = new ArrayList<Integer>();
        List<Double> sumOfDataList = new ArrayList<Double>();
        int iteration = 0;


        double[] taskSizes = Helper.calculateTaskSizes(taskBinMatrix);
        //double efficiency;
        int k_hat = 0;
        while (k_hat < taskBinMatrixSize) {
            Task arrivalTask = arrivalQueue.peek();
            Task terminationTask = terminationQueue.peek();
            totalDataSize = taskSizes[arrivalTask.getIndex()];
            boolean flag = false;
            //System.out.println("total data size for task " + arrivalTask.getIndex() + " is: " + totalDataSize);

            int selectedServerIndex = -1; //this is k_tilde
            double delta_jk = 0.0;
            if (arrivalTask != null && terminationTask != null) {
                if (arrivalTask.getArrivalTime() < terminationTask.getTerminationTime()) {
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount ; k++) {
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            // System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            delta_jk = 0.0;
                            //  System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }
                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            // Calculate efficiency
                            flag = true;
                            //System.out.println("efficiency is: " + efficiency);
                            arrivalTask.setCandid(true);
                            selectedServerIndex = k;
                            break;
                            // Update max efficiency and selected server index if necessary

                        } else {
                            flag = false;
                            arrivalTask.setCandid(false);
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }

                    //System.out.println("program inside of if statement do something more");
                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        // System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        //System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElements(taskBinMatrix, j, selectedServerIndex)));
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSize, j)));
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }

                        selectedServer.addTask(arrivalQueue.poll());
                        //System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, use add
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }
                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

                        // System.out.println("\nPrint list: ");
//                            for (Integer[] array : unionOfDataSizesList) {
//                                System.out.println(Arrays.toString(array));
//                            }
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        // System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());

                        List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                        allBinAndTaskDetails.addAll(binAndTaskDetails);
                        allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                        iteration++;
                    }
                }
                //else if is here

                else if (Objects.equals(arrivalTask.getArrivalTime(), terminationTask.getTerminationTime())) {
                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), arrivalTask.getArrivalTime())) {
                                terminationQueue.poll();
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if (binItems[i].getTaskList().isEmpty()) {
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));

                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                } else {
                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    // System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));

                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                        for (Integer[] array : unionOfDataSizesList) {
//                                            System.out.println(Arrays.toString(array));
//                                        }
                                    // System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }

                        }
                    }
                    //after removing offloaded task add new arrival task
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount; k++) {
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            //System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            delta_jk = 0.0;
                            // System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }

                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            flag = true;
                            arrivalTask.setCandid(true);
                            selectedServerIndex = k;
                            break;
                            // Update max efficiency and selected server index if necessary

                        } else {
                            flag = false;
                            arrivalTask.setCandid(false);
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }

                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        // System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        // System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }


                        selectedServer.addTask(arrivalQueue.poll());
                        // System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        // System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, use add
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }
                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

                        // System.out.println("\nPrint list: ");
//                            for (Integer[] array : unionOfDataSizesList) {
//                                System.out.println(Arrays.toString(array));
//                            }
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        // System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());
                        List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                        allBinAndTaskDetails.addAll(binAndTaskDetails);
                        allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                        iteration++;

                    }
                } else if (arrivalTask.getArrivalTime() > terminationTask.getTerminationTime()) {
                    List<Task> tasksToRemove = new ArrayList<>();
                    for (Task terminationTask1 : terminationQueue) {
                        if (Objects.equals(terminationTask1.getTerminationTime(), terminationTask.getTerminationTime())) {
                            tasksToRemove.add(terminationTask1);
                            //System.out.println("task is "+ terminationTask1.getId());
                        }
                    }

                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), terminationTask.getTerminationTime())) {
                                //if (terminationQueue.peek()!=null && Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())){
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if (binItems[i].getTaskList().isEmpty()) {
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                } else {

                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));

                                    List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                    allBinAndTaskDetails.addAll(binAndTaskDetails);
                                    allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                    iteration++;
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                        for (Integer[] array : unionOfDataSizesList) {
//                                            System.out.println(Arrays.toString(array));
//                                        }

                                    //System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }
                        }
                        // System.out.println(Arrays.deepToString(binItems));
                    }
                    for (Task taskToRemove : tasksToRemove) {
                        terminationQueue.remove(taskToRemove); // Remove from the terminationQueue as well
                        //System.out.println("Task " + taskToRemove.getIndex() + " is being offloaded on Server 1 (after).");
                    }
                    continue;
                }
            }
            //increment k_hat here
            k_hat++;


            if (k_hat >= taskBinMatrixSize && !terminationQueue.isEmpty()) {
                boolean flag1 = false;
                int i = 0;
                while (i < binItems.length) {
                    for (Task binTask : binItems[i].getTaskList()) {
                        if (terminationQueue.peek() != null && Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())) {
                            flag1 = true;
                            terminationQueue.poll();
                            binItems[i].removeTask(binTask);
                            binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                            if (binItems[i].getTaskList().isEmpty()) {
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(null);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                binItems[i].setUsed(false);
                                numOfActiveBinsList.add(countActiveBins(binItems));

                                List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(binAndTaskDetails);
                                allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                iteration++;
                            } else {
                                // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                binItems[i].setUsed(true);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                List<String[]> binAndTaskDetails = collectBinAndTaskDetails(Arrays.asList(binItems));
                                allBinAndTaskDetails.addAll(binAndTaskDetails);
                                allBinAndTaskDetails.add(new String[]{""}); // Adding a blank line
                                iteration++;
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                binItems[i].setUnionDataSizes(unionOfDataSize);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                //                                    for (Integer[] array : unionOfDataSizesList) {
                                //                                        System.out.println(Arrays.toString(array));
                                //                                    }
                                //System.out.println("\nPrint unionOfDataSizesList: ");
                            }
                            break;
                        }
                    }
                    if (flag1 == true) {
                        i = 0;
                    } else {
                        i++;
                    }
                    flag1 = false;
                }

            }
            if (k_hat >= taskBinMatrixSize && terminationTask == null) {
                break;
            }
            if (k_hat >= taskBinMatrixSize) {
                break;
            }

        }


        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                int numOfActiveBins = numOfActiveBinsList.get(i);
                double sumOfData = sumOfDataList.get(i);

                AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                pairList.add(pair);
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }


//        for (AbstractMap.SimpleEntry<Integer, Double> pair : pairList) {
//            System.out.println("Active Bins: " + pair.getKey() + ", Sum: " + pair.getValue());
//        }

        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter("FF-Online-Sharing-Oblivious.csv"))) {
                for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                    int numOfActiveBins = numOfActiveBinsList.get(i);
                    double sumOfData = sumOfDataList.get(i);

                    AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                    pairList.add(pair);

                    // Write the data to the CSV file
                    String[] data = {String.valueOf(numOfActiveBins), String.valueOf(sumOfData)};
                    csvWriter.writeNext(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }

        writeDetailsToCSV(allBinAndTaskDetails, "BinAndTaskDetails.csv");


    }


    /************************implementation of Online Nexst Fit Sharing-Oblivious ********************************/
    public void runOnline_NF() {
        List<AbstractMap.SimpleEntry<Integer, Double>> pairList = new ArrayList<>();
        PriorityQueue<Task> arrivalQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));
        PriorityQueue<Task> terminationQueue = new PriorityQueue<>((tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getArrivalTime(), tB.getArrivalTime()));


        for (Task task : taskItems) {
            arrivalQueue.add(task);
        }

        Collections.sort(taskItems, (tA, tB) -> Double.compare(tA.getTerminationTime(), tB.getTerminationTime()));


        for (Task task : taskItems) {
            terminationQueue.add(task);
        }

        List<Integer> numOfActiveBinsList = new ArrayList<Integer>();
        List<Double> sumOfDataList = new ArrayList<Double>();


        double[] taskSizes = Helper.calculateTaskSizes(taskBinMatrix);
        //double efficiency;
        int k_hat = 0;
        boolean[] closedBins = new boolean[totalServerCount];

        while (k_hat < taskBinMatrixSize) {
            Task arrivalTask = arrivalQueue.peek();
            Task terminationTask = terminationQueue.peek();
            totalDataSize = taskSizes[arrivalTask.getIndex()];
            boolean flag = false;
            //System.out.println("total data size for task " + arrivalTask.getIndex() + " is: " + totalDataSize);

            int selectedServerIndex = -1; //this is k_tilde
            double delta_jk = 0.0;
            if (arrivalTask != null && terminationTask != null) {
                if (arrivalTask.getArrivalTime() < terminationTask.getTerminationTime()) {
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount ; k++) {
                        if (closedBins[k]) {
                            continue;
                        }
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            // System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            delta_jk = 0.0;
                            //  System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }
                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            // Calculate efficiency
                            flag = true;
                            //System.out.println("efficiency is: " + efficiency);
                            arrivalTask.setCandid(true);
                            selectedServerIndex = k;
                            break;
                            // Update max efficiency and selected server index if necessary

                        } else {
                            //flag = false;
                            selectedServerIndex = k;
                            arrivalTask.setCandid(false);
                            closedBins[selectedServerIndex] = true;
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }

                    //System.out.println("program inside of if statement do something more");
                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        // System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        //System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElements(taskBinMatrix, j, selectedServerIndex)));
                            //selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[j] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSize, j)));
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }

                        selectedServer.addTask(arrivalQueue.poll());
                        //System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, use add
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }
                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

                        // System.out.println("\nPrint list: ");
//                            for (Integer[] array : unionOfDataSizesList) {
//                                System.out.println(Arrays.toString(array));
//                            }
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        // System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());
                    }

                    //break;

                    //}
                }
                //else if is here

                else if (Objects.equals(arrivalTask.getArrivalTime(), terminationTask.getTerminationTime())) {
                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), arrivalTask.getArrivalTime())) {
                                terminationQueue.poll();
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if (binItems[i].getTaskList().isEmpty()) {
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                } else {
                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    // System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));

                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                        for (Integer[] array : unionOfDataSizesList) {
//                                            System.out.println(Arrays.toString(array));
//                                        }
                                    // System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }

                        }
                    }
                    //after removing offloaded task add new arrival task
                    // Loop through available servers up to k_hat
                    for (int k = 0; k < totalServerCount ; k++) {
                        if (closedBins[k]) {
                            continue;
                        }
                        Bin bin = binItems[k];
                        if (!bin.isUsed()) {
                            delta_jk = 0.0;
                        } else {
                            // System.out.println("Union of Assigned Data Sizes Before: " + Arrays.toString(unionOfDataSize));
                            delta_jk = 0.0;
                            //  System.out.println("\nprint delta_" + arrivalTask.getIndex() + k + ": " + delta_jk);
                        }
                        // Check if the server can accommodate the task
                        if (bin.getBinCPUSize() >= arrivalTask.getRequest() && bin.getBinMemorySize() >= (totalDataSize - delta_jk)) {
                            // Calculate efficiency
                            flag = true;
                            //System.out.println("efficiency is: " + efficiency);
                            arrivalTask.setCandid(true);
                            selectedServerIndex = k;
                            break;
                            // Update max efficiency and selected server index if necessary

                        } else {
                            flag = false;
                            selectedServerIndex = k;
                            arrivalTask.setCandid(false);
                            closedBins[selectedServerIndex] = true;
                        }
                    }
                    if (!arrivalTask.isCandid()) {
                        arrivalQueue.remove(arrivalTask);
                        terminationQueue.remove(arrivalTask);
                    }

                    // If a server is selected, assign the task to it and update server resources
                    if (selectedServerIndex != -1) {
                        Bin selectedServer = binItems[selectedServerIndex];
                        // System.out.println("Before memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("taskSizes[j]: " + taskSizes[arrivalTask.getIndex()]);
                        // System.out.println("taskSizes[j] after: " + taskSizes[arrivalTask.getIndex()]);
                        selectedServer.setBinCPUSize(selectedServer.getBinCPUSize() - arrivalTask.getRequest());
                        if (selectedServer.isUsed()) {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));

                        } else {
                            selectedServer.setBinMemorySize(selectedServer.getBinMemorySize() - (taskSizes[arrivalTask.getIndex()] - 0.0));
                        }


                        selectedServer.addTask(arrivalQueue.poll());
                        // System.out.println("Task " + arrivalTask.getIndex() + " is now online (after).");
                        dataItems = extractDataTypeItemsFromMatrixRow(taskBinMatrix[arrivalTask.getIndex()]);
                        arrivalTask.setDataItems(dataItems);
                        unionOfDataSize = calculateSummationOfAssignedDataSizes(selectedServer);  // this union is correct

                        // System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                        selectedServer.setUnionDataSizes(unionOfDataSize);

                        if (selectedServerIndex < unionOfDataSizesList.size()) {
                            // If the index exists in the list, use set
                            unionOfDataSizesList.set(selectedServerIndex, unionOfDataSize);
                        } else {
                            // If the index is new, use add
                            unionOfDataSizesList.add(selectedServerIndex, unionOfDataSize);
                        }
                        sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));

                        // System.out.println("\nPrint list: ");
//                            for (Integer[] array : unionOfDataSizesList) {
//                                System.out.println(Arrays.toString(array));
//                            }
                        selectedServer.setUsed(true);
                        numOfActiveBinsList.add(countActiveBins(binItems));
                        // System.out.println("\nAfter memory update - Bin Memory Size: " + selectedServer.getBinMemorySize());
                        // System.out.println("After memory update - Bin CPU Size: " + selectedServer.getBinCPUSize());

                    }
                } else if (arrivalTask.getArrivalTime() > terminationTask.getTerminationTime()) {
                    List<Task> tasksToRemove = new ArrayList<>();
                    for (Task terminationTask1 : terminationQueue) {
                        if (Objects.equals(terminationTask1.getTerminationTime(), terminationTask.getTerminationTime())) {
                            tasksToRemove.add(terminationTask1);
                            //System.out.println("task is "+ terminationTask1.getId());
                        }
                    }

                    for (int i = 0; i < binItems.length; i++) {
                        for (Task binTask : binItems[i].getTaskList()) {
                            if (Objects.equals(binTask.getTerminationTime(), terminationTask.getTerminationTime())) {
                                //if (terminationQueue.peek()!=null && Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())){
                                binItems[i].removeTask(binTask);
                                binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                                if (binItems[i].getTaskList().isEmpty()) {
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                    binItems[i].setUnionDataSizes(null);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                    binItems[i].setUsed(false);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                } else {

                                    // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                    binItems[i].setUsed(true);
                                    numOfActiveBinsList.add(countActiveBins(binItems));
                                    unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                    //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                    binItems[i].setUnionDataSizes(unionOfDataSize);
                                    if (i < unionOfDataSizesList.size()) {
                                        // If the index exists in the list, use set
                                        unionOfDataSizesList.set(i, unionOfDataSize);
                                    } else {
                                        // If the index is new, use add
                                        unionOfDataSizesList.add(i, unionOfDataSize);
                                    }
                                    binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                    sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
//                                        for (Integer[] array : unionOfDataSizesList) {
//                                            System.out.println(Arrays.toString(array));
//                                        }

                                    //System.out.println("\nPrint unionOfDataSizesList: ");
                                }
                            }
                        }
                        // System.out.println(Arrays.deepToString(binItems));
                    }
                    for (Task taskToRemove : tasksToRemove) {
                        terminationQueue.remove(taskToRemove); // Remove from the terminationQueue as well
                        //System.out.println("Task " + taskToRemove.getIndex() + " is being offloaded on Server 1 (after).");
                    }
                    continue;
                }
            }
            //increment k_hat here
            k_hat++;


            if (k_hat >= taskBinMatrixSize && !terminationQueue.isEmpty()) {
                boolean flag1 = false;
                int i = 0;
                while (i < binItems.length) {
                    for (Task binTask : binItems[i].getTaskList()) {
                        if (terminationQueue.peek() != null && Objects.equals(binTask.getTerminationTime(), terminationQueue.peek().getTerminationTime())) {
                            flag1 = true;
                            terminationQueue.poll();
                            binItems[i].removeTask(binTask);
                            binItems[i].setBinCPUSize(binItems[i].getBinCPUSize() + binTask.getRequest());

                            if (binItems[i].getTaskList().isEmpty()) {
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0));
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);
                                binItems[i].setUnionDataSizes(null);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                binItems[i].setUsed(false);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                            } else {
                                // System.out.println("print bin memory size: "+ binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - Helper.countCommonElementsUnion(taskBinMatrix, unionOfDataSizesList.get(i), binTask.getIndex()))) );
                                binItems[i].setUsed(true);
                                numOfActiveBinsList.add(countActiveBins(binItems));
                                unionOfDataSize = calculateSummationOfAssignedDataSizes(binItems[i]);  // this union is correct
                                //System.out.println("Union of Assigned Data Sizes: " + Arrays.toString(unionOfDataSize));
                                binItems[i].setUnionDataSizes(unionOfDataSize);
                                if (i < unionOfDataSizesList.size()) {
                                    // If the index exists in the list, use set
                                    unionOfDataSizesList.set(i, unionOfDataSize);
                                } else {
                                    // If the index is new, use add
                                    unionOfDataSizesList.add(i, unionOfDataSize);
                                }
                                binItems[i].setBinMemorySize(binItems[i].getBinMemorySize() + (taskSizes[binTask.getIndex()] - 0.0 ));
                                sumOfDataList.add(sumOfDataOnServer(unionOfDataSizesList));
                                //                                    for (Integer[] array : unionOfDataSizesList) {
                                //                                        System.out.println(Arrays.toString(array));
                                //                                    }
                                //System.out.println("\nPrint unionOfDataSizesList: ");
                            }
                            break;
                        }
                    }
                    if (flag1 == true) {
                        i = 0;
                    } else {
                        i++;
                    }
                    flag1 = false;
                }

            }
            if (k_hat >= taskBinMatrixSize && terminationTask == null) {
                break;
            }
            if (k_hat >= taskBinMatrixSize) {
                break;
            }

        }


        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                int numOfActiveBins = numOfActiveBinsList.get(i);
                double sumOfData = sumOfDataList.get(i);

                AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                pairList.add(pair);
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }


        for (AbstractMap.SimpleEntry<Integer, Double> pair : pairList) {
            System.out.println("Active Bins: " + pair.getKey() + ", Sum: " + pair.getValue());
        }

        if (numOfActiveBinsList.size() == sumOfDataList.size()) {
            try (CSVWriter csvWriter = new CSVWriter(new FileWriter("NF-Online-Sharing-Oblivious.csv"))) {
                for (int i = 0; i < numOfActiveBinsList.size(); i++) {
                    int numOfActiveBins = numOfActiveBinsList.get(i);
                    double sumOfData = sumOfDataList.get(i);

                    AbstractMap.SimpleEntry<Integer, Double> pair = new AbstractMap.SimpleEntry<>(numOfActiveBins, sumOfData);
                    pairList.add(pair);

                    // Write the data to the CSV file
                    String[] data = {String.valueOf(numOfActiveBins), String.valueOf(sumOfData)};
                    csvWriter.writeNext(data);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // Handle the case where the lists have different sizes
            System.err.println("Lists are of different sizes.");
        }


    }

}
