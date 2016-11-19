package adc.data.viewer.controllers;

import adc.data.viewer.ADCreader.DataParams;
import adc.data.viewer.ADCreader.DataParser;
import adc.data.viewer.ADCreader.DataTypesList;
import adc.data.viewer.MainApp;
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
import java.util.regex.Pattern;
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


        if(isInteger(txtChannelNum.getText(),10)&&isDouble(txtChannelRate.getText())){

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
    
    private static boolean isInteger(String s, int radix) {
        if(s.isEmpty()) return false;
        for(int i = 0; i < s.length(); i++) {
            if(i == 0 && s.charAt(i) == '-') {
                if(s.length() == 1) return false;
                else continue;
            }
            if(Character.digit(s.charAt(i),radix) < 0) return false;
        }
        return true;
    }
    private  static boolean isDouble(String myString){
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
                ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
                        "[+-]?(" + // Optional sign character
                        "NaN|" +           // "NaN" string
                        "Infinity|" +      // "Infinity" string

                        // A decimal floating-point string representing a finite positive
                        // number without a leading sign has at most five basic pieces:
                        // Digits . Digits ExponentPart FloatTypeSuffix
                        //
                        // Since this method allows integer-only strings as input
                        // in addition to strings of floating-point literals, the
                        // two sub-patterns below are simplifications of the grammar
                        // productions from section 3.10.2 of
                        // The Java Language Specification.

                        // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
                        "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

                        // . Digits ExponentPart_opt FloatTypeSuffix_opt
                        "(\\.("+Digits+")("+Exp+")?)|"+

                        // Hexadecimal strings
                        "((" +
                        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "(\\.)?)|" +

                        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
                        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

                        ")[pP][+-]?" + Digits + "))" +
                        "[fFdD]?))" +
                        "[\\x00-\\x20]*");// Optional trailing "whitespace"

        return Pattern.matches(fpRegex, myString);
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
