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
    //override default constructors of the GridView (it's required to prevent errors from compilation)
    public GraphViewDrawTask(Context context) {super(context);}
    public GraphViewDrawTask(Context context, AttributeSet attrs) {super(context, attrs);}
    public GraphViewDrawTask(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}

    //list of points to draw on a graph
    LineGraphSeries<DataPoint> series = new LineGraphSeries<>();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    //initiate graph drawing task (need: altitude, date/time when measure occurred, min and max altitude recorded)
    public void performDrawingTask(ArrayList<Location> locationsList)
    {

    }

    public void deliverGraph(ArrayList<Double> list)
    {
        //if we call to draw for the first time (series is empty, without any data and we add it for a first time)
        if (series.isEmpty())
        {
            //define list with DataPoints based on given altitude list
            ArrayList<DataPoint> pointList = new ArrayList<>();
            int i = 0;
            for (Double point: list)
            {
                pointList.add(new DataPoint(i, point));
                i++;
            }
            //add points to series
            series = new LineGraphSeries<DataPoint>(pointList.toArray(new DataPoint[]{}));

            //draw graph on a screen
            this.addSeries(series);
        }
        //series already have some data (case when we update/add new points to graph)
        else
        {
            int xAxis = list.size();
            series.appendData(new DataPoint(xAxis, list.get(xAxis-1)), true, list.size());

            //update Graph screen
            this.refreshDrawableState();
        }

        this.getViewport().setScalable(true);
    }
}
