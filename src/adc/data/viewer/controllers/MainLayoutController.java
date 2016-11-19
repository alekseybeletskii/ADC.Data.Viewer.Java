package adc.data.viewer.controllers;

import adc.data.viewer.MainApp;
import adc.data.viewer.ADCreader.DataParser;
import adc.data.viewer.model.SignalMarker;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;


public class MainLayoutController {

    private MainApp mainApp;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "data files (*.dat,*.txt)", "*.dat", "*.DAT","*.txt","*.TXT");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> inpList = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());

        if (inpList != null) {
            new DataParser(inpList,mainApp);
            mainApp.fillSignalList();
        }
    }

    @FXML
    public void handleSignalsToText() {

        if (mainApp.getSignalList().size() != 0) {
            mainApp.getDataParser().saveToFile();
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ADC binary data viewer");
        alert.setHeaderText("About");
        alert.setContentText("Author: \nAleksey Beletskii\n\nWebsite:\nhttps://ua.linkedin.com/in/beletskii-aleksey");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        if(mainApp.getPlotsStage()!=null) {
            mainApp.getPlotsStage().close();
        }
        System.exit(0);
    }

    @FXML
    public void handleClear() {
        mainApp.getSignalList().clear();
    }

    @FXML
    public void handleDrawPlots() {
        boolean isAnyChecked =false;
        for (SignalMarker signalMarker : mainApp.getSignalList())
        {
            isAnyChecked = signalMarker.isSignalSelected();
            if(isAnyChecked)break;
        }

        if (mainApp.getSignalList().size() != 0&&isAnyChecked) {
            mainApp.drawPlots();

        }

    }

    @FXML
    public void handleReadme() {
        mainApp.showReadme();
    }
}
