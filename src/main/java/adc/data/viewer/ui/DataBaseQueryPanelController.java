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


import adc.data.viewer.dao.MongodbManager;
import adc.data.viewer.exeptions.ADCDataRecordsDaoException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.List;

public class DataBaseQueryPanelController extends BaseController {

    private ObservableList<String> alldevices = FXCollections.observableArrayList();
    private ObservableList<String> alldiagnostics = FXCollections.observableArrayList();
    private ObservableList<String> alldates = FXCollections.observableArrayList();
    private ObservableList<String> allshots = FXCollections.observableArrayList();
    private List<String> finalQueryBasic = new ArrayList<>();
    private List<String> finalQueryShots = new ArrayList<>();
    @FXML
    private ListView<String> deviceNames;
    @FXML
    private ListView<String> diagnosticsNames;

    @FXML
    private ListView<String> dates;

    @FXML
    private ListView<String> shotNumbers;

     private Stage dataBaseQueryStage;
     void setPlotterSettingStage(Stage dataBaseQueryStage) {
         this.dataBaseQueryStage = dataBaseQueryStage;
    }

//mainApp.getAdcDataRecords().addAll(MongodbManager.findAllRecords());

    @FXML
    private void initialize() throws ADCDataRecordsDaoException {

         List<String> allDevises = MongodbManager.findAllDevicesNames();
         if(allDevises!=null){
         alldevices.addAll(allDevises);}
         if(allDevises==null)
             BaseController.alertMongoDBConnectionError();

         shotNumbers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

//        shotNumbers.setCellFactory(CheckBoxListCell.forListView(item -> {
//            BooleanProperty observable = new SimpleBooleanProperty();
//            observable.addListener((obs, wasSelected, isNowSelected) -> {
//                System.out.println("Check box for " + item + " changed from " + wasSelected + " to " + isNowSelected);
//                if(isNowSelected){
//                    finalQueryShots.add(item);
//                }
//                if(!isNowSelected){
//                    finalQueryShots.remove(item);
//                }
//                    }
//
//            );
//            return observable ;
//        }));


        deviceNames.itemsProperty().setValue(alldevices);
        diagnosticsNames.itemsProperty().setValue(alldiagnostics);
        dates.itemsProperty().setValue(alldates);
        shotNumbers.itemsProperty().setValue(allshots);

         deviceNames.getSelectionModel().selectedItemProperty().addListener(observable  -> {
             finalQueryBasic.add(deviceNames.getSelectionModel().getSelectedItem());
             diagnosticsNames.getItems().clear();
             try {
                 alldiagnostics.addAll(MongodbManager.findAllDiagnosticsNames(deviceNames.getSelectionModel().getSelectedItem()));
             } catch (ADCDataRecordsDaoException e) {
                 e.printStackTrace();
             }
         });

        diagnosticsNames.getSelectionModel().selectedItemProperty().addListener(observable  -> {
            finalQueryBasic.add(diagnosticsNames.getSelectionModel().getSelectedItem());
            dates.getItems().clear();
            try {
                alldates.addAll(MongodbManager.findAllDates(diagnosticsNames.getSelectionModel().getSelectedItem()));
            } catch (ADCDataRecordsDaoException e) {
                e.printStackTrace();
            }
        });

        dates.getSelectionModel().selectedItemProperty().addListener(observable  -> {
            finalQueryBasic.add(dates.getSelectionModel().getSelectedItem());
            shotNumbers.getItems().clear();
            try {
                allshots.addAll(MongodbManager.findAllShots(dates.getSelectionModel().getSelectedItem()));
            } catch (ADCDataRecordsDaoException e) {
                e.printStackTrace();
            }
        });
//
        shotNumbers.getSelectionModel().selectedItemProperty().addListener(observable  -> {
            finalQueryShots.addAll(shotNumbers.getSelectionModel().getSelectedItems());
        });

        shotNumbers.getSelectionModel().selectedItemProperty().addListener(observable  -> {
            finalQueryShots.addAll(shotNumbers.getSelectionModel().getSelectedItems());
        });
    }




    @FXML
    private void handleCancel(ActionEvent actionEvent) {
         dataBaseQueryStage.close();

    };

    @FXML
    private void handleFetch(ActionEvent actionEvent) {
        try {
            if(!finalQueryBasic.isEmpty()&&!finalQueryShots.isEmpty())
            mainApp.getAdcDataRecords().addAll(
                    MongodbManager.findADCRecordsByCriterion(finalQueryBasic,finalQueryShots));
        } catch (ADCDataRecordsDaoException e) {
            e.printStackTrace();
        }

        dataBaseQueryStage.close();

    };
}

