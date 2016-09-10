package adc.data.viewer.ADCPlotter;


import javafx.beans.binding.Bindings;
import javafx.geometry.Side;
import javafx.scene.chart.NumberAxis;
import javafx.scene.layout.Pane;


class Axes extends Pane {
    private NumberAxis xAxis;
    private NumberAxis yAxis;
    private double xAxisOffset;
//    private double yAxisOffset;

     double getXAxisOffset() {
        return xAxisOffset;
    }

     void setXAxisOffset(double XAxisOffset) {
        this.xAxisOffset = XAxisOffset;
    }

     Axes(double xLow, double xHi, double xTickUnit, double yLow, double yHi, double yTickUnit ) {

        xAxis = new NumberAxis("time, ms",xLow, xHi, xTickUnit);
        xAxis.setSide(Side.BOTTOM);
        xAxis.setMinorTickVisible(true);
        xAxis.setMinorTickCount(2);
        xAxis.setAnimated(false);
        xAxisOffset=0.0;

        yAxis = new NumberAxis(yLow, yHi, yTickUnit);
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

     NumberAxis getXAxis() {
        return xAxis;
    }

     NumberAxis getYAxis() {
        return yAxis;
    }


}