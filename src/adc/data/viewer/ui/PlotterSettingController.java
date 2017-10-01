/*
 * ******************** BEGIN LICENSE BLOCK *********************************
 *
 * ADCDataViewer
 * Copyright (c) 2016 onward, Aleksey Beletskii  <beletskiial@gmail.com>
 * All rights reserved
 *
 * github: https://github.com/alekseybeletskii
 *
 * The ADCDataViewer software serves for visualization and simple processing
 * of any data recorded with Analog Digital Converters in binary or text form.
 *
 * Commercial support is available. To find out more contact the author directly.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright notice, this
 *          list of conditions and the following disclaimer.
 *     2. Redistributions in binary form must reproduce the above copyright notice,
 *         this list of conditions and the following disclaimer in the documentation
 *         and/or other materials provided with the distribution.
 *
 * The software is distributed to You under terms of the GNU General Public
 * License. This means it is "free software". However, any program, using
 * ADCDataViewer _MUST_ be the "free software" as well.
 * See the GNU General Public License for more details
 * (file ./COPYING in the root of the distribution
 * or website <http://www.gnu.org/licenses/>)
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ******************** END LICENSE BLOCK ***********************************
 */

package adc.data.viewer.ui;

import adc.data.viewer.model.ADCDataRecords;
import adc.data.viewer.processing.TestDataType;
import adc.data.viewer.util.ApplicationPreferences;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.prefs.Preferences;



public class PlotterSettingController extends BaseController{



    private Stage plotterSettingsStage;
    private PlotterController plotterController;
    private Preferences appPreferencesRootNode = MainApp.appPreferencesRootNode;


     private Double ymin;
     private Double xmin;
     private Double ymax;
     private Double xmax;
     private int sgleft;
     private int sgright;
     private int sgorder;
     private int fftsize;
     private String fftwindow;
     private int fftoverlap;
     private double startZero;
     private double endZero;
     private boolean fixZero;
     private boolean isUseNewDefaults;
     private double widthOfLine;
     private String plotStyle;

    private ObservableList<String> lineOrScatter = FXCollections.observableArrayList();
    private ObservableList<String> allDataLables = FXCollections.observableArrayList();


    @FXML
    private TextField dataStartTime;
    @FXML
    ComboBox<String> setDataStartTime;
    @FXML
    private TextField lineWidth;
    @FXML
    private TextField zeroShiftStart;
    @FXML
    private TextField zeroShiftEnd;

    @FXML
    private CheckBox UseNewDefaults;
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


    public void setPlotterSettingStage(Stage plotterSettingsStage) {
        this.plotterSettingsStage = plotterSettingsStage;
    }

    @FXML
    private void initialize() {



    }

    @FXML
    private void handleCancel(ActionEvent actionEvent) {
        plotterSettingsStage.close();
    }

    @FXML
    private void handleApply(ActionEvent actionEvent){  ApplyNewSettings() ;}

    @FXML
    private void handleOk(ActionEvent actionEvent) {

        if (ApplyNewSettings()) plotterSettingsStage.close();
        setDataStartTime.getSelectionModel().clearSelection();

    }

    @FXML
    private void handleReset(ActionEvent actionEvent){
        ApplicationPreferences.setAllPreferencesToBasicDefaults();
        plotterController.toggleButton("Raw");
        plotterController.getPlotter().getCanvasData().drawData();
        setDataStartTime.getSelectionModel().clearSelection();
        dataStartTime.setText("0.0");
        initializeSettings();

//        plotterSettingsStage.close();
    }

    private boolean ApplyNewSettings() {
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
                        TestDataType.isDouble(zeroShiftEnd.getText())&&
                        TestDataType.isDouble(dataStartTime.getText())&&
                        Double.parseDouble(manualXmin.getText())<Double.parseDouble(manualXmax.getText())&&
                        Double.parseDouble(manualYmin.getText())<Double.parseDouble(manualYmax.getText())
                )
        {
            xmin=Double.parseDouble(manualXmin.getText());
            xmax=Double.parseDouble(manualXmax.getText());
            ymin=Double.parseDouble(manualYmin.getText());
            ymax=Double.parseDouble(manualYmax.getText());

            if ((xmin>xmax)|(ymin>ymax)) {mainApp.getBaseController().alertInvalidSettingsParam();}

            sgleft=Integer.parseInt(manualSGFilterLeft.getText());
            sgright=Integer.parseInt(manualSGFilterRight.getText());
            sgorder=Integer.parseInt(manualSGFilterOrder.getText());

            fftsize=Integer.parseInt(FFTSize.getText());
            fftwindow=FFTWindowType.getText();
            fftoverlap=Integer.parseInt(FFTWindowOverlap.getText());
            widthOfLine=Double.parseDouble(lineWidth.getText());
            startZero=Double.parseDouble(zeroShiftStart.getText());
            endZero=Double.parseDouble(zeroShiftEnd.getText());
            fixZero=fixZeroShift.isSelected();
            isUseNewDefaults = UseNewDefaults.isSelected();

            plotterController.getPlotter().getAxes().setAxesBounds(xmin,xmax,ymin,ymax);

            if(!setDataStartTime.getSelectionModel().isEmpty()&&
               !setDataStartTime.getSelectionModel().isSelected(0)){
                ADCDataRecords adcr = mainApp.getAdcDataRecords().get(setDataStartTime.getSelectionModel().getSelectedIndex()-1);
                updateSignalXData(adcr);
            }else if(setDataStartTime.getSelectionModel().isSelected(0)){
                for(ADCDataRecords adcr : mainApp.getAdcDataRecords()) {
                updateSignalXData(adcr);
                }
            }

            setAllPreferencesToNewDefaults();
            plotterController.getPlotter().getCanvasData().drawData();

            return true;
        }

