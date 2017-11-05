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


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class DataBaseConnectionController extends BaseController {

    @FXML
    private TextField DataBaseName;
    @FXML
    private TextField DataBaseCollectionName;
    @FXML
    private TextField databaseLogin;
    @FXML
    private TextField databasePassword;
    @FXML
    private TextField databaseHost;
    @FXML
    private TextField databasePort;

    private Stage dbConnectStage;

     void setdbConnectStage(Stage dbConnectStage) {
        this.dbConnectStage = dbConnectStage;
    }

    @FXML
    private void  handleApply(ActionEvent actionEven){
        MainApp.appPreferencesRootNode.put("host", databaseHost.getText());
        MainApp.appPreferencesRootNode.put("hostport", databasePort.getText());
        MainApp.appPreferencesRootNode.put("dbLogin", databaseLogin.getText());
        MainApp.appPreferencesRootNode.put("dbPassword", databasePassword.getText());
        MainApp.appPreferencesRootNode.put("ADCDataViewerDataBaseCollection", DataBaseCollectionName.getText());
        MainApp.appPreferencesRootNode.put("ADCDataViewerDataBase", DataBaseName.getText());
        dbConnectStage.close();


}
    @FXML
    private void handleDefault(ActionEvent actionEvent) {
        MainApp.appPreferencesRootNode.put("host", "localhost");
        MainApp.appPreferencesRootNode.put("hostport", "27017");
        MainApp.appPreferencesRootNode.put("dbLogin", "");
        MainApp.appPreferencesRootNode.put("dbPassword", "");
        MainApp.appPreferencesRootNode.put("ADCDataViewerDataBaseCollection", "adcDataRecords");
        MainApp.appPreferencesRootNode.put("ADCDataViewerDataBase", "ADCDataViewerDB");
        dbConnectStage.close();
    }

    @FXML
    private void handleCancel(ActionEvent actionEvent){
        dbConnectStage.close();
    }



    @FXML
    private void initialize() {
        databaseHost.setText(MainApp.appPreferencesRootNode.get("host", "localhost"));
        databasePort.setText(MainApp.appPreferencesRootNode.get("hostport", "27017"));
        databaseLogin.setText(MainApp.appPreferencesRootNode.get("dbLogin", ""));
        databasePassword.setText(MainApp.appPreferencesRootNode.get("dbPassword", ""));
        DataBaseCollectionName.setText(MainApp.appPreferencesRootNode.get("ADCDataViewerDataBaseCollection", "adcDataRecords"));
        DataBaseName.setText(MainApp.appPreferencesRootNode.get("ADCDataViewerDataBase", "ADCDataViewerDB"));


    }

}

