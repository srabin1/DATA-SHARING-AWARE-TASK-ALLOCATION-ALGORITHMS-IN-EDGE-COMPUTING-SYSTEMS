package com.multiKnapsackAlgorithm;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Helper {
    public static List<String> readLinesFromInputFile(String inputFilePath) throws IOException,SecurityException {
        List<String> lines= Files.readAllLines(Paths.get(inputFilePath));
        return lines;
    }
    public static Double[] readDoubleArrayFromString(String line,int size,String splitter){
        ArrayList<Double> rowData=new ArrayList<>();
        List<String> rowList= Arrays.asList( line.trim().split(splitter));
        rowList.forEach(col->{
            rowData.add(Double.valueOf(col));
        });
        if(size==0)
            size=rowList.size();
        Double[] doubleArray =rowData.toArray(new Double[size]);
        return doubleArray;
    }
    public static Integer[][] read2DIntegerArrayFromStringList(List<String> lines,int size,String splitter){
        Integer[][] taskDataTypeMatrix=new Integer[size][size];

        for ( int row=0,currentLine = 1; currentLine <= size; currentLine++,row++) {
            ArrayList<Integer> rowData=new ArrayList<>();
            List<String> rowList= Arrays.asList( lines.get(currentLine).trim().split(splitter));
            rowList.forEach(col->{
                rowData.add(Integer.valueOf(col));
            });
            taskDataTypeMatrix[row]=rowData.toArray(new Integer[size]);
        }
        return taskDataTypeMatrix;
    }

}