        else{
            mainApp.getBaseController().alertInvalidSettingsParam();;
            return false;
        }

    }

    private void updateSignalXData(ADCDataRecords adcr) {
        adcr.setSignalTimeShift(Double.parseDouble(dataStartTime.getText()));
        if (adcr.getSignalXData().length > 0) {
            double[] xtmp = new double[adcr.getSignalXData().length];
            double tStart = adcr.getSignalXData()[0];
            int i = 0;
            for (double x : adcr.getSignalXData()) {
                xtmp[i] = adcr.getSignalXData()[i] - tStart + Double.parseDouble(dataStartTime.getText());
                i++;
            }

            adcr.setSignalXData(xtmp);
        }
    }

    private void setAllPreferencesToNewDefaults() {
        appPreferencesRootNode.put("defaultPlotStyle",plotStyle);
        appPreferencesRootNode.putDouble("defaultWidthOfLine",widthOfLine);
        appPreferencesRootNode.putInt("defaultSGFilterLeft",sgleft); //points
        appPreferencesRootNode.putInt("defaultSGFilterRight",sgright); //points
        appPreferencesRootNode.putInt("defaultSGFilterLeftOrder",sgorder);
        appPreferencesRootNode.putDouble("defaultFixZeroShiftStart",startZero); //ms
        appPreferencesRootNode.putDouble("defaultFixZeroShiftEnd",endZero);
        appPreferencesRootNode.putBoolean("defaultFixZeroShift",fixZero);
        appPreferencesRootNode.putDouble("defaultXAxisMin",xmin);
        appPreferencesRootNode.putDouble("defaultXAxisMax",xmax);
        appPreferencesRootNode.putDouble("defaultYAxisMin",ymin);
        appPreferencesRootNode.putDouble("defaultYAxisMax",ymax);
        appPreferencesRootNode.putInt("defaultSpectrogramFFTSize",fftsize);
        appPreferencesRootNode.put("defaultSpectrogramFFTWindow",fftwindow);
        appPreferencesRootNode.putInt("defaultSpectrogramFFTOverlap",fftoverlap);
        appPreferencesRootNode.putBoolean("defaultUseNewDefaults",isUseNewDefaults);
    }

    public void initializeSettings (){
        manualYmax.setText(String.valueOf(plotterController.getPlotter().getAxes().getYAxis().getUpperBound()));
        manualYmin.setText(String.valueOf(plotterController.getPlotter().getAxes().getYAxis().getLowerBound()));
        manualXmax.setText(String.valueOf(plotterController.getPlotter().getAxes().getXAxis().getUpperBound()));
        manualXmin.setText(String.valueOf(plotterController.getPlotter().getAxes().getXAxis().getLowerBound()));

        manualSGFilterLeft.setText(String.valueOf(appPreferencesRootNode.getInt("defaultSGFilterLeft",50)));
        manualSGFilterRight.setText(String.valueOf(appPreferencesRootNode.getInt("defaultSGFilterRight",50)));
        manualSGFilterOrder.setText(String.valueOf(appPreferencesRootNode.getInt("defaultSGFilterOrder",1)));

        FFTSize.setText(String.valueOf(appPreferencesRootNode.getInt("defaultSpectrogramFFTSize",256)));
        FFTWindowType.setText(appPreferencesRootNode.get("defaultSpectrogramFFTWindow","Hunning"));
        FFTWindowOverlap.setText(String.valueOf(appPreferencesRootNode.getInt("defaultSpectrogramFFTOverlap",50)));

        lineWidth.setText(String.valueOf(appPreferencesRootNode.getDouble("defaultWidthOfLine",1.0)));

        zeroShiftStart.setText(String.valueOf(appPreferencesRootNode.getDouble("defaultFixZeroShiftStart",0)));
        zeroShiftEnd.setText(String.valueOf(appPreferencesRootNode.getDouble("defaultFixZeroShiftEnd",1)));
        fixZeroShift.setSelected(appPreferencesRootNode.getBoolean("defaultFixZeroShift",false));

        plotStyle =appPreferencesRootNode.get("defaultPlotStyle","line");
        lineOrScatter.addAll("line+scatter","line","scatter");
        chooseLineOrScatter.itemsProperty().setValue(lineOrScatter);
        chooseLineOrScatter.setValue(plotStyle);
        chooseLineOrScatter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            plotterController.getPlotter().getCanvasData().setPlotStyle(newValue);
            plotStyle =newValue;
        });
        allDataLables.add("set for all data");
        for(ADCDataRecords adcr : mainApp.getAdcDataRecords()){
            allDataLables.add(adcr.getSignalLabel());
        }
        dataStartTime.setText("0.0");
        setDataStartTime.itemsProperty().setValue(allDataLables);
        setDataStartTime.getSelectionModel().clearSelection();
        setDataStartTime.setOnAction(event -> {
            if(!setDataStartTime.getSelectionModel().isEmpty()&&!setDataStartTime.getSelectionModel().isSelected(0)){double startTime = mainApp.getAdcDataRecords().get(setDataStartTime.getSelectionModel().getSelectedIndex()-1).getSignalTimeShift();
            dataStartTime.setText(String.valueOf(startTime));
            }

        });

        UseNewDefaults.setSelected(appPreferencesRootNode.getBoolean("defaultUseNewDefaults",false));
    }
}




