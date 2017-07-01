package pl.gregoryiwanek.altimeter.app.recordingsession;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.GraphPoint;
import pl.gregoryiwanek.altimeter.app.utils.stylecontroller.StyleController;

/**
 *  Consist extension of external library class GraphView (http://www.android-graphview.org/) and required customized methods;
 *  Takes list with locations as a parameter to draw or update altitude graph inside a widget.
 */
public class GraphViewWidget extends GraphView {

    private LineGraphSeries<DataPoint> mDiagramSeries = new LineGraphSeries<>();
    private int mCurSeriesCount = 0;
    private Long mRecordingStartTime;
    private StyleController themePicker;

    public GraphViewWidget(Context context) {
        super(context);
        themePicker = new StyleController(context);
    }

    public GraphViewWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GraphViewWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void initGraphViewDefault() {
        setDiagramAppearance();
        setGridAppearance();
        setGraphBounds();
        setTextAppearance();
    }

    private void setDiagramAppearance() {
        setDiagramLine();
        setDiagramBackground();
    }

    private void setDiagramLine() {
        int colorId = themePicker.getAttrColor(R.attr.colorGraphLine);
        mDiagramSeries.setColor(colorId);
        mDiagramSeries.setThickness(2);
    }

    private void setDiagramBackground() {
        int colorId = themePicker.getAttrColor(R.attr.colorGraphBackground);
        int colorAlpha = Color.argb(180, Color.red(colorId), Color.green(colorId), Color.blue(colorId));
        mDiagramSeries.setDrawBackground(true);
        mDiagramSeries.setBackgroundColor(colorAlpha);
    }

    private void setGridAppearance() {
        int colorId = themePicker.getAttrColor(R.attr.colorGraphGrid);
        getGridLabelRenderer().setGridColor(colorId);
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
        double sizeMultiplier = 0.8;
        if (mDiagramSeries.getHighestValueX() > this.getViewport().getMaxX(false) * sizeMultiplier) {
            int xRange = getNewXBoundsRange();
            this.getViewport().setMaxX(xRange);
        }
    }

    private int getNewXBoundsRange() {
        int sizeMultiplier = 2;
        return (int) (mDiagramSeries.getHighestValueX() * sizeMultiplier);
    }

    private void setYBounds() {
        int heightMid = getMiddleHeight();
        setYMaxBounds(heightMid);
        setYMinBounds(heightMid);
    }

    private void setYMaxBounds(int heightMiddle) {
        int borderCheck = 40;
        if ((mDiagramSeries.getHighestValueY() - heightMiddle) > borderCheck) {
            int yMaxRange = getNewYMaxBoundsRange(heightMiddle);
            this.getViewport().setMaxY(yMaxRange);
        }else {
            int borderDistance = 50;
            int yMaxRange = heightMiddle + borderDistance;
            this.getViewport().setMaxY(yMaxRange);
        }
    }

    private int getNewYMaxBoundsRange(int heightMiddle) {
        int heightDiff = getHeightDifference();
        double sizeMultiplier = 1.25;
        return (int) (heightMiddle + (heightDiff/2)*sizeMultiplier);
    }

    private void setYMinBounds(int heightMiddle) {
        int borderCheck = 40;
        if ((heightMiddle - mDiagramSeries.getLowestValueY()) > borderCheck) {
            int yMinRange = getNewYMinBoundsRange(heightMiddle);
            this.getViewport().setMinY(yMinRange);
        }else {
            int borderDistance = 50;
            int yMinRange = heightMiddle - borderDistance;
            this.getViewport().setMinY(yMinRange);
        }
    }

    private int getNewYMinBoundsRange(int heightMiddle) {
        int heightDiff = getHeightDifference();
        double sizeMultiplier = 1.25;
        int newYMinRange;
        if (mDiagramSeries.getLowestValueY() < 0) {
            newYMinRange = (int) (heightMiddle - (heightDiff/2)*sizeMultiplier);
        }else {
            newYMinRange = (int) (heightMiddle - (heightDiff/2)*sizeMultiplier);
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

    public void deliverGraph(ArrayList<GraphPoint> graphPointList) {
        checkIsSeriesNull();
        setRecordingStartTime(graphPointList.get(0).getXValue());
        drawGraph(graphPointList);
        refreshGraphLook();
    }

    private void checkIsSeriesNull() {
        if (mDiagramSeries == null) {
            mDiagramSeries = new LineGraphSeries<>();
            initGraphViewDefault();
        }
    }

    private void setRecordingStartTime(Long startTime) {
        if (mRecordingStartTime == null) {
            mRecordingStartTime = startTime;
        }
    }

    private void drawGraph(ArrayList<GraphPoint> graphPointList) {
        int listSize = graphPointList.size();
        for (int i=mCurSeriesCount; i<graphPointList.size(); i++) {
            Long recordingTime = getRecordingTime(graphPointList.get(i).getXValue());
            if (recordingTime > mDiagramSeries.getHighestValueX() || recordingTime == 0) {
                double yValue = graphPointList.get(i).getYValue();
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

    private void setTextAppearance() {
        setLabelsTextSize();
        setTextColor();
        setLabelsFormatSymbols("m", "s");
    }

    private void setLabelsTextSize() {
        getGridLabelRenderer().setTextSize(20);
    }

    @SuppressWarnings("SameParameterValue")
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

    private void setTextColor() {
        int colorId = themePicker.getAttrColor(R.attr.colorGraphGrid);
        getGridLabelRenderer().setHorizontalLabelsColor(colorId);
        getGridLabelRenderer().setVerticalLabelsColor(colorId);
        getGridLabelRenderer().setHorizontalAxisTitleColor(colorId);
        getGridLabelRenderer().setVerticalAxisTitleColor(colorId);
        getGridLabelRenderer().setVerticalLabelsSecondScaleColor(colorId);
    }

    public void clearData() {
        removeSeries(mDiagramSeries);
        mDiagramSeries = null;
        mCurSeriesCount = 0;
        mRecordingStartTime = null;
        onDataChanged(false, false);
    }
}