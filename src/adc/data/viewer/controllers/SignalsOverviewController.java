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

package adc.data.viewer.controllers;

import adc.data.viewer.MainApp;
import adc.data.viewer.model.SignalMarker;
import adc.data.viewer.ui.TableCellColoredView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

/**
 *
 */
public class SignalsOverviewController {

    // Reference to the main application.
    private MainApp mainApp;
    private boolean checkedFlag;



    @FXML
    private AnchorPane signalsOverviewRightPane;

    @FXML
    private GridPane signalFeaturesGrid;

    @FXML
    private TableView<SignalMarker> signalsTable;

    @FXML
    private TableColumn<SignalMarker, Boolean> signalSelectedColumn;
    @FXML
    private TableColumn<SignalMarker, Color> signalColorColumn;
    @FXML
    private TableColumn<SignalMarker, String> signalLabelColumn;
    @FXML
    private Label deviceNameLabel;
    @FXML
    private Label creationDateLabel;
    @FXML
    private Label adcRateLabel;
    @FXML
    private Label channelRateLabel;
    @FXML
    private Label channelNumberLabel;

    @FXML
    private Label channelSamplesLabel;
    @FXML
    private Label channelDurationLabel;

    @FXML
    private SplitPane signalsOverviewSplitPane;

    public AnchorPane getSignalsOverviewRightPane() {
        return signalsOverviewRightPane;
    }
    public SplitPane getSignalsOverviewSplitPane() {
        return signalsOverviewSplitPane;
    }

    @FXML
    private void initialize() {
        checkedFlag =true;
        signalsTable.setPlaceholder(new Label("Check \"Help->How to use\" for instructions"));

        signalSelectedColumn.setCellValueFactory(cellData -> cellData.getValue().signalSelectedProperty() );
        signalColorColumn.setCellValueFactory(cellData -> cellData.getValue().signalColorProperty());
        signalLabelColumn.setCellValueFactory(cellData -> cellData.getValue().signalLabelProperty());
//        signalSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(signalSelectedColumn));
        signalColorColumn.setCellFactory(c->new TableCellColoredView<>(signalColorColumn));


//        signalSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(param -> mainApp.getSignalList().get(param).signalSelectedProperty()));
        signalSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(param ->
         mainApp.getSignalList().get(param).signalSelectedProperty()
        ));


        showSignalDetails(null);

//        Listen for selection changes and show the signal details when changed.
        signalsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                {   showSignalDetails(newValue);
                }
                );
    }

    private void showSignalDetails(SignalMarker signal) {
        if (signal != null){

            deviceNameLabel.setText(mainApp.getDataParser().getDataParams().getDeviceName()[signal.getFileNumber()]);
            creationDateLabel.setText(mainApp.getDataParser().getDataParams().getCreateDateTime()[signal.getFileNumber()]);
            adcRateLabel.setText(String.format("%.3f",mainApp.getDataParser().getDataParams().getAdcRate()[signal.getFileNumber()]));
            channelRateLabel.setText(String.format("%.3f",mainApp.getDataParser().getDataParams().getChannelRate()[signal.getFileNumber()]));
            channelNumberLabel.setText(signal.getSignalLabel().substring(signal.getSignalLabel().lastIndexOf("#")+1));
            channelSamplesLabel.setText(String.format("%d",mainApp.getDataParser().getDataParams().getRealCadresQuantity()[signal.getFileNumber()]));
            channelDurationLabel.setText(String.format("%.2f",
                    mainApp.getDataParser().getDataParams().getRealCadresQuantity()[signal.getFileNumber()]
                            /mainApp.getDataParser().getDataParams().getChannelRate()[signal.getFileNumber()]));
        }
        else{
            deviceNameLabel.setText("-----");
            creationDateLabel.setText("-----");
            adcRateLabel.setText("-----");
            channelRateLabel.setText("-----");
            channelNumberLabel.setText("-----");
            channelSamplesLabel.setText("-----");
            channelDurationLabel.setText("-----");
        }
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setTableItems() {
        // Add observable list data to the table
        signalsTable.itemsProperty().setValue(mainApp.getSignalList());
//        signalSelectedColumn.getCellObservableValue(0).addListener((observable, oldValue, newValue) -> System.out.println(newValue));
//        signalSelectedColumn.onEditCommitProperty().addListener((observable, oldValue, newValue) -> System.out.println("gggggg"));
//        signalsTable.getSelectionModel().getSelectedCells()
//        signalsTable.getSelectionModel().selectedIndexProperty().addListener   ( c -> System.out.println("fbsdf"));
//        signalSelectedColumn.cellValueFactoryProperty().addListener((observable, oldValue, newValue) -> System.out.println(newValue));
    }



    @FXML
    private void handleClickedOnTable(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==2){
            for(SignalMarker sigM : mainApp.getSignalList())
            {
                sigM.setSignalSelected(checkedFlag);
            }
            checkedFlag=!checkedFlag;
        }
    }
}
