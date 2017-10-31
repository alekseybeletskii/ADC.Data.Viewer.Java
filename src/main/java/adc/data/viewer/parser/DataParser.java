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

package adc.data.viewer.parser;


import adc.data.viewer.model.ADCDataRecord;
import adc.data.viewer.ui.BaseController;
import adc.data.viewer.ui.MainApp;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;
import java.util.stream.IntStream;


public class DataParser {

    private DataParams dataParams;
    private DataFormatsDetect dataFormatsDetect;
    private int signalIndex;

    private Path [] signalPath;
    private MainApp mainApp;
    private List<ADCDataRecord> ADCDataRecordList = new ArrayList<>();
    private Path[] dataFilePath;
    private Path[] parFilePath;
    private String[] fileNames;
    private  int totalFiles;
    private  int totalSignals;
    private Color[] signalColors;
    private ExportToTextAndSnapshot exportToTextAndSnapshot;
    private Path configPath;
    private Map<String,String> adcRecordsConfig;
    public List<ADCDataRecord> getADCDataRecordList() {
        return ADCDataRecordList;
    }
    public String[] getFileNames() {
        return fileNames;
    }
    public ExportToTextAndSnapshot getExportToTextAndSnapshot() {
        return exportToTextAndSnapshot;
    }
    public void setExportToTextAndSnapshot(ExportToTextAndSnapshot exportToTextAndSnapshot) {
        this.exportToTextAndSnapshot = exportToTextAndSnapshot;
    }
    public DataParams getDataParams() {
        return dataParams;
    }
    public Path[] getDataFilePath() {
        return dataFilePath;
    }
    public Path[] getParFilePath() {
        return parFilePath;
    }
    public MainApp getMainApp() {
        return mainApp;
    }
    public Path[] getSignalPath() {
        return signalPath;
    }

    public DataParser( MainApp mainApp) {
        this.mainApp=mainApp;
        dataFormatsDetect = new DataFormatsDetect(mainApp);
        exportToTextAndSnapshot =new ExportToTextAndSnapshot(mainApp);
    }

    public  void parseNewList(List<Path> dataPath) {
        signalIndex =-1;
        ADCDataRecordList.clear();
        totalFiles=dataPath.size();
        makePaths(dataPath);
        setParam();
        makeSignalColors();
        signalPath = new Path[totalSignals];
        setData();
        mainApp.getAdcDataRecords().addAll(ADCDataRecordList);
    }

     Map<String,String> readAdcRecordsConfiguration(Path configPath) {
        if(configPath.equals(this.configPath)) {
            return adcRecordsConfig;
        }
        this.configPath=configPath;
        String line;
         adcRecordsConfig =new HashMap<>();

        try (BufferedReader signalDataFromText = Files.newBufferedReader(configPath, Charset.forName("UTF-8"))) {
            while ((line = signalDataFromText.readLine()) != null) {
                adcRecordsConfig.put(line.split("=")[0],line.split("=")[1]);
            }
            return adcRecordsConfig;
        }
        catch (IOException x) {
            BaseController.alertInvalidConfigurationFile();
            System.err.format("IOException: %s%n", x);

        }
         adcRecordsConfig.put("device","unknown");
         adcRecordsConfig.put("diagnostics","unknown");
         adcRecordsConfig.put("creationDate","unknown");
         adcRecordsConfig.put("portLabel","unknown");
         adcRecordsConfig.put("unitOfMeasurements","unknown");
        return adcRecordsConfig;
    }


    private void makePaths(List<Path> pathes) {
        int count = 0;
        dataFilePath = new Path[pathes.size()];
        parFilePath = new Path[pathes.size()];
        fileNames = new String[pathes.size()];

        for (Path p : pathes) {
            try {
                dataFilePath[count] = p;
                fileNames[count] = dataFilePath[count].getFileName().toString();
                fileNames[count] = fileNames[count].substring(0, fileNames[count].lastIndexOf('.'));
                parFilePath[count] = dataFilePath[count].getParent().resolve(fileNames[count] + ".par");
            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                return;
            }
            count++;
        }

    }

