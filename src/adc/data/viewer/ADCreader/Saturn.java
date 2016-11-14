package adc.data.viewer.ADCreader;

import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.util.Arrays;

/**
 * This is the class that defines ADC binary data format used in the freeware program Saturn
 * This format is named here "Saturn"
 */

class Saturn implements  DataFormat {
    private DataData dataData;

    public void setDataData(DataData dataData) {
        this.dataData = dataData;
    }

    public static void setParam(MappedByteBuffer parBuf, int fnum, DataParams dataParams){


        byte[] creatDateTimeByte = new byte[18];
        byte[] array5Byte = new byte[5];
        byte[] array15Byte = new byte[15];
        byte[] array11Byte = new byte[11];
        byte[] array32Byte = new byte[32];
        double samplingperiod;

        dataParams.setDeviceName("SATURNSDIAD12128H",fnum);
        parBuf.get(creatDateTimeByte);
        dataParams.setCreateDateTime(new String(creatDateTimeByte, Charset.forName("UTF-8")),fnum) ;
        parBuf.get(array5Byte);
        dataParams.setChannelsMax( Integer.parseInt(new String(array5Byte, Charset.forName("UTF-8")).trim()),fnum);
        parBuf.get(array5Byte);
        dataParams.setRealChannelsQuantity( Integer.parseInt(new String(array5Byte, Charset.forName("UTF-8")).trim()),fnum);
        parBuf.get(array5Byte);//the first channel
        parBuf.get(array5Byte);//the last channel
        parBuf.get(array15Byte);
        samplingperiod = Double.parseDouble(new String(array15Byte, Charset.forName("UTF-8")).trim());//microseconds
        parBuf.get(array11Byte);
        dataParams.setRealCadresQuantity(Long.parseLong(new String(array11Byte, Charset.forName("UTF-8")).trim()),fnum);
        dataParams.setRealSamplesQuantity(dataParams.getRealChannelsQuantity()[fnum]* dataParams.getRealCadresQuantity()[fnum],fnum);
        dataParams.setTotalTime(dataParams.getRealCadresQuantity()[fnum]*samplingperiod/1000000,fnum) ;//second
        dataParams.setAdcRate(1000/samplingperiod,fnum) ;// kHz
        dataParams.setInterCadreDelay(samplingperiod/1000,fnum) ;//millisecond
        dataParams.setChannelRate(1000/(samplingperiod* dataParams.getRealChannelsQuantity()[fnum]),fnum) ;//kHz
        Arrays.fill(array32Byte, (byte) 1);
        dataParams.setActiveAdcChannelArray(array32Byte, fnum  ) ;
        dataParams.setAdcChannelArray(array32Byte,fnum) ;
        Arrays.fill(array32Byte, (byte) 0);
        dataParams.setIsSignalArray(array32Byte,fnum) ;
        dataParams.setAdcGainArray(array32Byte,fnum) ;
    }

    public  void setData(int fnum, int sigCount, DataData dataData) {

        MappedByteBuffer dataBuf;
        DataParams dataParams = dataData.getDataParams();
        double [] oneSignal = new double [(int) dataParams.getRealCadresQuantity()[fnum]];
        int [] chanAdcNum = new int [dataParams.getRealChannelsQuantity()[fnum]];
        int [] chanAdcGain = new int [dataParams.getRealChannelsQuantity()[fnum]];
        int activeCh=0, allCh =0;
        while (allCh< dataParams.getRealChannelsQuantity()[fnum])
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

                    oneSignal[i] = ((double)dataBuf.getShort ( 6000+ i* dataParams.getRealChannelsQuantity()[fnum]*2+jj*2) )/chanAdcGain[jj] ;

                    if  (-2046<= oneSignal[i] && oneSignal[i] <=-1)  //ADC от -1 до -2046 => -0.005...-5.12 V
                    {oneSignal[i] = (5.12/2048)*( oneSignal[i]- 1);}
                    else if (-4095<= oneSignal[i] && oneSignal[i] <=-2047) //ADC от -2047 до -4095  =>  +5.12....0.005 V
                    {oneSignal[i] = (5.12/2048)*(4095+ oneSignal[i]);}
                    else if (oneSignal[i]==0  || oneSignal[i]==-4096)                 //"0" V;
                    {oneSignal[i] = 0;}
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
