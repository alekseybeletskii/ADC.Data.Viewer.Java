/*******************************************************************************
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
 ******************************************************************************/

package adc.data.viewer.ADCPlotter;

import adc.data.viewer.ADCreader.DataParser;
import adc.data.viewer.MainApp;
import adc.data.viewer.dataProcessing.SavitzkyGolayFilter;
import adc.data.viewer.dataProcessing.SimpleMath;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

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
    private final List<Integer> selectedSignals;
    private final MainApp mainApp;
    private double dx;
    private double dy;
    private double shiftZero;
    private String plotType;
    private SavitzkyGolayFilter sgfilter;

    public void setPlotType(String plotType) {
        this.plotType = plotType;
    }

    public void setSGfilter(SavitzkyGolayFilter sgfilter) {
        this.sgfilter = sgfilter;
    }

    public double getShiftZero() {
        return shiftZero;
    }



    CanvasDrawing(MainApp mainApp, Axes axes, List<Integer> selectedSignals, String plotType) {
        this.plotType =plotType;
        this.mainApp = mainApp;
        this.axes = axes;
        this.allSignals = mainApp.getDataParser();
        this.selectedSignals = selectedSignals;
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

        this.shiftZero = axes.getYAxis().getUpperBound()/abs((axes.getYAxis().getUpperBound()
                - axes.getYAxis().getLowerBound())/getHeight())
                +1;

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, getWidth(), getHeight());
        gc.setFill(Color.TRANSPARENT);
        gc.fillRect(0,0,getWidth(), getHeight());
//draw mesh
        drawmesh();
//draw zero line
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.setLineDashes(3);
        gc.beginPath();
        gc.moveTo(0,(int)shiftZero+0.5);
        gc.lineTo(getWidth(),(int)shiftZero+0.5);
        gc.stroke();
//draw data
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.setLineDashes(0);


        for (int nextSignal : selectedSignals) {
            String label = mainApp.getDataParser().getSignalLabels()[nextSignal];

            int xshift = Integer.parseInt(label.substring(label.lastIndexOf('\u0023')+1))-1;


//dt,dtCadre in milliseconds
            double dt = 1.0/(mainApp.getDataParser().getDataParams().getChannelRate()[mainApp.getSignalList().get(nextSignal).getFileNumber()]);
            double dtCadre = mainApp.getDataParser().getDataParams().getInterCadreDelay()[mainApp.getSignalList().get(nextSignal).getFileNumber()];

            double [] sigSubarray = Arrays.copyOfRange(allSignals.getSignals()[nextSignal],
                    (int)round(axes.getXAxis().getLowerBound()/dt),
                    (int)round(axes.getXAxis().getUpperBound()/dt));

            switch (plotType){
                case "Raw":
                    gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                    gc.beginPath();
                    decimator(gc, xshift, dt, dtCadre, sigSubarray);
                    break;
                case "SGFiltered":
                    gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                    gc.beginPath();
                    int i=0;
                    for (double yy : sgfilter.filterData(sigSubarray)){
                        sigSubarray[i]=sigSubarray[i]-yy;
                        i++;
                    }
                    decimator(gc, xshift, dt, dtCadre, sigSubarray);
                    break;
                case "RawAndSGFilter":
                    gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                    gc.beginPath();
                    decimator(gc, xshift, dt, dtCadre, sigSubarray);
                    sigSubarray = sgfilter.filterData(sigSubarray);
                    gc.setStroke(Color.BLACK);
                    gc.beginPath();
                    decimator(gc, xshift, dt, dtCadre, sigSubarray);
                    break;
                case "SGFilter":
                    sigSubarray = sgfilter.filterData(sigSubarray);
                    gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());
                    gc.beginPath();
                    decimator(gc, xshift, dt, dtCadre, sigSubarray);
                    break;
            }
        }
    }

    private void drawmesh() {
        GraphicsContext gc = getGraphicsContext2D();

        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1.0);
        gc.setLineDashes(3);

        //draw X axis mesh
        double ticksXNext =0;
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

    private void decimator(GraphicsContext gc, int xshift, double dt, double dtCadre, double[] sigSubarray) {
        int step = (int)(sigSubarray.length/widthProperty().get());

        if (step > 1) {

            for (int i=0;i<sigSubarray.length;i=i+step)
            {
                double [] sigSegment = Arrays.copyOfRange(sigSubarray,
                        i,i+step);
                SimpleMath.findMaxMin(sigSegment);

                if (i == 0) {
                    gc.moveTo(mapX(i, dt) + xshift * dx * dtCadre, mapY(sigSegment[i]));
                }
                gc.lineTo(mapX(i+step/2, dt) + xshift * dx * dtCadre, mapY(SimpleMath.getMax()));
                gc.lineTo(mapX(i+step/2, dt) + xshift * dx * dtCadre, mapY(SimpleMath.getMin()));

            }
        }
        else {
            int x=0;
            for (double y : sigSubarray) {
                if (x == 0) {
                    gc.moveTo(mapX(x, dt) + xshift * dx * dtCadre, mapY(y));
                }
                gc.lineTo(mapX(x, dt) + xshift * dx * dtCadre, mapY(y));
                x++;
            }
        }
        gc.stroke();
    }
    private double mapX(double x, double dt) {
        return x*dx*dt;
    }
    private double mapY(double y) {
        return -y * dy+shiftZero;
    }

}
