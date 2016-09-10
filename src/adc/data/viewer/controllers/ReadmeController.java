package adc.data.viewer.controllers;


import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ReadmeController {

    private Stage stageReadme;

    public void setStageReadme(Stage stageReadme) { this.stageReadme = stageReadme; }
    @FXML
    private void handleOnMouseClick(MouseEvent mouseEvent) {
        if(mouseEvent.getClickCount()>1){
            stageReadme.close();
        }
    }
}
