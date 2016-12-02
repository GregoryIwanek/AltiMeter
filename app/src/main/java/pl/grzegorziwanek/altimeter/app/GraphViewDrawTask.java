package pl.grzegorziwanek.altimeter.app;

import android.content.Context;
import android.graphics.Canvas;
import android.location.Location;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 * Created by Grzegorz Iwanek on 01.12.2016.
 * Consist extension of external library class GraphView (http://www.android-graphview.org/) and required customized methods
 *
 */
public class GraphViewDrawTask extends GraphView
{
    public GraphViewDrawTask(Context context) {super(context);}
    public GraphViewDrawTask(Context context, AttributeSet attrs) {super(context, attrs);}
    public GraphViewDrawTask(Context context, AttributeSet attrs, int defStyle) {super(context, attrs, defStyle);}

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

    public void deliverGraph()
    {
        //TODO->remove from here, just to check after clicking button
        //required classes imported from com.jjoe64, not from google service
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 12),
                new DataPoint(1, 1),
                new DataPoint(2, 5),
                new DataPoint(4, 12),
                new DataPoint(6, 11),
                new DataPoint(8, 12),
                new DataPoint(9, 1),
                new DataPoint(10, 5),
                new DataPoint(11, 12),
                new DataPoint(12, 11),
                new DataPoint(67, 100),
        });

        //this.addSeries(series);
        this.addSeries(series);
        series.appendData(new DataPoint(100,22), false, 10);
        this.refreshDrawableState();
    }
}
