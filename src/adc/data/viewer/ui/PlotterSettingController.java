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

import adc.data.viewer.processing.TestDataType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

import static javafx.scene.control.Alert.AlertType.WARNING;


public class PlotterSettingController {


    private MainApp mainApp;
    private Stage plotterSettingsStage;
    private Alert alertInvalidParam;
    private PlotterController plotterController;
    private Preferences appPreferencesRootNode = MainApp.appPreferencesRootNode;


    public static Double ymin;
    public static Double xmin;
    public static Double ymax;
    public static Double xmax;
    public static int sgleft;
    public static int sgright;
    public static int sgorder;
    public static int fftsize;
    public static String fftwindow;
    public static int fftoverlap;
    public static double startZero;
    public static double endZero;
    public static boolean fixZero;
    public static boolean isUseSavedAxesRange;
    public static double widthOfLine;
    static String chosenLineOrScatter;

    private ObservableList<String> lineOrScatter = FXCollections.observableArrayList();

    @FXML
    private TextField lineWidth;
    @FXML
    private TextField zeroShiftStart;
    @FXML
    private TextField zeroShiftEnd;

//
//    public void setIsFixZeroShift(boolean fixZeroShift) {
//        this.fixZeroShift.setSelected(fixZeroShift);
//    }
    @FXML
    private CheckBox useSavedAxesRange;
    @FXML
    private CheckBox fixZeroShift;
    @FXML
    private ChoiceBox<String> chooseLineOrScatter;
    @FXML
    private TextField manualYmax;
    @FXML
    private TextField manualYmin;
    @FXML
    private TextField manualXmax;
    @FXML
    private TextField manualXmin;
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




