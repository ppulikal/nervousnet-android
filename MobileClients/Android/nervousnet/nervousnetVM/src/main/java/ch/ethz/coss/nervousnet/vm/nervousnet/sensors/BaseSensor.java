package ch.ethz.coss.nervousnet.vm.nervousnet.sensors;

import android.content.Context;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetManagerDB;

/**
 * Created by ales on 24/10/16.
 */
public abstract class BaseSensor {

    private static final String LOG_TAG = BaseSensor.class.getSimpleName();

    // Handlers
    private NervousnetManagerDB databaseHandler;

    // Sensor configuration
    protected long sensorID;
    protected String sensorName;
    protected int samplingPeriod;
    protected ArrayList<String> paramNames;

    public BaseSensor(Context context, long sensorID){
        this.databaseHandler = NervousnetManagerDB.getInstance(context);

        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorID);
        this.sensorID = sensorID;
        this.sensorName = confSensor.getSensorName();
        this.paramNames = confSensor.getParametersNames();
        this.databaseHandler.createTableIfNotExists(sensorID);
        this.samplingPeriod = confSensor.getSamplingPeriod();
    }

    public void push(SensorReading reading){
        databaseHandler.store(reading);
    }

    public void start(){
        stopListener();
        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorID);
        this.sensorName = confSensor.getSensorName();
        this.paramNames = confSensor.getParametersNames();
        this.databaseHandler.createTableIfNotExists(sensorID);
        this.samplingPeriod = confSensor.getSamplingPeriod();
        startListener();
    }

    public void stop(){
        stopListener();
    }

    protected abstract boolean startListener();
    protected abstract boolean stopListener();

}
