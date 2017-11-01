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

package adc.data.viewer.dao;

import adc.data.viewer.model.ADCDataRecord;
import javafx.scene.paint.Color;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ADCDataRecordsMongoConverter {


    // convert ADCDataRecord Object to MongoDB DBObject
    public static Document toDocument(ADCDataRecord adcRecord) {
//        List<Double> YasList = Double.as
        List<Double> Ylist = Arrays.stream(adcRecord.getSignalYData()).boxed().collect(Collectors.toList());

double hue = adcRecord.getSignalColor().getHue();
double satur = adcRecord.getSignalColor().getSaturation();
double bright = adcRecord.getSignalColor().getBrightness();

        return new Document()
                .append("device", adcRecord.getDevice())
                .append("diagnostics", adcRecord.getDiagnostics())
                .append("creationDate", adcRecord.getCreationDate())
                .append("creationDateTime", adcRecord.getCreationDateTime())
                .append("unitOfMeasurements", adcRecord.getUnitOfMeasurement())
                .append("signalRate_kHz", adcRecord.getSignalRate_kHz())
                .append("adcChannelNumber", adcRecord.getAdcChannelNumber())
                .append("recordLabel",adcRecord.getRecordLabel())
                .append("portLabel",adcRecord.getPortLabel())
                .append("nextShot",adcRecord.getNextShot())
                .append ("signalYData", Ylist)
                .append("signalColor",Arrays.asList(hue,satur,bright))
                .append("_id", adcRecord.getId())
                ;

    }

//     convert DBObject Object to ADCDataRecord
    public static ADCDataRecord toADCDataRecord(Document doc) {
        double [] colorArray = ((ArrayList<Double>)doc.get("signalColor")).stream().mapToDouble(Double::doubleValue).toArray();
        Color color = Color.hsb(colorArray[0],colorArray[1],colorArray[2]);
        ADCDataRecord adcRecord = new ADCDataRecord();
        adcRecord.setId(doc.getString("_id"));
        adcRecord.setDevice((String) doc.get("device"));
        adcRecord.setDiagnostics((String) doc.get("diagnostics"));
        adcRecord.setCreationDate((String) doc.get("creationDate"));
        adcRecord.setCreationDateTime((String) doc.get("creationDateTime"));
        adcRecord.setUnitOfMeasurement((String) doc.get("unitOfMeasurements"));
        adcRecord.setSignalRate_kHz(doc.getDouble("signalRate_kHz"));
        adcRecord.setAdcChannelNumber((String) doc.get("adcChannelNumber"));
        adcRecord.setPortLabel((String)doc.get("portLabel"));
        adcRecord.setRecordLabel((String)doc.get("recordLabel"));
        double[] Yarray = ((ArrayList<Double>)doc.get("signalYData")).stream().mapToDouble(Double::doubleValue).toArray();
        adcRecord.setSignalYData(Yarray);
        adcRecord.setSignalColor(color);

        return adcRecord;
    }

}