    public void setPlotterController(PlotterController plotterController) {
        this.plotterController = plotterController;
    }
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }


    public static void setChosenLineOrScatter(String chosenLineOrScatter) {
        PlotterSettingController.chosenLineOrScatter = chosenLineOrScatter;
    }
    public static void setWidthOfLineDefault(double widthOfLine) {
        PlotterSettingController.widthOfLine = widthOfLine;
    }


    public static void setCurrentAxesSettings(Double xmn, Double xmx,  Double ymn, Double ymx,boolean useSavedAxesRange){
        xmin=xmn;
        xmax=xmx;
        ymin=ymn;
        ymax=ymx;
        isUseSavedAxesRange = useSavedAxesRange;
    }


    public static void setSGFilterSettingsDefault(int sglft,int sgrt,int ord){
        sgleft=sglft;
        sgright=sgrt;
        sgorder=ord;
    }

    public static void setSpectrogramSettingsDefault(int ftsize,String ftwindow,int ftoverlap){
        fftsize=ftsize;
        fftwindow=ftwindow;
        fftoverlap=ftoverlap;
    }

    public  static void setFixZeroShiftDefault(double startZ, double endZ, boolean fixZ){
        startZero =startZ;
        endZero =endZ;
        fixZero = fixZ;
    }


    public void setPlotterSettingStage(Stage plotterSettingsStage) {
        this.plotterSettingsStage = plotterSettingsStage;
    }

    @FXML
    public void initialize() {
        System.out.println("next");
        System.out.println(MainApp.appPreferencesRootNode);

        alertInvalidParam = new Alert(WARNING);
        DialogPane dialogPane = alertInvalidParam.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);
        dialogPane.toFront();
        alertInvalidParam.setTitle("Warning");
        alertInvalidParam.setHeaderText("Invalid data format!");
        alertInvalidParam.setContentText("all axes parameters should be of type float,\nother pframeters should be Integer\nand \"FFTWindowType\" of type String\n\n");
        manualYmax.setText(String.valueOf(ymax));
        manualYmin.setText(String.valueOf(ymin));
        manualXmax.setText(String.valueOf(xmax));
        manualXmin.setText(String.valueOf(xmin));
        manualSGFilterLeft.setText(String.valueOf(sgleft));
        manualSGFilterRight.setText(String.valueOf(sgright));
        manualSGFilterOrder.setText(String.valueOf(sgorder));
        FFTSize.setText(String.valueOf(fftsize));
        FFTWindowType.setText(fftwindow);
        FFTWindowOverlap.setText(String.valueOf(fftoverlap));
        lineWidth.setText(String.valueOf(widthOfLine));
        zeroShiftStart.setText(String.valueOf(startZero));
        zeroShiftEnd.setText(String.valueOf(endZero));
        fixZeroShift.setSelected(fixZero);
        lineOrScatter.addAll("line+scatter","line","scatter");
        chooseLineOrScatter.itemsProperty().setValue(lineOrScatter);
        chooseLineOrScatter.setValue(chosenLineOrScatter);
        useSavedAxesRange.setSelected(isUseSavedAxesRange);
        chooseLineOrScatter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            plotterController.getPlotter().getCanvasData().setPlotStyle(newValue);
            chosenLineOrScatter=newValue;
        });

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
                        TestDataType.isDouble(manualYmin.getText())&&
                        TestDataType.isDouble(manualYmax.getText())&&
                        TestDataType.isInteger(manualSGFilterLeft.getText(),10)&&
                        TestDataType.isInteger(manualSGFilterRight.getText(),10)&&
                        TestDataType.isInteger(manualSGFilterOrder.getText(),10)&&
                        TestDataType.isInteger(FFTSize.getText(),10)&&
                        TestDataType.isInteger(FFTWindowOverlap.getText(),10)&&
                        TestDataType.isDouble(lineWidth.getText())&&
                        TestDataType.isDouble(zeroShiftStart.getText())&&
                        TestDataType.isDouble(zeroShiftEnd.getText())
                )
        {
            xmin=Double.parseDouble(manualXmin.getText());
            xmax=Double.parseDouble(manualXmax.getText());
            ymin=Double.parseDouble(manualYmin.getText());
            ymax=Double.parseDouble(manualYmax.getText());
            sgleft=Integer.parseInt(manualSGFilterLeft.getText());
            sgright=Integer.parseInt(manualSGFilterRight.getText());
            sgorder=Integer.parseInt(manualSGFilterOrder.getText());
            fftsize=Integer.parseInt(FFTSize.getText());
            fftoverlap=Integer.parseInt(FFTWindowOverlap.getText());
            fftwindow=FFTWindowType.getText();
            widthOfLine=Double.parseDouble(lineWidth.getText());
            startZero=Double.parseDouble(zeroShiftStart.getText());
            endZero=Double.parseDouble(zeroShiftEnd.getText());
            fixZero=fixZeroShift.isSelected();
            isUseSavedAxesRange = useSavedAxesRange.isSelected();

            if ((xmin>xmax)|(ymin>ymax)) plotterController.getPlotter().getAxes().setAxesBasicSetup();
            else plotterController.getPlotter().getAxes().setAxesBounds(xmin,xmax,ymin,ymax);

            plotterController.getPlotter().getCanvasData().setWidthOfLine(widthOfLine);
            plotterController.getPlotter().getCanvasData().setPlotStyle(chosenLineOrScatter);
            plotterController.getPlotter().getCanvasData().setSgFilterSettings(new int[]{sgleft,sgright,sgorder});
            plotterController.getPlotter().getCanvasData().setFixZeroShiftRange(new double[]{startZero,endZero});
            plotterController.getPlotter().getCanvasData().setIsFixZeroShift(fixZero);

            mainApp.setDefaultPlotStyle(chosenLineOrScatter);
            mainApp.setDefaultWidthOfLine(widthOfLine);
            mainApp.setDefaultSGFilterSettings(new int[]{sgleft,sgright,sgorder});
            mainApp.setDefaultFixZeroShift(fixZero);
            mainApp.setDefaultFixZeroShiftRange(new double [] {startZero,endZero});
            mainApp.setSavedAxesRange(new double [] {xmin,xmax,ymin,ymax});
            mainApp.setUseSavedAxesRange(isUseSavedAxesRange);
            plotterSettingsStage.close();

            plotterController.getPlotter().getCanvasData().drawData();

        }

        else{

            alertInvalidParam.showAndWait();
        }

    }

    public void setDefaults() {
    }
}




