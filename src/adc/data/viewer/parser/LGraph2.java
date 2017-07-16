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

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

/**
 * This is the class that defines ADC binary data format used in the freeware program L-Graph II
 * This format is named here "LGraph2"
 */
class LGraph2  implements DataTypes {

//    LGraph2 (DataParser dataData){}

    private DataParser dataData;
    private DataParams dataParams;
    private Path[] dataFilePath;
    private Path[] parFilePath;

    public void setDataParser(DataParser dataData) {

        this.dataData = dataData;
        this.dataParams = dataData.getDataParams();
        this.dataFilePath = dataData.getDataFilePath();
        this.parFilePath = dataData.getParFilePath();
    }

    public void setParam(int fileIndex) {

        if (!Files.exists(parFilePath[fileIndex])) {
            parFilePath[fileIndex] = dataFilePath[fileIndex];
        }

        try (FileChannel fChan = (FileChannel) Files.newByteChannel(parFilePath[fileIndex])) {

            long fSize = fChan.size();
            MappedByteBuffer parBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);
            parBuf.order(ByteOrder.LITTLE_ENDIAN);


            byte[] deviceNameByte = new byte[17];
        byte[] creatDateTimeByte = new byte[26];
        byte[] arrayByte = new byte[32];
        double[] arrayDouble32 = new double[32];
        double[] arrayDouble1024 = new double[1024];

        parBuf.position(20);
        parBuf.get(deviceNameByte);
        parBuf.position(37);
        parBuf.get(creatDateTimeByte);
        parBuf.rewind();
        dataParams.setDeviceName(new String(deviceNameByte, Charset.forName("UTF-8")).trim(),fileIndex);
        dataParams.setCreateDateTime(new String(creatDateTimeByte, Charset.forName("UTF-8")).trim(),fileIndex) ;
        dataParams.setChannelsMax(parBuf.getShort(63), fileIndex) ;
        dataParams.setRealChannelsQuantity(parBuf.getShort(65), fileIndex);
        dataParams.setRealCadresQuantity(parBuf.getInt(67), fileIndex) ;
        dataParams.setRealSamplesQuantity(parBuf.getInt(71), fileIndex) ;
        dataParams.setTotalTime(parBuf.getDouble(75),fileIndex) ;
        dataParams.setAdcRate(parBuf.getFloat(83),fileIndex) ;
        //In LGraph-2, "ADC rate" was somehow written equal to "channel rate",
        // but real value of "ADC rate" is  "channelRate" * "RealChannelsQuantity", or 1/("TotalTime"/"RealCadresQuantity")
        //So it is recalculated below:
        dataParams.setAdcRate(parBuf.getFloat(91)*parBuf.getShort(65),fileIndex) ;
        dataParams.setInterCadreDelay(parBuf.getFloat(87),fileIndex) ;
        dataParams.setChannelRate(parBuf.getFloat(91),fileIndex) ;
        parBuf.position(95);
        parBuf.get(arrayByte);
        dataParams.setActiveAdcChannelArray(arrayByte,fileIndex) ;
        parBuf.get(arrayByte);
        dataParams.setAdcChannelArray(arrayByte,fileIndex) ;
        parBuf.get(arrayByte);
        dataParams.setAdcGainArray(arrayByte,fileIndex) ;
        parBuf.get(arrayByte);
        dataParams.setIsSignalArray(arrayByte,fileIndex) ;
        dataParams.setDataFormat(parBuf.getInt(),fileIndex); // start= byte#223
        dataParams.setRealCadres64(parBuf.getLong(),fileIndex);
        DoubleBuffer dbuf = parBuf.asDoubleBuffer();
        dbuf.get(arrayDouble32);
        dataParams.setAdcScale(arrayDouble32,fileIndex);
        dbuf.get(arrayDouble32);
        dataParams.setAdcOffset(arrayDouble32,fileIndex);
        dbuf.get(arrayDouble1024);
        dataParams.setCalibrOffset(arrayDouble1024,fileIndex);
        dbuf.get(arrayDouble1024);
        dataParams.setCalibrScale(arrayDouble1024,fileIndex);

        dataParams.setSegments(parBuf.getInt(17131), fileIndex);  //start= byte#17131


        } catch (InvalidPathException e) {
            System.out.println("Path Error " + e);
            dataParams.setDataParamsValid(false);
        } catch (IOException e) {
            System.out.println("I/O Error " + e);
            dataParams.setDataParamsValid(false);
        }


    }

    public void setData(int fileIndex, int signalIndex) {

        MappedByteBuffer dataBuf;
        double [] oneSignal = new double [(int) dataParams.getRealCadresQuantity()[fileIndex]];
        int [] chanAdcNum = new int [dataParams.getRealChannelsQuantity()[fileIndex]];
        int [] chanAdcGain = new int [dataParams.getRealChannelsQuantity()[fileIndex]];
        int activeCh=0, allCh =0;
        while (allCh< dataParams.getActiveAdcChannelArray()[fileIndex].length)
        {
            if(dataParams.getActiveAdcChannelArray()[fileIndex][allCh]==1){
                chanAdcNum[activeCh] = allCh+1;
                switch (dataParams.getAdcGainArray()[fileIndex][allCh]){
                    case 0: chanAdcGain[activeCh]=1;
                        break;
                    case 1: chanAdcGain[activeCh]=2;
                        break;
                    case 2: chanAdcGain[activeCh]=4;
                        break;
                    case 3: chanAdcGain[activeCh]=8;
                        break;
                    default : chanAdcGain[activeCh]=1;
                }
                activeCh++;}
            allCh++;
        }
        try (FileChannel fChan = (FileChannel) Files.newByteChannel(dataData.getDataFilePath()[fileIndex])) {
            long fSize = fChan.size();
            dataBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);
            dataBuf.order(ByteOrder.LITTLE_ENDIAN);
            int indexOfADCInputRange = 0; //index of a channel input range,
            //define what part of ADC calibration table is used
            //corresponding to input voltage range (+-3V, +-1V, +-0.3V in case of ADC e20-10)
            int jj = 0;
            while (jj < dataParams.getRealChannelsQuantity()[fileIndex])
            {
                int i = 0;
                while (i < dataParams.getRealCadresQuantity()[fileIndex]) {

                    switch (dataParams.getDeviceName()[fileIndex]) {
                        case "L783" : oneSignal[i] = (((double) dataBuf.getShort(i * dataParams.getRealChannelsQuantity()[fileIndex] * 2 + jj * 2) + dataParams.getAdcOffset()[fileIndex][jj + indexOfADCInputRange * dataParams.getRealChannelsQuantity()[fileIndex]]) * 5d / 2000d) / chanAdcGain[jj];
                            break;
                        case "E2010" :   oneSignal[i] = (((double) dataBuf.getShort(i * dataParams.getRealChannelsQuantity()[fileIndex] * 2 + jj * 2) + dataParams.getAdcOffset()[fileIndex][jj + indexOfADCInputRange * dataParams.getRealChannelsQuantity()[fileIndex]]) *0.000404) / chanAdcGain[jj];// 3d / 8000d) / chanAdcGain[jj];
                            break;
                        default: oneSignal[i] = (((double) dataBuf.getShort(i * dataParams.getRealChannelsQuantity()[fileIndex] * 2 + jj * 2) + dataParams.getAdcOffset()[fileIndex][jj + indexOfADCInputRange * dataParams.getRealChannelsQuantity()[fileIndex]]) * 1d / 1d) / 1d;
                    }
                    i++;
                }
                signalIndex++;
                dataData.PutADCDataRecords(oneSignal,signalIndex,fileIndex,chanAdcNum[jj]);
                jj++;
            }


        } catch (InvalidPathException e) {
            System.out.println("Path Error " + e);
        } catch (IOException e) {
            System.out.println("I/O Error " + e);
        }
    }
}
