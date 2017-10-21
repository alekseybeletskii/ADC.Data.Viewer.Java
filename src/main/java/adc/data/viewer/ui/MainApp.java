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

import adc.data.viewer.model.ADCDataRecord;
import adc.data.viewer.parser.DataParser;
import adc.data.viewer.plotter.Plotter;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import static adc.data.viewer.ui.TextFileParamController.isRememberTxtFileSettings;
import static java.lang.Math.round;

public  class MainApp extends Application {


    private final BaseController baseController = new BaseController();
    private boolean newFileCreated;
    private  boolean redrawAllowed;
    private final Image logo = new Image("images/logo.png");
    private ADCDataRecord nextSignalToDraw;
    private String defaultPlotsLayoutType;
    private Stage primaryStage;
    private BorderPane mainLayout;
    private DataParser dataParser;
    public  static final Preferences appPreferencesRootNode = Preferences.userRoot().node("ADCDataViewer");
    private ObservableList<ADCDataRecord> adcDataRecords = FXCollections.observableArrayList();
    private TextFileParamController textFileParamController;
    private int nextSignalToDrawIndex;
    private int howManyPlots;
    private List<PlotterController> plotterControllerlist =new ArrayList<>();
    private SignalsOverviewController signalsOverviewController;
    private double [] signalUsedAsFilter;
    private double splitPaneDivisionPosition;

    public BaseController getBaseController() {
        return baseController;
    }
    public void setDirectoryWatcher(WatchDirectory directoryWatcher) {
        this.directoryWatcher = directoryWatcher;
    }
    public WatchDirectory getDirectoryWatcher() {
        return directoryWatcher;
    }
    public void setListOfFiles(List<File> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }
    private  WatchDirectory directoryWatcher;
    public boolean isNewFileCreated() {
        return newFileCreated;
    }
    public void setNewFileCreated(boolean newFileCreated) {
        this.newFileCreated = newFileCreated;
    }
    private List<File> listOfFiles;
    public void setNextSignalToDraw(ADCDataRecord nextSignalToDraw) {
        this.nextSignalToDraw = nextSignalToDraw;
    }
    public double getSplitPaneDivisionPosition() {
        return splitPaneDivisionPosition;
    }
    public double[] getSignalUsedAsFilter() {
        return signalUsedAsFilter;
    }
    public void setSignalUsedAsFilter(double[] signalUsedAsFilter) {
        this.signalUsedAsFilter = signalUsedAsFilter;
    }
    public List<PlotterController> getPlotterControllerlist() {
        return plotterControllerlist;
    }
    public String getDefaultPlotsLayoutType() {
        return defaultPlotsLayoutType;
    }
    public void setDefaultPlotsLayoutType(String defaultPlotsLayoutType) {
        this.defaultPlotsLayoutType = defaultPlotsLayoutType;
    }
    public ADCDataRecord getNextSignalToDraw() {
        return nextSignalToDraw;
    }
    public SignalsOverviewController getSignalsOverviewController() {
        return signalsOverviewController;
    }
    public TextFileParamController getTextFileParamController() {
        return textFileParamController;
    }
    public ObservableList<ADCDataRecord> getAdcDataRecords() {
        return adcDataRecords;
    }
    Stage getPrimaryStage() {
        return primaryStage;
    }
    public DataParser getDataParser() {
        return dataParser;
    }
    public double getHowManyPlots() {
        return howManyPlots;
    }



    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{

        BaseController.mainApp=this;
        primaryStage.setOnCloseRequest(e ->
        {Platform.exit();
         System.exit(0);});
        splitPaneDivisionPosition =0.12;
        nextSignalToDrawIndex =-1;
        this.dataParser =new DataParser(this);
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ADC Signal Viewer");
        this.primaryStage.getIcons().add(logo);
        initMainLayout();
        showSignalsOverview();
        defaultPlotsLayoutType = appPreferencesRootNode.get("defaultPlotsLayoutType","AllPlots");
        MainApp.appPreferencesRootNode.putBoolean("defaultIsSubtractSignal",false);


    }

