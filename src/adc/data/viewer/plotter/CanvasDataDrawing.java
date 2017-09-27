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

package adc.data.viewer.plotter;

import adc.data.viewer.model.ADCDataRecords;
import adc.data.viewer.processing.SavitzkyGolayFilter;
import adc.data.viewer.processing.SimpleMath;
import adc.data.viewer.ui.MainApp;
import adc.data.viewer.ui.PlotterController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

import static java.lang.Math.abs;
import static java.lang.Math.round;


/*
 * Canvas is normally not resizable but by overriding isResizable() and
 * binding its width and height to the width and height of the Pane it will
 * automatically resize.
 */
public class CanvasDataDrawing extends Canvas {


    private int xLeft;
    private int xRight;

    private DoubleProperty nextSignalTimeShift = new SimpleDoubleProperty();

    public double getAdcZeroShift() {
        return adcZeroShift;
    }

    private double adcZeroShift;

    public void setNextSignalToDraw(ADCDataRecords nextSignalToDraw) {
        this.nextSignalToDraw = nextSignalToDraw;
    }

    public ADCDataRecords getNextSignalToDraw() {
        return nextSignalToDraw;
    }

    public void setPlotterController(PlotterController plotterController) {
        this.plotterController = plotterController;
    }

    public double getxTheMIN() {
        return xTheMIN;
    }

    public double getxTheMAX() {
        return xTheMAX;
    }

    public double getyTheMIN() {
        return yTheMIN;
    }

    public double getyTheMAX() {
        return yTheMAX;
    }


    private double xTheMIN,xTheMAX,yTheMIN,yTheMAX;

    private PlotterController plotterController;
    private ADCDataRecords nextSignalToDraw;
    private final Axes axes;
    private final MainApp mainApp;
    private DoubleProperty dx = new SimpleDoubleProperty();
    private DoubleProperty dy = new SimpleDoubleProperty();
    private DoubleProperty shiftXZero = new SimpleDoubleProperty();
    private DoubleProperty shiftYZero = new SimpleDoubleProperty();

    private double maxZeroShift;

    private int SGFilterLeft;
    private int SGFilterRight;
    private int SGFilterOrder;
    private double FixZeroShiftStart;
    private double FixZeroShiftEnd;
    private boolean isFixADCZeroShift;

    private double widthOfLine;
    private String plotType;
    private String plotStyle;
    private int pointSize;
    private GraphicsContext graphicContext;


    //     public void setWidthOfLine(double widthOfLine) {
//         this.widthOfLine = widthOfLine;
//     }
//
    public void setPlotStyle(String plotStyle) {
        this.plotStyle = plotStyle;
    }



    public double getShiftYZero() {
        return shiftYZero.get();
    }

    public double getShiftXZero() {
        return shiftXZero.get();
    }

    public void resetCanvasDefault (){
        widthOfLine =MainApp.appPreferencesRootNode.getDouble("defaultWidthOfLine",1.0);
        plotType = MainApp.appPreferencesRootNode.get("defaultPlotType","Raw");
        plotStyle = MainApp.appPreferencesRootNode.get("defaultPlotStyle","line");
        pointSize = MainApp.appPreferencesRootNode.getInt("defaultPointSize",6);

        SGFilterLeft= MainApp.appPreferencesRootNode.getInt("defaultSGFilterLeft",50); //points
        SGFilterRight= MainApp.appPreferencesRootNode.getInt("defaultSGFilterRight",50); //points
        SGFilterOrder= MainApp.appPreferencesRootNode.getInt("defaultSGFilterLeftOrder",1);
        FixZeroShiftStart=MainApp.appPreferencesRootNode.getDouble("defaultFixZeroShiftStart",0); //ms
        FixZeroShiftEnd= MainApp.appPreferencesRootNode.getDouble("defaultFixZeroShiftEnd",1);
        isFixADCZeroShift = MainApp.appPreferencesRootNode.getBoolean("defaultFixZeroShift",false);
    }


