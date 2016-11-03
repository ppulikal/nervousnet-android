package ch.ethz.coss.nervousnet.vm.nervousnet.wrappers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetManagerDB;

/**
 * Created by ales on 24/10/16.
 */
public abstract class aWrapper  implements SensorEventListener {
    private static final String LOG_TAG = aWrapper.class.getSimpleName();

    private final SensorManager mSensorManager;
    private NervousnetManagerDB databaseHandler;
    private final Sensor sensor;

    protected String sensorName;
    protected ArrayList<String> paramNames;
    protected int samplingPeriod;


    public aWrapper(Context context, String sensorName){
        this.mSensorManager = (SensorManager)context.getSystemService(context.SENSOR_SERVICE);
        this.databaseHandler = NervousnetManagerDB.getInstance(context);

        this.sensorName = sensorName;
        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorName);
        this.paramNames = confSensor.getParametersNames();
        this.databaseHandler.createTableIfNotExists(sensorName);
        this.sensor = mSensorManager.getDefaultSensor(confSensor.getAndroidSensorType());
        this.samplingPeriod = confSensor.getSamplingPeriod();
    }

    public void push(SensorReading reading){
        databaseHandler.store(reading);
    }

    public boolean start() {
        Log.d(LOG_TAG, "Register sensor " + sensorName);
        mSensorManager.registerListener(this, sensor, samplingPeriod);
        return true;
    }

    public boolean stop() {
        Log.d(LOG_TAG, "Unregister sensor " + sensorName);
        mSensorManager.unregisterListener(this, this.sensor);
        return true;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }
}
