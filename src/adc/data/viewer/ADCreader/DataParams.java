package adc.data.viewer.ADCreader;

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

    DataParams(int amountOfFiles) {
        this.dataParamsValid =false;
        this.dataFormatStr = new String[amountOfFiles];
        this.deviceName = new String[amountOfFiles];
        this.CreateDateTime = new String[amountOfFiles];
        this.ChannelsMax = new int[amountOfFiles];
        this. RealChannelsQuantity= new int[amountOfFiles];
        this.RealCadresQuantity= new long[amountOfFiles];
        this.RealSamplesQuantity= new long[amountOfFiles];
        this.TotalTime= new double[amountOfFiles];
        this.AdcRate= new double[amountOfFiles];
        this.InterCadreDelay= new double[amountOfFiles];
        this. ChannelRate= new double[amountOfFiles];
        this. ActiveAdcChannelArray = new byte[amountOfFiles][32];
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
    public void setDataFormatStr(String dataFormatSt, int fnum) {
        dataFormatStr[fnum] = dataFormatSt;
    }
    public void setDeviceName(String deviceName, int fnum) {
        this.deviceName[fnum] = deviceName;
    }
    public void setCreateDateTime(String CreateDateTime, int fnum) {
        this.CreateDateTime[fnum] = CreateDateTime;
    }
    public void setChannelsMax(int channelsMax, int fnum) {
        ChannelsMax[fnum] = channelsMax;
    }
    public void setRealChannelsQuantity(int realChannelsQuantity, int fnum) {
        RealChannelsQuantity[fnum] = realChannelsQuantity;
    }
    public void setRealCadresQuantity(long realCadresQuantity, int fnum) {
        RealCadresQuantity[fnum] = realCadresQuantity;
    }
    public void setRealSamplesQuantity(long realSamplesQuantity, int fnum) {
        RealSamplesQuantity[fnum] = realSamplesQuantity;
    }
    public void setTotalTime(double totalTime, int fnum) {
        TotalTime[fnum] = totalTime;
    }
    public void setAdcRate(double adcRate, int fnum) {
        AdcRate[fnum] = adcRate;
    }
    public void setInterCadreDelay(double interCadreDelay, int fnum) {
        InterCadreDelay[fnum] = interCadreDelay;
    }
    public void setChannelRate(double channelRate, int fnum) {
        ChannelRate[fnum] = channelRate;
    }
    public void setActiveAdcChannelArray(byte[] activeAdcChannelArray, int fnum) {
        ActiveAdcChannelArray[fnum] = activeAdcChannelArray.clone();
    }
    public void setAdcChannelArray(byte[] adcChannelArray, int fnum) {
        AdcChannelArray[fnum] = adcChannelArray.clone();
    }
    public void setAdcGainArray(byte[] adcGainArray, int fnum) {
        AdcGainArray[fnum] = adcGainArray.clone();
    }
    public void setIsSignalArray(byte[] isSignalArray, int fnum) {
        IsSignalArray[fnum] = isSignalArray.clone();
    }
    public void setDataFormat(int dataFormat, int fnum) {
        DataFormat[fnum] = dataFormat;
    }
    public void setRealCadres64(long realCadres64, int fnum) {
        RealCadres64[fnum] = realCadres64;
    }
    public void setAdcOffset(double[] adcOffset, int fnum) {
        AdcOffset[fnum] = adcOffset.clone();
    }
    public void setAdcScale(double[] adcScale, int fnum) {
        AdcScale[fnum] = adcScale.clone();
    }
    public void setCalibrScale(double[] calibrScale, int fnum) {
        CalibrScale[fnum] = calibrScale.clone();
    }
    public void setCalibrOffset(double[] calibrOffset, int fnum) {
        CalibrOffset[fnum] = calibrOffset.clone();
    }
    public void setSegments(int segments, int fnum) {
        Segments[fnum] = segments;
    }

}


