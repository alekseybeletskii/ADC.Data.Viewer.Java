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

/**
 * This class stores ADC parameters of all extracted signals 
 */
public class DataParams  {

    private boolean dataParamsValid;
    private  String [] dataFormatStr;
    private String [] deviceName;
    private  String [] CreateDateTime;
    private  int [] ChannelsMax;
    private  int [] RealChannelsQuantity;
    private  long [] RealCadresQuantity;
    private  long [] RealSamplesQuantity;
    private  double [] TotalTime;
    private  double [] AdcRate;
    private  double [] InterCadreDelay;
    private  double [] ChannelRate;
    private  byte [] [] ActiveAdcChannelArray;
    private  byte [] [] AdcChannelArray;
    private  byte [] [] AdcGainArray;
    private  byte [] [] IsSignalArray;
    //LGraph2-specific:
    private  int [] DataFormat;
    private  long [] RealCadres64 ;
    private  double [][] AdcScale;
    private  double [][] AdcOffset;
    private  double [][] CalibrScale;
    private  double [][] CalibrOffset;
    private  int [] Segments;
    //textFiles-specific:
    public  int[] channelNum;
    public  int[] yColumnNum;
    public  int[] xColumnNum;
    public  int[] amountOfHeaderLines;
    public  String []columnSeparator;

    DataParams(int amountOfFiles) {
        this.dataParamsValid =false;
        this.dataFormatStr = new String[amountOfFiles];
        this.deviceName = new String[amountOfFiles];
        this.CreateDateTime = new String[amountOfFiles];
        this.ChannelsMax = new int[amountOfFiles];
        this.RealChannelsQuantity= new int[amountOfFiles];
        this.RealCadresQuantity= new long[amountOfFiles];
        this.RealSamplesQuantity= new long[amountOfFiles];
        this.TotalTime= new double[amountOfFiles];
        this.AdcRate= new double[amountOfFiles];
        this.InterCadreDelay= new double[amountOfFiles];
        this.ChannelRate= new double[amountOfFiles];
        this.ActiveAdcChannelArray = new byte[amountOfFiles][32];
        this.AdcChannelArray= new byte[amountOfFiles] [32];
        this.AdcGainArray= new byte[amountOfFiles] [32];
        this.IsSignalArray= new byte[amountOfFiles] [32];
        //LGraph2-specific:
        this.DataFormat = new int[amountOfFiles];
        this.RealCadres64 = new long[amountOfFiles];
        this.AdcScale = new double[amountOfFiles][32];
        this.AdcOffset = new double[amountOfFiles][32];
        this.CalibrScale = new double[amountOfFiles][1024];
        this.CalibrOffset = new double[amountOfFiles][1024];
        this.Segments = new int[amountOfFiles];
        //txtFiles-specifc:
        this.channelNum= new int[amountOfFiles];
        this.yColumnNum= new int[amountOfFiles];
        this.xColumnNum= new int[amountOfFiles];
        this.amountOfHeaderLines= new int[amountOfFiles];
        this.columnSeparator= new String[amountOfFiles];
    }


    public int[] getChannelNum() {
        return channelNum;
    }
    public int[] getyColumnNum() {
        return yColumnNum;
    }
    public int[] getxColumnNum() {
        return xColumnNum;
    }
    public int[] getAmountOfHeaderLines() {
        return amountOfHeaderLines;
    }
    public String[] getColumnSeparator() {
        return columnSeparator;
    }


    public void setChannelNum(int channelNum, int fileIndex) {
        this.channelNum[fileIndex] = channelNum;
    }

    public void setyColumnNum(int yColumnNum, int fileIndex) {
        this.yColumnNum[fileIndex] = yColumnNum;
    }

    public void setxColumnNum(int xColumnNum, int fileIndex) {
        this.xColumnNum[fileIndex] = xColumnNum;
    }

    public void setAmountOfHeaderLines(int amountOfHeaderLines, int fileIndex) {
        this.amountOfHeaderLines[fileIndex] = amountOfHeaderLines;
    }

    public void setColumnSeparator(String columnSeparator, int fileIndex) {
        this.columnSeparator[fileIndex] = columnSeparator;
    }



