package pl.grzegorziwanek.altimeter.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * Created by Grzegorz Iwanek on 01.12.2016.
 * Consist extension of external library class GraphView (http://www.android-graphview.org/) and required customized methods
 */
public class GraphViewDrawTask extends GraphView {
    //list of points to draw on a graph screen
    private static LineGraphSeries<DataPoint> sSeries = new LineGraphSeries<>();
    private static int xAxisBorder = 0;

    //override default constructors of the GridView (it's required to prevent errors from compilation); initiate basic settings;
    public GraphViewDrawTask(Context context) {
        super(context);
        setSettings();
    }

    public GraphViewDrawTask(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSettings();
    }

    public GraphViewDrawTask(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setSettings();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    //initial settings, called at least one time (from constructor)
    public void setSettings() {
        //set bounds of graphs manual (graph is not scalable and scrollable by user)
        this.getViewport().setXAxisBoundsManual(true);
        this.getViewport().setMinX(0);
        this.getViewport().setMaxX(120);

        System.out.println("WAS CALLED");
        sSeries.setColor(Color.GREEN);
        sSeries.setThickness(5);
        sSeries.setDrawBackground(true);
        sSeries.setBackgroundColor(Color.RED);

        setFormatLabels("m", "s");
    }

    //update X axis max bound (after refresh screen is resized to fit new value); Y axis is resized automatically;
    private void updateBounds(int xAxisEnd) {
        this.getViewport().setMinX(0);
        this.getViewport().setMaxX(500);

        this.getViewport().setMinY(0);
        this.getViewport().setMaxY(300);
    }

    private void updateXBorderValue(int xBorder)
    {
        System.out.println("THIS X BORDER " + xAxisBorder);
        System.out.println("THIS X BORDER " + xBorder);
        if (xAxisBorder < xBorder) {
            System.out.println("THIS X BORDER " + xAxisBorder);
            xAxisBorder = xBorder;
            System.out.println("THIS X BORDER " + xBorder);
        }
    }

    public void deliverGraph(ArrayList<Double> list) {
        System.out.println("DELIVER GRAPH CALLED");
        //update xAxisBorder
        updateXBorderValue(list.size());

        //if we call to draw for the first time (sSeries is empty, without any data and we add it for a first time to the GraphView Viewport)
        //or use that on button click to create new graph from given data
        if (sSeries.isEmpty()) {
            //TODO->REMOVE LATER
            //define list with DataPoints based on given altitude list
            //ArrayList<DataPoint> pointList = new ArrayList<>();

            int i = 0;
            for (Double point: list) {
                //TODO->REMOVE LATER
                //pointList.add(new DataPoint(i, point+i));
                sSeries.appendData(new DataPoint(i, point), true, xAxisBorder);
                i++;
            }
            //TODO->REMOVE LATER
            //add points to sSeries of graph
            //sSeries = new LineGraphSeries<DataPoint>(pointList.toArray(new DataPoint[]{}));

            //draw sSeries on a graph screen
            this.addSeries(sSeries);
        } else {//sSeries already have some data (case when we update/add new points to graph)
            //TODO -> convert that to use time (sec/min/hours) on X axis
            int xAxis = list.size();
            sSeries.appendData(new DataPoint(xAxis, list.get(xAxis-1)), true, xAxisBorder);
        }

        refreshGraphLook(list.size());
    }

    //TODO->fix X axis positions
    public void deliverGraphOnResume(ArrayList<Double> list) {
        System.out.println("DELIVER GRAPH ON RESUME CALLED");
        System.out.println("SIZE OF LIST ON RESUME " + list.size());
        //update xAxisBorder
        updateXBorderValue(list.size());

//        int i = getSeries().size();
        System.out.println("CHECK SERIES BEFORE CLEAR " + this.getSeries());
        //this.getSeries().clear();
        //TODO->REMOVE LATER
        //define list with DataPoints based on given altitude list
        //ArrayList<DataPoint> pointList = new ArrayList<>();

//        for (Double point: list) {
//            //TODO->REMOVE LATER
//            //pointList.add(new DataPoint(i, point+1));
//            sSeries.appendData(new DataPoint(i, point), true, xAxisBorder);
//            i++;
//        }
        //TODO->REMOVE LATER
        //add points to sSeries of graph
        //sSeries = new LineGraphSeries<DataPoint>(pointList.toArray(new DataPoint[]{}));
        System.out.println("CHECK SERIES AFTER CLEAR "+ this.getSeries());
        this.addSeries(sSeries);
        System.out.println("CHECK SERIES AFTER ADDED ONRESUME " + this.getSeries());

        refreshGraphLook(list.size());
    }

    private void refreshGraphLook(int xBound) {
        //change X axis max bound to new value (added new point to graph, fixed bounds have to be changed)
        updateBounds(xBound);

        //update Graph screen
        refreshDrawableState();
    }

    public void setFormatLabels(String yFormat, String xFormat) {
        final String axisYFormat = yFormat;
        final String axisXFormat = xFormat;

        getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {

            @Override
            public String formatLabel(double isValueAxisY, boolean isValueAxisX) {
                if (isValueAxisX) {
                    //show on X axis
                    return super.formatLabel(isValueAxisY, isValueAxisX) + axisXFormat;
                } else {
                    //show on Y axis
                    return super.formatLabel(isValueAxisY, isValueAxisX) + axisYFormat;
                }
            }
        });
    }
}