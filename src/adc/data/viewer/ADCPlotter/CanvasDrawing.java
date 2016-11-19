package adc.data.viewer.ADCPlotter;

import adc.data.viewer.ADCreader.DataParser;
import adc.data.viewer.MainApp;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.List;

import static java.lang.Math.abs;


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


    public void draw() {
        this.dx = widthProperty().get()/
                (axes.getXAxis().getUpperBound() - axes.getXAxis().getLowerBound());
        this.dy = heightProperty().get()/
                (axes.getYAxis().getUpperBound() - axes.getYAxis().getLowerBound());

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
        double x;

        for (int indexOf : selectedSignals) {
            String label = mainApp.getDataParser().getSignalLabels()[indexOf];
            int xshift = Integer.parseInt(label.substring(label.lastIndexOf('\u0023')+1))-1;

//dt, milliseconds
            double dt = 1.0/(mainApp.getDataParser().getDataParams().getChannelRate()[mainApp.getSignalList().get(indexOf).getFileNumber()]);

            gc.setStroke(mainApp.getSignalList().get(indexOf).getSignalColor());
            x=0.0;
            gc.beginPath();

            double [] sigSubarry = Arrays.copyOfRange(allSignals.getSignals()[indexOf],
                    (int)(axes.getXAxis().getLowerBound()/dt),
                    (int)(axes.getXAxis().getUpperBound()/dt));
            for (double y : sigSubarry) {
                if (x==0.0)gc.moveTo(mapX(x+xshift, dt), mapY(y));
                gc.lineTo(mapX(x+xshift, dt),mapY(y));
                x++;
            }
            gc.stroke();
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
