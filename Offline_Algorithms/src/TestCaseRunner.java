package com.multiKnapsackAlgorithm;

import com.multiKnapsackAlgorithm.PGreedy.PGreedyAlgorithm;
import com.multiKnapsackAlgorithm.hm.ResultWriter;

import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

public class TestCaseRunner {
    private String instanceDirectoryName;
    private int numberOfInstances;
    private String[] inputFilePaths;
    private String currentInputFilePath;
    private GreedyAlgorithmBase[] greedyAlgorithmInstances;
    private ResultWriter resultWriter;
    {
        instanceDirectoryName="instance";
        numberOfInstances=10;
        inputFilePaths=new String[]{
                "1.LowDemand-LowSharing.txt","2.LowDemand-AverageSharing.txt","3.LowDemand-HighSharing.txt",
                "4.AverageDemand-LowSharing.txt","5.AverageDemand-AverageSharing.txt","6.AverageDemand-HighSharing.txt",
                "7.HighDemand-LowSharing.txt", "8.HighDemand-AverageSharing.txt","9.HighDemand-HighSharing.txt"
        };
        currentInputFilePath=inputFilePaths[0];
        resultWriter=ResultWriter.getInstance();
    }

    public TestCaseRunner(String instanceDirectoryName, int numberOfInstances, String[] inputFilePaths) {
        this.instanceDirectoryName = instanceDirectoryName;
        this.numberOfInstances = numberOfInstances;
        this.inputFilePaths = inputFilePaths;
    }

    public ArrayList<AlgorithmRunResult> runAlgorithms(String testCaseFilePath){
        ArrayList<AlgorithmRunResult> algorithmsRunResults=new ArrayList<>();
        System.out.println("----------------------------------------------------------");
        System.out.println("TestCaseFilePath: "+testCaseFilePath +"\t-> Begin");
        System.out.println("----------------------------------------------------------");
        greedyAlgorithmInstances= new GreedyAlgorithmBase[]{
                new GreedyAlgorithmPhase1(testCaseFilePath),
                new DGreedyAlgorithm(testCaseFilePath),
                new PGreedyAlgorithm(testCaseFilePath),
                //new NaivePGreedyAlgorithm(testCaseFilePath),
                //new GreedyAlgorithmPhase2(testCaseFilePath),
                //new ExhaustiveSearch(testCaseFilePath);
        };
        for (GreedyAlgorithmBase oGreedyAlgorithmItem:greedyAlgorithmInstances) {
            AlgorithmRunResult algorithmRunResult=new AlgorithmRunResult();
            System.out.println("----------------------------------------------------------");
            String algorithmName=oGreedyAlgorithmItem.getClass().getSimpleName();
            System.out.println("Algorithm: "+algorithmName);
            algorithmRunResult.setAlgorithmName(algorithmName);
            Instant runStart=Instant.now();
            oGreedyAlgorithmItem.run();
            Instant runEnd=Instant.now();
            long runTimeElapsedMillis=Duration.between(runStart,runEnd).toMillis() ;
            System.out.println("Run: TimeElapsed: "+runTimeElapsedMillis);
            algorithmRunResult.setElapsedTime(runTimeElapsedMillis);

            System.out.println("TotalProfit: "+oGreedyAlgorithmItem.getTotalProfit());
            algorithmRunResult.setTotalProfit(oGreedyAlgorithmItem.getTotalProfit());

            System.out.println("TotalDataSize: "+oGreedyAlgorithmItem.getTotalDataSize());
            algorithmRunResult.setTotalDataSize(oGreedyAlgorithmItem.getTotalDataSize());
            //System.out.println(oGreedyAlgorithmItem.toString());
            System.out.println("----------------------------------------------------------");
            System.out.println();
            algorithmsRunResults.add(algorithmRunResult);
        }
        System.out.println("----------------------------------------------------------");
        System.out.println("TestCaseFilePath: "+testCaseFilePath+"\t-> End");
        System.out.println("----------------------------------------------------------");
        return algorithmsRunResults;
    }
    public ArrayList<AlgorithmRunResult> runTestCases(String  instanceDirectoryName,int currentInstanceNumber,String[] inputFilePaths){
        ArrayList<AlgorithmRunResult> algorithmsRunResults=new ArrayList<>();
        instanceDirectoryName="data\\"+instanceDirectoryName+currentInstanceNumber+"\\";
        //String[] temp_inputFilePaths=modifyInputFilePathWithCurrentDirectoryName(instanceDirectoryName,inputFilePaths);
        for (String currentTestCaseInputFileName: inputFilePaths){
            String currentTestCaseInputFilePath=inputFilePathBuilder( instanceDirectoryName, currentTestCaseInputFileName) ;
            ArrayList<AlgorithmRunResult>  currentInputFileAlgorithmsRunResult= runAlgorithms(currentTestCaseInputFilePath);
            currentInputFileAlgorithmsRunResult.forEach(current->{
                current.setTestCaseName(currentTestCaseInputFileName);
            });
            algorithmsRunResults.addAll(currentInputFileAlgorithmsRunResult);
        }
        algorithmsRunResults.forEach(current->{
            current.setInstanceNo(currentInstanceNumber);
        });
        return algorithmsRunResults;
    }
    public ArrayList<AlgorithmRunResult> executeForAllInstances(String instanceDirectoryName,int numberOfInstances,String[] inputFilePaths){
        ArrayList<AlgorithmRunResult> algorithmRunResults=new ArrayList<>();
        for (int currentInstanceNumber = 0; currentInstanceNumber < numberOfInstances; currentInstanceNumber++) {
            ArrayList<AlgorithmRunResult> testCasesRunResults=new ArrayList<>();
            System.out.println("**********************************************************");
            System.out.println("Instance: #"+currentInstanceNumber+"\t-> End");
            System.out.println("**********************************************************");

            testCasesRunResults= runTestCases(instanceDirectoryName,currentInstanceNumber,inputFilePaths);
            algorithmRunResults.addAll(testCasesRunResults);

            System.out.println("**********************************************************");
            System.out.println("Instance: #"+currentInstanceNumber+"\t-> End");
            System.out.println("**********************************************************");
        }
        return algorithmRunResults;
    }

    private String inputFilePathBuilder(String instanceDirectoryName,String inputFileName) {

            String inputFilePath=instanceDirectoryName+inputFileName;
            return inputFilePath;
    }

    public void executeAutomation(){

        resultWriter.data=new ArrayList<>(){{
            add("#Instance");
            add("TestCaseName");
            add("Algorithm");
            add("ExecutionTime");
            add("TotalProfit");
            add("TotalDataSize");
        }};
        resultWriter.write();
        ArrayList<AlgorithmRunResult> runResults= executeForAllInstances(instanceDirectoryName,numberOfInstances, inputFilePaths);
        ArrayList<String> result=null;
        for (AlgorithmRunResult runResult:runResults) {
            result=new ArrayList<>(){{
                add(String.valueOf(runResult.getInstanceNo()));
                add(runResult.getTestCaseName());
                add(runResult.getAlgorithmName());
                add(String.valueOf( runResult.getElapsedTime()));
                add(String.valueOf( runResult.getTotalProfit()));
                add(String.valueOf(runResult.getTotalDataSize()));
            }};
            resultWriter.data=result;
            resultWriter.write();
        }

        resultWriter.close();
    }

}
