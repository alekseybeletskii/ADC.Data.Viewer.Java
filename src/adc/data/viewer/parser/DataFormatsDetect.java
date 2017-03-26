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

import adc.data.viewer.ui.MainApp;

import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.nio.file.*;


/**
 * A helper class that infers data format of a current file
 */
class DataFormatsDetect {

    private int adcSignatureLength;
    private MainApp mainApp;

    DataFormatsDetect( MainApp mainApp) {
        this.mainApp = mainApp;
        adcSignatureLength = 20;//bytes from *.par file beginning
    }

    public  void detectFormat() {
        Path[] dataFilePath = mainApp.getDataParser().getDataFilePath();
        Path[] parFilePath = mainApp.getDataParser().getParFilePath();
        DataParams dataParams = mainApp.getDataParser().getDataParams();
        int fileIndex = 0;
        while (fileIndex < dataFilePath.length) {

            if (!Files.exists(parFilePath[fileIndex])) {
                parFilePath[fileIndex] = dataFilePath[fileIndex];
            }

            try (FileChannel fChan = (FileChannel) Files.newByteChannel(parFilePath[fileIndex])) {
                long fSize = fChan.size();
                MappedByteBuffer parBuf = fChan.map(FileChannel.MapMode.READ_ONLY, 0, fSize);
                parBuf.order(ByteOrder.LITTLE_ENDIAN);
                byte[] adcTypeByte = new byte[adcSignatureLength];
                parBuf.get(adcTypeByte);
                String dataFormat = new String(adcTypeByte, Charset.forName("UTF-8")).trim();
                parBuf.rewind();
                if (dataFormat.equals("2571090,1618190")) {
                    dataParams.setDataFormatStr("LGraph1", fileIndex);
                } else if (dataFormat.equals("2571090,1618190 A")) {
                    dataParams.setDataFormatStr("LGraph1_2008", fileIndex);
                } else if (dataFormat.charAt(2) == '/' & dataFormat.charAt(5) == '/') {
                    dataParams.setDataFormatStr("Saturn", fileIndex);
                } else if (dataFormat.equals("3571090,7859525")) {
                    dataParams.setDataFormatStr("LGraph2", fileIndex);
                } else {
                    dataParams.setDataFormatStr("UnknownADC", fileIndex);
                }
                dataParams.setDataParamsValid(true);

            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                dataParams.setDataParamsValid(false);
            } catch (IOException e) {
                System.out.println("I/O Error " + e);
                dataParams.setDataParamsValid(false);
            }
            fileIndex++;
        }

    }
}













