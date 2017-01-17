package adc.data.viewer.ADCPlotter;


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

    public void avaluateAxesTicksSteps(double xTicksAmount, double yTicksAmount){
        xAxis.setTickUnit(abs((xAxis.getUpperBound()-xAxis.getLowerBound()) /xTicksAmount));
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
    }

    public void axesZoomRescale(DoubleProperty zoomTopLeftX, DoubleProperty zoomTopLeftY, DoubleProperty zoomBottomRightX, DoubleProperty zoomBottomRightY)
    {
        double xScale = (xAxis.getUpperBound()-xAxis.getLowerBound()) /xAxis.getWidth();
        double yScale = (yAxis.getUpperBound()-yAxis.getLowerBound()) /yAxis.getHeight();
        xAxis.setLowerBound(xAxis.getLowerBound()+ zoomTopLeftX.get()*xScale);
        xAxis.setUpperBound(xAxis.getLowerBound()+ (zoomBottomRightX.get()- zoomTopLeftX.get())*xScale);
        setXAxisOffset(xAxis.getLowerBound());
        double yOffset = (zoomBottomRightY.get()-yAxis.getHeight())*yScale;
        yAxis.setLowerBound(yAxis.getLowerBound()-yOffset);
        yAxis.setUpperBound(yAxis.getLowerBound()+(zoomBottomRightY.get()- zoomTopLeftY.get())*yScale);
        avaluateAxesTicksSteps(xTicksAmount, yTicksAmount);
    }


}