    public boolean isDataParamsValid() {
        return dataParamsValid;
    }
    public String[] getDataFormatStr() {
        return dataFormatStr;
    }
    public    String[] getDeviceName() {
        return deviceName;
    }
    public   String[] getCreateDateTime() {
        return CreateDateTime;
    }
    public    int[] getChannelsMax() {
        return ChannelsMax;
    }
    public   int[] getRealChannelsQuantity() {
        return RealChannelsQuantity;
    }
    public   long[] getRealCadresQuantity() {
        return RealCadresQuantity;
    }
    public    long[] getRealSamplesQuantity() {
        return RealSamplesQuantity;
    }
    public double[] getTotalTime() {
        return TotalTime;
    }
    public double[] getAdcRate() {
        return AdcRate;
    }
    public double[] getInterCadreDelay() {
        return InterCadreDelay;
    }
    public  double[] getChannelRate() {
        return ChannelRate;
    }
    public byte[] [] getActiveAdcChannelArray() {
        return ActiveAdcChannelArray;
    }
    public byte[][] getAdcChannelArray() {
        return AdcChannelArray;
    }
    public byte[][] getAdcGainArray() {
        return AdcGainArray;
    }
    public  byte[][] getIsSignalArray() {
        return IsSignalArray;
    }
    public  int[] getDataFormat() {
        return DataFormat;
    }
    public  long[] getRealCadres64() {
        return RealCadres64;
    }
    public  double[][] getAdcOffset() {
        return AdcOffset;
    }
    public  double[][] getAdcScale() {
        return AdcScale;
    }
    public  double[][] getCalibrScale() {
        return CalibrScale;
    }
    public  double[][] getCalibrOffset() {
        return CalibrOffset;
    }
    public int[] getSegments() {
        return Segments;
    }

    public void setDataParamsValid(boolean dataParamsValid) {
        this.dataParamsValid = dataParamsValid;
    }
    public void setDataFormatStr(String dataFormatSt, int fileIndex) {
        dataFormatStr[fileIndex] = dataFormatSt;
    }
    public void setDeviceName(String deviceName, int fileIndex) {
        this.deviceName[fileIndex] = deviceName;
    }
    public void setCreateDateTime(String CreateDateTime, int fileIndex) {
        this.CreateDateTime[fileIndex] = CreateDateTime;
    }
    public void setChannelsMax(int channelsMax, int fileIndex) {
        ChannelsMax[fileIndex] = channelsMax;
    }
    public void setRealChannelsQuantity(int realChannelsQuantity, int fileIndex) {
        RealChannelsQuantity[fileIndex] = realChannelsQuantity;
    }
    public void setRealCadresQuantity(long realCadresQuantity, int fileIndex) {
        RealCadresQuantity[fileIndex] = realCadresQuantity;
    }
    public void setRealSamplesQuantity(long realSamplesQuantity, int fileIndex) {
        RealSamplesQuantity[fileIndex] = realSamplesQuantity;
    }
    public void setTotalTime(double totalTime, int fileIndex) {
        TotalTime[fileIndex] = totalTime;
    }
    public void setAdcRate(double adcRate, int fileIndex) {
        AdcRate[fileIndex] = adcRate;
    }
    public void setInterCadreDelay(double interCadreDelay, int fileIndex) {
        InterCadreDelay[fileIndex] = interCadreDelay;
    }
    public void setChannelRate(double channelRate, int fileIndex) {
        ChannelRate[fileIndex] = channelRate;
    }
    public void setActiveAdcChannelArray(byte[] activeAdcChannelArray, int fileIndex) {
        ActiveAdcChannelArray[fileIndex] = activeAdcChannelArray.clone();
    }
    public void setAdcChannelArray(byte[] adcChannelArray, int fileIndex) {
        AdcChannelArray[fileIndex] = adcChannelArray.clone();
    }
    public void setAdcGainArray(byte[] adcGainArray, int fileIndex) {
        AdcGainArray[fileIndex] = adcGainArray.clone();
    }
    public void setIsSignalArray(byte[] isSignalArray, int fileIndex) {
        IsSignalArray[fileIndex] = isSignalArray.clone();
    }
    public void setDataFormat(int dataFormat, int fileIndex) {
        DataFormat[fileIndex] = dataFormat;
    }
    public void setRealCadres64(long realCadres64, int fileIndex) {
        RealCadres64[fileIndex] = realCadres64;
    }
    public void setAdcOffset(double[] adcOffset, int fileIndex) {
        AdcOffset[fileIndex] = adcOffset.clone();
    }
    public void setAdcScale(double[] adcScale, int fileIndex) {
        AdcScale[fileIndex] = adcScale.clone();
    }
    public void setCalibrScale(double[] calibrScale, int fileIndex) {
        CalibrScale[fileIndex] = calibrScale.clone();
    }
    public void setCalibrOffset(double[] calibrOffset, int fileIndex) {
        CalibrOffset[fileIndex] = calibrOffset.clone();
    }
    public void setSegments(int segments, int fileIndex) {
        Segments[fileIndex] = segments;
    }

}


