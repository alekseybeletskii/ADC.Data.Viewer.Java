package adc.data.viewer.ADCPlotter;

import adc.data.viewer.MainApp;
import adc.data.viewer.controllers.PlotterController;
import adc.data.viewer.dataProcessing.SimpleMath;
import adc.data.viewer.model.SignalMarker;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;

import static java.lang.Math.abs;

public class PlotsBuilder extends AnchorPane {

    private  List<Integer>  selectedSignals;
    private  Axes axes;
    private  CanvasDrawing canvas;
    private double xMinBasic;
    private double xMaxBasic;
    private double yMinBasic;
    private double yMaxBasic;
    private Rectangle zoomRectangle;


    public CanvasDrawing getCanvas() {
        return canvas;
    }

    public PlotsBuilder(MainApp mainApp, AnchorPane axesAnchorPane, PlotterController plotterController)
    {
        this.selectedSignals = new ArrayList<>();
        this.zoomRectangle =null;
        buildAxes(mainApp);
        canvas = new CanvasDrawing(mainApp, axes, selectedSignals);
        canvas.widthProperty().bind(axes.getXAxis().widthProperty());
        canvas.heightProperty().bind(axes.getYAxis().heightProperty());

        axesAnchorPane.getChildren().add(this);
        AnchorPane.setLeftAnchor(this, 50.0);
        AnchorPane.setRightAnchor(this, 50.0);
        AnchorPane.setBottomAnchor(this, 50.0);
        AnchorPane.setTopAnchor(this, 30.0);

        this.setStyle("-fx-background-color: rgb(255, 255, 255);");

        getChildren().addAll(axes, canvas);
        AnchorPane.setLeftAnchor(canvas, 0.0);
        AnchorPane.setRightAnchor(canvas, 0.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);
        AnchorPane.setTopAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(axes, 0.0);
        AnchorPane.setRightAnchor(axes, 0.0);
        AnchorPane.setBottomAnchor(axes, 0.0);
        AnchorPane.setTopAnchor(axes, 0.0);

        showMouseXY(plotterController);
        zoom();
    }

    private void buildAxes(MainApp mainApp) {

        double longestTime =0; // longest signal, ms
        double minYValue =0;
        double maxYValue =0;
        double xTicksStepBasic = 0.01;
        double yTicksStepBasic = 0.01;

        long samples;
        double dt; // time scale of longest amongst selected signal, ms

        //detect longest signal
        for (SignalMarker signalMarker : mainApp.getSignalList()){

            if (signalMarker.getSignalSelected())
            {
                dt = 1.0/(mainApp.getDataParser().getDataParams().getChannelRate()[signalMarker.getFileNumber()]);
                samples = mainApp.getDataParser().getDataParams().getRealCadresQuantity()[signalMarker.getFileNumber()];
                selectedSignals.add(signalMarker.getSignalIndex());
                if (samples*dt > longestTime) {
                    longestTime = samples*dt;
                }

                double[] testSignal = mainApp.getDataParser().getSignals()[signalMarker.getSignalIndex()];
                SimpleMath.findMaxMin(testSignal);
                if (SimpleMath.getMax()>maxYValue) maxYValue=SimpleMath.getMax();
                if (SimpleMath.getMin()<minYValue) minYValue=SimpleMath.getMin();
            }

        }
        xMinBasic = 0.0;
        xMaxBasic = longestTime; // milliseconds
        yMinBasic = minYValue;
        yMaxBasic = maxYValue;

        this.axes = new Axes(xMinBasic, xMaxBasic, xTicksStepBasic,yMinBasic, yMaxBasic, yTicksStepBasic);

        setAxesTicksSteps(10,10); // can be realized as option to set by user
    }

