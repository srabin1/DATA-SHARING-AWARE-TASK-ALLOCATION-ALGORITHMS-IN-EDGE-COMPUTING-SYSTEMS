//import java.util.Arrays;
//import java.util.List;
//
//public class Main {
//
//    public static void main(String[] args) throws Exception {
//        //String inputFilepath= "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\old\\instance05.txt";
//        //String inputFilepath= "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\old\\instance-500.txt";
//        //String inputFilepath= "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\old\\instance-200.txt";
//        //String inputFilepath= "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\old\\instance-100-avg-dominant.txt";
//
//
//        String[] inputFilepaths = {
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\1.low-sharing-low-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\2.low-sharing-medium-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\3.low-sharing-high-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\4.medium-sharing-low-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\5.medium-sharing-medium-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\6.medium-sharing-high-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\7.high-sharing-low-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\8.high-sharing-medium-demand-500.txt",
////                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\9.high-sharing-high-demand-500.txt",
//                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\old\\instance_tabu01.txt",
//        };
//
//
//        for (String inputFilepath : inputFilepaths) {
//            OnlineSharingAlgorithms OnlineSA_BFS = new OnlineSharingAlgorithms(inputFilepath);
//            OnlineSA_BFS.runOnline_BFS();
//            OnlineSA_BFS.prepareItems();
//            Bin[] binItems = OnlineSA_BFS.getBinItems();
//
////            OnlineSharingAlgorithms OnlineSA_WFS = new OnlineSharingAlgorithms(inputFilepath);
////            OnlineSA_WFS.runOnline_WFS();
//
////            OnlineSharingAlgorithms OnlineSA_FFS = new OnlineSharingAlgorithms(inputFilepath);
////            OnlineSA_FFS.runOnline_FFS();
//
////            OnlineSharingAlgorithms OnlineSA_NFS = new OnlineSharingAlgorithms(inputFilepath);
////            OnlineSA_NFS.runOnline_NFS();
//
//
//            //   for (String inputFilepath : inputFilepaths) {
//            // OnlineSharingObliviousAlgorithms OnlineSOA_BF = new OnlineSharingObliviousAlgorithms(inputFilepath);
//            // OnlineSOA_BF.runOnline_BF();
//
//            //  OnlineSharingObliviousAlgorithms OnlineSOA_WF = new OnlineSharingObliviousAlgorithms(inputFilepath);
//            //  OnlineSOA_WF.runOnline_WF();
//
//            //  OnlineSharingObliviousAlgorithms OnlineSOA_FF = new OnlineSharingObliviousAlgorithms(inputFilepath);
//            //  OnlineSOA_FF.runOnline_FF();
//
////            OnlineSharingObliviousAlgorithms OnlineSOA_NF = new OnlineSharingObliviousAlgorithms(inputFilepath);
////            OnlineSOA_NF.runOnline_NF();
//            // }
//
//
////            List<List<Double>> inputItems = Arrays.asList(
////                    Arrays.asList(0.0, 2.0, 0.0, 0.0),
////                    Arrays.asList(0.0, 2.0, 3.0, 0.0),
////                    Arrays.asList(0.0, 0.0, 0.0, 4.0),
////                    Arrays.asList(1.0, 2.0, 0.0, 4.0),
////                    Arrays.asList(1.0, 0.0, 0.0, 0.0),
////                    Arrays.asList(1.0, 2.0, 0.0, 0.0),
////                    Arrays.asList(1.0, 0.0, 3.0, 4.0)
////            );
//
//            // Corresponding CPU sizes for the items
//            // List<Double> cpuItems = Arrays.asList(1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);
//            // List<Double> cpuItems = Arrays.asList(1.0, 2.0, 4.0, 2.0, 4.0, 5.0, 2.0);
//           // List<Double> cpuItems = Arrays.asList(11.0, 12.0, 4.0, 2.0, 1.0, 10.0, 2.0);
//
//            // Parameters for the tabu search
//            int maxIterations = 100; // Maximum iterations
//            int stableThreshold = 10; // Threshold for stable iterations before stopping
//
//
////        System.out.println("Tabu Search with modified sharing: ");
////        TabuSearch_Sharing_Modified tabuSearchModifiedSharing = new TabuSearch_Sharing_Modified(inputItems, cpuItems, maxIterations, stableThreshold);
////        tabuSearchModifiedSharing.execute();
//
//            List<List<Double>> inputItems = OnlineSA_BFS.getInputItems();
//            List<Double> cpuItems = OnlineSA_BFS.getCpuItems();
//
//
//
//
//            System.out.println("Tabu Search with sharing final version: ");
//            TabuSearch_Sharing_final tabuSearchSharingFinal = new TabuSearch_Sharing_final(inputItems, cpuItems, binItems, maxIterations, stableThreshold);
//            tabuSearchSharingFinal.execute();
//            tabuSearchSharingFinal.calculateAndAppendMinimumsToCsv();
//
//
//        }
//
//    }
//}
//
//
//
//
//
//
//
//
//

