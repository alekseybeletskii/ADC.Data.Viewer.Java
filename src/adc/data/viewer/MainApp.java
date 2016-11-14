package adc.data.viewer;

import adc.data.viewer.controllers.*;
import adc.data.viewer.ADCreader.DataParams;
import adc.data.viewer.ADCreader.DataData;
import adc.data.viewer.model.SignalMarker;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;

/**
 * @author Aleksey Beletskii
 *
 * This application serves for a simple signal visualization
 * using binary data recorded with Analog Digital Converters.
 * In order to achieve a better performance, Canvas was used instead of a standard XYChart
 */


public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;
    public Stage getPlotsStage() {
        return plotsStage;
    }
    private Stage plotsStage;
    private BorderPane mainLayout;
    private DataData allSignals;
    private ObservableList<SignalMarker> signalList = FXCollections.observableArrayList();

    public ObservableList<SignalMarker> getSignalList() {
        return signalList;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public DataData getAllSignals() {
        return allSignals;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ADC Signal Viewer");
        this.plotsStage = null;
        this.primaryStage.getIcons().add(new Image("images/IPP-logo.png"));
        initMainLayout();
        showSignalsOverview();
    }

    private void initMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/MainLayout.fxml"));
            mainLayout =  loader.load();
            Scene scene = new Scene(mainLayout);
            primaryStage.setScene(scene);
            MainLayoutController controller = loader.getController();
            controller.setMainApp(this);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showSignalsOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/SignalsOverview.fxml"));
            AnchorPane signalsOverview = loader.load();
            mainLayout.setCenter(signalsOverview);
            SignalsOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAllSignals(DataData allSignals) {
        signalList.clear();
        this.allSignals = allSignals;
        int i=0;
        float []  hueArray = new float[allSignals.getSignalLabels().length];
        for (int jj=0;jj<allSignals.getSignalLabels().length;jj++) {
            hueArray[jj]=i;
            i+=40;
            if(i>360) {i=30;}
        }
        int ii =0;
        for (String siglabel : allSignals.getSignalLabels()){
            final float hue = hueArray[ii];
            final float saturation = 1f;//1.0 for brilliant, 0.0 for dull
            final float brightness = 1f; //1.0 for brighter, 0.0 for black
            Color color = Color.hsb(hue, saturation, brightness);
            if(siglabel!=null&&!siglabel.isEmpty()) {
                signalList.add(new SignalMarker(ii, false, color, siglabel, allSignals.getFileNumbers()[ii]));
            }
            ii++;
        }
    }

    public void drawPlots() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/Plotter.fxml"));
            AnchorPane plotsLayout = loader.load();
            PlotterController controller = loader.getController();
            controller.setMainApp(this);
            controller.setPlotsOnPane();

            if(plotsStage!=null)plotsStage.close();
            plotsStage = new Stage();
            plotsStage.setTitle("selected ADC signals");
            plotsStage.getIcons().add(new Image("images/IPP-logo.png"));
            Scene scene = new Scene(plotsLayout);
            plotsStage.setScene(scene);
            plotsStage.show();

            controller.getPlots().getCanvas().draw();
            controller.getPlots().getCanvas().widthProperty().addListener(it -> controller.getPlots().getCanvas().draw());
            controller.getPlots().getCanvas().heightProperty().addListener(it -> controller.getPlots().getCanvas().draw());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTextFileParams(int fnum, DataData dataData) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/textFileParams.fxml"));
            BorderPane textFileParams = loader.load();
            TextFileParamsController controller = loader.getController();
            Stage textFileParamsStage = new Stage();
            textFileParamsStage.initStyle(StageStyle.UNDECORATED);
            textFileParamsStage.setResizable(false);
            controller.setTextFileParamsStage(textFileParamsStage);
            controller.setAllData(dataData);
            controller.setFileNumber(fnum);
            textFileParamsStage.setTitle("Set siganl parameters");
            Scene scene = new Scene(textFileParams);
            textFileParamsStage.setScene(scene);
            textFileParamsStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showReadme(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/Readme.fxml"));
            TextArea aReadme = loader.load();
            ReadmeController controller =loader.getController();
            Stage aReadmeStage = new Stage();
            controller.setStageReadme(aReadmeStage);
            aReadmeStage.setResizable(false);
            aReadmeStage.setTitle("Description");
            Scene scene = new Scene(aReadme);
            aReadmeStage.setScene(scene);
            aReadmeStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
