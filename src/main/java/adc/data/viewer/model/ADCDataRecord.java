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

package adc.data.viewer.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.Map;


public class ADCDataRecord {

    private String id;
    private String device;
    private String diagnostics;
    private String creationDate;
    private String creationDateTime;
    private String unitOfMeasurement;
    private  DoubleProperty signalRate_kHz;
    private  double interCadreDelay_ms;
    private  StringProperty adcChannelNumber;
    private  StringProperty recordLabel;
    private  double [] signalYData;
    private  double [] signalXData;
    private  DoubleProperty dataMultiplier;
    private  DoubleProperty signalTimeShift_ms; // in milliseconds
    private  IntegerProperty signalIndex;
    private  BooleanProperty signalSelected;
    private  ObjectProperty<Color> signalColor;
    private  IntegerProperty fileOrdinalNumber;
    private  String nextShot;
    private  String portLabel;

    public ADCDataRecord(){

        this.dataMultiplier = new SimpleDoubleProperty(1);
        this.signalIndex = new SimpleIntegerProperty(-1);
        this.signalSelected = new SimpleBooleanProperty(false);
        this.signalColor = new SimpleObjectProperty<>(Color.BLACK);
        this.fileOrdinalNumber = new SimpleIntegerProperty(-1);
        this.signalTimeShift_ms = new SimpleDoubleProperty(0);
        this.signalYData = new double[0];
        this.signalXData = new double[0];
        this.adcChannelNumber = new SimpleStringProperty("-1");
        this.signalRate_kHz = new SimpleDoubleProperty(-1);
        this.device = "unknown";
        this.diagnostics = "unknown";
        this.unitOfMeasurement = "unknown";
        this.creationDate = "unknown";
        this.creationDateTime = "unknown";
        this.id =null;
        this.interCadreDelay_ms =0;
        this.recordLabel = new SimpleStringProperty("unknown");
        this.portLabel = "unknown";
        this.nextShot = "unknown";

    }



    public ADCDataRecord(String adcChannelNumber, int signalIndex, Boolean signalSelected,
                         Color signalColor, String signalLable, int fileOrdinalNumber,
                         double [] signalXdata, double [] signalYdata, double signalTimeShift_ms,
                         double dataMultiplier, double signalRate_kHz,
                         String creationDateTime, double interCadreDelay_ms, Map<String,String> config, String nextShot) {
        this.dataMultiplier = new SimpleDoubleProperty(dataMultiplier);
        this.signalIndex = new SimpleIntegerProperty(signalIndex);
        this.signalSelected = new SimpleBooleanProperty(signalSelected);
        this.signalColor = new SimpleObjectProperty<>(signalColor);
        this.recordLabel = new SimpleStringProperty(signalLable);
        this.fileOrdinalNumber = new SimpleIntegerProperty(fileOrdinalNumber);
        this.signalTimeShift_ms = new SimpleDoubleProperty(signalTimeShift_ms);
        this.signalYData = signalYdata;
        this.signalXData = signalXdata;
        this.adcChannelNumber = new SimpleStringProperty(adcChannelNumber);
        this.signalRate_kHz = new SimpleDoubleProperty(signalRate_kHz);
        this.creationDateTime = creationDateTime;
        this.interCadreDelay_ms = interCadreDelay_ms;
        this.device = config.get("device");
        this.diagnostics = config.get("diagnostics");
        this.unitOfMeasurement = config.get("unitOfMeasurements");
        this.creationDate = config.get("creationDate");
        this.portLabel = config.get("portLabel");
        this.nextShot = nextShot;

        StringBuilder sb = new StringBuilder();
        for (char c : (device + diagnostics +portLabel+adcChannelNumber+creationDate+ creationDateTime).toCharArray()) {
            sb.append((int) c);
        }
        this.id = sb.toString();
    }

    public String getNextShot() {
        return nextShot;
    }

    public void setNextShot(String nextShot) {
        this.nextShot = nextShot;
    }

    public String getPortLabel() {
        return portLabel;
    }

