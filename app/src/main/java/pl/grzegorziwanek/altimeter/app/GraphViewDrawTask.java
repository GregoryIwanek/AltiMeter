package pl.grzegorziwanek.altimeter.app;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
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
    private static Long sRecordStartTime = null;
    private static Integer sMaxElevation = 0;
    private static Integer sMinElevation = 0;

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

        System.out.println("SET DIAGRAM APPEARANCE SETTINGS WAS CALLED");
        int color = Color.argb(50, 0, 255, 255);
        sSeries.setColor(Color.GREEN);
        sSeries.setThickness(5);
        sSeries.setDrawBackground(true);
        sSeries.setBackgroundColor(color);

        setFormatLabels("m", "s");
    }

    //update X axis max bound (after refresh screen is resized to fit new value); Y axis is resized automatically;
    private void updateBounds(int xAxisEnd) {
        this.getViewport().setMinX(0);
        this.getViewport().setMaxX(120);

        //TODO-> implement belows
        //TODO-> add catching time of location record to onLocationChanged method
        //-> shared preferences / preferences -> user sets preferred time period to show on diagram (ex 1 hour, 2 hours etc.)
        //-> here, depending on chosen preferred time set xAxis border as the set of numbers converted to seconds/minutes/hours
        //-> define x position of points by using difference of time between recordings of two points
        //-> example: point 1, measured 18:00 -> position x=0; point 2, measured 18:01, position x=60 ( 1 sec == 1 unit on diagram)
        //-> so diagram with 1 hour will have xAxis border at 60min*60sec = 3600 units

        this.getViewport().setYAxisBoundsManual(true);
        this.getViewport().setMinY(sMinElevation*0.75);
        this.getViewport().setMaxY(sMaxElevation*1.25);
    }

    private void updateYBounds(int yToCheck) {
        if (sMaxElevation == null || sMinElevation == null){
            sMaxElevation = yToCheck;
            sMinElevation = yToCheck;
        }else {
            if(sMaxElevation < yToCheck){
                sMaxElevation = yToCheck;
            }
            if(sMinElevation > yToCheck){
                sMinElevation = yToCheck;
            }
        }
    }

    public void deliverGraph(ArrayList<Location> locationsList) {
        System.out.println("DELIVER GRAPH CALLED");
        if (sRecordStartTime == null){
            sRecordStartTime = locationsList.get(0).getTime();
        }

        //if we call to draw for the first time (sSeries is empty, without any data and we add it for a first time to the GraphView Viewport)
        //or use that on button click to create new graph from given data
        if (sSeries.isEmpty()) {
            for (int i=0; i<locationsList.size(); i++){
                if (i > 0) {
                    Long timeBetweenRecords = (locationsList.get(i).getTime() - sRecordStartTime)/1000;
                    DataPoint graphPoint = new DataPoint(timeBetweenRecords, locationsList.get(i).getAltitude());
                    sSeries.appendData(graphPoint, true, locationsList.size());
                    updateYBounds((int) graphPoint.getY());
                }else {
                    DataPoint graphPoint = new DataPoint(i, locationsList.get(i).getAltitude());
                    sSeries.appendData(graphPoint, true, locationsList.size());
                    updateYBounds((int) graphPoint.getY());
                }
            }
            //draw sSeries on a graph screen
            this.addSeries(sSeries);
        } else {
            //sSeries already have some data (case when we update/add new points to graph)
            //TODO -> convert that to use time (sec/min/hours) on X axis
            if (locationsList.size() > 1){
                int listSize = locationsList.size();
                Long timeBetweenRecords = (locationsList.get(listSize-1).getTime() - sRecordStartTime)/1000;
                DataPoint graphPoint = new DataPoint(timeBetweenRecords, locationsList.get(listSize-1).getAltitude());
                sSeries.appendData(graphPoint, true, listSize);
                updateYBounds((int) graphPoint.getY());
            }else {
                System.out.println("WRONG LOCATIONSLIST SIZE, HAS TO BE BIGGER THAN 1");
            }
        }
        refreshGraphLook(locationsList.size());
    }

    public void deliverGraphOnResume(int listSize) {
        System.out.println("DELIVER GRAPH ON RESUME CALLED");
        this.addSeries(sSeries);

//        if(listSize > 0){
//            refreshGraphLook(listSize);
//        }
    }

    private void refreshGraphLook(int xBound) {
        //change X axis max bound to new value (added new point to graph, fixed bounds have to be changed)
        updateBounds(xBound);

        //update Graph screen
        refreshDrawableState();
    }

    private void clearView(){
        this.getSeries().clear();
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