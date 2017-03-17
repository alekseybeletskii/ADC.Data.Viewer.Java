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
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Arrays;

/**
 * This is the class that defines ADC binary data format used in the freeware program Saturn
 * This format is named here "Saturn"
 */

class Saturn implements DataTypes {

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



            byte[] creatDateTimeByte = new byte[18];
        byte[] array5Byte = new byte[5];
        byte[] array15Byte = new byte[15];
        byte[] array11Byte = new byte[11];
        byte[] array32Byte = new byte[32];
        double samplingperiod;

        dataParams.setDeviceName("SATURNSDIAD12128H",fileIndex);
        parBuf.get(creatDateTimeByte);
        dataParams.setCreateDateTime(new String(creatDateTimeByte, Charset.forName("UTF-8")),fileIndex) ;
        parBuf.get(array5Byte);
        dataParams.setChannelsMax( Integer.parseInt(new String(array5Byte, Charset.forName("UTF-8")).trim()),fileIndex);
        parBuf.get(array5Byte);
        dataParams.setRealChannelsQuantity( Integer.parseInt(new String(array5Byte, Charset.forName("UTF-8")).trim()),fileIndex);
        parBuf.get(array5Byte);//the first channel
        parBuf.get(array5Byte);//the last channel
        parBuf.get(array15Byte);
        samplingperiod = Double.parseDouble(new String(array15Byte, Charset.forName("UTF-8")).trim());//microseconds
        parBuf.get(array11Byte);
        dataParams.setRealCadresQuantity(Long.parseLong(new String(array11Byte, Charset.forName("UTF-8")).trim()),fileIndex);
        dataParams.setRealSamplesQuantity(dataParams.getRealChannelsQuantity()[fileIndex]* dataParams.getRealCadresQuantity()[fileIndex],fileIndex);
        dataParams.setTotalTime(dataParams.getRealCadresQuantity()[fileIndex]*samplingperiod/1000000,fileIndex) ;//second
        dataParams.setAdcRate(1000/samplingperiod,fileIndex) ;// kHz
        dataParams.setInterCadreDelay(samplingperiod/1000,fileIndex) ;//millisecond
        dataParams.setChannelRate(1000/(samplingperiod* dataParams.getRealChannelsQuantity()[fileIndex]),fileIndex) ;//kHz
        Arrays.fill(array32Byte, (byte) 1);
        dataParams.setActiveAdcChannelArray(array32Byte, fileIndex  ) ;
        dataParams.setAdcChannelArray(array32Byte,fileIndex) ;
        Arrays.fill(array32Byte, (byte) 0);
        dataParams.setIsSignalArray(array32Byte,fileIndex) ;
        dataParams.setAdcGainArray(array32Byte,fileIndex) ;

        } catch (InvalidPathException e) {
            System.out.println("Path Error " + e);
            dataParams.setDataParamsValid(false);
        } catch (IOException e) {
            System.out.println("I/O Error " + e);
            dataParams.setDataParamsValid(false);
        }
    }

    public  void setData(int fileIndex, int signalIndex) {

        MappedByteBuffer dataBuf;
        double [] oneSignal = new double [(int) dataParams.getRealCadresQuantity()[fileIndex]];
        int [] chanAdcNum = new int [dataParams.getRealChannelsQuantity()[fileIndex]];
        int [] chanAdcGain = new int [dataParams.getRealChannelsQuantity()[fileIndex]];
        int activeCh=0, allCh =0;
        while (allCh< dataParams.getRealChannelsQuantity()[fileIndex])
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
            int jj = 0;

            while (jj < dataParams.getRealChannelsQuantity()[fileIndex])
            {
                int i = 0;
                while (i < dataParams.getRealCadresQuantity()[fileIndex]) {

                    oneSignal[i] = ((double)dataBuf.getShort ( 6000+ i* dataParams.getRealChannelsQuantity()[fileIndex]*2+jj*2) )/chanAdcGain[jj] ;

                    if  (-2046<= oneSignal[i] && oneSignal[i] <=-1)  //ADC от -1 до -2046 => -0.005...-5.12 V
                    {oneSignal[i] = (5.12/2048)*( oneSignal[i]- 1);}
                    else if (-4095<= oneSignal[i] && oneSignal[i] <=-2047) //ADC от -2047 до -4095  =>  +5.12....0.005 V
                    {oneSignal[i] = (5.12/2048)*(4095+ oneSignal[i]);}
                    else if (oneSignal[i]==0  || oneSignal[i]==-4096)                 //"0" V;
                    {oneSignal[i] = 0;}
                    i++;
                }
                signalIndex++;
                dataData.setSignals(oneSignal,signalIndex,fileIndex,chanAdcNum[jj]);
                jj++;
            }
        } catch (InvalidPathException e) {
            System.out.println("Path Error " + e);
        } catch (IOException e) {
            System.out.println("I/O Error " + e);
        }
    }

}
