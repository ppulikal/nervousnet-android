package ch.ethz.coss.nervousnet.vm.nervousnet.sensor_wrappers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetManagerDB;

/**
 * Created by ales on 20/09/16.
 */
public class Wrapper_v3 implements iWrapper, SensorEventListener {

    private static final String LOG_TAG = Wrapper_v3.class.getSimpleName();

    private final SensorManager mSensorManager;
    private final Sensor sensor;
    private NervousnetManagerDB databaseHandler;
    private final String sensorName;
    private ArrayList<String> paramNames;
    private int samplingPeriod;
    private final int[] parametersPositions;

    // This is actually extraction from config file
    public Wrapper_v3(Context context, String sensorName){
        this.sensorName = sensorName;
        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorName);
        this.paramNames = confSensor.getParametersNames();
        this.databaseHandler = new NervousnetManagerDB(context);
        this.databaseHandler.createTableIfNotExists(sensorName);
        this.mSensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        this.sensor = mSensorManager.getDefaultSensor(confSensor.getAndroidSensorType());
        this.samplingPeriod = confSensor.getSamplingPeriod();
        this.parametersPositions = confSensor.getAndroidParametersPositions();
    }

    @Override
    public boolean start() {
        Log.d(LOG_TAG, "Register sensor " + sensorName);
        mSensorManager.registerListener(this, sensor, samplingPeriod);
        return true;
    }

    @Override
    public boolean stop() {
        Log.d(LOG_TAG, "Unregister sensor " + sensorName);
        mSensorManager.unregisterListener(this, this.sensor);
        return true;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long timestamp = System.currentTimeMillis();

        SensorReading reading = new SensorReading(sensorName, paramNames);
        reading.setTimestampEpoch(timestamp);

        // 2. Insert values
        Log.d(LOG_TAG, "timestamp " + reading.getTimestampEpoch() + " " + Arrays.toString(sensorEvent.values));
        for (int i = 0; i < parametersPositions.length; i++) {
            Object val = sensorEvent.values[parametersPositions[i]];
            reading.setValue(paramNames.get(i), val);
        }

        databaseHandler.store(reading);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
