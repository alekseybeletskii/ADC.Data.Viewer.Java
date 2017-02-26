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

import adc.data.viewer.model.SignalMarker;
import adc.data.viewer.parser.DataParser;
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
import java.util.ArrayList;
import java.util.List;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private Stage plotsStage;
    private  boolean redrawAllowed;
    private SignalMarker nextSignalToDraw;
    private String plotsLayoutType;
    private Stage primaryStage;
    private BorderPane mainLayout;
    private DataParser dataParser;
    private ObservableList<SignalMarker> signalList = FXCollections.observableArrayList();
    private TextFileDataController textFileDataController;

    public List<PlotterController> getPlotterControllerlist() {
        return plotterControllerlist;
    }

    private List<PlotterController> plotterControllerlist =new ArrayList<>();
    private PlotterController plotterController;
    private PlotterSettingController plotterSettingController;
    private SignalsOverviewController signalsOverviewController;

    public String getPlotsLayoutType() {
        return plotsLayoutType;
    }
    public SignalMarker getNextSignalToDraw() {
        return nextSignalToDraw;
    }
//    void setPlotterController(PlotterController plotterController) {
//        this.plotterController = plotterController;
//    }
    public SignalsOverviewController getSignalsOverviewController() {
        return signalsOverviewController;
    }
//    PlotterController getPlotterController() {
//        return plotterController;
//    }
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
    public void setDataPars(DataParser dataParser) {
        this.dataParser = dataParser;
    }
    Stage getPlotsStage() {
        return plotsStage;
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
            signalsOverviewController.getPlotsScrollPane().setVisible(false);
            signalsOverviewController.getSignalsOverviewSplitPane().setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     void fillSignalList() {
        clearAll();
         redrawAllowed=true;
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
                signalMarkerAddListeners(sigmrk);
                signalList.add(sigmrk);
            }
            ii++;
        }
         signalsOverviewController.getSignalsOverviewSplitPane().setVisible(true);

     }

    private void signalMarkerAddListeners(SignalMarker sigmrk) {
        sigmrk.signalSelectedProperty().addListener((observable, oldValue, newValue) -> {
            if(plotterController!=null&&redrawAllowed) {
                plotterController.getPlotter().getCanvasData().drawData();
            }
        }
        );

        sigmrk.signalColorProperty().addListener((observable, oldValue, newValue) -> {
            if(plotterController!=null&&redrawAllowed) {
                plotterController.getPlotter().getCanvasData().drawData();
            }
        }
        );
    }

    void drawPlots(String plotsLayoutType) {
        this.plotsLayoutType=plotsLayoutType;
        clearPlots();
        switch (plotsLayoutType){
            case "AllPlots":
                signalsOverviewController.getPlotsScrollPane().setFitToHeight(true);
                nextSignalToDraw=null;
                layoutLoader();
                plotterControllerlist.get(plotterControllerlist.size()-1)
                        .getPlotsLayout().prefHeightProperty()
                        .bind(signalsOverviewController.getPlotsVBox().heightProperty());
                break;
            case "AllPlotsByOne":
                signalsOverviewController.getPlotsScrollPane().setFitToHeight(true);
                for (SignalMarker sm:signalList) {
                    if (sm.getSignalSelected()) {
                        nextSignalToDraw=sm;
                        layoutLoader();
                        plotterControllerlist.get(plotterControllerlist.size()-1)
                                .getPlotsLayout().prefHeightProperty()
                                .bind(signalsOverviewController.getPlotsVBox().heightProperty());
                    }
                }
                break;
            case "AllPlotsByOneScroll":
                signalsOverviewController.getPlotsScrollPane().setFitToHeight(false);
                for (SignalMarker sm:signalList) {
                    if (sm.getSignalSelected()) {
                        nextSignalToDraw=sm;
                        layoutLoader();
                    }
                }
                break;
            default:
                break;
        }

        for (PlotterController pc:plotterControllerlist){
//            pc.getPlotsLayout().prefHeightProperty().bind(signalsOverviewController.getPlotsVBox().heightProperty());
//            pc.getPlotsLayout().setPrefHeight(signalsOverviewController.getSignalsOverviewRightPane().getHeight());
            pc.getPlotter().getCanvasData().widthProperty().addListener(it -> pc.getPlotter().getCanvasData().drawData());
            pc.getPlotter().getCanvasData().heightProperty().addListener(it -> pc.getPlotter().getCanvasData().drawData());
            signalsOverviewController.getPlotsVBox().getChildren().add(pc.getPlotsLayout());
        }
        signalsOverviewController.getPlotsScrollPane().setVisible(true);
    }

    private void layoutLoader() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("Plotter.fxml"));
            loader.load();
            plotterController = loader.getController();
            plotterController.setMainApp(this);
            plotterController.setPlotsOnPane();
            plotterController.getPlotter().getCanvasData().setPlotterController(plotterController);
            plotterController.getPlotter().getCanvasData().setNextSignalToDraw(nextSignalToDraw);
            plotterControllerlist.add(plotterController);

//            plotterController.getPlotsLayout().prefHeightProperty().bind(signalsOverviewController.getPlotsVBox().heightProperty());

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

     void setPlotterSetting(PlotterController pc) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("PlotterSetting.fxml"));
            BorderPane plotterSetting = loader.load();
            plotterSettingController = loader.getController();
            Stage plotterSettingStage = new Stage();
            plotterSettingStage.setResizable(false);
            plotterSettingStage.setAlwaysOnTop(true);
            plotterSettingController.setMainApp(this);
            plotterSettingController.setPlotterController(pc);
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

     void clearAll(){
             signalList.clear();
             signalsOverviewController.getPlotsVBox().getChildren().clear();
             plotterControllerlist.clear();
             signalsOverviewController.getPlotsScrollPane().setVisible(false);
    }

    private void clearPlots(){
        if(plotsLayoutType.equals("AllPlots")) {
            redrawAllowed=true;
            signalsOverviewController.getPlotsVBox().getChildren().clear();
            plotterControllerlist.clear();
            signalsOverviewController.getPlotsScrollPane().setVisible(false);
        }
        if(plotsLayoutType.equals("AllPlotsByOne")){
            redrawAllowed=false;
            signalsOverviewController.getPlotsVBox().getChildren().clear();
            plotterControllerlist.clear();
            signalsOverviewController.getPlotsScrollPane().setVisible(false);
        }
    }
}