    private void setAxesTicksSteps(double xTicksAmount, double yTicksAmount){
        axes.getXAxis().setTickUnit(abs((axes.getXAxis().getUpperBound()-axes.getXAxis().getLowerBound()) /xTicksAmount));
        if((axes.getYAxis().getLowerBound()<0.0&axes.getYAxis().getUpperBound()<0.0)|(axes.getYAxis().getLowerBound()>0.0&axes.getYAxis().getUpperBound()>0.0))
        {
            axes.getYAxis().setTickUnit(abs(axes.getYAxis().getLowerBound()-axes.getYAxis().getUpperBound())/yTicksAmount);
        }
        else if(axes.getYAxis().getLowerBound()==0.0)
        {
            axes.getYAxis().setTickUnit(abs(axes.getYAxis().getUpperBound())/yTicksAmount);
        }
        else if(abs(axes.getYAxis().getLowerBound()/axes.getYAxis().getUpperBound())<0.2)
        {
            axes.getYAxis().setTickUnit(abs(axes.getYAxis().getLowerBound()));
        }
        else {
            axes.getYAxis().setTickUnit(abs(axes.getYAxis().getLowerBound()) /yTicksAmount);
        }
    }

    private void zoom() {
        DoubleProperty zoomTopLeftX = new SimpleDoubleProperty();
        DoubleProperty zoomTopLeftY = new SimpleDoubleProperty();
        DoubleProperty zoomBottomRightX = new SimpleDoubleProperty();
        DoubleProperty zoomBottomRightY = new SimpleDoubleProperty();

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
        });


        canvas.setOnMouseDragged(mdragged -> {
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
        });

        canvas.setOnMouseReleased(mreleased -> {

            if(zoomRectangle.getWidth()==0.0&zoomRectangle.getHeight()==0.0)
            {
                axes.getXAxis().setLowerBound(xMinBasic);
                axes.getXAxis().setUpperBound(xMaxBasic);
                axes.getYAxis().setLowerBound(yMinBasic);
                axes.getYAxis().setUpperBound(yMaxBasic);
                setAxesTicksSteps(10,10);
                axes.setXAxisOffset(0.0);

                this.getChildren().remove(zoomRectangle);
                zoomRectangle = null;
                canvas.draw();
            }
            else if(zoomRectangle.getWidth()!=0.0&zoomRectangle.getHeight()!=0.0){

                double xScale = (axes.getXAxis().getUpperBound()-axes.getXAxis().getLowerBound()) /axes.getXAxis().getWidth();
                double yScale = (axes.getYAxis().getUpperBound()-axes.getYAxis().getLowerBound()) /axes.getYAxis().getHeight();
                axes.getXAxis().setLowerBound(axes.getXAxis().getLowerBound()+ zoomTopLeftX.get()*xScale);
                axes.getXAxis().setUpperBound(axes.getXAxis().getLowerBound()+ zoomRectangle.getWidth()*xScale);
                axes.setXAxisOffset(axes.getXAxis().getLowerBound());
                double yOffset = (zoomBottomRightY.get()-axes.getYAxis().getHeight())*yScale;
                axes.getYAxis().setLowerBound(axes.getYAxis().getLowerBound()-yOffset);
                axes.getYAxis().setUpperBound(axes.getYAxis().getLowerBound()+zoomRectangle.getHeight()*yScale);

                setAxesTicksSteps(10,10);

                this.getChildren().remove(zoomRectangle);
                zoomRectangle = null;
                canvas.draw();
            }
        });
    }

    private void showMouseXY(PlotterController plotterController) {
        this.setOnMouseMoved(event -> {
            double xScale = (axes.getXAxis().getUpperBound()-axes.getXAxis().getLowerBound()) /axes.getXAxis().getWidth();
            double yScale = (axes.getYAxis().getUpperBound()-axes.getYAxis().getLowerBound()) /axes.getYAxis().getHeight();
            String coordinatesAxes = String.format("x= %.4f ; y= %.4f",
                    event.getX()* xScale+axes.getXAxisOffset(),
                    -(event.getY()-canvas.getShiftZero()-1)* yScale);
            plotterController.getXyLabel().setText(coordinatesAxes);
        });
    }

}