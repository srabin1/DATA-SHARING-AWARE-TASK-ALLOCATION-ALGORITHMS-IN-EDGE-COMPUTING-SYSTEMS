package com.multiKnapsackAlgorithm.hm;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ResultWriter {
    private static String filePath;
    public static ArrayList<String> data;
    private static CSVWriter writer;
    {
        filePath="result.csv";
        data=new ArrayList<>();
        // first create file object for file placed at location
        // specified by filepath
        File file = new File(filePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputfile = new FileWriter(file);

            // create CSVWriter object filewriter object as parameter
            writer = new CSVWriter(outputfile);
        }catch (IOException ioException){
            // TODO Auto-generated catch block
            ioException.printStackTrace();
        }
    }


    // private instance, so that it can be
    // accessed by only by getInstance() method
    private static  ResultWriter instance ;
    private ResultWriter() {
        // private constructor
    }
    //method to return instance of class
    public static ResultWriter getInstance()
    {
        if (instance == null)
        {
            //synchronized block to remove overhead
            synchronized (ResultWriter.class)
            {
                if(instance==null)
                {
                    // if instance is null, initialize
                    instance = new ResultWriter();
                }
            }
        }
        return instance;
    }
    public static void close(){
        try {
            writer.close();
        }catch (IOException exception){
            // TODO Auto-generated catch block
            exception.printStackTrace();
        }
    }
    public static void write(){
         String[] dataArray = new String[data.size()];
         dataArray = data.toArray(dataArray);
         writer.writeNext(dataArray);

    }


}