    private void initMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("MainLayout.fxml"));
            mainLayout =  loader.load();
            Scene scene = new Scene(mainLayout);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
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
            signalsOverviewController.setTableItems();
            signalsOverviewController.getPlotsScrollPane().setVisible(false);
            signalsOverviewController.getSignalsOverviewSplitPane().setVisible(true);
            setKeyPressedAction();
            getSignalsOverviewController().getSignalsOverviewSplitPane().setDividerPositions(splitPaneDivisionPosition);
            mainLayout.widthProperty().addListener(observable -> {this.getSignalsOverviewController().getSignalsOverviewSplitPane().setDividerPositions(splitPaneDivisionPosition);});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    synchronized void drawPlots() {
        Plotter.setPlotterObjectsCounter(0);

        clearPlots();
        switch (defaultPlotsLayoutType){
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
                for (ADCDataRecord sm: adcDataRecords) {
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
                for (ADCDataRecord sm: adcDataRecords) {
                    if (sm.getSignalSelected()) {
                        nextSignalToDraw=sm;
                        layoutLoader();

                        plotterControllerlist.get(plotterControllerlist.size()-1)
                                .getPlotsLayout().setPrefHeight
                                (signalsOverviewController.getSignalsOverviewRightPane().getHeight());
                    }
                }
                break;
            default:
                break;
        }

        howManyPlots=0;
        for (PlotterController pc:plotterControllerlist){

            if(defaultPlotsLayoutType.equals("AllPlotsByOne")){
               AnchorPane.setBottomAnchor(pc.getPlotter(),25.0);
            }
            howManyPlots++;
;
            pc.getPlotter().getCanvasData().widthProperty().addListener(it -> pc.getPlotter().getCanvasData().drawData());
            pc.getPlotter().getCanvasData().heightProperty().addListener(it -> pc.getPlotter().getCanvasData().drawData());
            signalsOverviewController.getPlotsVBox().getChildren().add(pc.getPlotsLayout());
        }


        signalsOverviewController.getPlotsScrollPane().setVisible(true);
        signalsOverviewController.getPlotsScrollPane().requestFocus();
    }

    private void layoutLoader() {
        PlotterController plotterController;
        try {

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("Plotter.fxml"));
            loader.load();
            plotterController = loader.getController();
            plotterController.setPlotsOnPane();
            plotterController.getPlotter().getCanvasData().setPlotterController(plotterController);
            plotterController.getPlotter().getCanvasData().setNextSignalToDraw(nextSignalToDraw);
            plotterControllerlist.add(plotterController);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setTextFileParams(int fileIndex) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("TextFileParam.fxml"));
            BorderPane textFileParams = loader.load();
            textFileParamController = loader.getController();
            textFileParamController.getTxtNextFileName().setText(dataParser.getFileNames()[fileIndex]);
            Stage textFileParamsStage = new Stage();
            textFileParamsStage.initStyle(StageStyle.UNDECORATED);
            textFileParamsStage.getIcons().add(logo);
            textFileParamsStage.initOwner(primaryStage);
            textFileParamsStage.setResizable(false);
            textFileParamsStage.toFront();
            textFileParamController.setDataParser(dataParser);
            textFileParamController.setTextFileParamsStage(textFileParamsStage);
            textFileParamController.setFileNumber(fileIndex);
            textFileParamsStage.setTitle("Set siganl parameters");
            Scene scene = new Scene(textFileParams);
            textFileParamsStage.setScene(scene);
            if(isRememberTxtFileSettings) {
                textFileParamController.pushOk();
            }else {textFileParamsStage.showAndWait();}

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     void setPlotterSetting(PlotterController pc) {
        try {
            pc.setSettingsInProgress(true);
            PlotterSettingController plotterSettingController;
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("PlotterSetting.fxml"));
            BorderPane plotterSetting = loader.load();
            plotterSettingController = loader.getController();
            Stage plotterSettingStage = new Stage();
            plotterSettingStage.getIcons().add(logo);
            plotterSettingStage.initOwner(primaryStage);
            plotterSettingStage.setResizable(false);
            plotterSettingStage.setAlwaysOnTop(true);
            plotterSettingController.setPlotterController(pc);
            plotterSettingController.initializeSettings();
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
            aReadmeStage.setTitle("Description. Double left-click to close");
            Scene scene = new Scene(aReadme);
            aReadmeStage.setScene(scene);
            aReadmeStage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

     void clearAll(){
             nextSignalToDrawIndex=-1;
             adcDataRecords.clear();
             signalsOverviewController.getPlotsVBox().getChildren().clear();
             plotterControllerlist.clear();
             signalsOverviewController.getPlotsScrollPane().setVisible(false);

    }

    private void clearPlots(){
        if(defaultPlotsLayoutType.equals("AllPlots")) {
            redrawAllowed=true;
            signalsOverviewController.getPlotsVBox().getChildren().clear();
            plotterControllerlist.clear();
            signalsOverviewController.getPlotsScrollPane().setVisible(false);
        }
        if(defaultPlotsLayoutType.equals("AllPlotsByOne")){
            redrawAllowed=false;
            signalsOverviewController.getPlotsVBox().getChildren().clear();
            plotterControllerlist.clear();
            signalsOverviewController.getPlotsScrollPane().setVisible(false);
        }
        if(defaultPlotsLayoutType.equals("AllPlotsByOneScroll")){
            redrawAllowed=false;
            signalsOverviewController.getPlotsVBox().getChildren().clear();
            plotterControllerlist.clear();
            signalsOverviewController.getPlotsScrollPane().setVisible(false);
        }
    }

    public void signalMarkerAddListeners() {

        for (ADCDataRecord sgmrk : adcDataRecords){
            sgmrk.signalSelectedProperty().addListener((observable, oldValue, newValue) -> {
                redrawAllCanvas(sgmrk,newValue);
                    }
            );

            sgmrk.signalColorProperty().addListener((observable, oldValue, newValue) -> {
                redrawAllCanvas(sgmrk,false);
                }
        );
    }
    }

    private void redrawAllCanvas(ADCDataRecord sgmrk, boolean isChecked) {
        if (!plotterControllerlist.isEmpty() && redrawAllowed) {
            int i = 0;
            for (PlotterController pc: plotterControllerlist) {
                if(isChecked&&!appPreferencesRootNode.getBoolean("defaultUseNewDefaults",false)){
                    plotterControllerlist.get(i).getPlotter().getAxes().obtainDataAndTimeMargins(sgmrk);
                    plotterControllerlist.get(i).getPlotter().getAxes().setAxesBasicSetup();
                }
                plotterControllerlist.get(i).getPlotter().getCanvasData().drawData();
                i++;
            }
        }
    }

    private void setKeyPressedAction(){


        signalsOverviewController.getPlotsScrollPane().setOnKeyPressed(k -> {

            if(k.getCode().getName().equals("W")&&k.isControlDown()){
                double oldDividerPos = signalsOverviewController.getSignalsOverviewSplitPane().getDividerPositions()[0];
                double newDividerPosition = oldDividerPos>0.05?0:0.2;
                signalsOverviewController.getSignalsOverviewSplitPane().setDividerPosition(0,newDividerPosition);
            }

            if(k.getCode().getName().equals("F")&&k.isShiftDown()){
                baseController.alertSourceDataReplaced();
                MainApp.appPreferencesRootNode.putBoolean("defaultIsReplaceRawWithFilter", true);
                for (PlotterController pc: plotterControllerlist) {
                    pc.getPlotter().getAxes().setAxesBasicSetup();
                    pc.getPlotter().getCanvasData().drawData();
                }
            }

            if(k.getCode().getName().equals("P")&&k.isShiftDown()){
            String outPath = dataParser.getExportToTextAndSnapshot().takeSnapShot( signalsOverviewController.getPlotsScrollPane());
                baseController.alertExport(outPath);
            }


            if(k.getCode().getName().equals("S")&&k.isShiftDown()){
                String outPath = "";
                        String delims = "\\s+";
                if (!plotterControllerlist.isEmpty() ){
                    String s = plotterControllerlist.get(0).getXyLabel().getText();
                    double profileTime = Double.parseDouble(s.split(delims)[1]);
                    outPath = dataParser.getExportToTextAndSnapshot().saveProfile(profileTime);
                }
                baseController.alertSaveProfile(outPath);
            }




            if(defaultPlotsLayoutType.equals("AllPlots")) {
                if (k.getCode() == KeyCode.HOME && k.isShortcutDown()) {
                    nextSignalToDraw=null;
                    redrawAllowed=false;
                    adcDataRecords.forEach(sig -> sig.setSignalSelected(true));
                    plotterControllerlist.get(0).getPlotter().getAxes().obtainDataAndTimeMargins(nextSignalToDraw);
                    if(!appPreferencesRootNode.getBoolean("defaultUseNewDefaults",false))
                        plotterControllerlist.get(0).getPlotter().getAxes().setAxesBasicSetup();
                    nextSignalToDrawIndex = -1;
                    plotterControllerlist.get(0).getPlotter().getCanvasData().drawData();
                    redrawAllowed=true;
                } else
                    switch (k.getCode()) {
                        case HOME:
                            adcDataRecords.forEach(sig -> sig.setSignalSelected(false));
                            nextSignalToDrawIndex = 0;
                            nextSignalToDraw= adcDataRecords.get(nextSignalToDrawIndex);
                            plotterControllerlist.get(0).getPlotter().getCanvasData().setNextSignalToDraw(nextSignalToDraw);
                            plotterControllerlist.get(0).getPlotter().getAxes().obtainDataAndTimeMargins(nextSignalToDraw);
                            if(!appPreferencesRootNode.getBoolean("defaultUseNewDefaults",false))
                                plotterControllerlist.get(0).getPlotter().getAxes().setAxesBasicSetup();
                            adcDataRecords.get(nextSignalToDrawIndex).setSignalSelected(true);
                            break;
                        case END:
                            adcDataRecords.forEach(sig -> sig.setSignalSelected(false));
                            nextSignalToDrawIndex = adcDataRecords.size() - 1;
                            nextSignalToDraw= adcDataRecords.get(nextSignalToDrawIndex);
                            plotterControllerlist.get(0).getPlotter().getCanvasData().setNextSignalToDraw(nextSignalToDraw);
                            plotterControllerlist.get(0).getPlotter().getAxes().obtainDataAndTimeMargins(nextSignalToDraw);
                            if(!appPreferencesRootNode.getBoolean("defaultUseNewDefaults",false))
                                plotterControllerlist.get(0).getPlotter().getAxes().setAxesBasicSetup();
                            adcDataRecords.get(nextSignalToDrawIndex).setSignalSelected(true);
                            break;
                        case DOWN:
                            adcDataRecords.forEach(sig -> sig.setSignalSelected(false));
                            nextSignalToDrawIndex++;
                            nextSignalToDrawIndex = nextSignalToDrawIndex > adcDataRecords.size() - 1 ? 0 : nextSignalToDrawIndex;
                            nextSignalToDraw= adcDataRecords.get(nextSignalToDrawIndex);
                            plotterControllerlist.get(0).getPlotter().getCanvasData().setNextSignalToDraw(nextSignalToDraw);
                            plotterControllerlist.get(0).getPlotter().getAxes().obtainDataAndTimeMargins(nextSignalToDraw);
                            if(!appPreferencesRootNode.getBoolean("defaultUseNewDefaults",false))
                                plotterControllerlist.get(0).getPlotter().getAxes().setAxesBasicSetup();
                            adcDataRecords.get(nextSignalToDrawIndex).setSignalSelected(true);
                            break;
                        case UP:
                            adcDataRecords.forEach(sig -> sig.setSignalSelected(false));
                            nextSignalToDrawIndex--;
                            nextSignalToDrawIndex = nextSignalToDrawIndex < 0 ? adcDataRecords.size() - 1 : nextSignalToDrawIndex;
                            nextSignalToDraw= adcDataRecords.get(nextSignalToDrawIndex);
                            plotterControllerlist.get(0).getPlotter().getCanvasData().setNextSignalToDraw(nextSignalToDraw);
                            plotterControllerlist.get(0).getPlotter().getAxes().obtainDataAndTimeMargins(nextSignalToDraw);
                            if(!appPreferencesRootNode.getBoolean("defaultUseNewDefaults",false))
                                plotterControllerlist.get(0).getPlotter().getAxes().setAxesBasicSetup();
                            adcDataRecords.get(nextSignalToDrawIndex).setSignalSelected(true);
                            break;
                        default:
                            break;
                    }
            }

        });

    }








    public  void parse (List<Path> inpList) {

        isRememberTxtFileSettings =false;
        int fullStopIndex = inpList.get(0).getFileName().toString().lastIndexOf(".");
        String fileExtension = inpList.get(0).getFileName().toString().substring(fullStopIndex + 1).toLowerCase();

        if ((fileExtension.equalsIgnoreCase("dat") || fileExtension.equalsIgnoreCase("txt")
        || fileExtension.equalsIgnoreCase("csv")) && inpList.get(0).toFile().length() > 0) {
            clearAll();
            setDefaultPlotsLayoutType("AllPlots");
            redrawAllowed=true;
            dataParser.parseNewList(inpList);
            signalMarkerAddListeners();
            signalsOverviewController.getSignalsOverviewSplitPane().setVisible(true);
            drawPlots();
        }
        synchronized (this){
            if(directoryWatcher!=null)directoryWatcher.resumeWatcher();
        }
    }




}
