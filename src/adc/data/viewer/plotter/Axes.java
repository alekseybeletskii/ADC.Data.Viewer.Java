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


//import adc.data.viewer.ui.PlotterSettingController;
import adc.data.viewer.model.ADCDataRecords;
import adc.data.viewer.processing.SimpleMath;
import adc.data.viewer.ui.MainApp;
import adc.data.viewer.ui.PlotterSettingController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Side;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static java.lang.Math.abs;


public class Axes extends Pane {
    private MainApp mainApp;

    private long mostSamples;
    private double absMaxYValue;
    private StableTicksAxis xAxis;
    private StableTicksAxis yAxis;

    private double fixYZeroShift=0.0;
    private double xAxisOffset;
    private double yAxisOffset;
    private double xMinBasic;
    private double xMaxBasic;
    private double yMinBasic;
    private double yMaxBasic;
    private Rectangle axesBoxRectangle;
    private Canvas meshCanvas;
    private GraphicsContext graphicContext;
    private DoubleProperty dx = new SimpleDoubleProperty();
    private DoubleProperty dy = new SimpleDoubleProperty();
    private DoubleProperty shiftXZero = new SimpleDoubleProperty();
    private DoubleProperty shiftYZero = new SimpleDoubleProperty();


    public double getXAxisOffset() {
        return xAxisOffset;
    }
    private void setXAxisOffset(double XAxisOffset) {
        this.xAxisOffset = XAxisOffset;
    }
    public double getYAxisOffset() {
        return yAxisOffset;
    }
    public void setYAxisOffset(double yOffset) {
        this.yAxisOffset = yOffset;
    }
    public void setFixYZeroShift(double fixYZeroShift) {
        this.fixYZeroShift = fixYZeroShift;
    }

    Axes(MainApp mainApp) {


        this.mainApp= mainApp;
        this.meshCanvas= new Canvas();


        axesBoxRectangle = new Rectangle(0,0);


        obtainDataAndTimeMargins(mainApp.getNextSignalToDraw());


        if (mainApp.isUseSavedAxesRange()) {
            PlotterSettingController.setCurrentAxesSettings(mainApp.getSavedAxesRange()[0], mainApp.getSavedAxesRange()[1], mainApp.getSavedAxesRange()[2], mainApp.getSavedAxesRange()[3],true);
            setSavedAxesBounds(mainApp.getSavedAxesRange()[0], mainApp.getSavedAxesRange()[1], mainApp.getSavedAxesRange()[2], mainApp.getSavedAxesRange()[3]);
        }
        else PlotterSettingController.setCurrentAxesSettings(xMinBasic,xMaxBasic,yMinBasic,yMaxBasic,false);

        xAxis = new StableTicksAxis(xMinBasic, xMaxBasic);

        xAxis.setLabel("time, ms");
        xAxis.setSide(Side.BOTTOM);
        xAxis.setMinorTickVisible(true);
        xAxis.setMinorTickCount(1);
        xAxis.setAnimated(false);

        yAxis = new StableTicksAxis(yMinBasic, yMaxBasic);


        yAxis.setLabel("a.u.");
        yAxis.setSide(Side.LEFT);
        yAxis.setMinorTickVisible(true);
        yAxis.setMinorTickCount(1);
        yAxis.setAnimated(false);


        xAxis.layoutYProperty().bind(heightProperty());
        xAxis.layoutXProperty().bind(Bindings.subtract((Bindings.subtract(widthProperty(),widthProperty())),0));
        yAxis.layoutYProperty().bind(Bindings.subtract(heightProperty(),yAxis.heightProperty()));
        yAxis.layoutXProperty().bind(Bindings.subtract(0.0,yAxis.widthProperty()));


        yAxis.prefHeightProperty().bind(heightProperty());
        xAxis.prefWidthProperty().bind(widthProperty());


        axesBoxRectangle.widthProperty().bind(widthProperty());
        axesBoxRectangle.heightProperty().bind(heightProperty());
        axesBoxRectangle.layoutXProperty().bind(Bindings.add(xAxis.layoutXProperty(),0));
        axesBoxRectangle.layoutYProperty().bind(Bindings.add(yAxis.layoutYProperty(),0));

        axesBoxRectangle.getStyleClass().add("axesBorder");


        meshCanvas.widthProperty().bind(xAxis.widthProperty());
        meshCanvas.heightProperty().bind(yAxis.heightProperty());
        graphicContext = meshCanvas.getGraphicsContext2D();

        getChildren().setAll(axesBoxRectangle,xAxis, yAxis, meshCanvas);


    }



