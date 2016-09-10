package adc.data.viewer.ADCreader;


import javafx.scene.control.Alert;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.WARNING;

/**
 * Text files with only one column of data from only one ADC channel are supported now.
 * User will be asked to provide ADC rate peer channel
 */
public class TextFile {

    private static int channelNumber;

    public static void setParam(String recordCreationDate, String deviceName, int channelNumber, double channelRate, int fnum, DataParams dataParams ){
        TextFile.channelNumber=channelNumber;
        byte[] arrayByte = {(byte) 1};

        dataParams.setChannelsMax(1, fnum) ; //int
        dataParams.setRealChannelsQuantity(1, fnum); //int
        dataParams.setInterCadreDelay(0.0,fnum) ; //Double
        dataParams.setActiveAdcChannelArray(arrayByte,fnum) ; //Byte
        dataParams.setAdcChannelArray(arrayByte,fnum) ; //Byte
        dataParams.setAdcGainArray(arrayByte,fnum) ; //Byte
        dataParams.setIsSignalArray(arrayByte,fnum) ; //Byte
        dataParams.setAdcRate(0.0,fnum) ; //Double

        //get this from user:
        dataParams.setDeviceName(deviceName,fnum); //string
        dataParams.setCreateDateTime(recordCreationDate,fnum) ; //string
        dataParams.setChannelRate(channelRate,fnum) ; //Double
        dataParams.setRealCadresQuantity(0, fnum) ; //long
        dataParams.setRealSamplesQuantity(dataParams.getRealCadresQuantity()[fnum], fnum) ; //long
        dataParams.setTotalTime(0.0,fnum) ; //Double

    }

    static void setData( int fnum, int sigCount, DataData allData, DataParams dataParams) {
        String line;
        List<Double> allLines = new ArrayList<>();

        try (BufferedReader signalDataFromText = Files.newBufferedReader(DataPaths.getDataFilePath()[fnum], Charset.forName("US-ASCII"))) {
            try {
                while ((line = signalDataFromText.readLine()) != null) {
                    allLines.add(Double.parseDouble(line));
                }

                dataParams.setRealCadresQuantity(allLines.size(), fnum);
                dataParams.setTotalTime(allLines.size() / dataParams.getChannelRate()[fnum], fnum);

                double[] oneSignal = new double[allLines.size()];
                int i = 0;
                for (Double d : allLines) {
                    oneSignal[i] = d;
                    i++;
                }

                sigCount++;
                allData.setSignals(oneSignal, sigCount, fnum, channelNumber);
            }
            catch (NumberFormatException e){
                Alert alert = new Alert(WARNING);
                alert.setTitle("Warning");
                alert.setHeaderText("Invalid data format!");
                alert.setContentText("*.txt file should contain only one column of float");
                alert.showAndWait();
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }
}
