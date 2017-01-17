/*******************************************************************************
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
 ******************************************************************************/

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













