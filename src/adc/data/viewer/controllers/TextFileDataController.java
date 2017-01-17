package adc.data.viewer.controllers;

import adc.data.viewer.ADCreader.DataParams;
import adc.data.viewer.ADCreader.DataParser;
import adc.data.viewer.MainApp;
import adc.data.viewer.dataProcessing.TestDataType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class TextFileDataController {

    private Alert alertInvalidParam;
    private Stage textFileParamsStage;
    private DataParser dataParser;
    private static String creationDate;
    private static String deviceName;
    private static int channelNum;
    private static double channelRate;
    private int fnum;

    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    public void setDataParser(DataParser dataParser) { this.dataParser = dataParser; }

    public void setTextFileParamsStage(Stage textFileParamsStage) {
        this.textFileParamsStage = textFileParamsStage;
    }

    public void setFileNumber(int fnum) {
        this.fnum = fnum;
    }


    @FXML
    private TextField txtCreationDate;
    @FXML
    private TextField txtDeviceName;
    @FXML
    private TextField txtChannelRate;
    @FXML
    private TextField txtChannelNum;

    @FXML
    public void initialize() {

        alertInvalidParam = new Alert(WARNING);
        DialogPane dialogPane = alertInvalidParam.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
//        alertInvalidParam.initStyle(StageStyle.UNDECORATED);
        alertInvalidParam.setTitle("Warning");
        alertInvalidParam.setHeaderText("Invalid data format!");
        alertInvalidParam.setContentText("channel number: integer;\nchannel rate: float ");

        txtCreationDate.setText(creationDate);
        txtDeviceName.setText(deviceName);
        txtChannelNum.setText(String.valueOf(channelNum));
        txtChannelRate.setText(String.valueOf(channelRate));
    }

    @FXML
    private void handleOk(ActionEvent actionEvent)  {
        if(TestDataType.isInteger(txtChannelNum.getText(),10)&&TestDataType.isDouble(txtChannelRate.getText())){

            creationDate = txtCreationDate.getText();
            deviceName = txtDeviceName.getText();
            channelNum = Integer.parseInt(txtChannelNum.getText());
            channelRate = Double.parseDouble(txtChannelRate.getText());

            setParamInteractive(txtCreationDate.getText(),txtDeviceName.getText(),
                               Double.parseDouble(txtChannelRate.getText()), fnum);


            dataParser.getDataParams().setDataParamsValid(true);

            textFileParamsStage.close();
        }
        else{

            alertInvalidParam.showAndWait();
        }
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        dataParser.getDataParams().setDataParamsValid(false);
        textFileParamsStage.close();
    }
    



     private void  setParamInteractive(String recordCreationDate, String deviceName, double channelRate,int fnum) {
        
        byte[] arrayByte = {(byte) 1};
        dataParser.getDataParams().setChannelsMax(1, fnum) ; //int
        dataParser.getDataParams().setRealChannelsQuantity(1, fnum); //int
        dataParser.getDataParams().setInterCadreDelay(0.0,fnum) ; //Double
        dataParser.getDataParams().setActiveAdcChannelArray(arrayByte,fnum) ; //Byte
        dataParser.getDataParams().setAdcChannelArray(arrayByte,fnum) ; //Byte
        dataParser.getDataParams().setAdcGainArray(arrayByte,fnum) ; //Byte
        dataParser.getDataParams().setIsSignalArray(arrayByte,fnum) ; //Byte
        dataParser.getDataParams().setAdcRate(0.0,fnum) ; //Double

        //get this from user:
        dataParser.getDataParams().setDeviceName(deviceName,fnum); //string
        dataParser.getDataParams().setCreateDateTime(recordCreationDate,fnum) ; //string
        dataParser.getDataParams().setChannelRate(channelRate,fnum) ; //Double
        dataParser.getDataParams().setRealCadresQuantity(0, fnum) ; //long
        dataParser.getDataParams().setRealSamplesQuantity(dataParser.getDataParams().getRealCadresQuantity()[fnum], fnum) ; //long
        dataParser.getDataParams().setTotalTime(0.0,fnum) ; //Double

    }

    public void setData( int fnum, int sigCount) {
        String line;
        List<Double> allLines = new ArrayList<>();
        DataParams dataParams = dataParser.getDataParams();

        try (BufferedReader signalDataFromText = Files.newBufferedReader(dataParser.getDataPaths().getDataFilePath()[fnum], Charset.forName("US-ASCII"))) {
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
                dataParser.setSignals(oneSignal, sigCount, fnum, channelNum);
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