    private void setParam() {
        dataParams = new DataParams(totalFiles);
        for ( DataTypesList frmt : DataTypesList.values()) frmt.getDataType().setDataParser(this);
        dataFormatsDetect.detectFormat();

        String formatName;
        int fileOrdinalNumber=0;
        while (fileOrdinalNumber < dataFilePath.length)
        {
            formatName = dataParams.getDataFormatStr()[fileOrdinalNumber].toUpperCase();
                DataTypesList.valueOf(formatName).getDataType().setParam(fileOrdinalNumber);
            fileOrdinalNumber++;
        }
        totalSignals= IntStream.of(dataParams.getRealChannelsQuantity()).sum();
    }


    private  void setData (){
        if(!dataParams.isDataParamsValid()) return;
        String formatName;
        int fileOrdinalNumber=0;
        while (fileOrdinalNumber < dataFilePath.length)
        {
            formatName = dataParams.getDataFormatStr()[fileOrdinalNumber].toUpperCase();
            DataTypesList.valueOf(formatName).getDataType().setData(fileOrdinalNumber,signalIndex);
            fileOrdinalNumber++;
        }
    }

    /**
     * This method produce ADC data records from source files and will be invoked by a corresponding ADC-type class
     *
     * @param Ydata
     * a signal extracted from binary file by corresponding ADC-type-class
     * @param signalIndex
     * an extracted signal's sequence number in the "signals" array
     * @param fileOrdinalNumber
     * a number of a processed source file
     * @param adcChannelNumber
     * an ADC channel number of extracted signal
     */


     void PutADCDataRecords(double [] Xdata, double [] Ydata, int signalIndex, int fileOrdinalNumber, int adcChannelNumber, double signalTimeShift, Map<String,String> config) {

        String adcChannelNumberAsString = String.format("%02d", adcChannelNumber);
        String nextSignalLabel = adcChannelNumberAsString+"\u0040"+ fileNames[fileOrdinalNumber];
        String nextShot = fileNames[fileOrdinalNumber];
        Path nextSignalPath = dataFilePath[fileOrdinalNumber].getParent();
        this.signalPath[signalIndex] = nextSignalPath;
        this.signalIndex = signalIndex;
        double dataMultiplier = 1.0;
        boolean drawThisSignal = false;
        double signalRate_kHz = dataParams.getChannelRate()[fileOrdinalNumber];
        String creationDateTime = dataParams.getCreateDateTime()[fileOrdinalNumber];
        config.put("creationDate",config.get("creationDate").equals("unknown")?creationDateTime:config.get("creationDate"));
        double interCadreDelay_ms = dataParams.getInterCadreDelay()[fileOrdinalNumber];
        ADCDataRecord singleDataRecord =new ADCDataRecord(adcChannelNumberAsString ,signalIndex,  drawThisSignal, signalColors[signalIndex], nextSignalLabel, fileOrdinalNumber,
                Xdata.clone(), Ydata.clone(), signalTimeShift, dataMultiplier,signalRate_kHz,
                creationDateTime,interCadreDelay_ms,config, nextShot);

        ADCDataRecordList.add(singleDataRecord);

//         System.out.println(singleDataRecord.getId());
    }

    private  void makeSignalColors() {
        Color [] signalColors = new Color[totalSignals];
        final float saturation = 0.7f;//1.0 for brilliant, 0.0 for dull
        final float brightness = 0.7f; //1.0 for brighter, 0.0 for black
        int hue=0;
        for (int jj = 0; jj< totalSignals; jj++) {
            signalColors[jj] = Color.hsb(hue, saturation, brightness);
            hue+=40;
            if(hue>360) {hue=40;}
        }
        this.signalColors = signalColors;
    }







}


