/*******************************************************************************
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
 ******************************************************************************/

package adc.data.viewer.controllers;

import adc.data.viewer.MainApp;
import adc.data.viewer.dataProcessing.TestDataType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static javafx.scene.control.Alert.AlertType.WARNING;

/**
 * Created by a on 1/15/17.
 */
public class PlotterSettingController {


    private MainApp mainApp;
    private Stage plotterSettingsStage;
    private Alert alertInvalidParam;

    static Double ymin;
    static Double xmin;
    static Double ymax;
    static Double xmax;
    static Double xstep;
    static Double ystep;
    static Double sgleft;
    static Double sgright;
    static Double sgorder;
    static Double fftsize;
    static String fftwindow;
    static Double fftoverlap;

    @FXML
    private TextField manualYmax;
    @FXML
    private TextField manualYmin;
    @FXML
    private TextField manualXmax;
    @FXML
    private TextField manualXmin;
    @FXML
    private TextField manualXstep;
    @FXML
    private TextField manualYstep;
    @FXML
    private TextField manualSGFilterLeft;
    @FXML
    private TextField manualSGFilterRight;
    @FXML
    private TextField manualSGFilterOrder;
    @FXML
    private TextField FFTSize;
    @FXML
    private TextField FFTWindowType;
    @FXML
    private TextField FFTWindowOverlap;


    public static void setAxesSettingsDefault(
            Double xminn,
            Double xmax,
            Double xstep,
            Double ymin,
            Double ymax,
            Double ystep)
    {
        PlotterSettingController.xmin=xminn;
        PlotterSettingController.xmax=xmax;
        PlotterSettingController.xstep=xstep;
        PlotterSettingController.ymin=ymin;
        PlotterSettingController.ymax=ymax;
        PlotterSettingController.ystep=ystep;

    };
    public static void setSGFilterSettingsDefault(
            Double sgleft,
            Double sgright,
            Double order)
    {
        PlotterSettingController.sgleft=sgleft;
        PlotterSettingController.sgright=sgright;
        PlotterSettingController.sgorder=order;
    };
    public static void setSpectrogramSettingsDefault(
            Double fftsize,
            String fftwindow,
            Double fftoverlap)
    {
        PlotterSettingController.fftsize=fftsize;
        PlotterSettingController.fftwindow=fftwindow;
        PlotterSettingController.fftoverlap=fftoverlap;
    };



    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setPlotterSettingStage(Stage plotterSettingsStage) {
        this.plotterSettingsStage = plotterSettingsStage;
    }

    @FXML
    public void initialize() {

        alertInvalidParam = new Alert(WARNING);
        DialogPane dialogPane = alertInvalidParam.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
//        alertInvalidParam.initStyle(StageStyle.UNDECORATED);
        alertInvalidParam.setTitle("Warning");
        alertInvalidParam.setHeaderText("Invalid data format!");
        alertInvalidParam.setContentText("all fields should be numeric of type float,\n except \"FFTWindowType\" of type String");
        manualYmax.setText(String.valueOf(ymax));
        manualYmin.setText(String.valueOf(ymin));
        manualXmax.setText(String.valueOf(xmax));
        manualXmin.setText(String.valueOf(xmin));
        manualXstep.setText(String.valueOf(xstep));
        manualYstep.setText(String.valueOf(ystep));
        manualSGFilterLeft.setText(String.valueOf(sgleft));
        manualSGFilterRight.setText(String.valueOf(sgright));
        manualSGFilterOrder.setText(String.valueOf(sgorder));
        FFTSize.setText(String.valueOf(fftsize));
        FFTWindowType.setText(fftwindow);
        FFTWindowOverlap.setText(String.valueOf(fftoverlap));

    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        plotterSettingsStage.close();
    }

    @FXML
    private void handleOk(ActionEvent actionEvent)  {

        if(
                TestDataType.isDouble(manualXmin.getText())&&
                        TestDataType.isDouble(manualXmax.getText())&&
                        TestDataType.isDouble(manualXstep.getText())&&
                        TestDataType.isDouble(manualYmin.getText())&&
                        TestDataType.isDouble(manualYmax.getText())&&
                        TestDataType.isDouble(manualYstep.getText())&&
                        TestDataType.isDouble(manualSGFilterLeft.getText())&&
                        TestDataType.isDouble(manualSGFilterRight.getText())&&
                        TestDataType.isDouble(manualSGFilterOrder.getText())&&
                        TestDataType.isDouble(FFTSize.getText())&&
                        TestDataType.isDouble(FFTWindowOverlap.getText())
                )
        {
            xmin=Double.parseDouble(manualXmin.getText());
            xmax=Double.parseDouble(manualXmax.getText());
            xstep=Double.parseDouble(manualXstep.getText());
            ymin=Double.parseDouble(manualYmin.getText());
            ymax=Double.parseDouble(manualYmax.getText());
            ystep=Double.parseDouble(manualYstep.getText());
            sgleft=Double.parseDouble(manualSGFilterLeft.getText());
            sgright=Double.parseDouble(manualSGFilterRight.getText());
            sgorder=Double.parseDouble(manualSGFilterOrder.getText());
            fftsize=Double.parseDouble(FFTSize.getText());
            fftoverlap=Double.parseDouble(FFTWindowOverlap.getText());
            fftwindow=FFTWindowType.getText();
            PlotterController.getPlots().getAxes().setAxesBounds(xmin, xmax,xstep,ymin,ymax,ystep );
            PlotterController.getPlots().getCanvas().draw();
            plotterSettingsStage.close();
        }

        else{

            alertInvalidParam.showAndWait();
        }

        plotterSettingsStage.close();
    }
}




