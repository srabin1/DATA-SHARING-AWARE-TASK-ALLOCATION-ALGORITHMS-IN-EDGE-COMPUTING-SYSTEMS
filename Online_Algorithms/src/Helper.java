import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;



public class Helper {
    //This function reads input files (instances) line by line
    public static List<String> readInputFileLineByLine(String inputFilePath) throws Exception {
        List<String> listOfStrings = new ArrayList<String>();
        BufferedReader bf = new BufferedReader(new FileReader(inputFilePath));
        String line = bf.readLine();
        while (line != null) {
            listOfStrings.add(line);
            line = bf.readLine();
        }
        bf.close();
       // System.out.println("print the list of string: " + listOfStrings);
        return listOfStrings;
    }
    // This function takes a string line containing numerical values separated by a specified splitter,
    // and it converts these values into an array of Double objects
    public static Double[] readDoubleTypeArrayFromString(String line, int size, String splitter) {
        String[] elements = line.trim().split(splitter);
        if (size == 0) {
            size = elements.length;
        }
        Double[] doubleArray = new Double[size];
        for (int i = 0; i < size; i++) {
            doubleArray[i] = Double.valueOf(elements[i]);
        }
        return doubleArray;
    }

    // This function reads two lines of input strings, each containing numerical values separated by a specified splitter,
    // and converts these values into a two-dimensional array of Double objects
    public static Double[][] readTwoDimensionalDoubleArrayFromString(String line1, String line2, int size, String splitter) {
        String[] elements1 = line1.trim().split(splitter);
        String[] elements2 = line2.trim().split(splitter);

        if (size == 0) {
            size = Math.min(elements1.length, elements2.length);
        }

        Double[][] doubleArray = new Double[2][size];

        for (int i = 0; i < size; i++) {
            doubleArray[0][i] = Double.valueOf(elements1[i]);
            doubleArray[1][i] = Double.valueOf(elements2[i]);
        }

        return doubleArray;
    }

    // This function creates a two-dimensional array of Double objects (taskBinMatrix) based on the provided row and column sizes
    public static Double[][] createTaskBinMatrixFromStringList(List<String> lines, int sizeRow, int sizeCol, String splitter) throws NumberFormatException, IndexOutOfBoundsException {
        Double[][] taskBinMatrix = new Double[sizeRow][sizeCol];

        for (int row = 0; row < sizeRow; row++) {
            String[] elements = lines.get(row + 2).trim().split(splitter);
            if (elements.length != sizeCol) {
                throw new IndexOutOfBoundsException("Invalid number of elements in line: " + (row + 1));
            }

            for (int col = 0; col < sizeCol; col++) {
                taskBinMatrix[row][col] = Double.valueOf(elements[col]);
            }
        }

        // Uncomment the following code if you want to print the matrix

//    System.out.println("print bin-item matrix: ");
//    for (int i = 0; i < sizeRow; i++) {
//        for (int j = 0; j < sizeCol; j++) {
//            System.out.print(" " + taskBinMatrix[i][j]);
//        }
//        System.out.println();
//    }


        return taskBinMatrix;
    }

    // This function calculates the sizes of each task by summing up the non-zero values in each row of the matrix
    // where each row represents a task, and each column represents a data.
    public static double[] calculateTaskSizes(Double[][] taskBinMatrix) {
        int numRows = taskBinMatrix.length;
        int numCols = taskBinMatrix[0].length;
        double[] taskSizes = new double[numRows];

        for (int row = 0; row < numRows; row++) {
            double size = 0.0;
            for (int col = 0; col < numCols; col++) {
                if (taskBinMatrix[row][col] != 0.0) {
                    size= size + taskBinMatrix[row][col];
                }
            }
            taskSizes[row] = size;
        }

        return taskSizes;
    }


    // This function counts the common elements between the unionOfData array and the specified row (task j) of the taskBinMatrix
    public static double countCommonElementsUnion(Double[][] taskBinMatrix, Double[] unionOfData, int j) {
        double commonCount = 0.0;

        // Assuming both rows have the same number of columns
        int numColumns = taskBinMatrix[0].length;

        for (int col = 0; col < numColumns; col++) {
            if (unionOfData[col].equals(taskBinMatrix[j][col]) ) {
                commonCount = commonCount + unionOfData[col];
            }
        }

        return commonCount;
    }





}

