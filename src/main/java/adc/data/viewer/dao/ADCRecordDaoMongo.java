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
import adc.data.viewer.ui.BaseController;
import adc.data.viewer.ui.MainApp;
import com.mongodb.*;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import static com.mongodb.client.model.Filters.*;

import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleConsumer;


public class ADCRecordDaoMongo implements ADCRecordDao  {

    private String dbName = MainApp.appPreferencesRootNode.get("ADCDataViewerDataBase", "ADCDataViewerDB");
    private String collectionName = MainApp.appPreferencesRootNode.get("ADCDataViewerDataBaseCollection", "adcDataRecords");
    private MongoClient mongoClient;
    private  MongoCollection<Document> adcRecordsCollection ;

    public ADCRecordDaoMongo() {
        adcRecordsCollection= getMongoDBDataBaseCollection();
    }

    private MongoCollection<Document> getMongoDBDataBaseCollection() {
        MongoCollection<Document> collection=null;
        ConnectionBuilder connectionBuilder = ConnectionBuilderFactory.getConnectionBuilder();
        if(connectionBuilder!=null) {

                if(mongoClient==null) mongoClient = (MongoClient) connectionBuilder.getConnection();

                if(mongoClient!=null) {
                    final MongoDatabase ADCDataViewer = mongoClient.getDatabase(dbName);
                    collection= ADCDataViewer.getCollection(collectionName);
                }
        }
        return  collection;
    }

    @Override
    public List<ADCDataRecord> findAll() throws ADCDataRecordsDaoException {
        List<ADCDataRecord> adcRecords = new ArrayList<>();

        try (MongoCursor<Document> cursor = adcRecordsCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                adcRecords.add(ADCDataRecordsMongoConverter.toADCDataRecord(doc));
            }
        }
        return adcRecords;

    }

    @Override
    public boolean insertOne(ADCDataRecord entity)throws ADCDataRecordsDaoException  {
        return false;

    }

    @Override
    public boolean deleteOne(ADCDataRecord entity)throws ADCDataRecordsDaoException  {
        return  false;
    }

    @Override
    public boolean  updateOne(ADCDataRecord entity)throws ADCDataRecordsDaoException  {
        return  false;
    }

    @Override
    public boolean  insertMany(List<ADCDataRecord> entityList)throws ADCDataRecordsDaoException  {


        for (ADCDataRecord nextRecord:entityList ) {
            Document doc = ADCDataRecordsMongoConverter.toDocument(nextRecord);
            if(adcRecordsCollection!=null&&adcRecordsCollection.find(new Document().append("_id",(String)doc.get("_id"))).first()==null)
            {
            adcRecordsCollection.insertOne(doc);
//                return true;
            }
        }
        return true;
    }

    @Override
    public List<String> findAllDevicesNames() throws ADCDataRecordsDaoException {



        if(adcRecordsCollection==null){
//            BaseController.alertMongoDBConnection();
            return null;
        }

        List<String> out=new ArrayList<>();

            AggregateIterable<Document> output = adcRecordsCollection.aggregate(Arrays.asList(
//                new Document("$unwind", "$views"),
//                new Document("$match", new Document("views.isActive", true)),
//                new Document("$limit", 200),
//                        .append("date", "$views.date"))
                    new Document("$group", new Document("_id","$device")),
                    new Document("$project", new Document("_id", 1)),
                    new Document("$sort", new Document("_id", 1))
             ));

            for (Document dbObject : output)

            {
                out.add(dbObject.getString("_id"));
//                System.out.println(out);
            }
            return out;

    }

    @Override
    public List<String> findAllDiagnosticsNames(String device) throws ADCDataRecordsDaoException {
        List<String> out=new ArrayList<>();
        AggregateIterable<Document> output = adcRecordsCollection.aggregate(Arrays.asList(
                new Document("$match", new Document("device", device)),
                new Document("$group", new Document("_id","$diagnostics")),
                new Document("$project", new Document("_id", 1)),
                new Document("$sort", new Document("_id", 1))
        ));

        for (Document dbObject : output)

        {
            out.add(dbObject.getString("_id"));
//            System.out.println(out);
        }
        return out;
    }

    @Override
    public List<String> findAllDates(String diagnostics) throws ADCDataRecordsDaoException  {
        List<String> out=new ArrayList<>();
        AggregateIterable<Document> output = adcRecordsCollection.aggregate(Arrays.asList(
                new Document("$match", new Document("diagnostics", diagnostics)),
                new Document("$group", new Document("_id","$creationDate")),
                new Document("$project", new Document("_id", 1)),
                new Document("$sort", new Document("_id", 1))
        ));

        for (Document dbObject : output)

        {
            out.add(dbObject.getString("_id"));
//            System.out.println(out);
        }
        return out;
    }

    @Override
    public List<String> findAllShots(String creationDate) throws ADCDataRecordsDaoException  {
        List<String> out=new ArrayList<>();
        AggregateIterable<Document> output = adcRecordsCollection.aggregate(Arrays.asList(
                new Document("$match", new Document("creationDate", creationDate)),
                new Document("$group", new Document("_id","$nextShot")),
                new Document("$project", new Document("_id", 1)),
                new Document("$sort", new Document("_id", 1))
        ));

        for (Document dbObject : output)

        {
            out.add(dbObject.getString("_id"));
//            System.out.println(out);
        }
        return out;
    }
    @Override
    public List<ADCDataRecord> findADCRecordsByCriterion(List<String> finalQueryBasic, List<String> finalQueryShots) throws  ADCDataRecordsDaoException {
        List<ADCDataRecord> out=new ArrayList<>();

        List<Bson> filters = new ArrayList<>();
        Bson filterBasic =  and(
                        eq("device",finalQueryBasic.get(0)),
                eq("diagnostics",finalQueryBasic.get(1)),
                eq("creationDate",finalQueryBasic.get(2))
                );
        filters.add(filterBasic);
        Bson filterShots;
        for (String str : finalQueryShots){
            filters.add(filterShots = eq("nextShot",str));
        }

        try (MongoCursor<Document> cursor = adcRecordsCollection.find(or(filters)).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                out.add(ADCDataRecordsMongoConverter.toADCDataRecord(doc));
            }
        }
        return out;
    }
}