import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Main {

    public static void main(String[] args) throws Exception {
        // Define the file paths for data processing
        String[] inputFilepaths = {
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\1.low-sharing-low-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\2.low-sharing-medium-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\3.low-sharing-high-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\4.medium-sharing-low-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\5.medium-sharing-medium-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\6.medium-sharing-high-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\7.high-sharing-low-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\8.high-sharing-medium-demand-500.txt",
                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-3\\9.high-sharing-high-demand-500.txt",

        };
        //                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\old\\instance_tabu01.txt",


        // To run the inputs on Ubuntu
//        String[] inputFilepaths = {
//                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/1.low-sharing-low-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/2.low-sharing-medium-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/3.low-sharing-high-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/4.medium-sharing-low-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/5.medium-sharing-medium-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/6.medium-sharing-high-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/7.high-sharing-low-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/8.high-sharing-medium-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/500-1/9.high-sharing-high-demand-500.txt",
////                "/mnt/c/Users/sanaz/DataSharing_BinPacking/Data/old/instance_tabu01.txt",
//        };


        PrintWriter csvWriter = new PrintWriter(new FileWriter("BFS_Execution_time_sharing_equal_all.csv"));
        csvWriter.println("Iteration,Execution Time (ms)"); // Header for CSV file


        for (String inputFilepath : inputFilepaths) {




            // execution time for online sharing oblivious algorithms

            OnlineSharingObliviousAlgorithms OnlineSOA_BF = new OnlineSharingObliviousAlgorithms(inputFilepath);
            long startTime_BF = System.nanoTime();
            OnlineSOA_BF.runOnline_RandomSO();  // Assume this prepares the data and stores it internally
            long endTime_BF = System.nanoTime();
            System.out.println("Execution time for BF in main : " + (endTime_BF - startTime_BF) / 1_000_000.0 + " ms");




//            // execution time for online sharing algorithms
            OnlineSharingAlgorithms onlineSA = new OnlineSharingAlgorithms(inputFilepath);
            long startTime_BFS = System.nanoTime();
            //onlineSA.runOnline_BFS();  // Assume this prepares the data and stores it internally
            onlineSA.runOnline_RandomS();
            long endTime_BFS = System.nanoTime();
           // System.out.println("Execution time for DSA_BF in main : " + (endTime_BFS - startTime_BFS) / 1_000_000.0 + " ms");



            // Prepare the item lists for tabu search
//            onlineSA.prepareItems();
//            // Retrieve prepared data for all iterations
//            List<List<Double>> cpuItemsList = onlineSA.getCpuItemsList();
//            List<List<List<Double>>> inputItemsList = onlineSA.getInputItemsList();
//            Bin[] binItems = onlineSA.getBinItems();  // Assuming getBinItems() returns all required bin configurations
//
//            int maxIterations = 100;  // Maximum iterations
//            int stableThreshold = 10;  // Threshold for stable iterations before stopping

            // Execute tabu search for each iteration
//            for (int i = 0; i < inputItemsList.size(); i++) {
//
//                // Suggest to JVM to perform garbage collection
//                System.gc();
//                List<List<Double>> inputItems = inputItemsList.get(i);
//                List<Double> cpuItems = cpuItemsList.get(i);
//
//              //  System.out.println("Local Search with sharing final version for Iteration: " + (i + 1));
//                LocalSearch localSearch = new LocalSearch(inputItems, cpuItems, binItems, maxIterations, stableThreshold);
//                long startTime_localSearch = System.nanoTime();
//                localSearch.execute();  // Execute the search
//                long endTime_localSearch = System.nanoTime();
//                double executionTimeMs = (endTime_localSearch - startTime_localSearch) / 1_000_000.0;
//               // System.out.println("Local search in main : " + (endTime_localSearch - startTime_localSearch) / 1_000_000.0 + " ms");
//                localSearch.calculateAndAppendMinimumsToCsv();  // Calculate and append results to CSV
//
//               // localSearch.copyCsvFile();  // Calculate and append results to CSV
//
//                System.out.println();  // Print a blank line to separate outputs of different iterations
//
//                csvWriter.println((i + 1) + "," + executionTimeMs);
//            }
            csvWriter.flush();
        }
        csvWriter.close();
    }
}



//import java.util.List;
//
//public class Main {
//
//    public static void main(String[] args) throws Exception {
//        String[] inputFilepaths = {
//                "C:\\Users\\sanaz\\DataSharing_BinPacking\\Data\\500-1\\1.low-sharing-low-demand-500.txt",
//                // Additional file paths...
//        };
//
//        for (String inputFilepath : inputFilepaths) {
//            OnlineSharingAlgorithms onlineSA = new OnlineSharingAlgorithms(inputFilepath);
//            onlineSA.runOnline_BFS(); // This should prepare and directly return the required data for LocalSearch
//
//            // Assuming onlineSA.prepareItems() now returns a structure containing all needed data
//            List<DataBundle> dataBundles = onlineSA.prepareItems_new(); // DataBundle is a hypothetical class containing all necessary data
//
//            int maxIterations = 100;
//            int stableThreshold = 10;
//
//            for (DataBundle bundle : dataBundles) {
//                LocalSearch localSearch = new LocalSearch(bundle.getInputItems(), bundle.getCpuItems(), bundle.getBinItems(), maxIterations, stableThreshold);
//                localSearch.execute();
//                localSearch.calculateAndAppendMinimumsToCsv();
//                // Output results, handle logging or any other required tasks
//            }
//        }
//    }
//}



