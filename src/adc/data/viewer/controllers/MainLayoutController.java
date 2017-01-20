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
    private static File initDir;
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(initDir);
        // Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
                "data files (*.dat,*.txt)", "*.dat", "*.DAT","*.txt","*.TXT");
        fileChooser.getExtensionFilters().add(extFilter);
        List<File> inpList = fileChooser.showOpenMultipleDialog(mainApp.getPrimaryStage());
        if (inpList != null) {
            initDir=inpList.get(0).getParentFile();
            new DataParser(inpList,mainApp);
            mainApp.fillSignalList();
        }
    }

    @FXML
    private void handleSignalsToText() {

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
    private void handleClear() {
        mainApp.getSignalList().clear();
    }

    @FXML
    private void handleDrawPlots() {
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
    private void handleReadme() {
        mainApp.showReadme();
    }

    @FXML
    public void initialize() {
        initDir=new File(System.getProperty("user.home"));
    }
}
