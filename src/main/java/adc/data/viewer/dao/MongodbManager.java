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
import adc.data.viewer.dao.interfaces.Dao;
import adc.data.viewer.exeptions.ADCDataRecordsDaoException;
import adc.data.viewer.model.ADCDataRecord;

import java.util.ArrayList;
import java.util.List;

public class MongodbManager {


    private static ADCRecordDao dao;

//    public MongodbManager() {
//         dao = ADCRecordDaoFactory.getADCRecordDao();
//
//    }
    public static void setUpDao(){
        dao =ADCRecordDaoFactory.getADCRecordDao();
    }

    public static boolean  insertADCDataRecordsList (List<ADCDataRecord> records) throws ADCDataRecordsDaoException {

        return dao != null && dao.insertMany(records);
    }

    public static List <ADCDataRecord> findAllRecords () throws ADCDataRecordsDaoException{
        if (dao != null) {
            return dao.findAll();
        }

        return new ArrayList<>();

    }

    public  static  List <String> findAllDevicesNames() throws ADCDataRecordsDaoException {
        if (dao != null) {
            return dao.findAllDevicesNames();
        }
        return null;
    }
    public  static  List <String> findAllDiagnosticsNames(String devicename) throws ADCDataRecordsDaoException {
        if (dao != null) {
            return dao.findAllDiagnosticsNames( devicename);
        }
        return new ArrayList<>();
    }

    public  static  List <String> findAllDates(String diagnostics) throws ADCDataRecordsDaoException {
        if (dao != null) {
            return dao.findAllDates( diagnostics);
        }
        return new ArrayList<>();
    }

    public  static  List <String> findAllShots(String date) throws ADCDataRecordsDaoException {
        if (dao != null) {
            return dao.findAllShots( date);
        }
        return new ArrayList<>();
    }



    public  static  List <ADCDataRecord> findADCRecordsByCriterion(List<String> finalQueryBasic, List<String> finalQueryShots) throws ADCDataRecordsDaoException {
        if (dao != null) {
            return dao.findADCRecordsByCriterion( finalQueryBasic,finalQueryShots);
        }
        return new ArrayList<>();
    }
}
