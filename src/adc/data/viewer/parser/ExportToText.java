package adc.data.viewer.parser;

import adc.data.viewer.model.ADCDataRecords;
import adc.data.viewer.ui.MainApp;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.Canvas;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.round;


public class ExportToText {

    private int linesWrittenWithSaveProfile =0;
    private MainApp mainApp;
    private List<ADCDataRecords> ADCDataRecordsList = new ArrayList<>();


    public ExportToText (MainApp mainApp){
        this.mainApp = mainApp;
    }

    /**
     * The method saves avery signal as a separate text file to the "txt" subdirectory
     */
    public void saveAllToText(){
        int i=0;
        byte [] stringBytes;
        while (i< mainApp.getDataParser().getADCDataRecordsList().size()){

            Path outTxtPath = mainApp.getDataParser().getSignalPath()[i].resolve(Paths.get("txt"));

            try {
                Files.createDirectories(outTxtPath);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try ( FileChannel fWrite = (FileChannel)Files.newByteChannel(outTxtPath.resolve(mainApp.getDataParser().getADCDataRecordsList().get(i).getSignalLabel() +".txt"), StandardOpenOption.WRITE,StandardOpenOption.READ, StandardOpenOption.CREATE))
            {

                MappedByteBuffer wrBuf = fWrite.map(FileChannel.MapMode.READ_WRITE, 0, mainApp.getDataParser().getADCDataRecordsList().get(i).getSignalYData().length*16);
                int j=0;
                while(j< mainApp.getDataParser().getADCDataRecordsList().get(i).getSignalYData().length) {
                    stringBytes = String.format("%15.7f", mainApp.getDataParser().getADCDataRecordsList().get(i).getSignalYData()[j]).getBytes("UTF-8");
                    wrBuf.put(stringBytes);
                    wrBuf.put((byte)System.getProperty("line.separator").charAt(0));
                    j++;
                }
            } catch(InvalidPathException e) {
                System.out.println("Path Error " + e);
            } catch (IOException e) {
                System.out.println("I/O Error " + e);
            }
            i++;
        }
    }


    public void saveProfile (double profileTime) {
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
                    stringBytes = String.format("%16s, %16s, %16s, %16s, %16s", "x", "y", "channel@file", "profileTime", "ordinal").getBytes("UTF-8");
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


                    for (ADCDataRecords nextSignal : mainApp.getAdcDataRecords()) {
                        if (nextSignal.getSignalSelected()) {
                            double dt = 1.0 / (mainApp.getDataParser().getDataParams().getChannelRate()[nextSignal.getFileOrdinalNumber()]);
                            int profileIndex = (int) round(profileTime / dt);
                            double profileDataPoint = nextSignal.getSignalYData()[profileIndex];
                            stringBytes = String.format("%16d, %16.7f, %16s, %16.7f, %16d", linesWrittenWithSaveProfile, profileDataPoint, nextSignal.getSignalLabel(), profileTime, linesWrittenWithSaveProfile).getBytes("UTF-8");
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
            for (ADCDataRecords nextSignal : mainApp.getAdcDataRecords()){
              if(nextSignal.isSignalSelected())i++;
            }
            return i;
        }


public void takeSnapShot(ScrollPane plotter){

    Date nowTime = new Date();
    SnapshotParameters params = new SnapshotParameters();
    WritableImage writableImage = new WritableImage((int)plotter.getWidth(), (int)plotter.getHeight());
    plotter.snapshot(params,writableImage);
    Path path =  Paths.get(MainApp.appPreferencesRootNode.get("defaultWorkingDirectory", System.getProperty("user.home")),nowTime.toString()+"_snapshot.png");
File  file = path.toFile();




    try {
        ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
    } catch (IOException ex) {
        ex.printStackTrace();
    }
}



    }
