package ch.ethz.coss.nervousnet.vm.sensors;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.database.NervousnetDBManager;

/**
 * Created by ales on 24/10/16.
 */
public abstract class BaseSensor {

    private static final String LOG_TAG = BaseSensor.class.getSimpleName();

    // Handlers
    private NervousnetDBManager databaseHandler;

    // Sensor configuration
    protected BasicSensorConfiguration configuration;

    // Just a shortcut of configuration above and maybe more intuitive
    // representation
    protected long sensorID;
    protected String sensorName;
    protected long samplingRate;
    protected ArrayList<String> paramNames;

    private long nextSampling = 0;


    /**
     * Constructor of an abstract class for basic sensor reading.
     */
    public BaseSensor(Context context, BasicSensorConfiguration conf){
        this.databaseHandler = NervousnetDBManager.getInstance(context);
        this.configuration = conf;
        this.sensorID = conf.getSensorID();
        this.sensorName = conf.getSensorName();
        this.paramNames = conf.getParametersNames();
        this.databaseHandler.createTableIfNotExists(conf);
    }


    /**
     * Method for accepting all new sensor readings from subclasses. It takes care of passing new
     * readings further for storing.
     */
    public void push(SensorReading reading){
        if (reading.getTimestampEpoch() >= nextSampling && configuration.getSamplingRate() >= 0) {
            Log.d(LOG_TAG, reading.toString());
            nextSampling = reading.getTimestampEpoch() + configuration.getSamplingRate();
            databaseHandler.store(reading);
        }
    }

    /**
     * Start sensor reading.
     */
    public void start(){
        stopListener();
        this.databaseHandler.createTableIfNotExists(configuration);
        this.samplingRate = configuration.getSamplingRate();
        startListener();
    }

    /**
     * Stop sensor reading
     */
    public void stop(){
        stopListener();
    }

    /**
     * Abstract method to register a sensor.
     */
    protected abstract boolean startListener();

    /**
     * Abstract method to unregister a sensor.
     */
    protected abstract boolean stopListener();

}
