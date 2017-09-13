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

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.prefs.Preferences;


public class MainLayoutController {

//    static {
//        initDir=new File(System.getProperty("user.home"));
//    }

    private MainApp mainApp;
    private File initDir;
    private WatchDirectory watcher;
    private Thread watchDirThread;
    private Preferences appPreferencesRootNode = MainApp.appPreferencesRootNode;
    private  List<Path> inpList;

    public List<Path> getInpList() {
        return inpList;
    }




    void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleOpen() {


            initDir = new File(appPreferencesRootNode.get("defaultWorkingDirectory", System.getProperty("user.home")));

            if(!initDir.exists()) initDir =new File (System.getProperty("user.home"));

//        mainApp.getSignalsOverviewController().getSignalsOverviewSplitPane().setDividerPositions(mainApp.getSplitPaneDivisionPosition());
//        mainApp.getPlotterControllerlist().clear();
        //        List<File> chosenFiles;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(initDir);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "data files (*.dat,*.txt, *.csv)", "*.dat", "*.DAT","*.txt","*.TXT", "*.csv","*.CSV");
        fileChooser.getExtensionFilters().add(extFilter);
        getMoreFiles(fileChooser);

        boolean morefiles = false;
        while(morefiles) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setHeaderText("More files?");
//            alert.initStyle(StageStyle.UTILITY);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.getDialogPane().setPrefSize(250,120);
            alert.getDialogPane().setMinSize(250,120);

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK) {
                getMoreFiles( fileChooser);
            }
            else {
                morefiles=false;
            }
        }
        if (!inpList.isEmpty()) {
            appPreferencesRootNode.put("defaultWorkingDirectory", inpList.get(inpList.size()-1).getParent().toString());
            mainApp.parse(inpList);
        }
    }


            @FXML
        private void handleOpenAuto(){
                mainApp.getSignalsOverviewController().getSignalsOverviewSplitPane().setDividerPositions(0);
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Set Directory to Watch....");
            chooser.setInitialDirectory(initDir);
                File dirToWatch = chooser.showDialog(mainApp.getPrimaryStage());

          if(dirToWatch!=null&&dirToWatch.exists()) {
              final Path directoryToWatch = Paths.get(dirToWatch.getPath());
              watcher = new WatchDirectory(directoryToWatch, mainApp, "newWatcher");
              mainApp.setDirectoryWatcher(watcher);
              watcher.getWatcherThread().start();
          }

        }


    private void getMoreFiles(FileChooser fileChooser) {
        List<File> chosenFiles = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());
        List<Path> chosenPaths =new ArrayList<>();

        if(chosenFiles!=null&& !chosenFiles.isEmpty()) {
            for (File f : chosenFiles) {
                chosenPaths.add(f.toPath());
            }
        }

        if(!chosenPaths.isEmpty()) {
            inpList.addAll(chosenPaths);
            appPreferencesRootNode.put("defaultWorkingDirectory",inpList.get(inpList.size()-1).getParent().toString());
            fileChooser.setInitialDirectory(inpList.get(inpList.size()-1).getParent().toFile());
        }
    }

    @FXML
    private void handleSignalsToText() {

        if (mainApp.getAdcDataRecords().size() != 0) {
            mainApp.getDataParser().saveToText();
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ADC binary data viewer");
        alert.setHeaderText("JavaFX8-based application for digital signals visualisation");
        alert.setContentText("Author: \nAleksey Beletskii\n\nWebsite:\nhttps://ua.linkedin.com/in/beletskii-aleksey");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.exit(0);
    }

    @FXML
    private void handleClear() {
        mainApp.clearAll();
        mainApp.getDataParser().getADCDataRecordsList().clear();
        inpList.clear();
    }

    @FXML
    private void handleDrawPlots() {
        mainApp.setDefaultPlotsLayoutType("AllPlots");
        if (!mainApp.getAdcDataRecords().isEmpty()) {
            mainApp.drawPlots();
        }
    }
    @FXML
    private void handleDrawPlotsByOne(){
        mainApp.setDefaultPlotsLayoutType("AllPlotsByOne");
        if (!mainApp.getAdcDataRecords().isEmpty()) {
            mainApp.drawPlots();
        }
    }
    @FXML
    private void handleDrawPlotsByOneScroll(){
        mainApp.setDefaultPlotsLayoutType("AllPlotsByOneScroll");
        if (!mainApp.getAdcDataRecords().isEmpty()) {
            mainApp.drawPlots();
        }
    }


    @FXML
    private void handleHidePlots() {

        if(!mainApp.getSignalsOverviewController().getPlotsScrollPane().isVisible())
            mainApp.getSignalsOverviewController().getPlotsScrollPane().setVisible(true);
        else mainApp.getSignalsOverviewController().getPlotsScrollPane().setVisible(false);
    }

    @FXML
    private void handleReadme() {
        mainApp.showReadme();
    }

//    @FXML
//    private void initialize() {
//        initDir=new File(System.getProperty("user.home"));
//    }

    @FXML
    private void initialize (){
        inpList = new ArrayList<>();
    };
}
