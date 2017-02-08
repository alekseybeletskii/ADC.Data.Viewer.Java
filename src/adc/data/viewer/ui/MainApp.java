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

package adc.data.viewer.ui;

import adc.data.viewer.parser.DataParser;
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

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage primaryStage;
    private Stage plotsStage;
    private BorderPane mainLayout;
    private AnchorPane plotsLayout;
    private DataParser dataParser;
    private ObservableList<SignalMarker> signalList = FXCollections.observableArrayList();
    private TextFileDataController textFileDataController;



    private PlotterController plotterController;
    private PlotterSettingController plotterSettingController;
    private SignalsOverviewController signalsOverviewController;

     void setPlotterController(PlotterController plotterController) {
        this.plotterController = plotterController;
    }
    public SignalsOverviewController getSignalsOverviewController() {
        return signalsOverviewController;
    }
     AnchorPane getPlotsLayout() {
        return plotsLayout;
    }
     PlotterController getPlotterController() {
        return plotterController;
    }
    public TextFileDataController getTextFileDataController() {
        return textFileDataController;
    }
    public ObservableList<SignalMarker> getSignalList() {
        return signalList;
    }
     Stage getPrimaryStage() {
        return primaryStage;
    }
    public DataParser getDataParser() {
        return dataParser;
    }
     Stage getPlotsStage() {
        return plotsStage;
    }
    public void setDataPars(DataParser dataParser) {
        this.dataParser = dataParser;
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ADC Signal Viewer");
        this.primaryStage.getIcons().add(new Image("images/IPP-logo.png"));
        initMainLayout();
        showSignalsOverview();
    }

    private void initMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("MainLayout.fxml"));
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
            loader.setLocation(MainApp.class.getResource("SignalsOverview.fxml"));
            AnchorPane signalsOverview = loader.load();
            mainLayout.setCenter(signalsOverview);
            signalsOverviewController  = loader.getController();
            signalsOverviewController.setMainApp(this);
            signalsOverviewController.setTableItems();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     void fillSignalList() {
        clearOldListAndPlots();
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
                SignalMarker sigmrk =new SignalMarker(ii, false, color, siglabel, dataParser.getFileNumbers()[ii]);
                sigmrk.signalSelectedProperty().addListener((observable, oldValue, newValue) -> {
                    if(plotterController!=null) {
                        plotterController.getPlots().getCanvas().draw();
                    }
                }
                );

                sigmrk.signalColorProperty().addListener((observable, oldValue, newValue) -> {
                    if(plotterController!=null) {
                        plotterController.getPlots().getCanvas().draw();
                    }
                }
                );



                signalList.add(sigmrk);
            }
            ii++;
        }

    }

     void drawPlots() {
        try {
            if(plotsLayout!=null) {
                signalsOverviewController.getSignalsOverviewRightPane().getChildren().remove(plotsLayout);
                plotsLayout = null;
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("Plotter.fxml"));
            plotsLayout = loader.load();

            plotterController = loader.getController();

            plotterController.setMainApp(this);
            plotterController.setPlotsOnPane();

//            if(plotsStage!=null)plotsStage.close();
//            plotsStage = new Stage();
//            plotsStage.setTitle("selected ADC signals");
//            plotsStage.getIcons().add(new Image("images/IPP-logo.png"));
//            Scene scene = new Scene(plotsLayout);
//            plotsStage.setScene(scene);
//            plotsStage.show();

            signalsOverviewController.getSignalsOverviewRightPane().getChildren().addAll(plotsLayout);
            AnchorPane.setLeftAnchor(plotsLayout, 0.0);
            AnchorPane.setRightAnchor(plotsLayout, 0.0);
            AnchorPane.setBottomAnchor(plotsLayout, 0.0);
            AnchorPane.setTopAnchor(plotsLayout, 0.0);

            plotterController.getPlots().getCanvas().widthProperty().addListener(it -> plotterController.getPlots().getCanvas().draw());
            plotterController.getPlots().getCanvas().heightProperty().addListener(it -> plotterController.getPlots().getCanvas().draw());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTextFileParams(int fnum) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("textFileParams.fxml"));
            BorderPane textFileParams = loader.load();
            textFileDataController = loader.getController();
            Stage textFileParamsStage = new Stage();
            textFileParamsStage.initStyle(StageStyle.UNDECORATED);
            textFileParamsStage.setResizable(false);
            textFileParamsStage.setAlwaysOnTop(true);
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

     void setPlotterSetting() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("PlotterSetting.fxml"));
            BorderPane plotterSetting = loader.load();
            plotterSettingController = loader.getController();
            Stage plotterSettingStage = new Stage();
            plotterSettingStage.setResizable(false);
            plotterSettingStage.setAlwaysOnTop(true);
            plotterSettingController.setMainApp(this);
            plotterSettingController.setPlotterSettingStage(plotterSettingStage);
            plotterSettingStage.setTitle("Plotter Settings");
            Scene scene = new Scene(plotterSetting);
            plotterSettingStage.setScene(scene);
            plotterSettingStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     void showReadme(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("Readme.fxml"));
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

     void clearOldListAndPlots(){
        signalList.clear();
        signalsOverviewController.getSignalsOverviewRightPane().getChildren().remove(plotsLayout);
        plotsLayout = null;
    }
}