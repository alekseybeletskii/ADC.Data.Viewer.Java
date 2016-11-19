package adc.data.viewer.ADCreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to process file paths
 */
public class DataPaths {

    DataPaths (File[] filesListToProcess){
        makePaths(filesListToProcess);
    }
    DataPaths (String[] filesListToProcess){
        makePaths(filesListToProcess);
    }
    DataPaths (String filesListToProcess){
        makePaths(filesListToProcess);
    }

    private  Path[] dataFilePath;
    private  Path[] parFilePath;
    private  String[] fileName;


     Path[] getParFilePath() {
        return parFilePath;
    }
      public Path[] getDataFilePath() {
        return dataFilePath;
    }
      String[] getFileName() {
        return fileName;
    }

    /**
     *
     * @param dataFileStr
     * load the list of files from String[] array
     */
     void makePaths(String[] dataFileStr) {
        int count = 0;
        dataFilePath = new Path[dataFileStr.length];
        parFilePath = new Path[dataFileStr.length];
        fileName = new String[dataFileStr.length];

        for (String fileStr : dataFileStr) {
            try {
                dataFilePath[count] = Paths.get(fileStr);
                fileName[count] = dataFilePath[count].getFileName().toString();
                fileName[count] = fileName[count].substring(0, fileName[count].lastIndexOf('.'));
                parFilePath[count] = dataFilePath[count].getParent().resolve(fileName[count] + ".par");
            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                return;
            }
            count++;
        }
    }

    /**
     *
     * @param filesListToProcess
     * load the list from external text file
     */
     void makePaths(String filesListToProcess) {

        List<String> stringList = new ArrayList<>();

        String line;

        try (BufferedReader readFilesToProcess = Files.newBufferedReader(Paths.get(filesListToProcess), Charset.forName("US-ASCII"))) {
            int i=0;
            while ((line = readFilesToProcess.readLine()) != null) {
                stringList.add(line.trim());
                i++;
            }
            String[] dataFileString = stringList.toArray(new String[stringList.size()]);


        dataFilePath = new Path[dataFileString.length];
        parFilePath = new Path[dataFileString.length];
        fileName = new String[dataFileString.length];
        int count = 0;
        for (String fileStr : dataFileString) {

            try {
                dataFilePath[count] = Paths.get(fileStr);
                fileName[count] = dataFilePath[count].getFileName().toString();
                fileName[count] = fileName[count].substring(0, fileName[count].lastIndexOf('.'));
                parFilePath[count] = dataFilePath[count].getParent().resolve(fileName[count] + ".par");
            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                return;
            }
            count++;
        }

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }

    /**
     *
     * @param dataFileStr
     * load the list from File[] array
     */

     void makePaths(File[] dataFileStr) {
        int count = 0;
        dataFilePath = new Path[dataFileStr.length];
        parFilePath = new Path[dataFileStr.length];
        fileName = new String[dataFileStr.length];

        for (File fileStr : dataFileStr) {
            try {
                dataFilePath[count] = Paths.get(fileStr.getPath());
                fileName[count] = dataFilePath[count].getFileName().toString();
                fileName[count] = fileName[count].substring(0, fileName[count].lastIndexOf('.'));
                parFilePath[count] = dataFilePath[count].getParent().resolve(fileName[count] + ".par");
            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                return;
            }
            count++;
        }

    }

}
