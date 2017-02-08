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

import adc.data.viewer.parser.DataParser;
import adc.data.viewer.ui.MainApp;
import adc.data.viewer.processing.SavitzkyGolayFilter;
import adc.data.viewer.processing.SimpleMath;
import adc.data.viewer.model.SignalMarker;
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
public class CanvasDrawing extends Canvas {


    private final DataParser allSignals;
    private final Axes axes;
    private final MainApp mainApp;
    private double dx;
    private double dy;
    private double shiftYZero;
    private double shiftXZero;
    private String plotType;
    private String lineOrScatter;
    private int pointSize;

    private SavitzkyGolayFilter sgfilter;

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
        return shiftYZero;
    }
    public SavitzkyGolayFilter getSgfilter() {
        return sgfilter;
    }
    public double getShiftXZero() {
        return shiftXZero;
    }


    CanvasDrawing(MainApp mainApp, Axes axes,  String plotType) {
        this.plotType =plotType;
        this.mainApp = mainApp;
        this.axes = axes;
        this.allSignals = mainApp.getDataParser();
        this.lineOrScatter = "line";
        this.pointSize =6;
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
    public void draw() {

        this.dx = widthProperty().get()/
                abs((axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound()));
        this.dy = heightProperty().get()/
                abs((axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound()));

        this.shiftYZero = axes.getYAxis().getUpperBound()*dy+1;

        this.shiftXZero = axes.getXAxis().getLowerBound()<0?-axes.getXAxis().getLowerBound()*dx:0.0;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0,0,getWidth(), getHeight());

        drawmesh(gc);

        drawData(gc);

        drawZeroLines(gc);
    }

    private void drawData(GraphicsContext gc) {
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.setLineDashes(0);
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
                           gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.beginPath();
                           decimator(gc, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                       case "SGFiltered":
                           gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.beginPath();
                           int i = 0;
                           for (double yy : sgfilter.filterData(sigSubarray)) {
                               sigSubarray[i] = sigSubarray[i] - yy;
                               i++;
                           }
                           decimator(gc, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                       case "RawAndSGFilter":
                           gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.beginPath();
                           decimator(gc, ADCChannelNum, dt, dtCadre, sigSubarray);
                           sigSubarray = sgfilter.filterData(sigSubarray);
                           gc.setStroke(Color.BLACK);
                           gc.setFill(Color.TRANSPARENT);
                           gc.beginPath();
                           decimator(gc, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                       case "SGFilter":
                           sigSubarray = sgfilter.filterData(sigSubarray);
                           gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.setFill(mainApp.getSignalList().get(nextSignal).getSignalColor());
                           gc.beginPath();
                           decimator(gc, ADCChannelNum, dt, dtCadre, sigSubarray);
                           break;
                   }
               }
            }
        }
    }

    private void drawZeroLines(GraphicsContext gc) {
        //draw X-Y zero lines
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setLineDashes(5);
        gc.beginPath();
        gc.moveTo(0,(int) shiftYZero +0.5);
        gc.lineTo(getWidth(),(int) shiftYZero +0.5);
        gc.stroke();

        gc.beginPath();
        gc.moveTo((int) shiftXZero +0.5,0);
        gc.lineTo((int) shiftXZero +0.5,getHeight());
        if (shiftXZero!=0.0)gc.stroke();
    }

    private void drawmesh(GraphicsContext gc) {

        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.0);
        gc.setLineDashes(3);

        //draw X axis mesh
        double ticksXNext =-shiftXZero/dx;
        double durationX = axes.getXAxis().getUpperBound()-axes.getXAxis().getLowerBound();
        while (ticksXNext < durationX)
        {
            gc.beginPath();
            ticksXNext = ticksXNext +axes.getXAxis().getTickUnit();
            gc.moveTo((int)mapX(ticksXNext,1)+0.5,getHeight());
            gc.lineTo((int)mapX(ticksXNext,1)+0.5,0);
            gc.stroke();
        }

        //draw Y axis mesh
        double ticksYNext = axes.getYAxis().getLowerBound();
        while (ticksYNext < axes.getYAxis().getUpperBound())
        {
            ticksYNext = ticksYNext + axes.getYAxis().getTickUnit();
            gc.beginPath();
            gc.moveTo(0,(int)mapY(ticksYNext)+0.5);
            gc.lineTo(getWidth(),(int)mapY(ticksYNext)+0.5);
            gc.stroke();
        }

    }

    private void decimator(GraphicsContext gc, int ADCChannelNum, double dt, double dtCadre, double[] sigSubarray) {
        int step = (int)(sigSubarray.length/widthProperty().get());

        if (step > 1) {

            for (int i=0;i<sigSubarray.length;i=i+step)
            {
                double [] sigSegment = Arrays.copyOfRange(sigSubarray,
                        i,i+step);
                SimpleMath.findMaxMin(sigSegment);

                if (i == 0) {
                    gc.moveTo(mapX(i, dt) + ADCChannelNum * dx * dtCadre, mapY(sigSegment[i]));
                }
                gc.lineTo(mapX(i+step/2, dt) + ADCChannelNum * dx * dtCadre, mapY(SimpleMath.getMax()));
                gc.lineTo(mapX(i+step/2, dt) + ADCChannelNum * dx * dtCadre, mapY(SimpleMath.getMin()));
            }
        }
        else {
            int x=0;
            for (double y : sigSubarray) {
                if (x == 0) {
                    gc.moveTo(mapX(x, dt) + ADCChannelNum * dx * dtCadre, mapY(y));
                }
                switch (lineOrScatter){
                    case "line":
                        gc.lineTo(mapX(x, dt) + ADCChannelNum * dx * dtCadre, mapY(y));
                        break;
                    case "line+scatter":
                        gc.lineTo(mapX(x, dt) + ADCChannelNum * dx * dtCadre, mapY(y));
                        gc.fillOval(mapX(x, dt) + ADCChannelNum * dx * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                        break;
                    case "scatter":
                        gc.fillOval(mapX(x, dt) + ADCChannelNum * dx * dtCadre-pointSize/2, mapY(y)-pointSize/2,pointSize,pointSize);
                        break;
                }
                x++;
            }
        }
        gc.stroke();
    }
    private double mapX(double x, double dt) {
        return x*dx*dt+shiftXZero;
    }
    private double mapY(double y) {
        return -y * dy+ shiftYZero;
    }

}
