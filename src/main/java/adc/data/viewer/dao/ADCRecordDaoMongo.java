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

import adc.data.viewer.dao.interfaces.ADCRecordDao;
import adc.data.viewer.dao.interfaces.ConnectionBuilder;
import adc.data.viewer.exeptions.ADCDataRecordsDaoException;
import adc.data.viewer.model.ADCDataRecord;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.xml.parsers.DocumentBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ADCRecordDaoMongo implements ADCRecordDao {
    public  ADCRecordDaoMongo() throws Exception {
    }
    private ConnectionBuilder connectionBuilder = ConnectionBuilderFactory.getConnectionBuilder();
    private final MongoClient mongoClient = getMongoClient();
    private final MongoDatabase ADCDataViewer =mongoClient.getDatabase("ADCDataViewerDB");
    private final MongoCollection<Document> adcRecordsCollection=ADCDataViewer.getCollection("adcDataRecords");

    private MongoClient getMongoClient () throws Exception {
        return (MongoClient) connectionBuilder.getConnection();
    }

    @Override
    public List<ADCDataRecord> findAll() throws ADCDataRecordsDaoException {

        List<ADCDataRecord> adcRecords = new ArrayList<ADCDataRecord>();

        try (MongoCursor<Document> cursor = adcRecordsCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                adcRecords.add(ADCDataRecordsMongoConverter.toADCDataRecord(doc));
            }
        }
        return adcRecords;

    }

    @Override
    public void insertOne(ADCDataRecord entity)throws ADCDataRecordsDaoException  {

    }

    @Override
    public void deleteOne(ADCDataRecord entity)throws ADCDataRecordsDaoException  {

    }

    @Override
    public void  updateOne(ADCDataRecord entity)throws ADCDataRecordsDaoException  {

    }

    @Override
    public void  insertMany(List<ADCDataRecord> entityList)throws ADCDataRecordsDaoException  {
        for (ADCDataRecord nextRecord:entityList ) {
//        ADCDataRecord nextRecord=entityList.get(0);
            Document doc = ADCDataRecordsMongoConverter.toDocument(nextRecord);
//            adcRecordsCollection.insertOne(new Document().append("test","test"));
            if(adcRecordsCollection.find(new Document().append("_id",(String)doc.get("_id"))).first()==null){
            adcRecordsCollection.insertOne(doc);}
        }

    }

    @Override
    public List<String> findAllDevicesNames() throws ADCDataRecordsDaoException {
        return null;
    }

    @Override
    public List<String> findAllDiagnosticsNames(String deviceName) throws ADCDataRecordsDaoException {
        return null;
    }

    @Override
    public List<String> findAllRecordsDates(String diagnosticsName) throws ADCDataRecordsDaoException  {
        return null;
    }

    @Override
    public List<String> findAllRecordsShots(String recordsDate) throws ADCDataRecordsDaoException  {
        return null;
    }

    @Override
    public List<ADCDataRecord> findADCRecordsByCriterion (String ...args) throws  ADCDataRecordsDaoException {
        return null;
    }
}
