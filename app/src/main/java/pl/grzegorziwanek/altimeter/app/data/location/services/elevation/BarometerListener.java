package pl.grzegorziwanek.altimeter.app.data.location.services.elevation;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;
import pl.grzegorziwanek.altimeter.app.data.location.managers.BarometerManager;
import rx.subjects.PublishSubject;

/**
 * Created by Grzegorz Iwanek on 18.02.2017.
 */

public class BarometerListener implements SensorEventListener {

    private static BarometerListener barometerListener;
    private final PublishSubject<Double> mAltitudePublishSubject;
    private final Context mContext;
    private Sensor mSensor;
    private boolean isListenerRegistered = false;
    private SensorManager sensorManager;

    private BarometerListener(Context context) {
        mContext = context;
        mAltitudePublishSubject = PublishSubject.create();
        setPressureSensor();
    }

    public static BarometerListener getInstance(Context context) {
        if (barometerListener == null) {
            barometerListener = new BarometerListener(context);
        }
        return barometerListener;
    }

    private void setPressureSensor() {
        sensorManager = (SensorManager) mContext.getSystemService(Service.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    public PublishSubject<Double> getPressureAltitudePublishSubject() {
        return mAltitudePublishSubject;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        String altitude = String.valueOf(SensorManager.getAltitude(
                getPressure(), event.values[0]));
        mAltitudePublishSubject.onNext(Double.valueOf(altitude));
    }

    public void registerListener() {
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        isListenerRegistered = true;
    }

    public void unregisterListener() {
        sensorManager.unregisterListener(this);
        isListenerRegistered = false;
    }

    public boolean isBarometerListenerRegistered() {
        return isListenerRegistered;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private float getPressure() {
        if (BarometerManager.getClosestAirportPressure() == 0) {
            return (float) 1013.5;
        } else {
            return (float) BarometerManager.getClosestAirportPressure();
        }
    }
}


