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

package adc.data.viewer.parser;

import adc.data.viewer.model.ADCDataRecord;
import adc.data.viewer.ui.MainApp;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;


public class ExportToTextAndSnapshot {

    private int linesWrittenWithSaveProfile =0;
    private MainApp mainApp;
    private List<ADCDataRecord> ADCDataRecordList = new ArrayList<>();


    public ExportToTextAndSnapshot(MainApp mainApp){
        this.mainApp = mainApp;
    }

    /**
     * The method saves avery signal as a separate text file to the "txt" subdirectory
     */
    public void saveAllToText() {
        byte[] stringBytes;
        for (ADCDataRecord nextData : mainApp.getAdcDataRecords()){
            if (nextData.getSignalSelected()) {

                Path outTxtPath = mainApp.getDataParser().getSignalPath()[nextData.getFileOrdinalNumber()].resolve(Paths.get("txt"));

                try {
                    Files.createDirectories(outTxtPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try (FileChannel fWrite = (FileChannel) Files.newByteChannel(outTxtPath.resolve(nextData.getRecordLabel() + ".txt"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE)) {

                    MappedByteBuffer wrBuf = fWrite.map(FileChannel.MapMode.READ_WRITE, 0, nextData.getSignalYData().length * 16 * 2);
                    int j = 0;
                    while (j < nextData.getSignalYData().length) {
                        stringBytes = String.format("%15.7f,%15.7f",
                                j * 1.0 / nextData.getSignalRate_kHz() + nextData.getSignalTimeShift_ms(),
                                nextData.getSignalYData()[j]).getBytes("UTF-8");
                        wrBuf.put(stringBytes);
                        wrBuf.put((byte) System.getProperty("line.separator").charAt(0));
                        j++;
                    }
                } catch (InvalidPathException e) {
                    System.out.println("Path Error " + e);
                } catch (IOException e) {
                    System.out.println("I/O Error " + e);
                }

            }
    }
    }


    public String saveProfile (double profileTime) {
        byte [] stringBytes;
        int totalSelected =howManySignalsIsSelected();
                Path outTxtPath = mainApp.getDataParser().getSignalPath()[0].resolve(Paths.get("txt","profile.txt"));
        try {
            Files.createDirectories(outTxtPath.getParent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        try ( FileChannel fWrite = (FileChannel)Files.newByteChannel(outTxtPath, StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE))
                {
                    getAmountOfLinesInFile(outTxtPath);
                    MappedByteBuffer wrBuf;
                    stringBytes = String.format("%16s, %16s, %30s, %16s, %16s", "x", "y", "channel@file", "profileTime", "ordinal").getBytes("UTF-8");
                    if(fWrite.size()==0){

                    int bufferSize = (totalSelected+1)*(stringBytes.length+1);
                    wrBuf = fWrite.map(FileChannel.MapMode.READ_WRITE, fWrite.size(), bufferSize);
                    wrBuf.put(stringBytes);
                    wrBuf.put((byte) System.getProperty("line.separator").charAt(0));
                        linesWrittenWithSaveProfile++;
                    }

                    else{
                    int bufferSize = (totalSelected)*(stringBytes.length+1);
                    wrBuf = fWrite.map(FileChannel.MapMode.READ_WRITE, fWrite.size(), bufferSize);
}


                    for (ADCDataRecord nextSignal : mainApp.getAdcDataRecords()) {
                        if (nextSignal.getSignalSelected()) {
                        String nextLabel = nextSignal.getRecordLabel().length()>30?nextSignal.getRecordLabel().substring(0,29):nextSignal.getRecordLabel();
                            double dt = 1.0 / (mainApp.getDataParser().getDataParams().getChannelRate()[nextSignal.getFileOrdinalNumber()]);
                            int profileIndex = (int) round(profileTime / dt);
                            double profileDataPoint = nextSignal.getSignalYData()[profileIndex];
                            stringBytes = String.format("%16d, %16.7f, %30s, %16.7f, %16d", linesWrittenWithSaveProfile, profileDataPoint, nextLabel, profileTime, linesWrittenWithSaveProfile).getBytes("UTF-8");
                            wrBuf.put(stringBytes);
                            wrBuf.put((byte) System.getProperty("line.separator").charAt(0));
                            linesWrittenWithSaveProfile++;
                        }
                    }

                } catch(InvalidPathException e) {
                    System.out.println("Path Error " + e);
                } catch (IOException e) {
                    System.out.println("I/O Error " + e);
                }
        return outTxtPath.toString();

        }

    private void getAmountOfLinesInFile(Path aFile)  {
        linesWrittenWithSaveProfile=0;
        Charset charset = Charset.forName("UTF-8");
        try (BufferedReader reader = Files.newBufferedReader(aFile, charset)) {
            while ((reader.readLine()) != null) {
                linesWrittenWithSaveProfile++;
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }


    private int howManySignalsIsSelected(){
            int i = 0;
            for (ADCDataRecord nextSignal : mainApp.getAdcDataRecords()){
              if(nextSignal.isSignalSelected())i++;
            }
            return i;
        }


public String takeSnapShot(ScrollPane plotter){
    String nowTimeString = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd'.'MM'.'yyyy'_'H'.'mm'.'ss"));
    SnapshotParameters params = new SnapshotParameters();
    WritableImage writableImage = new WritableImage((int)plotter.getWidth(), (int)plotter.getHeight());
    plotter.snapshot(params,writableImage);
    Path outImagePath =  Paths.get(MainApp.appPreferencesRootNode.get("defaultWorkingDirectory", System.getProperty("user.home")),nowTimeString+"_snapshot.png");
File  file = outImagePath.toFile();
    try {
        ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    return outImagePath.toString();
}



    }