    public void setPortLabel(String portLabel) {
        this.portLabel = portLabel;
    }

    public double getInterCadreDelay_ms() {
        return interCadreDelay_ms;
    }

    public void setInterCadreDelay_ms(double interCadreDelay_ms) {
        this.interCadreDelay_ms = interCadreDelay_ms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnitOfMeasurement() {
        return unitOfMeasurement;
    }

    public void setUnitOfMeasurement(String unitOfMeasurement) {
        this.unitOfMeasurement = unitOfMeasurement;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getDiagnostics() {
        return diagnostics;
    }

    public void setDiagnostics(String diagnostics) {
        this.diagnostics = diagnostics;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(String creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

    public double getSignalRate_kHz() {
        return signalRate_kHz.get();
    }

    public DoubleProperty signalRate_kHzProperty() {
        return signalRate_kHz;
    }

    public void setSignalRate_kHz(double signalRate_kHz) {
        this.signalRate_kHz.set(signalRate_kHz);
    }


    public double getDataMultiplier() {
        return dataMultiplier.get();
    }
    public DoubleProperty dataMultiplierProperty() {
        return dataMultiplier;
    }
    public void setDataMultiplier(double dataMultiplier) {
        this.dataMultiplier.set(dataMultiplier);
    }

    public double[] getSignalYData() {
        return signalYData;
    }
    public double[] getSignalXData() {
        return signalXData;
    }

    public void setSignalYData(double[] signalYData) {
        this.signalYData = signalYData;
    }

    public void setSignalXData(double[] signalXData) {
        this.signalXData = signalXData;
    }


    public double getSignalTimeShift_ms() {
        return signalTimeShift_ms.get();
    }

    public DoubleProperty signalTimeShift_msProperty() {
        return signalTimeShift_ms;
    }

    public void setSignalTimeShift_ms(double signalTimeShift_ms) {
        this.signalTimeShift_ms.set(signalTimeShift_ms);
    }

    public String getAdcChannelNumber() {
        return adcChannelNumber.get();
    }

    public StringProperty adcChannelNumberProperty() {
        return adcChannelNumber;
    }

    public void setAdcChannelNumber(String adcChannelNumber) {
        this.adcChannelNumber.set(adcChannelNumber);
    }

    public int getSignalIndex() {
        return signalIndex.get();
    }

    public IntegerProperty signalIndexProperty() {
        return signalIndex;
    }

    public void setSignalIndex(int signalIndex) {
        this.signalIndex.set(signalIndex);
    }

    public int getFileOrdinalNumber() { return fileOrdinalNumber.get();
    }

    public IntegerProperty fileOrdinalNumberProperty() {
        return fileOrdinalNumber;
    }

    public void setFileOrdinalNumber(int fileOrdinalNumber) {
        this.fileOrdinalNumber.set(fileOrdinalNumber);
    }

    public Color getSignalColor() {
        return signalColor.get();
    }

    public ObjectProperty<Color> signalColorProperty() {
        return signalColor;
    }

    public void setSignalColor(Color signalColor) {
        this.signalColor.set(signalColor);
    }

    public boolean getSignalSelected() {
        return signalSelected.get();
    }

    public boolean isSignalSelected() {
        return signalSelected.get();
    }

    public BooleanProperty signalSelectedProperty() {
        return signalSelected;
    }

    public void setSignalSelected(boolean signalSelected) {
        this.signalSelected.set(signalSelected);
    }

    public String getRecordLabel() {
        return recordLabel.get();
    }

    public StringProperty recordLabelProperty() {
        return recordLabel;
    }

    public void setRecordLabel(String recordLabel) {
        this.recordLabel.set(recordLabel);
    }


    @Override
    public String toString() {
        return "ADCDataRecord{" + "recordLabel=" + recordLabel.get() + ", fileOrdinalNumber=" + fileOrdinalNumber.get() +", portLabel"+ portLabel + ", adcChannelNumber=" + adcChannelNumber.get() + '}';
    }

}
