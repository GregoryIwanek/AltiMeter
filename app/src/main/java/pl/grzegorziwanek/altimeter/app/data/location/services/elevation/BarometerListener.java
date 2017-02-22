package pl.grzegorziwanek.altimeter.app.data.location.services.elevation;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import pl.grzegorziwanek.altimeter.app.data.location.LocationResponse;

/**
 * Created by Grzegorz Iwanek on 18.02.2017.
 */

public class BarometerListener implements SensorEventListener {

    private static BarometerListener barometerListener;
    private final Context mContext;
    private final LocationResponse.BarometerElevationCallback mCallback;
    private Sensor mSensor;
    private boolean isListenerRegistered = false;
    private SensorManager sensorManager;

    private BarometerListener(Context context, LocationResponse.BarometerElevationCallback callback) {
        mContext = context;
        mCallback = callback;
        setPressureSensor();
    }

    public static BarometerListener getInstance(Context context, LocationResponse.BarometerElevationCallback callback) {
        if (barometerListener == null) {
            barometerListener = new BarometerListener(context, callback);
        }
        return barometerListener;
    }

    private void setPressureSensor() {
        sensorManager = (SensorManager) mContext.getSystemService(Service.SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        System.out.println("SENSOR CHANGED : ");
        String altitude = String.valueOf(SensorManager.getAltitude(
                996, event.values[0]));
        mCallback.onBarometerElevationFound(Double.valueOf(altitude));
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
}


