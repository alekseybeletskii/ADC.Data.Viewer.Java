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
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 *
 */
public class SignalsOverviewController extends BaseController{

    private boolean checkedFlag;
    private FilteredList<ADCDataRecords> filteredListOfSignals;
    public ScrollPane getPlotsScrollPane() {
        return plotsScrollPane;
    }

    @FXML
    VBox plotsVBox;

    @FXML
    private ScrollPane plotsScrollPane;

    @FXML
    private AnchorPane signalsOverviewRightPane;

    @FXML
    private GridPane signalFeaturesGrid;

    @FXML
    private TableView<ADCDataRecords> signalsTable;

    @FXML
    private TableColumn<ADCDataRecords, Boolean> signalSelectedColumn;
    @FXML
    private TableColumn<ADCDataRecords, Color> signalColorColumn;
    @FXML
    private TableColumn<ADCDataRecords, String> signalLabelColumn;
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

    @FXML
    private TextField signalsListFilter;

    public VBox getPlotsVBox() {
        return plotsVBox;
    }



    public TableView<ADCDataRecords> getSignalsTable() {
        return signalsTable;
    }
    public AnchorPane getSignalsOverviewRightPane() {
        return signalsOverviewRightPane;
    }
    public SplitPane getSignalsOverviewSplitPane() {
        return signalsOverviewSplitPane;
    }

    @FXML
    private void initialize() {

        checkedFlag =false;
        signalsTable.setPlaceholder(new Label("Check \"Help->How to use\" for instructions"));
        signalSelectedColumn.setCellValueFactory(cellData -> cellData.getValue().signalSelectedProperty() );
        signalColorColumn.setCellValueFactory(cellData -> cellData.getValue().signalColorProperty());
        signalLabelColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSignalLabel()));

        signalColorColumn.setCellFactory(c->new TableCellColoredView<>(signalColorColumn));
//        signalSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(signalSelectedColumn));
        signalSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(param ->
                filteredListOfSignals.get(param).signalSelectedProperty()
        ));

        showSignalDetails(null);

//        Listen for selection changes and show the signal details when changed.
        signalsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) ->
                {   showSignalDetails(newValue);
                }
                );

        plotsScrollPane.getContent().setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double height = plotsScrollPane.getContent().getBoundsInLocal().getHeight();
                double vvalue = plotsScrollPane.getVvalue();
                double ScrollDeltaYVval = event.getDeltaY()/height;
                double paneContentShift =1/(mainApp.getHowManyPlots()-1);
                if(ScrollDeltaYVval >0) {
                    plotsScrollPane.setVvalue(vvalue + ScrollDeltaYVval - paneContentShift);
                }
                if(ScrollDeltaYVval <0) {
                    plotsScrollPane.setVvalue(vvalue + ScrollDeltaYVval + paneContentShift);
                }
            }
        });
    }




    private void showSignalDetails(ADCDataRecords signal) {
        if (signal != null){

            deviceNameLabel.setText(mainApp.getDataParser().getDataParams().getDeviceName()[signal.getFileOrdinalNumber()]);
            creationDateLabel.setText(mainApp.getDataParser().getDataParams().getCreateDateTime()[signal.getFileOrdinalNumber()]);
            adcRateLabel.setText(String.format("%.3f",mainApp.getDataParser().getDataParams().getAdcRate()[signal.getFileOrdinalNumber()]));
            channelRateLabel.setText(String.format("%.3f",mainApp.getDataParser().getDataParams().getChannelRate()[signal.getFileOrdinalNumber()]));
            channelNumberLabel.setText(signal.getAdcChannelNumber());
//            channelNumberLabel.setText(signal.getSignalLabel().substring(signal.getSignalLabel().lastIndexOf("#")+1));
            channelSamplesLabel.setText(String.format("%d",mainApp.getDataParser().getDataParams().getRealCadresQuantity()[signal.getFileOrdinalNumber()]));
            channelDurationLabel.setText(String.format("%.2f",
                    mainApp.getDataParser().getDataParams().getRealCadresQuantity()[signal.getFileOrdinalNumber()]
                            /mainApp.getDataParser().getDataParams().getChannelRate()[signal.getFileOrdinalNumber()]));
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



    public void setTableItems() {

        filteredListOfSignals = new FilteredList<>(mainApp.getAdcDataRecords(), n -> true);
        signalsListFilter.setOnKeyReleased(k -> {
            if (k.getCode() == KeyCode.ENTER){
                filteredListOfSignals.setPredicate(n -> {

                    boolean isContains = n.getSignalLabel().contains(signalsListFilter.getText());
                    if (signalsListFilter.getText() == null || signalsListFilter.getText().isEmpty()) {
                        n.setSignalSelected(false);
                        return true;
                    }

                    n.setSignalSelected(isContains);
                    return isContains;

                });
        }
        });

        // Add observable list data to the table
        signalsTable.itemsProperty().setValue(filteredListOfSignals);
//        signalsTable.setItems(filteredListOfSignals);

//        signalsTable.setItems(mainApp.getAdcDataRecords());
//        signalsTable.itemsProperty().setValue(mainApp.getAdcDataRecords());
    }

    @FXML
    private void handleClickedOnTable(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==2){

            for(ADCDataRecords sigM : filteredListOfSignals)
            {
                sigM.setSignalSelected(checkedFlag);

            }
            checkedFlag=!checkedFlag;
        }
    }
}
