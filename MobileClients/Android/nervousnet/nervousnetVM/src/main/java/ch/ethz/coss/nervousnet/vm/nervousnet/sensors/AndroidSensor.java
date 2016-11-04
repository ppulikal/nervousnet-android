package ch.ethz.coss.nervousnet.vm.nervousnet.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;

public class AndroidSensor extends BaseSensor  implements SensorEventListener {

    private static final String LOG_TAG = AndroidSensor.class.getSimpleName();
    private SensorManager mSensorManager;
    private Sensor sensor;

    private int[] androidParametersPositions;

    // Locking
    protected Lock listenerMutex = new ReentrantLock();


    public AndroidSensor(Context context, String sensorName) {
        // Abstract class will take care of acquiring parameter names and
        // everything that is needed to initialize this listener
        super(context, sensorName);

        this.mSensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorName);
        this.sensor = mSensorManager.getDefaultSensor(confSensor.getAndroidSensorType());
        this.androidParametersPositions = confSensor.getAndroidParametersPositions();
    }

    @Override
    public boolean startListener() {
        Log.d(LOG_TAG, "Register sensor " + sensorName);
        listenerMutex.lock();
        mSensorManager.registerListener(this, sensor, samplingPeriod);
        listenerMutex.unlock();
        return true;
    }

    @Override
    public boolean stopListener() {
        Log.d(LOG_TAG, "Unregister sensor " + sensorName);
        listenerMutex.lock();
        mSensorManager.unregisterListener(this, this.sensor);
        listenerMutex.unlock();
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Get timestamp for the sensor
        long timestamp = System.currentTimeMillis();
        // Get values
        ArrayList values = new ArrayList<>();
        for (int i = 0; i < androidParametersPositions.length; i++) {
            Object val = sensorEvent.values[androidParametersPositions[i]];
            values.add(val);
        }
        // Fill sensor reading
        SensorReading reading = new SensorReading(sensorName, paramNames);
        reading.setTimestampEpoch(timestamp);
        reading.setValues(values);
        // Push reading
        push(reading);
        //Log.d("SENSOR", ""+reading.getSensorName() + " " + TextUtils.join(", ", values));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
