package adc.data.viewer.ADCreader;


import adc.data.viewer.MainApp;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.List;
import java.util.stream.IntStream;



/**
 * This class produces an object that will incapsulate three output arrays:
 * -"signals" with all signals
 * -"signalLabels" with labels to be shown in table view for convenient signals selection
 * -"signalFullPath" full paths to every signal used when saving as separate text files
 * "saveToFile" method to convert data from the binary to the text format
 */


public class DataData {
    private DataParams dataParams;
    private double[] [] signals;
    private int sigCount=-1;
    private String [] signalLabels;
    private Path [] signalFullPath;
    private int [] fileNumbers;


    /**
     * The constructor takes a list of files in the "List<File>" format after the button "Open" is pressed
     * @param filesList
     * a list of files to be processed
     */

    public DataData(List<File> filesList, MainApp mainApp) {

        File[] filesListToProcess = filesList.toArray(new File[filesList.size()]);
        DataPaths.makePaths(filesListToProcess); //produce Paths from an input list of files
        dataParams =new DataParams(filesListToProcess.length); //This object will contain parameters from all files that have been opened
        DataFormats.dataFormats(DataPaths.getDataFilePath(), DataPaths.getParFilePath(), dataParams, mainApp); //infers data format of a current file
        signals = new double[IntStream.of(dataParams.getRealChannelsQuantity()).sum()] [];
        signalLabels = new String[signals.length];
        signalFullPath = new Path[signals.length];
        fileNumbers = new int[signals.length];
        int i=0;
        String formatName;

        if(dataParams.isDataParamsValid())
        {
            while (i < DataPaths.getFileName().length)

            {
                formatName = dataParams.getDataFormatStr()[i];

                 // call the "setData" method from corresponding class using data format identifier
                try {
                    Class[] methArg = new Class[]{int.class, int.class, DataData.class, DataParams.class};
                    Object[] methodArg = new Object[]{i, sigCount, this, dataParams};
                    Class<?> classAdcType = Class.forName(this.getClass().getPackage().getName() + '.' + formatName);
                    classAdcType.getDeclaredMethod("setData", methArg).invoke(classAdcType, methodArg);
                } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }

                i++;

            }
        }

    }

    public DataParams getDataParams() {
        return dataParams;
    }

    /**
     *
     * @return
     * return array of file numbers
     */

    public int[] getFileNumbers() {
        return fileNumbers;
    }

    /**
     * @return
     * return array of signals
     */
    public double[][] getSignals() {
        return signals;
    }

    /**
     * @return
     *  return an array of labels as strings to be shown in table view
     */
    public String[] getSignalLabels() {
        return signalLabels;
    }

    /**
     * @return
     * return an array of signals' full paths as strings to be used when saving as text files
     */

    private Path[] getSignalFullPath() {
        return signalFullPath;
    }

    /**
     * This method fills all the output arrays and will be invoked by a corresponding ADC-type class
     *
     * @param signal
     * a signal extracted from binary file by corresponding ADC-type-class
     * @param sigCount
     * an extracted signal's sequence number in the "signals" array
     * @param fNum
     * a number of a processed source file
     * @param sigAdcNum
     * an ADC channel number of extracted signal
     */

    void setSignals(double [] signal, int sigCount, int fNum, int sigAdcNum) {
        this.signals[sigCount] = signal.clone();
        this.signalLabels[sigCount] = DataPaths.getFileName()[fNum]+ "_#"+sigAdcNum;
        this.signalFullPath[sigCount] = Paths.get(DataPaths.getDataFilePath()[fNum].getParent() +System.getProperty("file.separator")+ DataPaths.getFileName()[fNum] + "_#" + sigAdcNum);
        this.sigCount = sigCount;
        this.fileNumbers[sigCount] = fNum;
    }

    /**
     * The method saves avery signal as a separate text file to the "txt" subdirectory
     */
    public void saveToFile (){
        int i=0;
        byte [] stringBytes;
        while (i< signalLabels.length){

            Path   outTxtPath =    getSignalFullPath()[i].getParent().resolve(Paths.get("txt"));
            try {
                Files.createDirectories(outTxtPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try ( FileChannel fWrite = (FileChannel)Files.newByteChannel(outTxtPath.resolve(signalLabels[i] +".txt"),StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE))

            {
                MappedByteBuffer wrBuf = fWrite.map(FileChannel.MapMode.READ_WRITE, 0, signals[i].length*16);
                int j=0;
                while(j<signals[i].length) {
                    stringBytes = String.format("%15.7f", signals[i][j]).getBytes("UTF-8");
                    wrBuf.put(stringBytes);
                    wrBuf.put((byte)System.getProperty("line.separator").charAt(0));

                    j++;
                }


            } catch(InvalidPathException e) {
                System.out.println("Path Error " + e);
            } catch (IOException e) {
                System.out.println("I/O Error " + e);
            }

            i++;
        }

    }
}


