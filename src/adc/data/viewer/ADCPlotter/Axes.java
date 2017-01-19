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


//import adc.data.viewer.controllers.PlotterSettingController;
import adc.data.viewer.controllers.PlotterSettingController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;

import static java.lang.Math.abs;


public class Axes extends Pane {
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private double xOffsetBasic;
    private double yOffsetBasic;
    private double xTicksAmount;
    private double yTicksAmount;
    private double xTickStepBasic;
    private double yTickStepBasic;
    private double xMinBasic;
    private double xMaxBasic;
    private double yMinBasic;
    private double yMaxBasic;

    public double getxScale() {
        return xScale;
    }

    public double getyScale() {
        return yScale;
    }

    private double xScale;
    private double yScale;

    
    
    public double getXAxisTicksAmount() {
        return xTicksAmount;
    }

    public void setXAxisTicksAmount(double xAxisTicksAmount) {
        this.xTicksAmount = xAxisTicksAmount;
    }

    public double getYAxisTicksAmount() {
        return yTicksAmount;
    }

    public void setYAxisTicksAmount(double yAxisTicksAmount) {
        this.yTicksAmount = yAxisTicksAmount;
    }



     double getXAxisOffset() {
        return xOffsetBasic;
    }

     void setXAxisOffset(double XAxisOffset) {
        this.xOffsetBasic = XAxisOffset;
    }

     Axes(double xMinBasic, double xMaxBasic,  double yMinBasic, double yMaxBasic,  double xTicksAmount, double yTicksAmount) {

         this.xTicksAmount = xTicksAmount;
         this.yTicksAmount = yTicksAmount;
         this.xMinBasic= xMinBasic;
         this.xMaxBasic= xMaxBasic;
         this.yMinBasic= yMinBasic;
         this.yMaxBasic= yMaxBasic;
         setTickStepBasic();
         PlotterSettingController.setAxesSettingsDefault( xMinBasic,xMaxBasic,xTickStepBasic,yMinBasic,yMaxBasic,yTickStepBasic);

        xAxis = new NumberAxis("time, ms", xMinBasic, xMaxBasic, xTickStepBasic);
        xAxis.setLabel("time, ms");
        xAxis.setSide(Side.BOTTOM);
        xAxis.setMinorTickVisible(true);
        xAxis.setMinorTickCount(2);
        xAxis.setAnimated(false);

        yAxis = new NumberAxis("a.u.",yMinBasic, yMaxBasic, yTickStepBasic);
        yAxis.setLabel("a.u.");
        yAxis.setSide(Side.LEFT);
        yAxis.setMinorTickVisible(true);
        yAxis.setMinorTickCount(2);
        yAxis.setAnimated(false);

        xAxis.layoutYProperty().bind(heightProperty());
        xAxis.layoutXProperty().bind(Bindings.subtract((Bindings.subtract(widthProperty(),widthProperty())),1));
        yAxis.layoutYProperty().bind(Bindings.subtract(heightProperty(),yAxis.heightProperty()));
        yAxis.layoutXProperty().bind(Bindings.subtract(0,yAxis.widthProperty()));

        yAxis.prefHeightProperty().bind(heightProperty());
        xAxis.prefWidthProperty().bind(widthProperty());

        getChildren().setAll(xAxis, yAxis);

    }

    public void setAxesBasicSetup( ){

        xAxis.setLowerBound(xMinBasic);
        xAxis.setUpperBound(xMaxBasic);
        yAxis.setLowerBound(yMinBasic);
        yAxis.setUpperBound(yMaxBasic);
        xAxis.setTickUnit(xTickStepBasic);
        yAxis.setTickUnit(yTickStepBasic);
        this.xOffsetBasic =0.0;
        this.yOffsetBasic =0.0;

    }

     NumberAxis getXAxis() {
        return xAxis;
    }

