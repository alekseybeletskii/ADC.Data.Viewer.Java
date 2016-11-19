package adc.data.viewer.controllers;

import adc.data.viewer.MainApp;
import adc.data.viewer.model.SignalMarker;
import adc.data.viewer.ui.TableCellColoredView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 */
public class SignalsOverviewController {

    // Reference to the main application.
    private MainApp mainApp;
    private boolean checkedFlag;
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
    private void initialize() {
        checkedFlag =true;
        signalsTable.setPlaceholder(new Label("Check \"Help->How to use\" for instructions"));
        signalSelectedColumn.setCellValueFactory(cellData -> cellData.getValue().signalSelectedProperty());
        signalColorColumn.setCellValueFactory(cellData -> cellData.getValue().signalColorProperty());
        signalLabelColumn.setCellValueFactory(cellData -> cellData.getValue().signalLabelProperty());

//        signalSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn((i)->mainApp.getSignalList().get(i).signalSelectedProperty()));
        signalSelectedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(signalSelectedColumn));
//        signalColorColumn.setCellFactory(TableCellColoredView::new);
        signalColorColumn.setCellFactory(c->new TableCellColoredView<>(signalColorColumn));
        showSignalDetails(null);
        // Listen for selection changes and show the signal details when changed.
//        signalsTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> showSignalDetails(newValue.intValue()));//;addListener((observable, oldValue, newValue) -> showSignalDetails(newValue));
        signalsTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showSignalDetails(newValue));
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
        //        .setItems(mainApp.getPersonData());
    }

    @FXML
    private void handleClickedOnTable(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()==2){

            for(SignalMarker sigM : mainApp.getSignalList())
            {
                sigM.setSignalSelected(checkedFlag);
//                sigM.setSignalSelected(sigM.isSignalSelected()^true);//revert current value
            }
            checkedFlag=!checkedFlag;
        }

    }
}
