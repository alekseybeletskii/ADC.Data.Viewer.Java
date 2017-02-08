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

import adc.data.viewer.ui.MainApp;
import adc.data.viewer.ui.PlotterController;
import adc.data.viewer.ui.PlotterSettingController;
import adc.data.viewer.processing.SimpleMath;
import adc.data.viewer.model.SignalMarker;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import static java.lang.Math.abs;

    public class PlotsBuilder extends AnchorPane {

    private  Axes axes;
    private CanvasDrawing canvas;
    private PlotterController plotterController;
    private Rectangle zoomRectangle;

    public CanvasDrawing getCanvas() {
        return canvas;
    }
    public Axes getAxes() {
        return axes;
    }

    public PlotsBuilder(MainApp mainApp, AnchorPane axesAnchorPane, PlotterController plotterController){
        this.plotterController=plotterController;

        this.zoomRectangle =null;
        buildAxes(mainApp);
        canvas = new CanvasDrawing(mainApp, axes,  "Raw");
        canvas.widthProperty().bind(axes.getXAxis().widthProperty());
        canvas.heightProperty().bind(axes.getYAxis().heightProperty());

        axesAnchorPane.getChildren().add(this);
        AnchorPane.setLeftAnchor(this, 70.0);
        AnchorPane.setRightAnchor(this, 50.0);
        AnchorPane.setBottomAnchor(this, 50.0);
        AnchorPane.setTopAnchor(this, 30.0);

//        this.setStyle("-fx-background-color: rgb(255, 255, 255);");

        getChildren().addAll(axes, canvas);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(axes, 0.0);
        AnchorPane.setRightAnchor(axes, 0.0);
        AnchorPane.setBottomAnchor(axes, 0.0);
        AnchorPane.setTopAnchor(axes, 0.0);

        showMouseXY();
        zoom();
    }

    private void buildAxes(MainApp mainApp) {

        PlotterSettingController.setSGFilterSettingsDefault(50,50,1);
        PlotterSettingController.setSpectrogramSettingsDefault(256,"Hanning",50);

        double longestTime =0; // longest signal, ms
        double minYValue =0;
        double maxYValue =0;
        long samples;
        double dt; // time scale of longest amongst selected signal, ms

        //detect longest signal
        for (SignalMarker signalMarker : mainApp.getSignalList()){

            if (signalMarker.getSignalSelected())
            {
                dt = 1.0/(mainApp.getDataParser().getDataParams().getChannelRate()[signalMarker.getFileNumber()]);
                samples = mainApp.getDataParser().getDataParams().getRealCadresQuantity()[signalMarker.getFileNumber()];

                if (samples*dt > longestTime) {
                    longestTime = samples*dt;
                }
                double[] testSignal = mainApp.getDataParser().getSignals()[signalMarker.getSignalIndex()];
                SimpleMath.findMaxMin(testSignal);
                if (SimpleMath.getMax()>maxYValue) maxYValue=SimpleMath.getMax();
                if (SimpleMath.getMin()<minYValue) minYValue=SimpleMath.getMin();
            }
        }

        double absMaxYValue = abs(maxYValue)>abs(minYValue)?abs(maxYValue):abs(minYValue);
        double xMinBasic = 0.0;
        double xMaxBasic = longestTime; // milliseconds
        double xAxisTicksAmount =10;
        double yAxisTicksAmount =10;

        this.axes = new Axes(xMinBasic, xMaxBasic, -absMaxYValue, absMaxYValue, xAxisTicksAmount,yAxisTicksAmount);

    }

    private void zoom() {
        DoubleProperty zoomTopLeftX = new SimpleDoubleProperty();
        DoubleProperty zoomTopLeftY = new SimpleDoubleProperty();
        DoubleProperty zoomBottomRightX = new SimpleDoubleProperty();
        DoubleProperty zoomBottomRightY = new SimpleDoubleProperty();
        DoubleProperty panStartX = new SimpleDoubleProperty();
        DoubleProperty panStartY = new SimpleDoubleProperty();

        canvas.setOnMousePressed(mpressed -> {
            if(mpressed.getButton()== MouseButton.PRIMARY){
                zoomTopLeftX.set(mpressed.getX());
                zoomTopLeftY.set(mpressed.getY());
                zoomRectangle = new Rectangle(mpressed.getX(),mpressed.getY(),0.0,0.0);
                zoomRectangle.setFill(Color.TRANSPARENT);
                zoomRectangle.setStroke(Color.BLACK);
                zoomRectangle.setStrokeType(StrokeType.OUTSIDE);
                zoomRectangle.setStrokeWidth(1);
                zoomRectangle.getStrokeDashArray().addAll(5.0, 5.0);
                this.getChildren().add(zoomRectangle);
            }
            if(mpressed.getButton()== MouseButton.SECONDARY){
                panStartX.set(mpressed.getX());
                panStartY.set(mpressed.getY());
            }
        });


        canvas.setOnMouseDragged(mdragged -> {

            setMouseXYText(mdragged);

            if(zoomRectangle!=null){
                zoomBottomRightX.set(mdragged.getX());
                zoomBottomRightY.set(mdragged.getY());
                double recWidth = zoomBottomRightX.get()- zoomTopLeftX.get();
                double recHeight = zoomBottomRightY.get()- zoomTopLeftY.get();
                if(recWidth>0&recHeight>0
                        &&(zoomRectangle.getX()+recWidth)<this.getWidth()
                        &&(zoomRectangle.getY()+recHeight)<this.getHeight()
                        )
                {
                    zoomRectangle.setWidth(recWidth);
                    zoomRectangle.setHeight(recHeight);
                }
                if(recWidth<0&recHeight<0){
                    zoomRectangle.setWidth(0.0);
                    zoomRectangle.setHeight(0.0);
                }
            }

            if(mdragged.getButton()== MouseButton.SECONDARY){

                double dxPan = mdragged.getX()-panStartX.get();
                double dyPan = mdragged.getY()-panStartY.get();
                axes.axesPanRescale(dxPan,dyPan);
                panStartX.set(mdragged.getX());
                panStartY.set(mdragged.getY());

                this.getScene().setCursor(Cursor.OPEN_HAND);
                canvas.draw();

            }
        });

        canvas.setOnMouseReleased(mreleased -> {
            if(mreleased.getButton()== MouseButton.PRIMARY) {
                if (zoomRectangle.getWidth() == 0.0 & zoomRectangle.getHeight() == 0.0) {
                    axes.setAxesBasicSetup();
                    this.getChildren().remove(zoomRectangle);
                    zoomRectangle = null;
                    canvas.draw();
                } else if (zoomRectangle.getWidth() != 0.0 & zoomRectangle.getHeight() != 0.0) {

                    axes.axesZoomRescale(zoomTopLeftX, zoomTopLeftY, zoomBottomRightX, zoomBottomRightY);
                    this.getChildren().remove(zoomRectangle);
                    zoomRectangle = null;

                    canvas.draw();
                }
            }
            this.getScene().setCursor(Cursor.DEFAULT);

        });
    }

    private void showMouseXY() {
        this.setOnMouseMoved(this::setMouseXYText);
    }

    private void setMouseXYText(MouseEvent event) {
        double xScale = (axes.getXAxis().getUpperBound()-axes.getXAxis().getLowerBound()) /axes.getXAxis().getWidth();
        double yScale = (axes.getYAxis().getUpperBound()-axes.getYAxis().getLowerBound()) /axes.getYAxis().getHeight();
        String coordinatesAxes = String.format("x= %.4f ; y= %.4f",
                event.getX()* xScale+axes.getXAxisOffset(),
                -(event.getY()-canvas.getShiftYZero()-1)* yScale);
        plotterController.getXyLabel().setText(coordinatesAxes);
    }

}