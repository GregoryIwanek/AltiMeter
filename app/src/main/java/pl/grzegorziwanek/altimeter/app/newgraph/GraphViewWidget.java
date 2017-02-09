package pl.grzegorziwanek.altimeter.app.newgraph;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.AttributeSet;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

/**
 *  Created by Grzegorz Iwanek on 01.12.2016.
 *  Consist extension of external library class GraphView (http://www.android-graphview.org/) and required customized methods;
 *  Takes list with locations as a parameter to draw or update altitude graph inside a widget.
 */
public class GraphViewWidget extends GraphView {

    private final LineGraphSeries<DataPoint> mDiagramSeries = new LineGraphSeries<>();
    private int mCurSeriesCount = 0;
    private Long mRecordingStartTime = null;

    public GraphViewWidget(Context context) {
        super(context);
        setGraphViewSettings();
    }

    public GraphViewWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        setGraphViewSettings();
    }

    public GraphViewWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setGraphViewSettings();
    }

    private void setGraphViewSettings() {
        defineDiagramAppearance();
        setGraphBounds();
        //TODO-> add part of code responsible for converting axis values to minutes/hours and kilometers
        setLabelsFormatSymbols("m", "s");
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
        if (mDiagramSeries.getHighestValueX() > this.getViewport().getMaxX(false) * 0.8) {
            int xRange = getNewXBoundsRange();
            this.getViewport().setMaxX(xRange);
        }
    }

    private int getNewXBoundsRange() {
        return (int) (mDiagramSeries.getHighestValueX() * 2);
    }

    private void setYBounds() {
        int heightMid = getMiddleHeight();
        setYMaxBounds(heightMid);
        setYMinBounds(heightMid);
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

    private int getHeightDifference() {
        return (int)(mDiagramSeries.getHighestValueY()
                - mDiagramSeries.getLowestValueY());
    }

    private int getMiddleHeight() {
        return (int)((mDiagramSeries.getHighestValueY()
                + mDiagramSeries.getLowestValueY())/2);
    }

    private void updateBounds() {
        setGraphBoundsValues();
    }

    public void deliverGraph(ArrayList<Location> locationsList) {
        setRecordingStartTime(locationsList.get(0).getTime());
        drawGraph(locationsList);
        refreshGraphLook();
    }

    private void setRecordingStartTime(Long startTime) {
        if (mRecordingStartTime == null) {
            mRecordingStartTime = startTime;
        }
    }

    private void drawGraph(ArrayList<Location> locationsList) {
        int listSize = locationsList.size();
        for (int i=mCurSeriesCount; i<locationsList.size(); i++) {
            Long recordingTime = getRecordingTime(locationsList.get(i).getTime());
            if (recordingTime > mDiagramSeries.getHighestValueX() || recordingTime == 0) {
                double yValue = locationsList.get(i).getAltitude();
                appendPointToSeries(listSize, yValue, recordingTime);
            }
        }

        if (this.getSeries().isEmpty()) {
            addSeriesToGraph();
        }
    }

    private void appendPointToSeries(int listSize, double yValue, long recordingTimeAsX) {
        DataPoint graphPoint = new DataPoint(recordingTimeAsX, yValue);
        mDiagramSeries.appendData(graphPoint, false, listSize);
        mCurSeriesCount++;
    }

    private Long getRecordingTime(Long recordTime) {
        return (recordTime - mRecordingStartTime)/1000;
    }

    private void addSeriesToGraph() {
        this.addSeries(mDiagramSeries);
    }

    private void refreshGraphLook(){
        updateBounds();
        refreshDrawableState();
    }

    private void setLabelsFormatSymbols(String yFormat, String xFormat){
        final String axisYFormat = yFormat;
        final String axisXFormat = xFormat;

        getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double isValueAxisY, boolean isValueAxisX) {
                if (isValueAxisX) {
                    return super.formatLabel(isValueAxisY, true) + axisXFormat;
                } else {
                    return super.formatLabel(isValueAxisY, false) + axisYFormat;
                }
            }
        });
    }

    public void clearData() {
        getSeries().clear();
        //TODO -> find way to reset data in mDiagramSeries (wtf is that class??)
        mCurSeriesCount = 0;
        mRecordingStartTime = null;
    }
}