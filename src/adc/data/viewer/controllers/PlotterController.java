package adc.data.viewer.controllers;


import adc.data.viewer.ADCPlotter.PlotsBuilder;
import adc.data.viewer.MainApp;
import adc.data.viewer.dataProcessing.SavitzkyGolayFilter;
import javafx.event.ActionEvent;
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

    @FXML
    public void handleRaw(ActionEvent actionEvent) {
        plots.getCanvas().setSGfilter(null);
        plots.getCanvas().setPlotType("Raw");
        plots.getCanvas().draw();
    }
    @FXML
    public void handleRawAndSGFilter(ActionEvent actionEvent) {
        plots.getCanvas().setSGfilter(new SavitzkyGolayFilter(200,200,2));
        plots.getCanvas().setPlotType("RawAndSGFilter");
        plots.getCanvas().draw();
    }
    @FXML
    public void handleSGFiltered(ActionEvent actionEvent) {
        plots.getCanvas().setSGfilter(new SavitzkyGolayFilter(200,200,2));
        plots.getCanvas().setPlotType("SGFiltered");
        plots.getCanvas().draw();
    }
    @FXML
    public void handleSGFilter(ActionEvent actionEvent) {
        plots.getCanvas().setSGfilter(new SavitzkyGolayFilter(200,200,2));
        plots.getCanvas().setPlotType("SGFilter");
        plots.getCanvas().draw();
    }
}
