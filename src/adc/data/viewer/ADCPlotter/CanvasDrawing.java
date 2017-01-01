package adc.data.viewer.ADCPlotter;

import adc.data.viewer.ADCreader.DataParser;
import adc.data.viewer.MainApp;
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



    public double getShiftZero() {
        return shiftZero;
    }

    private  double shiftZero;


    CanvasDrawing(MainApp mainApp, Axes axes, List<Integer> selectedSignals) {
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
     * due to simple point-per-pixel approach
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

            gc.setStroke(mainApp.getSignalList().get(nextSignal).getSignalColor());

            gc.beginPath();

            double [] sigSubarray = Arrays.copyOfRange(allSignals.getSignals()[nextSignal],
                    (int)round(axes.getXAxis().getLowerBound()/dt),
                    (int)round(axes.getXAxis().getUpperBound()/dt));

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
                gc.stroke();

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
                gc.stroke();

            };
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
    private double mapX(double x, double dt) {
        return x*dx*dt;
    }

    private double mapY(double y) {
        return -y * dy+shiftZero;
    }

}
