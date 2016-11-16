package ch.ethz.coss.nervousnet.vm.nervousnet.sensors;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetDBManager;

/**
 * Created by ales on 24/10/16.
 */
public abstract class BaseSensor {

    private static final String LOG_TAG = BaseSensor.class.getSimpleName();

    // Handlers
    private NervousnetDBManager databaseHandler;

    // Sensor configuration
    protected long sensorID;
    protected String sensorName;
    protected long samplingRate;
    protected ArrayList<String> paramNames;

    private long nextSampling = 0;

    public BaseSensor(Context context, long sensorID){
        this.databaseHandler = NervousnetDBManager.getInstance(context);

        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorID);
        this.sensorID = sensorID;
        this.sensorName = confSensor.getSensorName();
        this.paramNames = confSensor.getParametersNames();
        this.databaseHandler.createTableIfNotExists(sensorID);
        this.samplingRate = confSensor.getSamplingRate();
    }

    public void push(SensorReading reading){
        if (reading.getTimestampEpoch() >= nextSampling) {
            Log.d(LOG_TAG, reading.toString());
            nextSampling = reading.getTimestampEpoch() + samplingRate;
            databaseHandler.store(reading);
        }
    }

    public void start(){
        stopListener();
        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorID);
        this.sensorName = confSensor.getSensorName();
        this.paramNames = confSensor.getParametersNames();
        this.databaseHandler.createTableIfNotExists(sensorID);
        this.samplingRate = confSensor.getSamplingRate();
        startListener();
    }

    public void stop(){
        stopListener();
    }

    protected abstract boolean startListener();
    protected abstract boolean stopListener();

}
