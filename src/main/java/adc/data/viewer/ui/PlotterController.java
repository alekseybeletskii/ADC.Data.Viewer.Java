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


import adc.data.viewer.model.ADCDataRecord;
import adc.data.viewer.plotter.Plotter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;


public class PlotterController extends BaseController{



    private  boolean settingsInProgress;
    private Plotter plotter;
    private ADCDataRecord signalAsFilter;
    public FlowPane getLegendPane() {
        return legendPane;
    }
    public void setLegendPane(FlowPane legendPane) {
        this.legendPane = legendPane;
    }

    public boolean isSettingsInProgress() {
        return settingsInProgress;
    }
    public void setSettingsInProgress(boolean settingsInProgress) {
        this.settingsInProgress = settingsInProgress;
    }

    @FXML
    FlowPane legendPane;
    @FXML
    Label signalIndexLabel;
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

    private ToggleGroup toggleGroup;

    public ToggleGroup getToggleGroup() {
        return toggleGroup;
    }



    public Plotter getPlotter() {
        return plotter;
    }
    public Label getXyLabel() {
        return xyLabel;
    }
    public AnchorPane getPlotsLayout() {
        return plotsLayout;
    }


    public void setPlotsOnPane() {

        plotter = new Plotter(mainApp, plotsLayout, this);

    }

    public Label getSignalIndexLabel() {
        return signalIndexLabel;
    }



    @FXML
    public void handleRaw(ActionEvent actionEvent) {
        toggleButton("Raw");
        MainApp.appPreferencesRootNode.put("defaultPlotType","Raw");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleRawAndSGFilter(ActionEvent actionEvent) {
        toggleButton("RawAndSGFilter");
        MainApp.appPreferencesRootNode.put("defaultPlotType","RawAndSGFilter");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleSGFiltered(ActionEvent actionEvent) {
        toggleButton("SGFiltered");
        MainApp.appPreferencesRootNode.put("defaultPlotType","SGFiltered");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleSGFilter(ActionEvent actionEvent) {
        toggleButton("SGFilter");
        MainApp.appPreferencesRootNode.put("defaultPlotType","SGFilter");
        plotter.getCanvasData().drawData();
    }

    @FXML
    public void handleSubtractSignal(ActionEvent actionEvent) {
        MainApp.appPreferencesRootNode.putBoolean("defaultIsSubtractSignal", SubtractSignal.isSelected());
        if(SubtractSignal.isSelected()) {

            int i = 0;
            signalAsFilter = null;
            for (ADCDataRecord sigMarc : mainApp.getAdcDataRecords()) {
                if (sigMarc.getSignalSelected()) {
                    i++;
                    if(i>1) break;
                    signalAsFilter =sigMarc;
                }
            }
            if(i==1){
                MainApp.appPreferencesRootNode.putInt("defaultADCChannelUsedAsFilter",-1);
                mainApp.setSignalUsedAsFilter(signalAsFilter.getSignalYData().clone());
                mainApp.getBaseController().alertFilterIsApplied("one",signalAsFilter.getSignalLabel());
            }else if (i>1) {
                int numberOfADCChannelAsFilter =Integer.parseInt(signalAsFilter != null ? signalAsFilter.getAdcChannelNumber() : "-1");
                MainApp.appPreferencesRootNode.putInt("defaultADCChannelUsedAsFilter",numberOfADCChannelAsFilter);
                mainApp.getBaseController().alertFilterIsApplied("multy",signalAsFilter.getSignalLabel());
            }else {
                SubtractSignal.setSelected(false);
                MainApp.appPreferencesRootNode.putInt("defaultADCChannelUsedAsFilter",-1);
                MainApp.appPreferencesRootNode.putBoolean("defaultIsSubtractSignal", false);
                mainApp.getBaseController().alertFilterIsApplied("non",signalAsFilter.getSignalLabel());            }

        }
        else{
            SubtractSignal.setSelected(false);
      }
        plotter.getCanvasData().drawData();

    }



    @FXML
    public void handlePlotterSettings(ActionEvent actionEvent) {

        if(!isSettingsInProgress()){mainApp.setPlotterSetting(this);
        }
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

    }


}
