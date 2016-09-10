package adc.data.viewer.controllers;


import adc.data.viewer.ADCPlotter.PlotsBuilder;
import adc.data.viewer.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class PlotterController {

    private MainApp mainApp;
    private PlotsBuilder plots;

    @FXML
    private Label xyLabel;

    @FXML
    private AnchorPane axesAnchorPane;

    public PlotsBuilder getPlots() {
        return plots;
    }
    public Label getXyLabel() {
        return xyLabel;
    }
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }
    public void setPlotsOnPane() {
        this.plots = new PlotsBuilder(mainApp, axesAnchorPane, this);
    }


}
