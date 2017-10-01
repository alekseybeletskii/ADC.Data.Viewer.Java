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


import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.Region;

import java.util.Optional;

import static javafx.scene.control.Alert.AlertType.*;

 public class BaseController {

private Alert alertFilterIsApplied;
private Alert alertInvalidSettingsParam;
private Alert alertInvalidDataFormatDefinition;
private Alert alertSourceDataReplaced;
private Alert alertAboutThisProgram;
private Alert alertOpenMoreFiles;
private Alert alertNumberFormatExc;
static MainApp mainApp;


      void alertSourceDataReplaced(){
          alertSourceDataReplaced=buildWarning(alertSourceDataReplaced,WARNING);
          alertSourceDataReplaced.setTitle("Warning !");
          alertSourceDataReplaced.setHeaderText("Source data replacing...");
          alertSourceDataReplaced.setContentText("Source data being replacing\nwith ones processed by filtering applyed\n\n");
          alertSourceDataReplaced.showAndWait();
    }



     void alertFilterIsApplied(String amountOfSelectedCurves,String signalLable) {

         alertFilterIsApplied=buildWarning(alertFilterIsApplied,WARNING);

        switch (amountOfSelectedCurves) {
            case "non":
                alertFilterIsApplied.setTitle("Filter is NOT applied");
                alertFilterIsApplied.setHeaderText("please select at least one position from the list !");
                alertFilterIsApplied.setContentText("------?????-------\n\n");
                break;
            case "one":
                alertFilterIsApplied.setTitle("Filter is applied");
                alertFilterIsApplied.setHeaderText("The next ADC channel is used as a filter");
                alertFilterIsApplied.setContentText("the channel "+signalLable+"\nis selected as a filter for all remained\n\n");
                break;
            case "multy":
                alertFilterIsApplied.setTitle("Filter is applied");
                alertFilterIsApplied.setHeaderText("The next ADC channel is used as a filter");
                alertFilterIsApplied.setContentText("the channel # "+ signalLable+"\nis selected FROM EVERY file as a filter\n\n");
                break;
        }
        alertFilterIsApplied.showAndWait();
    }

     void alertInvalidSettingsParam() {
         alertInvalidSettingsParam= buildWarning(alertInvalidSettingsParam,WARNING);
         alertInvalidSettingsParam.setTitle("Warning");
         alertInvalidSettingsParam.setHeaderText("Invalid data format or axes ranges!");
         alertInvalidSettingsParam.setContentText("all axes parameters should be of type float,\nother pframeters should be Integer\nand \"FFTWindowType\" of type String\n\n");
         alertInvalidSettingsParam.showAndWait();
    }

     void alertInvalidDataFormatDefinition() {
         alertInvalidDataFormatDefinition=buildWarning(alertInvalidDataFormatDefinition,WARNING);
         alertInvalidDataFormatDefinition.setTitle("Warning");
         alertInvalidDataFormatDefinition.setHeaderText("Invalid data format!");
         alertInvalidDataFormatDefinition.setContentText("channel number: integer, >=0 \nchannel rate: float, >0 " +
                "\nX(Y) column number: int, >=0 \namount of header lines: integer, >=0\n\n");
         alertInvalidDataFormatDefinition.showAndWait();
    }

     Optional<ButtonType>  alertOpenMoreFiles(){
         alertOpenMoreFiles= buildWarning(alertOpenMoreFiles,CONFIRMATION);
         alertOpenMoreFiles.setTitle("");
         alertOpenMoreFiles.setHeaderText("More files?");
        return  alertOpenMoreFiles.showAndWait();
    }

     void alertAboutThisProgram (){
         alertAboutThisProgram=buildWarning(alertAboutThisProgram,INFORMATION);
         alertAboutThisProgram.setTitle("ADC binary data viewer");
         alertAboutThisProgram.setHeaderText("JavaFX8-based application for digital signals visualisation");
         alertAboutThisProgram.setContentText("Author: \nAleksey Beletskii\n\nWebsite:\nhttps://ua.linkedin.com/in/beletskii-aleksey");
         alertAboutThisProgram.showAndWait();
    }


      public void alertNumberFormatExc() {
          alertNumberFormatExc=buildWarning(alertNumberFormatExc,Alert.AlertType.WARNING);
          alertNumberFormatExc.setTitle("Warning");
          alertNumberFormatExc.setHeaderText("Invalid data format!");
          alertNumberFormatExc.setContentText("*.txt file should contain at least one column of float\nselect proper columns numbers, the first is #0\n\nCheck header lines amount!\n\nCheck columns separator!");
          alertNumberFormatExc.showAndWait();
     }

     private Alert buildWarning(Alert alert,Alert.AlertType alertType) {
         if(alert==null) {
             alert = new Alert(alertType);
             alert.initOwner(mainApp.getPrimaryStage());
//            alert.initStyle(StageStyle.UTILITY);
//             alert.getDialogPane().setPrefSize(250,120);
//             alert.getDialogPane().setMinSize(250,120);
             DialogPane dialogPane = alert.getDialogPane();
             dialogPane.getStylesheets().add(getClass().getResource("/css/dialog.css").toExternalForm());
             dialogPane.getStyleClass().add("myDialog");
             dialogPane.setMinHeight(Region.USE_PREF_SIZE);
             dialogPane.setMinWidth(Region.USE_PREF_SIZE);
             dialogPane.toFront();
         }
         return alert;
     }

}


