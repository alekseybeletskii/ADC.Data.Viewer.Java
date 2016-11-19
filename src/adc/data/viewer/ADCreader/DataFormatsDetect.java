package adc.data.viewer.ADCreader;

import adc.data.viewer.MainApp;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.file.*;


/**
 * A helper class that infers data format of a current file
 */
class DataFormatsDetect {

    DataFormatsDetect(DataParser dataData, MainApp mainApp)

//     private void dataFormats(DataParser dataData, MainApp mainApp)

    {
        Path[] dataFilePath = dataData.getDataPaths().getDataFilePath();
        Path[] parFilePath = dataData.getDataPaths().getParFilePath();
        DataParams dataParams = dataData.getDataParams();

        int fnum = 0;

        int adcTypeLen = 20;
        byte[] adcTypeByte;
        String dataFormat;
        MappedByteBuffer parBuf;

        while (fnum < dataFilePath.length) {

            if (!Files.exists(parFilePath[fnum])) {
                parFilePath[fnum] = dataFilePath[fnum];
            }
            if(dataFilePath[fnum].getFileName().toString().substring(dataFilePath[fnum].getFileName().toString().lastIndexOf('.')+1).toLowerCase().equals("txt"))
            {
                dataParams.setDataFormatStr("TextFile", fnum);
                mainApp.setTextFileParams(fnum);
                fnum++;
                continue;
            }

            try (FileChannel fChan = (FileChannel) Files.newByteChannel(parFilePath[fnum])) {

                long fSize = fChan.size();

                parBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);

                parBuf.order(ByteOrder.LITTLE_ENDIAN);

                adcTypeByte = new byte[adcTypeLen];

                parBuf.get(adcTypeByte);

                dataFormat = new String(adcTypeByte, Charset.forName("UTF-8")).trim();

                parBuf.rewind();

                if (dataFormat.equals("2571090,1618190")) {
                    dataParams.setDataFormatStr("LGraph1", fnum);
//                    DataTypesList.LGRAPH1.getDataType().setParam(parBuf, fnum);
                } else if (dataFormat.equals("2571090,1618190 A")) {
                    dataParams.setDataFormatStr("LGraph1_2008", fnum);
//                    DataTypesList.LGRAPH1_2008.getDataType().setParam(parBuf, fnum);
                } else if (dataFormat.charAt(2) == '/' & dataFormat.charAt(5) == '/') {
                    dataParams.setDataFormatStr("Saturn", fnum);
//                    DataTypesList.SATURN.getDataType().setParam(parBuf, fnum);
                } else if (dataFormat.equals("3571090,7859525")) {
                    dataParams.setDataFormatStr("LGraph2", fnum);
//                    DataTypesList.LGRAPH2.getDataType().setParam(parBuf, fnum);
                }

                dataParams.setDataParamsValid(true);

            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                dataParams.setDataParamsValid(false);
            } catch (IOException e) {
                System.out.println("I/O Error " + e);
                dataParams.setDataParamsValid(false);
            }
            fnum++;
        }
    }
}













