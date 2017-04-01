package pl.gregoryiwanek.altimeter.app.data;

/**
 * Consists container class for Graph View data.
 * Used as a point in a Graph View panel to draw altitude/time graph, where time value is a point on X axis, and altitude
 * is a point on Y axis.
 */
public class GraphPoint {

    private long xTime;
    private double yAltitude;

    GraphPoint(long xValue, double yValue) {
        xTime = xValue;
        yAltitude = yValue;
    }

    public long getXValue() {
        return xTime;
    }

    public double getYValue() {
        return yAltitude;
    }
}
