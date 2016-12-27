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
    private LineGraphSeries<DataPoint> mSeries = new LineGraphSeries<>();
    private Long mStartTimeRecord = null;
    private Integer mMaxElevation = 0;
    private Integer mMinElevation = 0;

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

    private void setSettings() {
        setGraphBounds();
        defineDiagramAppearance();

        setFormatLabels("m", "s");
    }

    private void setGraphBounds(){
        setChangeBoundsManually();
        initiateGraphViewBounds();
        setGraphBoundsValues();
    }

    private void setChangeBoundsManually(){
        this.getViewport().setXAxisBoundsManual(true);
        this.getViewport().setYAxisBoundsManual(true);
    }

    private void initiateGraphViewBounds(){
        this.getViewport().setMinX(0);
        this.getViewport().setMaxX(100);
        this.getViewport().setMinY(0);
        this.getViewport().setMaxY(100);
    }

    private void setGraphBoundsValues(){
        setXBoundsValues();
        setYBoundsValues();
    }

    private void setXBoundsValues(){
        if(mSeries.getHighestValueX() > this.getViewport().getMaxX(false)*0.8){
            int xRange = getNewXBoundsRange();
            this.getViewport().setMaxX(xRange);
        }
    }

    private int getNewXBoundsRange(){
        int newXRange = (int) (mSeries.getHighestValueX() * 1.2);
        return newXRange;
    }

    private void setYBoundsValues(){
        if(mSeries.getHighestValueY() > this.getViewport().getMaxY(false)*0.8){
            int yMaxRange = getNewYMaxBoundsRange();
            this.getViewport().setMaxY(yMaxRange);
        }
        if(mSeries.getLowestValueY() > this.getViewport().getMinY(false)*(0.8)){
            int yMinRange = getNewYMinBoundsRange();
            this.getViewport().setMinY(yMinRange);
        }
    }

    private int getNewYMaxBoundsRange(){
        int newYMaxRange = (int) (mSeries.getHighestValueY() * 1.2);
        return newYMaxRange;
    }

    private int getNewYMinBoundsRange(){
        //TODO->work on below zero values
        if(mSeries.getLowestValueY() < 0){
            int newYMinRange = (int) (mSeries.getLowestValueY() - 100);
            return newYMinRange;
        }else {
            int newYMinRange = (int) (mSeries.getLowestValueY() * 0.8);
            return newYMinRange;
        }
    }

    private void defineDiagramAppearance(){
        setDiagramLine();
        setDiagramBackground();
    }

    private void setDiagramLine(){
        int colorId = Color.rgb(35, 255, 15);
        mSeries.setColor(colorId);
        mSeries.setThickness(5);
    }

    private void setDiagramBackground(){
        int colorId = Color.argb(65, 0, 255, 255);
        mSeries.setDrawBackground(true);
        mSeries.setBackgroundColor(colorId);
    }

    //update X axis max bound (after refresh screen is resized to fit new value); Y axis is resized automatically;
    private void updateBounds(int xAxisEnd) {
        setGraphBoundsValues();

        //TODO-> implement belows
        //TODO-> add catching time of location record to onLocationChanged method
        //-> shared preferences / preferences -> user sets preferred time period to show on diagram (ex 1 hour, 2 hours etc.)
        //-> here, depending on chosen preferred time set xAxis border as the set of numbers converted to seconds/minutes/hours
        //-> define x position of points by using difference of time between recordings of two points
        //-> example: point 1, measured 18:00 -> position x=0; point 2, measured 18:01, position x=60 ( 1 sec == 1 unit on diagram)
        //-> so diagram with 1 hour will have xAxis border at 60min*60sec = 3600 units

        this.getViewport().setMinY(mMinElevation *0.75);
        this.getViewport().setMaxY(mMaxElevation *1.25);
    }

    private void updateYBounds(int yToCheck) {
        if (mMaxElevation == null || mMinElevation == null){
            mMaxElevation = yToCheck;
            mMinElevation = yToCheck;
        }else {
            if(mMaxElevation < yToCheck){
                mMaxElevation = yToCheck;
            }
            if(mMinElevation > yToCheck){
                mMinElevation = yToCheck;
            }
        }
    }

    public void deliverGraph(ArrayList<Location> locationsList) {
        System.out.println("DELIVER GRAPH CALLED");
        if (mStartTimeRecord == null){
            mStartTimeRecord = locationsList.get(0).getTime();
        }

        //if we call to draw for the first time (mSeries is empty, without any data and we add it for a first time to the GraphView Viewport)
        //or use that on button click to create new graph from given data
        if (mSeries.isEmpty()) {
            for (int i=0; i<locationsList.size(); i++){
                if (i > 0) {
                    Long timeBetweenRecords = (locationsList.get(i).getTime() - mStartTimeRecord)/1000;
                    DataPoint graphPoint = new DataPoint(timeBetweenRecords, locationsList.get(i).getAltitude());
                    mSeries.appendData(graphPoint, false, locationsList.size());
                    updateYBounds((int) graphPoint.getY());
                }else {
                    DataPoint graphPoint = new DataPoint(i, locationsList.get(i).getAltitude());
                    mSeries.appendData(graphPoint, false, locationsList.size());
                    updateYBounds((int) graphPoint.getY());
                }
            }
            //draw mSeries on a graph screen
            this.addSeries(mSeries);
        } else {
            //mSeries already have some data (case when we update/add new points to graph)
            //TODO -> convert that to use time (sec/min/hours) on X axis
            if (locationsList.size() > 1){
                int listSize = locationsList.size();
                Long timeBetweenRecords = (locationsList.get(listSize-1).getTime() - mStartTimeRecord)/1000;
                DataPoint graphPoint = new DataPoint(timeBetweenRecords, locationsList.get(listSize-1).getAltitude());
                mSeries.appendData(graphPoint, false, listSize);
                updateYBounds((int) graphPoint.getY());
            }else {
                System.out.println("WRONG LOCATIONSLIST SIZE, HAS TO BE BIGGER THAN 1");
            }
        }
        refreshGraphLook(locationsList.size());
    }

    public void deliverGraphOnResume(int listSize) {
        System.out.println("DELIVER GRAPH ON RESUME CALLED");
        this.addSeries(mSeries);

//        if(listSize > 0){
//            refreshGraphLook(listSize);
//        }
    }

    private void refreshGraphLook(int xBound) {
        updateBounds(xBound);
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
