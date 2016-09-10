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
class DataFormats {

    static void dataFormats(Path[] dataFilePath, Path[] parFilePath, DataParams dataParams, MainApp mainApp) {

        int fnum = 0;
        while (fnum < dataFilePath.length) {

            int adcTypeLen = 20;
            byte[] adcTypeByte;
            String dataFormat;
            MappedByteBuffer parBuf;

            if (!Files.exists(parFilePath[fnum])) {
                parFilePath[fnum] = dataFilePath[fnum];
            }
            if(dataFilePath[fnum].getFileName().toString().substring(dataFilePath[fnum].getFileName().toString().lastIndexOf('.')+1).toLowerCase().equals("txt"))
            {
                dataParams.setDataFormatStr("TextFile", fnum);
                mainApp.setTextFileParams(fnum, dataParams);
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
                    LGraph1.setParams(parBuf, fnum, dataParams);
                } else if (dataFormat.equals("2571090,1618190 A")) {
                    dataParams.setDataFormatStr("LGraph1_2008", fnum);
                    LGraph1_2008.setParam(parBuf, fnum, dataParams);
                } else if (dataFormat.charAt(2) == '/' & dataFormat.charAt(5) == '/') {
                    dataParams.setDataFormatStr("SATURN", fnum);
                    SATURN.setParam(parBuf, fnum, dataParams);
                } else if (dataFormat.equals("3571090,7859525")) {
                    dataParams.setDataFormatStr("LGraph2", fnum);
                    LGraph2.setParam(parBuf, fnum, dataParams);
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













