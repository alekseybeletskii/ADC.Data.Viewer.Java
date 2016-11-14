package adc.data.viewer.controllers;

import adc.data.viewer.ADCreader.DataData;
import adc.data.viewer.ADCreader.TextFile;
import adc.data.viewer.ADCreader.DataParams;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.regex.Pattern;
import static javafx.scene.control.Alert.AlertType.WARNING;

public class TextFileParamsController {

    private Alert alertInvalidParam;
    private Stage textFileParamsStage;
    private DataData dataData;
    private static String creationDate;
    private static String deviceName;
    private static int channelNum;
    private static double channelRate;



    private int fnum;

    public void setTextFileParamsStage(Stage textFileParamsStage) {
        this.textFileParamsStage = textFileParamsStage;
    }

    public void setFileNumber(int fnum) {
        this.fnum = fnum;
    }

    public void setAllData(DataData dataData) {
        this.dataData = dataData;
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

            TextFile.setParam(txtCreationDate.getText(),txtDeviceName.getText(),
                    Integer.parseInt(txtChannelNum.getText()),
                    Double.parseDouble(txtChannelRate.getText()), fnum, dataData.getDataParams());
            dataData.getDataParams().setDataParamsValid(true);
            textFileParamsStage.close();
        }
        else{
            alertInvalidParam.showAndWait();
        }

    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        dataData.getDataParams().setDataParamsValid(false);
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
}
