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

import adc.data.viewer.parser.DataParams;
import adc.data.viewer.parser.DataParser;
import adc.data.viewer.processing.TestDataType;
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
        alertInvalidParam.setTitle("Warning");
        alertInvalidParam.setHeaderText("Invalid data format!");
        alertInvalidParam.setContentText("channel number: integer;\nchannel rate: float\n ");

        txtCreationDate.setText(creationDate);
        txtDeviceName.setText(deviceName);
        txtChannelNum.setText(String.valueOf(channelNum));
        txtChannelRate.setText(String.valueOf(channelRate));
        dialogPane.toFront();
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
