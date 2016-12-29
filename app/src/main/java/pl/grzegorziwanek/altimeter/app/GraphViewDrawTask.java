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
    private LineGraphSeries<DataPoint> mDiagramSeries = new LineGraphSeries<>();
    private int currSeriesCount = 0;
    private Long mRecordingStartTime = null;

    //override default constructors of the GridView (it's required to prevent errors from compilation); initiate basic settings;
    public GraphViewDrawTask(Context context) {
        super(context);
        setGraphViewSettings();
    }

    public GraphViewDrawTask(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGraphViewSettings();
    }

    public GraphViewDrawTask(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setGraphViewSettings();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    private void setGraphViewSettings() {
        defineDiagramAppearance();
        setGraphBounds();
        setFormatLabels("m", "s");
    }

    private void defineDiagramAppearance() {
        setDiagramLine();
        setDiagramBackground();
    }

    private void setDiagramLine() {
        int colorId = Color.rgb(35, 255, 15);
        mDiagramSeries.setColor(colorId);
        mDiagramSeries.setThickness(2);
    }

    private void setDiagramBackground() {
        int colorId = Color.argb(65, 0, 255, 255);
        mDiagramSeries.setDrawBackground(true);
        mDiagramSeries.setBackgroundColor(colorId);
    }

    private void setGraphBounds() {
        setChangeBoundsManually();
        initiateGraphViewBounds();
        setGraphBoundsValues();
    }

    private void setChangeBoundsManually() {
        this.getViewport().setXAxisBoundsManual(true);
        this.getViewport().setYAxisBoundsManual(true);
    }

    private void initiateGraphViewBounds() {
        this.getViewport().setMinX(0);
        this.getViewport().setMaxX(300);
        this.getViewport().setMinY(0);
        this.getViewport().setMaxY(100);
    }

    private void setGraphBoundsValues() {
        setXBounds();
        setYBounds();
    }

    private void setXBounds() {
        if (mDiagramSeries.getHighestValueX() > this.getViewport().getMaxX(false)*0.8) {
            int xRange = getNewXBoundsRange();
            this.getViewport().setMaxX(xRange);
        }
    }

    private int getNewXBoundsRange(){
        return (int) (mDiagramSeries.getHighestValueX() * 2);
    }

    private void setYBounds() {
        int heightMid = getMiddleHeight();
        setYMaxBounds(heightMid);
        setYMinBounds(heightMid);
    }

    private int getHeightDifference() {
        return (int)(mDiagramSeries.getHighestValueY()
                - mDiagramSeries.getLowestValueY());
    }

    private int getMiddleHeight() {
        return (int)((mDiagramSeries.getHighestValueY()
                + mDiagramSeries.getLowestValueY())/2);
    }

    private void setYMaxBounds(int heightMiddle) {
        if ((mDiagramSeries.getHighestValueY() - heightMiddle) > 40) {
            int yMaxRange = getNewYMaxBoundsRange(heightMiddle);
            this.getViewport().setMaxY(yMaxRange);
        }else {
            int yMaxRange = heightMiddle + 50;
            this.getViewport().setMaxY(yMaxRange);
        }
    }

    private int getNewYMaxBoundsRange(int heightMiddle) {
        int heightDiff = getHeightDifference();
        return (int) (heightMiddle + (heightDiff/2)*1.25);
    }

    private void setYMinBounds(int heightMiddle) {
        if ((heightMiddle - mDiagramSeries.getLowestValueY()) > 40) {
            int yMinRange = getNewYMinBoundsRange(heightMiddle);
            this.getViewport().setMinY(yMinRange);
        }else {
            int yMinRange = heightMiddle - 50;
            this.getViewport().setMinY(yMinRange);
        }
    }

    private int getNewYMinBoundsRange(int heightMiddle) {
        int heightDiff = getHeightDifference();
        int newYMinRange;
        if (mDiagramSeries.getLowestValueY() < 0) {
            newYMinRange = (int) (heightMiddle - (heightDiff/2)*1.25);
        }else {
            newYMinRange = (int) (heightMiddle - (heightDiff/2)*1.25);
            if (newYMinRange < 0) {
                newYMinRange = 0;
            }
        }
        return newYMinRange;
    }

    private void updateBounds() {
        setGraphBoundsValues();
    }
    //TODO-> implement belows
    //TODO-> add catching time of location record to onLocationChanged method
    //-> shared preferences / preferences -> user sets preferred time period to show on diagram (ex 1 hour, 2 hours etc.)
    //-> here, depending on chosen preferred time set xAxis border as the set of numbers converted to seconds/minutes/hours
    //-> define x position of points by using difference of time between recordings of two points
    //-> example: point 1, measured 18:00 -> position x=0; point 2, measured 18:01, position x=60 ( 1 sec == 1 unit on diagram)
    //-> so diagram with 1 hour will have xAxis border at 60min*60sec = 3600 units
    //TODO->!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //TODO->code to assign whole list after onResume is triggered (mSeries and recording start time is reset)
    //TODO->make above corresponding and exchangeable with regural deliverGraph method content


    //TODO->implement both: after starting app (list size 1-2-3-4...) and after on resum (list size ...10-11-12...)

    public void deliverGraph(ArrayList<Location> locationsList) {
        //TODO->think about that part
        if (mRecordingStartTime == null) {
            setRecordingStartTime(locationsList.get(0).getTime());
        }

        drawGraph(locationsList);
    }

    private void setRecordingStartTime(Long startTime) {
        mRecordingStartTime = startTime;
    }

    private void drawGraph(ArrayList<Location> locationsList) {
        if (mDiagramSeries.isEmpty()) {
            drawGraphFirstTime(locationsList);
        } else {
            appendToExistingGraph(locationsList);
        }

        //if we call to draw for the first time (mDiagramSeries is empty, without any data and we add it for a first time to the GraphView Viewport)
        //or use that on button click to create new graph from given data
//        if (mDiagramSeries.isEmpty()){
//            for (int i=0; i<locationsList.size(); i++){
//                if (i > 0) {
//                    System.out.println("SERIES EMPTY NEXT ONE");
//                    Long timeBetweenRecords = (locationsList.get(i).getTime() - mRecordingStartTime)/1000;
//                    DataPoint graphPoint = new DataPoint(timeBetweenRecords, locationsList.get(i).getAltitude());
//                    mDiagramSeries.appendData(graphPoint, false, 10);
//                }else {
//                    System.out.println("SERIES EMPTY FIRST ONE");
//                    DataPoint graphPoint = new DataPoint(0, locationsList.get(i).getAltitude());
//                    mDiagramSeries.appendData(graphPoint, false, 10);
//                }
//            }
//            //draw mDiagramSeries on a graph screen
//            addSeriesToGraph();
//        }else {
//            //mDiagramSeries already have some data (case when we update/add new points to graph)
//            //TODO -> convert that to use time (sec/min/hours) on X axis
//            if (locationsList.size() > 1){
//                int listSize = locationsList.size();
//                Long timeBetweenRecords = (locationsList.get(listSize-1).getTime() - mRecordingStartTime)/1000;
//                DataPoint graphPoint = new DataPoint(timeBetweenRecords, locationsList.get(listSize-1).getAltitude());
//                mDiagramSeries.appendData(graphPoint, false, 10);
//            }else {
//                System.out.println("WRONG LOCATIONSLIST SIZE, HAS TO BE BIGGER THAN 1");
//            }
//        }
        refreshGraphLook();

        System.out.println("SIZE OF GRAPH SERIES = "+ this.getSeries().size());
        System.out.println("VALUES "+this.getSeries().lastIndexOf(mDiagramSeries));
    }

    private void drawGraphFirstTime(ArrayList<Location> locationsList) {
        int listSize = locationsList.size();
        for (int i=0; i<locationsList.size(); i++) {
            if (i > 0) {
                System.out.println("SERIES EMPTY NEXT ONE");
                Long timeBetweenRecords = (locationsList.get(i).getTime() - mRecordingStartTime)/1000;
                double yValue = locationsList.get(i).getAltitude();
                appendNextPointToSeries(listSize, yValue, timeBetweenRecords);
            }else {
                System.out.println("SERIES EMPTY FIRST ONE");
                double yValue = locationsList.get(0).getAltitude();
                appendFirstPointToSeries(listSize, yValue);
            }
        }

        addSeriesToGraph();
    }

    private void appendFirstPointToSeries(int listSize, double yValue) {
        DataPoint graphPoint = new DataPoint(0, yValue);
        mDiagramSeries.appendData(graphPoint, false, listSize);
        currSeriesCount++;
    }

    private void appendNextPointToSeries(int listSize, double yValue, long timeBetweenRecords){
        DataPoint graphPoint = new DataPoint(timeBetweenRecords, yValue);
        mDiagramSeries.appendData(graphPoint, false, listSize);
        currSeriesCount++;
    }

    private void appendToExistingGraph(ArrayList<Location> locationsList) {
        int listSize = locationsList.size();
        for (int i = currSeriesCount; i<locationsList.size(); i++) {
            Long timeBetweenRecords = (locationsList.get(i).getTime() - mRecordingStartTime)/1000;
            if (timeBetweenRecords > mDiagramSeries.getHighestValueX()){
                double yValue = locationsList.get(i).getAltitude();
                appendNextPointToSeries(listSize, yValue, timeBetweenRecords);
            }
        }
    }

    private void addSeriesToGraph() {
        this.addSeries(mDiagramSeries);
    }

    public void deliverGraphOnResume(ArrayList<Location> locationsList){
        if(locationsList.size() != 0){
            deliverGraph(locationsList);
            if(this.getSeries().isEmpty()){
                this.addSeries(mDiagramSeries);
            }
            refreshGraphLook();
        }
    }

    private void refreshGraphLook(){
        updateBounds();
        refreshDrawableState();
    }

    public void setFormatLabels(String yFormat, String xFormat){
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