    public void drawmesh() {
        dx.bind(Bindings.divide(widthProperty(), Bindings.subtract(getXAxis().upperBoundProperty(), getXAxis().lowerBoundProperty())));
        dy.bind(Bindings.divide(heightProperty(), Bindings.subtract(getYAxis().upperBoundProperty(), getYAxis().lowerBoundProperty())));
        shiftXZero.bind(Bindings.multiply(getXAxis().lowerBoundProperty(), dx));
        shiftYZero.bind(Bindings.multiply(getYAxis().upperBoundProperty(), dy));

        graphicContext.clearRect(0, 0, getWidth(), getHeight());
        graphicContext.setFill(Color.TRANSPARENT);
        graphicContext.fillRect(0, 0, getWidth(), getHeight());
        graphicContext.setStroke(Color.DARKGRAY);
        graphicContext.setLineWidth(1.0);
        graphicContext.setLineDashes(3);

        //drawData X axis mesh
        double tickXNext = getXAxis().getAxisFirstTick().get();
        double tickXUnit = getXAxis().getTickUnit().get();

        while (tickXNext < getXAxis().getUpperBound()) {
            tickXNext = tickXNext+tickXUnit;
            graphicContext.beginPath();
            graphicContext.moveTo((int)(tickXNext * dx.get() - shiftXZero.get()) + 0.5, getHeight());
            graphicContext.lineTo((int)(tickXNext * dx.get() - shiftXZero.get()) + 0.5, 0);
            graphicContext.stroke();

        }

        //drawData Y axis mesh
        double tickYNext = getYAxis().getAxisFirstTick().get();
        double tickYUnit = getYAxis().getTickUnit().get();

        while (tickYNext < getYAxis().getUpperBound()) {
            tickYNext = tickYNext+tickYUnit;
            graphicContext.beginPath();
            graphicContext.moveTo(0, (int)( -tickYNext * dy.get() + shiftYZero.get()) + 0.5);
            graphicContext.lineTo(getWidth(), (int)(-tickYNext * dy.get() + shiftYZero.get()) + 0.5);
            graphicContext.stroke();

        }

    }

    private void setSavedAxesBounds ( double xmn, double xmx, double ymn, double ymx)
    {
        xMinBasic=xmn;
        xMaxBasic=xmx;
        yMinBasic=ymn;
        yMaxBasic=ymx;
    }

    public void setAxesBasicSetup( ){

        xAxis.setLowerBound(xMinBasic);
        xAxis.setUpperBound(xMaxBasic);
        yAxis.setLowerBound(yMinBasic-fixYZeroShift);
        yAxis.setUpperBound(yMaxBasic-fixYZeroShift);
        xAxisOffset = xMinBasic;
        yAxisOffset = yMinBasic-fixYZeroShift;
        PlotterSettingController.setCurrentAxesSettings( xMinBasic,xMaxBasic,yMinBasic-fixYZeroShift,yMaxBasic-fixYZeroShift,false);
    }

    public StableTicksAxis getXAxis() {
        return xAxis;
    }

    public StableTicksAxis getYAxis() {
        return yAxis;
    }




