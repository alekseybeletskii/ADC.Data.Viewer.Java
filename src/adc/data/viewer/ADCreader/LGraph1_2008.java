package adc.data.viewer.ADCreader;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;


/**
 * This is the class that defines ADC binary data format used in the freeware program L-Graph I after 2008 year
 * This format is named here "LGraph1_2008"
 */
class LGraph1_2008 implements  DataFormat{
    private DataData dataData;

    public void setDataData(DataData dataData) {
        this.dataData = dataData;
    }


    public static void setParam(MappedByteBuffer parBuf, int fnum, DataParams dataParams) {

        byte[] deviceNameByte = new byte[17];
        byte[] creatDateTimeByte = new byte[26];
        byte[] arrayByte = new byte[32];


        parBuf.position(20);
        parBuf.get(deviceNameByte);
        parBuf.position(37);
        parBuf.get(creatDateTimeByte);
        parBuf.rewind();


        dataParams.setDeviceName(new String(deviceNameByte, Charset.forName("UTF-8")).trim(), fnum);
        dataParams.setCreateDateTime(new String(creatDateTimeByte, Charset.forName("UTF-8")).trim(), fnum);
        dataParams.setChannelsMax(parBuf.getShort(63), fnum);
        dataParams.setRealChannelsQuantity(parBuf.getShort(65), fnum);
        dataParams.setRealCadresQuantity(parBuf.getLong(67), fnum);
        dataParams.setRealSamplesQuantity(parBuf.getLong(75), fnum);
//            setTotalTime(parBuf.getDouble(83),fnum) ; 10-bytes-number??
        dataParams.setAdcRate(parBuf.getDouble(93), fnum);
        dataParams.setInterCadreDelay(parBuf.getDouble(101), fnum);
        dataParams.setChannelRate(parBuf.getDouble(109), fnum);
        parBuf.position(117);

        for (int i = 0; i < 32; i++) {
            arrayByte[i] = (byte) parBuf.getInt();
        }
        dataParams.setActiveAdcChannelArray(arrayByte, fnum);
        parBuf.get(arrayByte);
        dataParams.setAdcChannelArray(arrayByte, fnum);
        parBuf.get(arrayByte);
        dataParams.setAdcGainArray(arrayByte, fnum);
        parBuf.get(arrayByte);
        dataParams.setIsSignalArray(arrayByte, fnum);
    }

    public  void setData(int fnum, int sigCount, DataData dataData) {

        MappedByteBuffer dataBuf;
        DataParams dataParams = dataData.getDataParams();

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






        try (FileChannel fChan = (FileChannel) Files.newByteChannel(dataData.getDataPaths().getDataFilePath()[fnum])) {

            long fSize = fChan.size();
            dataBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);
            dataBuf.order(ByteOrder.LITTLE_ENDIAN);
            int jj = 0;

            while (jj < dataParams.getRealChannelsQuantity()[fnum])
            {
                int i = 0;
                while (i < dataParams.getRealCadresQuantity()[fnum]) {
                    oneSignal[i] = ((double)dataBuf.getShort ( i* dataParams.getRealChannelsQuantity()[fnum]*2+jj*2) * 5d / 2000d)/chanAdcGain[jj];
                    i++;
                }
                sigCount++;
                dataData.setSignals(oneSignal,sigCount,fnum,chanAdcNum[jj]);
                jj++;
            }


        } catch (InvalidPathException e) {
            System.out.println("Path Error " + e);
        } catch (IOException e) {
            System.out.println("I/O Error " + e);
        }

    }
}