    CanvasDataDrawing(MainApp mainApp, Axes axes) {

        this.mainApp = mainApp;
        this.axes = axes;

        dx.bind(Bindings.divide(widthProperty(), Bindings.subtract(axes.getXAxis().upperBoundProperty(), axes.getXAxis().lowerBoundProperty())));
        dy.bind(Bindings.divide(heightProperty(), Bindings.subtract(axes.getYAxis().upperBoundProperty(), axes.getYAxis().lowerBoundProperty())));
//        shiftXZero.bind(Bindings.multiply(Bindings.subtract(axes.getXAxis().lowerBoundProperty(),nextSignalTimeShift), dx));
        shiftXZero.bind(Bindings.multiply(axes.getXAxis().lowerBoundProperty(), dx));
        shiftYZero.bind(Bindings.multiply(axes.getYAxis().upperBoundProperty(), dy));
        graphicContext = getGraphicsContext2D();
        maxZeroShift=0;

    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    /**
     * Canvas drawing now successfully copes with millions of data points
     * due to simple point-per-pixel approach (see "decimator" method)
     */

    public void drawDataMeshZerolines() {

        cleanCanvas();
        drawmesh();
        drawData();
        drawZeroLines();

    }

    public void drawMeshZeroLines() {
        cleanCanvas();
        drawmesh();

    }

    public void cleanCanvas() {
        graphicContext.clearRect(0, 0, getWidth(), getHeight());
        graphicContext.setFill(Color.TRANSPARENT);
        graphicContext.fillRect(0, 0, getWidth(), getHeight());
    }


    public void drawData() {
        resetCanvasDefault();

//         System.out.println(getxTheMIN()+"\n"+getxTheMAX()+"\n"+getyTheMIN()+"\n"+getyTheMAX()+"\n");
//         axes.obtainDataAndTimeMargins();
//         axes.setAxesBasicSetup();
//         System.out.println("drawing..");
        xTheMIN=Integer.MAX_VALUE;
        xTheMAX=Integer.MIN_VALUE;
        yTheMIN=Integer.MAX_VALUE;
        yTheMAX=Integer.MIN_VALUE;

        cleanCanvas();
        drawmesh();

        graphicContext.setLineWidth(widthOfLine);
        graphicContext.setLineDashes(0);

        switch (mainApp.getDefaultPlotsLayoutType()) {
            case "AllPlots":
                int i=0;
                plotterController.getLegend().setText("");
                plotterController.getSignalIndexLabel().setText("");
                for (ADCDataRecords sigMarc : mainApp.getAdcDataRecords()) {

                    if (sigMarc.getSignalSelected()) {
                        i++;
                        nextSignalToDraw=sigMarc;
                        drawNextSignal(nextSignalToDraw);

                        plotterController.getLegend().setText(plotterController.getLegend().getText()+nextSignalToDraw.getSignalLabel()+"="+plotType+"\n");
                        plotterController.getSignalIndexLabel().setText(plotterController.getSignalIndexLabel().getText()+String.format("%d",(nextSignalToDraw.getSignalIndex()+1))+"\n");
                    }
                }

//                 if(i==1){
//                     plotterController.getLegend().setText(nextSignalToDraw.getSignalLabel()+"="+plotType);
//                     plotterController.getSignalIndexLabel().setText(String.format("%d",(nextSignalToDraw.getSignalIndex()+1)));
//                 }
//                 else{
//                     plotterController.getLegend().setText(
//                             nextSignalToDraw.getSignalLabel()+"="+plotType);
//                     plotterController.getSignalIndexLabel().setText("");
//                 }
//                     plotterController.getLegend().setText("="+plotType+"=");

                break;
            case "AllPlotsByOne":
                plotterController.getLegend().setText(nextSignalToDraw.getSignalLabel()+"="+plotType);
                plotterController.getSignalIndexLabel().setText(String.format("%d",(nextSignalToDraw.getSignalIndex()+1)));
                if(nextSignalToDraw.isSignalSelected())drawNextSignal(nextSignalToDraw);
                break;
            case "AllPlotsByOneScroll":
                plotterController.getLegend().setText(nextSignalToDraw.getSignalLabel()+"="+plotType);
                plotterController.getSignalIndexLabel().setText(String.format("%d",(nextSignalToDraw.getSignalIndex()+1)));
                drawNextSignal(nextSignalToDraw);
                break;
            default:
                break;
        }

        if(MainApp.appPreferencesRootNode.getBoolean("defaultIsReplaceRawWithFilter", false)){
            MainApp.appPreferencesRootNode.putBoolean("defaultIsReplaceRawWithFilter", false);
            drawData();
        }


        drawZeroLines();
    }


    public void drawNextSignal(ADCDataRecords nextSignalToDraw) {
        adcZeroShift = 0;

        int nextSignalLength =0;
        int nextSignalIndex = nextSignalToDraw.getSignalIndex();
        int ADCChannelNum = Integer.parseInt(nextSignalToDraw.getAdcChannelNumber());
        double [] nextYData = nextSignalToDraw.getSignalYData().clone();
        double [] nextXData = nextSignalToDraw.getSignalXData().clone();

        if(MainApp.appPreferencesRootNode.getBoolean("defaultIsSubtractSignal",false)){
            int numberOfADCChannelUsedAsFilter = MainApp.appPreferencesRootNode.getInt("defaultADCChannelUsedAsFilter",-1);

            int nextFilterIndex =mainApp.getDataParser().getDataParams()
                    .getRealChannelsQuantity()[nextSignalToDraw.getFileOrdinalNumber()]*nextSignalToDraw
                    .getFileOrdinalNumber()+numberOfADCChannelUsedAsFilter-1;


            if(numberOfADCChannelUsedAsFilter == -1){
                int i = 0;
                for (double yy : mainApp.getSignalUsedAsFilter()) {
                    nextYData[i] = nextYData[i] - yy;
                    i++;
                }
            }else {
                int i = 0;
                for (double yy : mainApp.getAdcDataRecords()
                        .get(nextFilterIndex)
                        .getSignalYData()) {
                    nextYData[i] = nextYData[i] - yy;
                    i++;
                }
            }
//            MainApp.appPreferencesRootNode.putBoolean("defaultIsSubtractSignal", false);
        }

//dt,dtCadre in milliseconds


        double dt = 1.0 / (mainApp.getDataParser().getDataParams().getChannelRate()[nextSignalToDraw.getFileOrdinalNumber()]);
        nextSignalTimeShift.set(nextSignalToDraw.getSignalTimeShift());
        xTheMIN=nextSignalToDraw.getSignalTimeShift()<xTheMIN?nextSignalToDraw.getSignalTimeShift():xTheMIN;
        xTheMAX=nextSignalToDraw.getSignalYData().length*dt>xTheMAX?nextSignalToDraw.getSignalYData().length*dt:xTheMAX;

        double dtCadre = mainApp.getDataParser().getDataParams().getInterCadreDelay()[nextSignalToDraw.getFileOrdinalNumber()];

        nextSignalLength = nextYData.length;
        xLeft = (int) round((axes.getXAxis().getLowerBound()-nextSignalTimeShift.get()) / dt)-10;
        xRight = (int) round((axes.getXAxis().getUpperBound()-nextSignalTimeShift.get()) / dt)+10;


//        System.out.println("xLeft="+xLeft+" ; "+"xRight="+xRight +" ; " +"dt="+dt+" ; "+"nextSignalTimeShift="+nextSignalTimeShift.get());


        if(isFixADCZeroShift){
            int zeroStart = (int) round(FixZeroShiftStart / dt);
            int zeroEnd = (int) round(FixZeroShiftEnd / dt);
            if(zeroEnd-zeroStart>0&&zeroStart>=0&&zeroEnd<nextSignalLength) {
                adcZeroShift =SimpleMath.findAverage(Arrays.copyOfRange(nextYData,zeroStart,zeroEnd));
                for(int i = 0; i< nextYData.length; i++)
                    nextYData[i]= nextYData[i]- adcZeroShift;
            }


        }else{
            adcZeroShift=0;
        }

        findMaxMinY(nextYData, dt);
        axes.setADCZeroShift(adcZeroShift);

        if((xRight >0)&&(xLeft <nextSignalLength)) {

            xLeft = xLeft < 0 ? 0 : xLeft;
            xRight = xRight > nextSignalLength ? nextSignalLength : xRight;

            double[] dataYSubarray = Arrays.copyOfRange(nextYData, xLeft, xRight);

            double[] dataXSubarray = nextXData.length!=0?Arrays.copyOfRange(nextXData, xLeft, xRight):new double[0];


            SavitzkyGolayFilter sgfilter;
            int sgLeft = (SGFilterLeft+SGFilterRight)>= dataYSubarray.length?1:SGFilterLeft;
            int sgRight = (SGFilterLeft+SGFilterRight)>= dataYSubarray.length?1:SGFilterRight;



            switch (plotType) {
                case "Raw":
                    graphicContext.setStroke(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.setFill(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.beginPath();
                    decimator(graphicContext, ADCChannelNum, dt, dtCadre, dataYSubarray, dataXSubarray);
                    break;
                case "SGFiltered":
                    sgfilter =new SavitzkyGolayFilter(sgLeft, sgRight, SGFilterOrder);
                    graphicContext.setStroke(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.setFill(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.beginPath();
                    int i = 0;
                    for (double yy : sgfilter.filterData(dataYSubarray)) {
                        dataYSubarray[i] = dataYSubarray[i] - yy;
                        i++;
                    }

                    decimator(graphicContext, ADCChannelNum, dt, dtCadre, dataYSubarray, dataXSubarray);
                    break;
                case "RawAndSGFilter":
                    sgfilter =new SavitzkyGolayFilter(sgLeft, sgRight, SGFilterOrder);

                    graphicContext.setStroke(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.setFill(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.beginPath();
                    decimator(graphicContext, ADCChannelNum, dt, dtCadre, dataYSubarray, dataXSubarray);
                    dataYSubarray = sgfilter.filterData(dataYSubarray);

                    graphicContext.setStroke(Color.BLACK);
                    graphicContext.setFill(Color.TRANSPARENT);
                    graphicContext.beginPath();
                    decimator(graphicContext, ADCChannelNum, dt, dtCadre, dataYSubarray, dataXSubarray);
                    break;
                case "SGFilter":
                    sgfilter =new SavitzkyGolayFilter(sgLeft, sgRight, SGFilterOrder);

                    dataYSubarray = sgfilter.filterData(dataYSubarray);
                    graphicContext.setStroke(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.setFill(mainApp.getAdcDataRecords().get(nextSignalIndex).getSignalColor());
                    graphicContext.beginPath();
                    decimator(graphicContext, ADCChannelNum, dt, dtCadre, dataYSubarray, dataXSubarray);
                    break;
            }

            if(MainApp.appPreferencesRootNode.getBoolean("defaultIsReplaceRawWithFilter", false)&&
                    dataYSubarray.length==nextYData.length){
                mainApp.getAdcDataRecords().get(nextSignalIndex).setSignalYData(dataYSubarray);
            }
        }

    }

    private void drawZeroLines() {
        //drawData X-Y zero lines
        graphicContext.setStroke(Color.BLACK);
        graphicContext.setLineWidth(1);
        graphicContext.setLineDashes(5);

        graphicContext.beginPath();
        graphicContext.moveTo(0,(int) shiftYZero.get() +0.5);
        graphicContext.lineTo(getWidth(),(int) shiftYZero.get() +0.5);
        graphicContext.stroke();

        graphicContext.beginPath();
        graphicContext.moveTo(-(int) shiftXZero.get() +0.5,0);
        graphicContext.lineTo(-(int) shiftXZero.get() +0.5,getHeight());
        if (shiftXZero.get()!=0.0)graphicContext.stroke();
    }

    public void drawmesh() {

        graphicContext.setStroke(Color.LIGHTGRAY);
        graphicContext.setLineWidth(1.0);
        graphicContext.setLineDashes(3);

        //drawData X axis mesh
        double tickXNext = axes.getXAxis().getAxisFirstTick().get();
        double tickXUnit = axes.getXAxis().getTickUnit().get();

        while (tickXNext < axes.getXAxis().getUpperBound()) {
            tickXNext = tickXNext+tickXUnit;
            graphicContext.beginPath();
            graphicContext.moveTo((int)(tickXNext * dx.get() - shiftXZero.get()) + 0.5, getHeight());
            graphicContext.lineTo((int)(tickXNext * dx.get() - shiftXZero.get()) + 0.5, 0);
            graphicContext.stroke();

        }

        //drawData Y axis mesh
        double tickYNext = axes.getYAxis().getAxisFirstTick().get();
        double tickYUnit = axes.getYAxis().getTickUnit().get();

        while (tickYNext < axes.getYAxis().getUpperBound()) {
            tickYNext = tickYNext+tickYUnit;
            graphicContext.beginPath();
            graphicContext.moveTo(0, (int)( -tickYNext * dy.get() + shiftYZero.get()) + 0.5);
            graphicContext.lineTo(getWidth(), (int)(-tickYNext * dy.get() + shiftYZero.get()) + 0.5);
            graphicContext.stroke();

        }

    }

    private void decimator(GraphicsContext graphicContext, int ADCChannelNum, double dt, double dtCadre, double[] dataYSubarry, double[] dataXSubarry) {


        findMaxMinY(dataYSubarry, dt);



        int step = (int)(dataYSubarry.length/widthProperty().get());

        String isAmptydataXSubarry = dataXSubarry.length==0?"noXDataFromFile":"xDataFromFile";

        switch (isAmptydataXSubarry) {
            case "noXDataFromFile" :             if (step > 1) {
                drawDecimatedData(graphicContext, ADCChannelNum, dt, dtCadre, dataYSubarry, step);
            }
            else {
                drawFullData(graphicContext, ADCChannelNum, dt, dtCadre, dataYSubarry);
            }

                break;

            case "xDataFromFile" : if (step > 1) {

                drawDecimatedDataXFromFile(graphicContext, ADCChannelNum,  dtCadre, dataYSubarry, dataXSubarry, step);
            }
            else {
                drawFullDataXFromFile(graphicContext, ADCChannelNum,  dtCadre, dataYSubarry, dataXSubarry);

            }

                break;
        }

        graphicContext.stroke();
    }

    private void drawFullData(GraphicsContext graphicContext, int ADCChannelNum, double dt, double dtCadre, double[] dataYSubarry) {
        int x=0;
        for (double y : dataYSubarry) {
            if (x == 0) {

                graphicContext.moveTo(mapX(x+xLeft, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
            }
            switch (plotStyle){
                case "line":
                    graphicContext.lineTo(mapX(x+xLeft, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
                    break;
                case "line+scatter":
                    graphicContext.lineTo(mapX(x+xLeft, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
                    graphicContext.fillOval(mapX(x+xLeft, dt) + ADCChannelNum * dx.get() * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                    break;
                case "scatter":
                    graphicContext.fillOval(mapX(x+xLeft, dt) + ADCChannelNum * dx.get() * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                    break;
            }

            x++;
        }
    }



    private void drawDecimatedData(GraphicsContext graphicContext, int ADCChannelNum, double dt, double dtCadre, double[] dataYSubarry, int step) {
        for (int i = 0; i< dataYSubarry.length-step; i=i+step)
        {
            double [] sigSegment = Arrays.copyOfRange(dataYSubarry,
                    i,i+step);
            if (i == 0) {

                graphicContext.moveTo(mapX(i+xLeft, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(sigSegment[i]));
            }


            graphicContext.lineTo(mapX(i+xLeft+step/2, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(SimpleMath.getMax(sigSegment)));
            graphicContext.lineTo(mapX(i+xLeft+step/2, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(SimpleMath.getMin(sigSegment)));
        }
    }

    private void drawFullDataXFromFile(GraphicsContext graphicContext, int ADCChannelNum, double dtCadre, double[] dataYSubarry, double[] dataXSubarry) {
        int x=0;
        for (double y : dataYSubarry) {
            if (x == 0) {

                graphicContext.moveTo(mapX(dataXSubarry[x]) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
            }
            switch (plotStyle){
                case "line":
                    graphicContext.lineTo(mapX(dataXSubarry[x]) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
                    break;
                case "line+scatter":
                    graphicContext.lineTo(mapX(dataXSubarry[x]) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
                    graphicContext.fillOval(mapX(dataXSubarry[x]) + ADCChannelNum * dx.get() * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                    break;
                case "scatter":
                    graphicContext.fillOval(mapX(dataXSubarry[x]) + ADCChannelNum * dx.get() * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                    break;
            }
            x++;
        }
    }


    private void drawDecimatedDataXFromFile(GraphicsContext graphicContext, int ADCChannelNum, double dtCadre, double[] dataYSubarry, double[] dataXSubarry, int step) {


        for (int x = 0; x< dataYSubarry.length-step; x=x+step)


        {
            double [] sigSegment = Arrays.copyOfRange(dataYSubarry,
                    x,x+step);
            if (x == 0) {
                graphicContext.moveTo(mapX(dataXSubarry[x]) + ADCChannelNum * dx.get() * dtCadre, mapY(sigSegment[x]));
            }


            graphicContext.lineTo(mapX(dataXSubarry[x+step/2]) + ADCChannelNum * dx.get() * dtCadre, mapY(SimpleMath.getMax(sigSegment)));
            graphicContext.lineTo(mapX(dataXSubarry[x+step/2]) + ADCChannelNum * dx.get() * dtCadre, mapY(SimpleMath.getMin(sigSegment)));
        }
    }

    private void findMaxMinY(double[] sigSubarray, double dt) {
//        xTheMAX=sigSubarray.length*dt>xTheMAX?sigSubarray.length*dt:xTheMAX;
        double ymax,ymin;
        ymax= SimpleMath.getMax(sigSubarray);
        ymin=SimpleMath.getMin(sigSubarray);
        yTheMIN=ymin<yTheMIN?ymin:yTheMIN;
        yTheMAX=ymax>yTheMAX?ymax:yTheMAX;
    }

    private double mapX(double x, double dt) {

        if(shiftXZero.get()<0){
            return x*dx.get()*dt-shiftXZero.get();
        }

        else  return x*dx.get()*dt-shiftXZero.get();    }


    private double mapX(double x) {

                if(shiftXZero.get()<0){
                    return x*dx.get()-shiftXZero.get();
                }

                else  return x*dx.get()-shiftXZero.get();


    }

    private double mapY(double y) {
        return -y * dy.get()+ shiftYZero.get();
    }

}