    public void setAxesBounds (double xMin, double xMax,  double yMin, double yMax){
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMax);

        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);

        setXAxisOffset(xMin);
        setYAxisOffset(yMin);
    }

    void axesZoomRescale(DoubleProperty zoomTopLeftX, DoubleProperty zoomTopLeftY, DoubleProperty zoomBottomRightX, DoubleProperty zoomBottomRightY)
    {

        double xOffset = (zoomTopLeftX.get())/xAxis.getScale();
        xAxis.setLowerBound(xAxis.getLowerBound()+ xOffset);
        xAxis.setUpperBound(xAxis.getLowerBound()+ (zoomBottomRightX.get()- zoomTopLeftX.get())/xAxis.getScale());
        setXAxisOffset(xAxis.getLowerBound());
        double yOffset = (zoomBottomRightY.get()-yAxis.getHeight())/-yAxis.getScale();
        yAxis.setLowerBound(yAxis.getLowerBound()-yOffset);
        yAxis.setUpperBound(yAxis.getLowerBound()+(zoomBottomRightY.get()- zoomTopLeftY.get())/-yAxis.getScale());
        setYAxisOffset(yAxis.getLowerBound());

        PlotterSettingController.setCurrentAxesSettings(xAxis.getLowerBound(),xAxis.getUpperBound(),yAxis.getLowerBound(),yAxis.getUpperBound(),false);

    }



    void axesPanRescale (double dxPan, double dyPan){

        xAxis.setLowerBound(xAxis.getLowerBound()- dxPan/xAxis.getScale());
        yAxis.setLowerBound(yAxis.getLowerBound()+ dyPan/-yAxis.getScale());
        xAxis.setUpperBound(xAxis.getUpperBound()- dxPan/xAxis.getScale());
        yAxis.setUpperBound(yAxis.getUpperBound()+ dyPan/-yAxis.getScale());
        setXAxisOffset(xAxis.getLowerBound());
        setYAxisOffset(yAxis.getLowerBound());

        PlotterSettingController.setCurrentAxesSettings(xAxis.getLowerBound(),xAxis.getUpperBound(),yAxis.getLowerBound(),yAxis.getUpperBound(),false);

    }

    public void obtainDataAndTimeMargins(ADCDataRecords nextSignalToDraw) {

xMinBasic= Integer.MAX_VALUE;
xMaxBasic= Integer.MIN_VALUE;
yMinBasic= Integer.MAX_VALUE;
yMaxBasic= Integer.MIN_VALUE;

        boolean isAnySelected =false;

        switch(mainApp.getDefaultPlotsLayoutType()){
            case "AllPlots":
                if(nextSignalToDraw==null)for (ADCDataRecords adcDataRecords : mainApp.getAdcDataRecords()){
                    if (adcDataRecords.getSignalSelected())
                    {
                        isAnySelected =true;
                        selectedSignalMargins(adcDataRecords);
                    }
                }
                else {selectedSignalMargins(nextSignalToDraw);
                isAnySelected =true;}
                break;
            case "AllPlotsByOne":
                isAnySelected =true;
                selectedSignalMargins(mainApp.getNextSignalToDraw());
                break;
            case "AllPlotsByOneScroll":
                isAnySelected =true;
                selectedSignalMargins(mainApp.getNextSignalToDraw());
                break;
            default:
                break;
        }

        xMinBasic=0.0;
        absMaxYValue = abs(yMaxBasic)>abs(yMinBasic)?abs(yMaxBasic):abs(yMinBasic);
//        yMaxBasic=absMaxYValue;
//        yMinBasic=-absMaxYValue;
        if(!isAnySelected){
            xMinBasic= 0.0;
            xMaxBasic= 1.0;
            yMinBasic= -1.0;
            yMaxBasic= 1.0;
        }
    }

    public void selectedSignalMargins(ADCDataRecords adcDataRecords) {
        double dt;
        dt = 1.0/(mainApp.getDataParser().getDataParams().getChannelRate()[adcDataRecords.getFileIndex()]);
        mostSamples = mainApp.getDataParser().getDataParams().getRealCadresQuantity()[adcDataRecords.getFileIndex()];

        if (mostSamples *dt > xMaxBasic) {
            xMaxBasic = mostSamples *dt;
        }
//        double[] testSignal = mainApp.getDataParser().getSignals()[adcDataRecords.getSignalIndex()];
        double[] testSignal = adcDataRecords.getSignalData();
        double maximum = SimpleMath.getMax(testSignal);
        double minimum = SimpleMath.getMin(testSignal);
        if (maximum> yMaxBasic) yMaxBasic =maximum;
        if (minimum< yMinBasic) yMinBasic =minimum;
    }

}