     NumberAxis getYAxis() {
        return yAxis;
    }

private void setTickStepBasic (){
    this.xTickStepBasic=abs(xMaxBasic-xMinBasic)/xTicksAmount;
    if ((yMinBasic<0.0&yMaxBasic<0.0)|(yMinBasic>0.0&yMaxBasic>0.0))
        this.yTickStepBasic=abs(yMinBasic-yMaxBasic)/yTicksAmount;
    else if(yMinBasic==0.0)
        this.yTickStepBasic=abs(yMaxBasic)/yTicksAmount;
    else if (abs(yMinBasic/yMaxBasic)<0.2)
        this.yTickStepBasic=abs(yMinBasic);
    else if (abs(yMaxBasic/yMinBasic)<0.2)
        this.yTickStepBasic=abs(yMaxBasic);
    else
        this.yTickStepBasic=abs(yMinBasic)/yTicksAmount;
}

    private void evaluateAxesTicksSteps(double xTicksAmount, double yTicksAmount){
        
        
//        xAxis.setTickUnit(abs((xAxis.getUpperBound()-xAxis.getLowerBound()) /xTicksAmount));

        if((xAxis.getLowerBound()<0.0&xAxis.getUpperBound()<0.0)|(xAxis.getLowerBound()>0.0&xAxis.getUpperBound()>0.0))
            xAxis.setTickUnit(abs(xAxis.getLowerBound()-xAxis.getUpperBound())/yTicksAmount);
        else if(xAxis.getLowerBound()==0.0)
            xAxis.setTickUnit(abs(xAxis.getUpperBound())/yTicksAmount);
        else if(abs(xAxis.getLowerBound()/xAxis.getUpperBound())<0.2)
            xAxis.setTickUnit(abs(xAxis.getLowerBound()));
        else if(abs(xAxis.getUpperBound()/xAxis.getLowerBound())<0.2)
            xAxis.setTickUnit(abs(xAxis.getUpperBound()));
        else
            xAxis.setTickUnit(abs(xAxis.getLowerBound()) /yTicksAmount);        
        
        if((yAxis.getLowerBound()<0.0&yAxis.getUpperBound()<0.0)|(yAxis.getLowerBound()>0.0&yAxis.getUpperBound()>0.0))
            yAxis.setTickUnit(abs(yAxis.getLowerBound()-yAxis.getUpperBound())/yTicksAmount);
        else if(yAxis.getLowerBound()==0.0)
            yAxis.setTickUnit(abs(yAxis.getUpperBound())/yTicksAmount);
        else if(abs(yAxis.getLowerBound()/yAxis.getUpperBound())<0.2)
            yAxis.setTickUnit(abs(yAxis.getLowerBound()));
        else if(abs(yAxis.getUpperBound()/yAxis.getLowerBound())<0.2)
            yAxis.setTickUnit(abs(yAxis.getUpperBound()));
        else
            yAxis.setTickUnit(abs(yAxis.getLowerBound()) /yTicksAmount);
    }

    public void setAxesBounds (double xMin, double xMax, double xStep, double yMin, double yMax, double yStep){
        xAxis.setLowerBound(xMin);
        xAxis.setUpperBound(xMax);
        xAxis.setTickUnit(xStep);
        yAxis.setLowerBound(yMin);
        yAxis.setUpperBound(yMax);
        yAxis.setTickUnit(yStep);
        setXAxisOffset(xMin);
//        evaluateAxesTicksSteps(xTicksAmount, yTicksAmount);
    }

    public void axesZoomRescale(DoubleProperty zoomTopLeftX, DoubleProperty zoomTopLeftY, DoubleProperty zoomBottomRightX, DoubleProperty zoomBottomRightY)
    {
        double xScale = (xAxis.getUpperBound()-xAxis.getLowerBound()) /xAxis.getWidth();
        double yScale = (yAxis.getUpperBound()-yAxis.getLowerBound()) /yAxis.getHeight();
        double xOffset = (zoomTopLeftX.get())*xScale;
        xAxis.setLowerBound(xAxis.getLowerBound()+ xOffset);
        xAxis.setUpperBound(xAxis.getLowerBound()+ (zoomBottomRightX.get()- zoomTopLeftX.get())*xScale);
        setXAxisOffset(xAxis.getLowerBound());
        double yOffset = (zoomBottomRightY.get()-yAxis.getHeight())*yScale;
        yAxis.setLowerBound(yAxis.getLowerBound()-yOffset);
        yAxis.setUpperBound(yAxis.getLowerBound()+(zoomBottomRightY.get()- zoomTopLeftY.get())*yScale);
        evaluateAxesTicksSteps(xTicksAmount, yTicksAmount);
    }


}