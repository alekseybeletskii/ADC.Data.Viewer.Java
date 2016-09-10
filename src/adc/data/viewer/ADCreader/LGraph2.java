package adc.data.viewer.ADCreader;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;

/**
 * This is the class that defines ADC binary data format used in the freeware program L-Graph II
 * This format is named here "LGraph2"
 */
class LGraph2 {


    static void setParam(MappedByteBuffer parBuf, int fnum, DataParams dataParams){

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
        dataParams.setDeviceName(new String(deviceNameByte, Charset.forName("UTF-8")).trim(),fnum);
        dataParams.setCreateDateTime(new String(creatDateTimeByte, Charset.forName("UTF-8")).trim(),fnum) ;
        dataParams.setChannelsMax(parBuf.getShort(63), fnum) ;
        dataParams.setRealChannelsQuantity(parBuf.getShort(65), fnum);
        dataParams.setRealCadresQuantity(parBuf.getInt(67), fnum) ;
        dataParams.setRealSamplesQuantity(parBuf.getInt(71), fnum) ;
        dataParams.setTotalTime(parBuf.getDouble(75),fnum) ;
        dataParams.setAdcRate(parBuf.getFloat(83),fnum) ;
        //In LGraph-2, "ADC rate" was somehow written equal to "channel rate",
        // but real value of "ADC rate" is  "channelRate" * "RealChannelsQuantity", or 1/("TotalTime"/"RealCadresQuantity")
        //So it is recalculated below:
        dataParams.setAdcRate(parBuf.getFloat(91)*parBuf.getShort(65),fnum) ;
        dataParams.setInterCadreDelay(parBuf.getFloat(87),fnum) ;
        dataParams.setChannelRate(parBuf.getFloat(91),fnum) ;
        parBuf.position(95);
        parBuf.get(arrayByte);
        dataParams.setActiveAdcChannelArray(arrayByte,fnum) ;
        parBuf.get(arrayByte);
        dataParams.setAdcChannelArray(arrayByte,fnum) ;
        parBuf.get(arrayByte);
        dataParams.setAdcGainArray(arrayByte,fnum) ;
        parBuf.get(arrayByte);
        dataParams.setIsSignalArray(arrayByte,fnum) ;
        dataParams.setDataFormat(parBuf.getInt(),fnum); // start= byte#223
        dataParams.setRealCadres64(parBuf.getLong(),fnum);
        DoubleBuffer dbuf = parBuf.asDoubleBuffer();
        dbuf.get(arrayDouble32);
        dataParams.setAdcScale(arrayDouble32,fnum);
        dbuf.get(arrayDouble32);
        dataParams.setAdcOffset(arrayDouble32,fnum);
        dbuf.get(arrayDouble1024);
        dataParams.setCalibrOffset(arrayDouble1024,fnum);
        dbuf.get(arrayDouble1024);
        dataParams.setCalibrScale(arrayDouble1024,fnum);

        dataParams.setSegments(parBuf.getInt(17131), fnum);  //start= byte#17131


    }
    static void setData(int fnum, int sigCount, DataData allData, DataParams dataParams) {

        MappedByteBuffer dataBuf;

        double [] oneSignal = new double [(int) dataParams.getRealCadresQuantity()[fnum]];

        int [] chanAdcNum = new int [dataParams.getRealChannelsQuantity()[fnum]];
        int [] chanAdcGain = new int [dataParams.getRealChannelsQuantity()[fnum]];

        int activeCh=0, allCh =0;

        while (allCh< dataParams.getActiveAdcChannelArray()[fnum].length)
        {
            if(dataParams.getActiveAdcChannelArray()[fnum][allCh]==1){
                chanAdcNum[activeCh] = allCh+1;

                switch (dataParams.getAdcGainArray()[fnum][allCh]){
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


        try (FileChannel fChan = (FileChannel) Files.newByteChannel(DataPaths.getDataFilePath()[fnum])) {
            long fSize = fChan.size();
            dataBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);
            dataBuf.order(ByteOrder.LITTLE_ENDIAN);
            int indexOfADCInputRange = 0; //index of a channel input range,
            //define what part of ADC calibration table is used
            //corresponding to input voltage range (+-3V, +-1V, +-0.3V in case of ADC e20-10)
            int jj = 0;
            while (jj < dataParams.getRealChannelsQuantity()[fnum])
            {
                int i = 0;
                while (i < dataParams.getRealCadresQuantity()[fnum]) {

                    switch (dataParams.getDeviceName()[fnum]) {
                        case "L783" : oneSignal[i] = (((double) dataBuf.getShort(i * dataParams.getRealChannelsQuantity()[fnum] * 2 + jj * 2) + dataParams.getAdcOffset()[fnum][jj + indexOfADCInputRange * dataParams.getRealChannelsQuantity()[fnum]]) * 5d / 2000d) / chanAdcGain[jj];
                            break;
                        case "E2010" :   oneSignal[i] = (((double) dataBuf.getShort(i * dataParams.getRealChannelsQuantity()[fnum] * 2 + jj * 2) + dataParams.getAdcOffset()[fnum][jj + indexOfADCInputRange * dataParams.getRealChannelsQuantity()[fnum]]) *0.000404) / chanAdcGain[jj];// 3d / 8000d) / chanAdcGain[jj];
                            break;
                        default: oneSignal[i] = (((double) dataBuf.getShort(i * dataParams.getRealChannelsQuantity()[fnum] * 2 + jj * 2) + dataParams.getAdcOffset()[fnum][jj + indexOfADCInputRange * dataParams.getRealChannelsQuantity()[fnum]]) * 1d / 1d) / 1d;
                    }
                    i++;
                }
                sigCount++;
                allData.setSignals(oneSignal,sigCount,fnum,chanAdcNum[jj]);
                jj++;
            }


        } catch (InvalidPathException e) {
            System.out.println("Path Error " + e);
        } catch (IOException e) {
            System.out.println("I/O Error " + e);
        }
    }
}
