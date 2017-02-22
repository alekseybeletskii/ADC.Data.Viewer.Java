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

import adc.data.viewer.model.SignalMarker;
import adc.data.viewer.parser.DataParser;
import adc.data.viewer.processing.SavitzkyGolayFilter;
import adc.data.viewer.processing.SimpleMath;
import adc.data.viewer.ui.MainApp;
import adc.data.viewer.ui.PlotterSettingController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;

import static java.lang.Math.round;


 /*
  * Canvas is normally not resizable but by overriding isResizable() and
  * binding its width and height to the width and height of the Pane it will
  * automatically resize.
  */
public class CanvasDataDrawing extends Canvas {


    private final DataParser allSignals;
    private final Axes axes;
    private final MainApp mainApp;
    private DoubleProperty dx = new SimpleDoubleProperty();
    private DoubleProperty dy = new SimpleDoubleProperty();
    private DoubleProperty shiftXZero = new SimpleDoubleProperty();
    private DoubleProperty shiftYZero = new SimpleDoubleProperty();

    private String plotType;
    private String lineOrScatter;
    private int pointSize;

    private SavitzkyGolayFilter sgfilter;
    private GraphicsContext graphicContext;

    public void setLineOrScatter(String lineOrScatter) {
        this.lineOrScatter = lineOrScatter;
    }
    public void setPlotType(String plotType) {
        this.plotType = plotType;
    }
    public void setSGfilter(SavitzkyGolayFilter sgfilter) {
        this.sgfilter = sgfilter;
    }
    public double getShiftYZero() {
        return shiftYZero.get();
    }
    public SavitzkyGolayFilter getSgfilter() {
        return sgfilter;
    }
    public double getShiftXZero() {
        return shiftXZero.get();
    }


    CanvasDataDrawing(MainApp mainApp, Axes axes, String plotType) {
        this.plotType =plotType;
        this.mainApp = mainApp;
        this.axes = axes;
        this.allSignals = mainApp.getDataParser();
        this.lineOrScatter = "line+scatter";
        this.pointSize =6;
        PlotterSettingController.setChosenLineOrScatter(lineOrScatter);
        dx.bind(Bindings.divide(widthProperty(),Bindings.subtract(axes.getXAxis().upperBoundProperty(),axes.getXAxis().lowerBoundProperty())));
        dy.bind(Bindings.divide(heightProperty(),Bindings.subtract(axes.getYAxis().upperBoundProperty(),axes.getYAxis().lowerBoundProperty())));
        shiftXZero.bind(Bindings.multiply(axes.getXAxis().lowerBoundProperty(),dx));
        shiftYZero.bind(Bindings.multiply(axes.getYAxis().upperBoundProperty(),dy));
        graphicContext = getGraphicsContext2D();

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

    public void drawMeshZeroLines(){
        cleanCanvas();
        drawmesh();

    }

    public void cleanCanvas() {
        graphicContext.clearRect(0, 0, getWidth(), getHeight());
        graphicContext.setFill(Color.TRANSPARENT);
        graphicContext.fillRect(0,0,getWidth(), getHeight());
    }

    public void drawData() {

        cleanCanvas();
        drawmesh();

        graphicContext.setLineWidth(1);
        graphicContext.setLineDashes(0);
        for (SignalMarker signalMarker:mainApp.getSignalList()){
            if(signalMarker.getSignalSelected()) {
                int nextSignal = signalMarker.getSignalIndex();
                String label = mainApp.getDataParser().getSignalLabels()[nextSignal];
                int ADCChannelNum = Integer.parseInt(label.substring(label.lastIndexOf('\u0023') + 1)) - 1;

//dt,dtCadre in milliseconds
                double dt = 1.0 / (mainApp.getDataParser().getDataParams().getChannelRate()[mainApp.getSignalList().get(nextSignal).getFileNumber()]);
                double dtCadre = mainApp.getDataParser().getDataParams().getInterCadreDelay()[mainApp.getSignalList().get(nextSignal).getFileNumber()];
                int nextSignalLength = allSignals.getSignals()[nextSignal].length;
                int xLeft = (int) round(axes.getXAxis().getLowerBound() / dt);
                int xRight = (int) round(axes.getXAxis().getUpperBound() / dt);


               if((xRight>0)&&(xLeft<nextSignalLength)) {


                   double[] sigSubarray = Arrays.copyOfRange(allSignals.getSignals()[nextSignal],
                           xLeft < 0 ? 0 : xLeft, xRight > nextSignalLength ? nextSignalLength : xRight);

                   switch (plotType) {
                       case "Raw":
                           graphicContext.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.beginPath();
                           decimator(graphicContext, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                       case "SGFiltered":
                           graphicContext.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.beginPath();
                           int i = 0;
                           for (double yy : sgfilter.filterData(sigSubarray)) {
                               sigSubarray[i] = sigSubarray[i] - yy;
                               i++;
                           }
                           decimator(graphicContext, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                       case "RawAndSGFilter":
                           graphicContext.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.beginPath();
                           decimator(graphicContext, ADCChannelNum, dt, dtCadre, sigSubarray);
                           sigSubarray = sgfilter.filterData(sigSubarray);
                           graphicContext.setStroke(Color.BLACK);
                           graphicContext.setFill(Color.TRANSPARENT);
                           graphicContext.beginPath();
                           decimator(graphicContext, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                       case "SGFilter":
                           sigSubarray = sgfilter.filterData(sigSubarray);
                           graphicContext.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           graphicContext.beginPath();
                           decimator(graphicContext, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                   }
               }
            }
        }
        drawZeroLines();
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

            graphicContext.setStroke(Color.DARKGRAY);
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

    private void decimator(GraphicsContext graphicContext, int ADCChannelNum, double dt, double dtCadre, double[] sigSubarray) {
        int step = (int)(sigSubarray.length/widthProperty().get());

        if (step > 1) {

            for (int i=0;i<sigSubarray.length;i=i+step)
            {
                double [] sigSegment = Arrays.copyOfRange(sigSubarray,
                        i,i+step);
                SimpleMath.findMaxMin(sigSegment);

                if (i == 0) {
                    graphicContext.moveTo(mapX(i, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(sigSegment[i]));
                }
                graphicContext.lineTo(mapX(i+step/2, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(SimpleMath.getMax()));
                graphicContext.lineTo(mapX(i+step/2, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(SimpleMath.getMin()));
            }
        }
        else {
            int x=0;
            for (double y : sigSubarray) {
                if (x == 0) {
                    graphicContext.moveTo(mapX(x, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
                }
                switch (lineOrScatter){
                    case "line":
                        graphicContext.lineTo(mapX(x, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
                        break;
                    case "line+scatter":
                        graphicContext.lineTo(mapX(x, dt) + ADCChannelNum * dx.get() * dtCadre, mapY(y));
                        graphicContext.fillOval(mapX(x, dt) + ADCChannelNum * dx.get() * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                        break;
                    case "scatter":
                        graphicContext.fillOval(mapX(x, dt) + ADCChannelNum * dx.get() * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                        break;
                }
                x++;
            }
        }
        graphicContext.stroke();
    }
    private double mapX(double x, double dt) {
        return (shiftXZero.get()<0? x*dx.get()*dt-shiftXZero.get():x*dx.get()*dt);
    }
    private double mapY(double y) {
        return -y * dy.get()+ shiftYZero.get();
    }

}
