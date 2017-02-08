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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * A helper class to process file paths
 */
public class DataPaths {

    DataPaths (File[] filesListToProcess){
        makePaths(filesListToProcess);
    }
    DataPaths (String[] filesListToProcess){
        makePaths(filesListToProcess);
    }
    DataPaths (String filesListToProcess){
        makePaths(filesListToProcess);
    }

    private  Path[] dataFilePath;
    private  Path[] parFilePath;
    private  String[] fileName;

    Path[] getParFilePath() {
        return parFilePath;
    }
    public Path[] getDataFilePath() {
        return dataFilePath;
    }
    String[] getFileName() {
        return fileName;
    }

    /**
     *
     * @param dataFileStr
     * load the list of files from String[] array
     */
    private void makePaths(String[] dataFileStr) {

        dataFilePath = new Path[dataFileStr.length];
        parFilePath = new Path[dataFileStr.length];
        fileName = new String[dataFileStr.length];

        parseFileList(dataFileStr);
    }

    /**
     *
     * @param filesListToProcess
     * load the list from external text file
     */
    private void makePaths(String filesListToProcess) {

        List<String> stringList = new ArrayList<>();

        String line;

        try (BufferedReader readFilesToProcess = Files.newBufferedReader(Paths.get(filesListToProcess), Charset.forName("US-ASCII"))) {
            int i=0;
            while ((line = readFilesToProcess.readLine()) != null) {
                stringList.add(line.trim());
                i++;
            }
            String[] dataFileString = stringList.toArray(new String[stringList.size()]);

            dataFilePath = new Path[dataFileString.length];
            parFilePath = new Path[dataFileString.length];
            fileName = new String[dataFileString.length];

            parseFileList(dataFileString);

        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

    }

    /**
     *
     * @param dataFileStr
     * load the list from File[] array
     */
    private void makePaths(File[] dataFileStr) {
        int count = 0;
        dataFilePath = new Path[dataFileStr.length];
        parFilePath = new Path[dataFileStr.length];
        fileName = new String[dataFileStr.length];

        for (File fileStr : dataFileStr) {
            try {
                dataFilePath[count] = Paths.get(fileStr.getPath());
                fileName[count] = dataFilePath[count].getFileName().toString();
                fileName[count] = fileName[count].substring(0, fileName[count].lastIndexOf('.'));
                parFilePath[count] = dataFilePath[count].getParent().resolve(fileName[count] + ".par");
            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                return;
            }
            count++;
        }

    }

    private void parseFileList(String[] dataFileStr) {
        int count = 0;
        for (String fileStr : dataFileStr) {
            try {
                dataFilePath[count] = Paths.get(fileStr);
                fileName[count] = dataFilePath[count].getFileName().toString();
                fileName[count] = fileName[count].substring(0, fileName[count].lastIndexOf('.'));
                parFilePath[count] = dataFilePath[count].getParent().resolve(fileName[count] + ".par");
            } catch (InvalidPathException e) {
                System.out.println("Path Error " + e);
                return;
            }
            count++;
        }
    }

}
