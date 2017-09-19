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


import adc.data.viewer.model.ADCDataRecords;
import adc.data.viewer.plotter.Plotter;
import adc.data.viewer.processing.SavitzkyGolayFilter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class PlotterController {


    private MainApp mainApp;
    private Plotter plotter;
    private Alert alertInvalidParam;



    @FXML
    Label signalIndexLabel;
    @FXML
    Label legend;
    @FXML
    private AnchorPane plotsLayout;
    @FXML
    private Label xyLabel;
    @FXML
    public ToggleButton Raw;
    @FXML
    public ToggleButton RawAndSGFilter;
    @FXML
    public ToggleButton SGFilter;
    @FXML
    private ToggleButton subtractSGFilter;
    @FXML
    private ToggleButton SubtractSignal;

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }

    private ToggleGroup toggleGroup;

    public Plotter getPlotter() {
        return plotter;
    }
    public Label getXyLabel() {
        return xyLabel;
    }
    public AnchorPane getPlotsLayout() {
        return plotsLayout;
    }
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setPlotsOnPane() {

        plotter = new Plotter(mainApp, plotsLayout, this);

    }

    public Label getSignalIndexLabel() {
        return signalIndexLabel;
    }
    public Label getLegend() {
        return legend;
    }


    @FXML
    public void handleRaw(ActionEvent actionEvent) {
        MainApp.appPreferencesRootNode.put("defaultPlotType","Raw");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleRawAndSGFilter(ActionEvent actionEvent) {

        MainApp.appPreferencesRootNode.put("defaultPlotType","RawAndSGFilter");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleSGFiltered(ActionEvent actionEvent) {

        MainApp.appPreferencesRootNode.put("defaultPlotType","SGFiltered");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleSGFilter(ActionEvent actionEvent) {

        MainApp.appPreferencesRootNode.put("defaultPlotType","SGFilter");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleSubtractSignal(ActionEvent actionEvent) {
        MainApp.appPreferencesRootNode.putBoolean("defaultIsSubtractSignal", SubtractSignal.isSelected());
        if(SubtractSignal.isSelected()) {
            int i = 0;
            ADCDataRecords signalAsFilter = null;
            for (ADCDataRecords sigMarc : mainApp.getAdcDataRecords()) {
                if (sigMarc.getSignalSelected()) {
                    signalAsFilter =sigMarc;
                    i++;
                }
            }
            if(i!=1){
                SubtractSignal.setSelected(false);
                MainApp.appPreferencesRootNode.putBoolean("defaultIsSubtractSignal", false);
                alertInvalidParam.showAndWait();
            }else if (!signalAsFilter.equals(null)){
                SavitzkyGolayFilter sgfilter;
                double [] sigAsFiltData =signalAsFilter.getSignalYData().clone();
                String plotType = MainApp.appPreferencesRootNode.get("defaultPlotType","Raw");
                if(!plotType.equals("Raw")){

                    int SGFilterLeft= MainApp.appPreferencesRootNode.getInt("defaultSGFilterLeft",50); //points
                    int  SGFilterRight= MainApp.appPreferencesRootNode.getInt("defaultSGFilterRight",50); //points
                    int SGFilterOrder= MainApp.appPreferencesRootNode.getInt("defaultSGFilterLeftOrder",1);
                    int sgLeft = (SGFilterLeft+SGFilterRight)>=sigAsFiltData.length?1:SGFilterLeft;
                    int sgRight = (SGFilterLeft+SGFilterRight)>=sigAsFiltData.length?1:SGFilterRight;
                    sgfilter =new SavitzkyGolayFilter(sgLeft, sgRight, SGFilterOrder);
                    sigAsFiltData = sgfilter.filterData(sigAsFiltData);
                }

                mainApp.setSignalUsedAsFilter(sigAsFiltData);
            }

        }
        else{
            SubtractSignal.setSelected(false);
      }
        plotter.getCanvasData().drawData();

    }



    @FXML
    public void handlePlotterSettings(ActionEvent actionEvent) {
        mainApp.setPlotterSetting(this);
    }

    public void toggleButton(String toggleName){
        switch (toggleName){
            case "Raw":
                toggleGroup.selectToggle(Raw);
                break;
            case "RawAndSGFilter":
                toggleGroup.selectToggle(RawAndSGFilter);
                break;
            case "SGFilter":
                toggleGroup.selectToggle(SGFilter);
                break;
            case "SGFiltered":
                toggleGroup.selectToggle(subtractSGFilter);
                break;
        }
    }

    @FXML
    private void initialize() {

        toggleGroup = new ToggleGroup();
        Raw.setToggleGroup(toggleGroup);
        RawAndSGFilter.setToggleGroup(toggleGroup);
        SGFilter.setToggleGroup(toggleGroup);
        subtractSGFilter.setToggleGroup(toggleGroup);

        toggleButton(MainApp.appPreferencesRootNode.get("defaultPlotType","Raw"));

        subtractSGFilter.setText("\u2013SGfilter");
        SubtractSignal.setText("\u2013Signal");
        SubtractSignal.setSelected(MainApp.appPreferencesRootNode.getBoolean("defaultIsSubtractSignal",false));


        alertInvalidParam = new Alert(WARNING);
        DialogPane dialogPane = alertInvalidParam.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
        dialogPane.getStyleClass().add("myDialog");
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);
        dialogPane.setMinWidth(Region.USE_PREF_SIZE);
        dialogPane.toFront();
        alertInvalidParam.setTitle("Warning");
        alertInvalidParam.setHeaderText("Improper selection!");
        alertInvalidParam.setContentText("In order to use any signal as a filter for all other signals,\n" +
                "there ought to be one and only one selected signal\n\n");
    }
}
