package ch.ethz.coss.nervousnet.vm.nervousnet.sensors;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetDBManager;

/**
 * Created by ales on 24/10/16.
 */
public abstract class BaseSensor {

    private static final String LOG_TAG = BaseSensor.class.getSimpleName();

    // Handlers
    private NervousnetDBManager databaseHandler;

    // Sensor configuration
    protected ConfigurationBasicSensor configuration;
    protected long sensorID;
    protected String sensorName;
    protected long samplingRate;
    protected ArrayList<String> paramNames;

    private long nextSampling = 0;

    public BaseSensor(Context context, ConfigurationBasicSensor conf){
        this.databaseHandler = NervousnetDBManager.getInstance(context);
        this.configuration = conf;
        this.sensorID = conf.getSensorID();
        this.sensorName = conf.getSensorName();
        this.paramNames = conf.getParametersNames();
        this.databaseHandler.createTableIfNotExists(conf);
    }

    public void push(SensorReading reading){
        if (reading.getTimestampEpoch() >= nextSampling) {
            Log.d(LOG_TAG, reading.toString());
            nextSampling = reading.getTimestampEpoch() + configuration.getSamplingRate();
            databaseHandler.store(reading);
        }
    }

    public void start(){
        stopListener();
        this.databaseHandler.createTableIfNotExists(configuration);
        this.samplingRate = configuration.getSamplingRate();
        startListener();
    }

    public void stop(){
        stopListener();
    }

    protected abstract boolean startListener();
    protected abstract boolean stopListener();

}
