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


import adc.data.viewer.dao.interfaces.ConnectionBuilder;
import adc.data.viewer.exeptions.ADCDataRecordsDaoException;
import adc.data.viewer.ui.BaseController;
import adc.data.viewer.ui.MainApp;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientException;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import javax.naming.ldap.BasicControl;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Map;

public class ConnectionBuilderMongo implements ConnectionBuilder
{
    private final String mongoURI;


    ConnectionBuilderMongo() {

        final String hostname = MainApp.appPreferencesRootNode.get("host", "localhost");
        final String hostport = MainApp.appPreferencesRootNode.get("hostport", "27017");
        final String dblogin = MainApp.appPreferencesRootNode.get("dbLogin", "");
        final String dbpassword = MainApp.appPreferencesRootNode.get("dbPassword", "");
        final String dbName = MainApp.appPreferencesRootNode.get("ADCDataViewerDataBase", "ADCDataViewerDB");
//        final String dblogin = MainApp.appPreferencesRootNode.get("dblogin", "admin");
//        final String dbpassword = MainApp.appPreferencesRootNode.get("dbpassword", "admin");
        String uriLogPas ="mongodb://" + dblogin + ":" + dbpassword + "@" + hostname + ":" + hostport + "/" + dbName;;
        String uriNoLogPas ="mongodb://" +  hostname + ":" + hostport ;
        mongoURI = dblogin.equals("")&&dbpassword.equals("")?uriNoLogPas:uriLogPas;
//      mongoURI = "mongodb://" + dblogin + ":" + dbpassword + "@" + hostname + ":" + hostport + "/" + dbName;

    }

//    standard URI connection scheme:
//    mongodb://[username:password@]host1[:port1][,host2[:port2],...[,hostN[:portN]]][/[database][?options]]
//    mongodb://sysop:moon@localhost:27017/records
    @Override
    public MongoClient getConnection()  {


        try  {

            MongoClient mongoClient = new MongoClient(new MongoClientURI(mongoURI));
                    mongoClient.getAddress();
                    return mongoClient;
                }
        catch (MongoException | IllegalArgumentException me) {
    System.out.println("Connection to MongoDB failed");
//    BaseController.alertMongoDBConnectionError();
    return null;
        }
}
}
