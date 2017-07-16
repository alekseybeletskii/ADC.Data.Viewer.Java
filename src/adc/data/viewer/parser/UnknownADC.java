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

package adc.data.viewer.parser;

import javafx.scene.control.Alert;
import javafx.scene.layout.Region;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static adc.data.viewer.ui.TextFileParamController.*;
import static javafx.scene.control.Alert.AlertType.WARNING;

class UnknownADC implements DataTypes {
    private DataParser dataParser;

    public void setDataParser(DataParser dataParser) {
       this.dataParser =dataParser;

    }

    public void setParam(int fileIndex) {

       dataParser.getMainApp().setTextFileParams(fileIndex);

       byte[] arrayByte = {(byte) 1};
       dataParser.getDataParams().setChannelsMax(1, fileIndex) ; //int
       dataParser.getDataParams().setRealChannelsQuantity(1, fileIndex); //int
       dataParser.getDataParams().setInterCadreDelay(0.0,fileIndex) ; //Double
       dataParser.getDataParams().setActiveAdcChannelArray(arrayByte,fileIndex) ; //Byte
       dataParser.getDataParams().setAdcChannelArray(arrayByte,fileIndex) ; //Byte
       dataParser.getDataParams().setAdcGainArray(arrayByte,fileIndex) ; //Byte
       dataParser.getDataParams().setIsSignalArray(arrayByte,fileIndex) ; //Byte
       dataParser.getDataParams().setAdcRate(0.0,fileIndex) ; //Double

       //get this from user:
       dataParser.getDataParams().setDeviceName(deviceName,fileIndex); //string
       dataParser.getDataParams().setCreateDateTime(creationDate,fileIndex) ; //string
       dataParser.getDataParams().setChannelRate(channelRate,fileIndex) ; //Double
       dataParser.getDataParams().setRealCadresQuantity(0, fileIndex) ; //long
       dataParser.getDataParams().setTotalTime(0.0,fileIndex) ; //Double
    }


   public void setData( int fileIndex, int signalIndex) {
      String line;
      List<Double> allLines = new ArrayList<>();
      DataParams dataParams = dataParser.getDataParams();

      try (BufferedReader signalDataFromText = Files.newBufferedReader(dataParser.getDataFilePath()[fileIndex], Charset.forName("UTF-8"))) {
         try {

            while ((line = signalDataFromText.readLine()) != null) {
            String[] columns = line.split(",");

               if(columns.length>= columnNum+1){allLines.add(Double.parseDouble(columns[columnNum]));}
               else {throw new NumberFormatException ();}
            }
            dataParams.setRealCadresQuantity(allLines.size(), fileIndex);
            dataParams.setTotalTime(allLines.size() / dataParams.getChannelRate()[fileIndex], fileIndex);
            double[] oneSignal = new double[allLines.size()];
             dataParser.getDataParams().setRealCadresQuantity(allLines.size(), fileIndex) ;
             dataParser.getDataParams().setTotalTime(allLines.size()/dataParser.getDataParams().getChannelRate()[fileIndex],fileIndex);
             dataParser.getDataParams().setRealSamplesQuantity(dataParser.getDataParams().getRealCadresQuantity()[fileIndex], fileIndex) ; //long
            int i = 0;
            for (Double d : allLines) {
               oneSignal[i] = d;
               i++;
            }

            signalIndex++;
            dataParser.PutADCDataRecords(oneSignal, signalIndex, fileIndex, channelNum);
         }
         catch (NumberFormatException e){
            Alert alert = new Alert(WARNING);
            alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.setTitle("Warning");
            alert.setHeaderText("Invalid data format!");
            alert.setContentText("*.txt file should contain at least one column of float\nselect proper column, the first is 0\n No any header lines !");
            alert.showAndWait();
         }
      } catch (IOException x) {
         System.err.format("IOException: %s%n", x);
      }
   }
 }
