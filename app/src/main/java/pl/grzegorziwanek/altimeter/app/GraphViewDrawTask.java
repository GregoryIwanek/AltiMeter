package pl.grzegorziwanek.altimeter.app;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * Created by Grzegorz Iwanek on 01.12.2016.
 * Consist extension of external library class GraphView (http://www.android-graphview.org/) and required customized methods
 */
public class GraphViewDrawTask extends GraphView
{
    //override default constructors of the GridView (it's required to prevent errors from compilation); initiate basic settings;
    public GraphViewDrawTask(Context context) {super(context); setSettings();}
    public GraphViewDrawTask(Context context, AttributeSet attrs) {super(context, attrs); setSettings();}
    public GraphViewDrawTask(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle); setSettings();}

    //list of points to draw on a graph screen
    private static LineGraphSeries<DataPoint> sSeries = new LineGraphSeries<>();

    @Override
    protected void onDraw(Canvas canvas) {super.onDraw(canvas);}

    @Override
    public boolean onTouchEvent(MotionEvent event) {return super.onTouchEvent(event);}

    //initial settings, called at least one time (from constructor)
    public void setSettings()
    {
        //set bounds of graphs manual (graph is not scalable and scrollable by user)
        this.getViewport().setXAxisBoundsManual(true);
        this.getViewport().setMinX(0);
        this.getViewport().setMaxX(120);
    }

    //update X axis max bound (after refresh screen is resized to fit new value); Y axis is resized automatically;
    public void updateBounds(int xAxisEnd)
    {
        this.getViewport().setMinX(0);
        //this.getViewport().setMaxX(xAxisEnd);
        this.getViewport().setMaxX(120);
    }

    //initiate graph drawing task (need: altitude, date/time when measure occurred, min and max altitude recorded)
    public void performDrawingTask(ArrayList<Location> locationsList)
    {

    }

    public void deliverGraph(ArrayList<Double> list)
    {
        //if we call to draw for the first time (sSeries is empty, without any data and we add it for a first time to the GraphView Viewport)
        //or use that on button click to create new graph from given data
        if (sSeries.isEmpty())
        {
            //define list with DataPoints based on given altitude list
            ArrayList<DataPoint> pointList = new ArrayList<>();
            int i = 0;
            for (Double point: list)
            {
                pointList.add(new DataPoint(i, point));
                i++;
            }
            //add points to sSeries of graph
            sSeries = new LineGraphSeries<DataPoint>(pointList.toArray(new DataPoint[]{}));

            //draw sSeries on a graph screen
            this.addSeries(sSeries);
        }
        //sSeries already have some data (case when we update/add new points to graph)
        else
        {
            //TODO -> convert that to use time (sec/min/hours) on X axis
            int xAxis = list.size();
            sSeries.appendData(new DataPoint(xAxis, list.get(xAxis-1)), true, list.size());
        }

        refreshGraphLook(list.size());
    }

    public void deliverGraphOnResume(ArrayList<Double> list)
    {
        this.getSeries().clear();

        //define list with DataPoints based on given altitude list
        ArrayList<DataPoint> pointList = new ArrayList<>();
        int i = 0;
        for (Double point: list)
        {
            pointList.add(new DataPoint(i, point));
            i++;
        }
        //add points to sSeries of graph
        sSeries = new LineGraphSeries<DataPoint>(pointList.toArray(new DataPoint[]{}));
        this.addSeries(sSeries);

        refreshGraphLook(list.size());
    }

    //TODO->change listsize name
    private void refreshGraphLook(int xBound)
    {
        //change X axis max bound to new value (added new point to graph, fixed bounds have to be changed)
        updateBounds(xBound);

        //update Graph screen
        refreshDrawableState();
    }
}
