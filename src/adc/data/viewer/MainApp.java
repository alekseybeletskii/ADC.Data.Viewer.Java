package adc.data.viewer;

import adc.data.viewer.ADCreader.DataParser;
import adc.data.viewer.controllers.*;
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
    private Stage plotsStage;
    private BorderPane mainLayout;
    private DataParser dataParser;
    private ObservableList<SignalMarker> signalList = FXCollections.observableArrayList();
    private TextFileDataController textFileDataController;

    public TextFileDataController getTextFileDataController() {
        return textFileDataController;
    }
    public ObservableList<SignalMarker> getSignalList() {
        return signalList;
    }
    public Stage getPrimaryStage() {
        return primaryStage;
    }
    public DataParser getDataParser() {
        return dataParser;
    }
    public Stage getPlotsStage() {
        return plotsStage;
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
            controller.setTableItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  void setDataPars(DataParser dataParser) {
        this.dataParser = dataParser;
    }

     public void fillSignalList() {
        signalList.clear();
        int i=0;
        float []  hueArray = new float[dataParser.getSignalLabels().length];
        for (int jj = 0; jj< dataParser.getSignalLabels().length; jj++) {
            hueArray[jj]=i;
            i+=40;
            if(i>360) {i=30;}
        }
        int ii =0;
        for (String siglabel : dataParser.getSignalLabels()){
            final float hue = hueArray[ii];
            final float saturation = 1f;//1.0 for brilliant, 0.0 for dull
            final float brightness = 1f; //1.0 for brighter, 0.0 for black
            Color color = Color.hsb(hue, saturation, brightness);
            if(siglabel!=null&&!siglabel.isEmpty()) {
                signalList.add(new SignalMarker(ii, false, color, siglabel, dataParser.getFileNumbers()[ii]));
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

            controller.getPlots().getCanvas().draw();
            controller.getPlots().getCanvas().widthProperty().addListener(it -> controller.getPlots().getCanvas().draw());
            controller.getPlots().getCanvas().heightProperty().addListener(it -> controller.getPlots().getCanvas().draw());
            plotsStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTextFileParams(int fnum) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("ui/textFileParams.fxml"));
            BorderPane textFileParams = loader.load();
            textFileDataController = loader.getController();
            Stage textFileParamsStage = new Stage();
            textFileParamsStage.initStyle(StageStyle.UNDECORATED);
            textFileParamsStage.setResizable(false);
            textFileDataController.setMainApp(this);
            textFileDataController.setDataParser(dataParser);
            textFileDataController.setTextFileParamsStage(textFileParamsStage);
            textFileDataController.setFileNumber(fnum);
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
