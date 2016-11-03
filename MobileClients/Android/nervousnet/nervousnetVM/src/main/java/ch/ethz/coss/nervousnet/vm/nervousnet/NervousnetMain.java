package ch.ethz.coss.nervousnet.vm.nervousnet;

import android.content.Context;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationLoader;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetManagerDB;

/**
 * Created by ales on 19/10/16.
 */
public class NervousnetMain {

    private NervousnetManagerDB nervousnetDB;

    public NervousnetMain(Context context){
        this.nervousnetDB = NervousnetManagerDB.getInstance(context);
        ConfigurationLoader confLoader = new ConfigurationLoader(context);
        ArrayList<ConfigurationBasicSensor> confClassList = confLoader.load();

        for (ConfigurationBasicSensor conf : confClassList)
            SensorsHandler.initSensor(context, conf);
    }

    public void startAllSensors(){
        SensorsHandler.startAllSensors();
    }

    public void stopAllSensors(){
        SensorsHandler.stopAllSensors();
    }

    public SensorReading getLatestReading(String sensorName){
        return NervousnetManagerDB.getLatestReading(sensorName);
    }

    public void store(SensorReading reading){
        nervousnetDB.store(reading);
    }

    public void store(ArrayList<SensorReading> readings){
        nervousnetDB.store(readings);
    }

    public ArrayList<SensorReading> getReadings(String sensorName){
        return nervousnetDB.getReadings(sensorName);
    }

    public ArrayList<SensorReading> getReadings(String sensorName, long start, long stop){
        return nervousnetDB.getReadings(sensorName, start, stop);
    }

    public void deleteTableIfExists(String sensorName){
        nervousnetDB.deleteTableIfExists(sensorName);
    }

    public void createTableIfNotExists(String sensorName){
        nervousnetDB.createTableIfNotExists(sensorName);
    }

    public void removeOldReadings(String sensorName, long threshold){
        nervousnetDB.removeOldReadings(sensorName, threshold);
    }

}
