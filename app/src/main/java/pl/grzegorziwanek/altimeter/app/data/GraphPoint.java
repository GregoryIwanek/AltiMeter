package pl.grzegorziwanek.altimeter.app.data;

/**
 * Created by Grzegorz Iwanek on 22.02.2017.
 */
public class GraphPoint {

    private long xTime;
    private double yAltitude;

    public GraphPoint(long xValue, double yValue) {
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
