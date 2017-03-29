package pl.grzegorziwanek.altimeter.app.data.location.services.elevation;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import rx.subjects.PublishSubject;

/**
 * Consist Pressure Sensor listener. Returns updates of pressure sensor every interval set.
 * Uses JavaRx to return updates of value.
 */
public class BarometerListener implements SensorEventListener {

    private final PublishSubject<Double> mAltitudePublishSubject;
    private final Context mContext;
    private Sensor mSensor;
    private boolean isListenerRegistered = false;
    private SensorManager mSensorManager;
    private double mClosestAirportPressure = 0;

    public BarometerListener(Context context) {
        mContext = context;
        mAltitudePublishSubject = PublishSubject.create();
        setPressureSensor();
    }

    private void setPressureSensor() {
        mSensorManager = (SensorManager) mContext.getSystemService(Service.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    public PublishSubject<Double> getPressureAltitudePublishSubject() {
        return mAltitudePublishSubject;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String altitude = String.valueOf(SensorManager.getAltitude(
                getPressure(), event.values[0]));
        System.out.println("closest airport pressure is: " + getPressure());
        System.out.println("sensor pressure is: " + event.values[0]);
        mAltitudePublishSubject.onNext(Double.valueOf(altitude));
    }

    public void registerListener() {
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        isListenerRegistered = true;
    }

    public void unregisterListener() {
        mSensorManager.unregisterListener(this);
        isListenerRegistered = false;
    }

    public boolean isBarometerListenerRegistered() {
        return isListenerRegistered;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private float getPressure() {
        if (mClosestAirportPressure == 0) {
            return (float) 1013.5;
        } else {
            return (float) mClosestAirportPressure;
        }
    }

    public void setClosestAirportPressure(double airportPressure) {
        System.out.println("closest airport pressure set: " + airportPressure);
        mClosestAirportPressure = airportPressure;
    }
}


