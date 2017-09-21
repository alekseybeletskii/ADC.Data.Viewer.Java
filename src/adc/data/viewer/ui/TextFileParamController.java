/*
 * 	********************* BEGIN LICENSE BLOCK *********************************
 * 	ADCDataViewer
 * 	Copyright (c) 2016 onward, Aleksey Beletskii  <beletskiial@gmail.com>
 * 	All rights reserved
 *
 * 	github: https://github.com/alekseybeletskii
 *
 * 	The ADCDataViewer software serves for visualization and simple processing
 * 	of any data recorded with Analog Digital Converters in binary or text form.
 *
 * 	Commercial support is available. To find out more contact the author directly.
 *
 * 	Redistribution and use in source and binary forms, with or without
 * 	modification, are permitted provided that the following conditions are met:
 *
 * 	  1. Redistributions of source code must retain the above copyright notice, this
 * 	     list of conditions and the following disclaimer.
 * 	  2. Redistributions in binary form must reproduce the above copyright notice,
 * 	     this list of conditions and the following disclaimer in the documentation
 * 	     and/or other materials provided with the distribution.
 *
 * 	The software is distributed to You under terms of the GNU General Public
 * 	License. This means it is "free software". However, any program, using
 * 	ADCDataViewer _MUST_ be the "free software" as well.
 * 	See the GNU General Public License for more details
 * 	(file ./COPYING in the root of the distribution
 * 	or website <http://www.gnu.org/licenses/>)
 *
 * 	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * 	ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * 	WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * 	DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * 	ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * 	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * 	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * 	ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * 	(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * 	SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * 	********************* END LICENSE BLOCK ***********************************
 */

package adc.data.viewer.ui;

import adc.data.viewer.parser.DataParser;
import adc.data.viewer.processing.TestDataType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class TextFileParamController {
    static{
        creationDate = "01.01.1111";
        deviceName = "my ADC";
        channelNum = 0;
        channelRate =1;
<<<<<<< HEAD
        yColumnNum = 1;
        xColumnNum = 0;
        amountOfHeaderLines = 0;
        intercadrDelay =0;
        columnSeparator = ",";
=======
        yColumnNum = 0;
        xColumnNum = 0;
        amountOfHeaderLines = 0;
        intercadrDelay =0;
        columnSeparator = "\\t";
>>>>>>> 3e3ce4108dae88a1743a6c1039cc20ef21edb4d8
        isRememberTxtFileSettings = false;
    }



    private Alert alertInvalidParam;
    private Stage textFileParamsStage;
    private DataParser dataParser;
    private int fileIndex;
    public static double channelRate;
    public static double intercadrDelay;
    public static String creationDate;
    public static String deviceName;
    public static int channelNum;
    public static int yColumnNum;
    public static int xColumnNum;
    public static int amountOfHeaderLines;
    public static String columnSeparator;

    public static boolean isRememberTxtFileSettings;

    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    public void setDataParser(DataParser dataParser) { this.dataParser = dataParser; }

    public void setTextFileParamsStage(Stage textFileParamsStage) {
        this.textFileParamsStage = textFileParamsStage;
    }

    public void setFileNumber(int fileIndex) {
        this.fileIndex = fileIndex;
    }


    public Label getTxtNextFileName() {
        return txtNextFileName;
    }

    @FXML
    private Label txtNextFileName;

    @FXML
    private TextField txtCreationDate;

    @FXML
    private TextField txtDeviceName;

    @FXML
    private TextField txtChannelRate;
    @FXML
    private TextField txtIntercadrDelay;

    @FXML
    private TextField txtChannelNum;

    @FXML
    private TextField txtYColumnNum;
    @FXML
    private TextField txtXColumnNum;
    @FXML
    private TextField txtAmountOfHeaderLines;
    @FXML
    private TextField txtColumnSeparator;
    @FXML
    CheckBox rememberCurrentFileSettings;

    @FXML
    Button OkButton;

    @FXML
    public void initialize() {



        txtCreationDate.setText(creationDate);
        txtDeviceName.setText(deviceName);
        txtChannelNum.setText(String.valueOf(channelNum));
        txtChannelRate.setText(String.valueOf(channelRate));
        txtIntercadrDelay.setText(String.valueOf(intercadrDelay));
        txtYColumnNum.setText(String.valueOf(yColumnNum));
<<<<<<< HEAD
        txtXColumnNum.setText(String.valueOf(xColumnNum));
=======
        txtXColumnNum.setText(String.valueOf(yColumnNum));
>>>>>>> 3e3ce4108dae88a1743a6c1039cc20ef21edb4d8
        txtAmountOfHeaderLines.setText(String.valueOf(amountOfHeaderLines));
        txtColumnSeparator.setText(String.valueOf(columnSeparator));
        rememberCurrentFileSettings.setSelected(isRememberTxtFileSettings);


    }

    public void makeAlert() {
        alertInvalidParam = new Alert(WARNING);
        alertInvalidParam.initOwner(textFileParamsStage);
        DialogPane dialogPane = alertInvalidParam.getDialogPane();
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);
        dialogPane.getStyleClass().add("myDialog");
        dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
        dialogPane.toFront();
        alertInvalidParam.setTitle("Warning");
        alertInvalidParam.setHeaderText("Invalid data format!");
        alertInvalidParam.setContentText("channel number: integer, >=0 \nchannel rate: float, >0 " +
                "\nX(Y) column number: int, >=0 \namount of header lines: integer, >=0");
        alertInvalidParam.showAndWait();
    }

    public void pushOk(){
        OkButton.fire();
    }

    @FXML
    private void handleOk(ActionEvent actionEvent)  {
        if(TestDataType.isInteger(txtChannelNum.getText(),10)&&
           TestDataType.isInteger(txtYColumnNum.getText(),10)&&
           TestDataType.isInteger(txtXColumnNum.getText(),10)&&
           TestDataType.isInteger(txtAmountOfHeaderLines.getText(),10)&&
           TestDataType.isDouble(txtChannelRate.getText())&&
           TestDataType.isDouble(txtIntercadrDelay.getText())&&
           Integer.parseInt(txtChannelNum.getText())>=0&&
           Integer.parseInt(txtYColumnNum.getText())>=0&&
<<<<<<< HEAD
=======
           Integer.parseInt(txtXColumnNum.getText())>=0&&
>>>>>>> 3e3ce4108dae88a1743a6c1039cc20ef21edb4d8
           Integer.parseInt(txtAmountOfHeaderLines.getText())>=0&&
           Double.parseDouble(txtChannelRate.getText())>0&&
           Double.parseDouble(txtIntercadrDelay.getText())>=0)
        {
            creationDate = txtCreationDate.getText();
            deviceName = txtDeviceName.getText();
            channelNum = Integer.parseInt(txtChannelNum.getText());
            channelRate = Double.parseDouble(txtChannelRate.getText());
            intercadrDelay = Double.parseDouble(txtIntercadrDelay.getText());
            yColumnNum = Integer.parseInt(txtYColumnNum.getText());
            xColumnNum = Integer.parseInt(txtXColumnNum.getText());
            amountOfHeaderLines = Integer.parseInt(txtAmountOfHeaderLines.getText());
            columnSeparator = txtColumnSeparator.getText();
            isRememberTxtFileSettings = rememberCurrentFileSettings.isSelected();
            dataParser.getDataParams().setDataParamsValid(true);
            textFileParamsStage.close();
        }
        else{
            makeAlert();
        }
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        dataParser.getDataParams().setDataParamsValid(false);
        textFileParamsStage.close();
    }

}
