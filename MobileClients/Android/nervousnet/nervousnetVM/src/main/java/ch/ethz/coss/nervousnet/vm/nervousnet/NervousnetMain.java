package ch.ethz.coss.nervousnet.vm.nervousnet;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.configuration.GeneralSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.configuration.JsonConfigurationLoader;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetDBManager;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.BaseSensor;

/**
 * Created by ales on 19/10/16.
 */
public class NervousnetMain implements iNervousnetMain {
    Context context;
    private NervousnetDBManager nervousnetDB;
    private HashMap<Long, BaseSensor> wrappers = new HashMap();
    HashMap<Long, GeneralSensorConfiguration> configMap;

    public NervousnetMain(Context context){
        this.context = context;
        this.configMap = new HashMap<>();
        //deleteDatabase();
        this.nervousnetDB = NervousnetDBManager.getInstance(context);
        JsonConfigurationLoader confLoader = new JsonConfigurationLoader(context);
        stopAllSensors();
    }

    public SensorReading getLatestReading(long sensorID){
        return NervousnetDBManager.getLatestReading(sensorID);
    }

    public void store(SensorReading reading){
        nervousnetDB.store(reading);
    }

    public void store(ArrayList<SensorReading> readings){
        nervousnetDB.store(readings);
    }

    public ArrayList<SensorReading> getReadings(long sensorID){
        return nervousnetDB.getReadings(configMap.get(sensorID));
    }

    public ArrayList<SensorReading> getReadings(long sensorID, long start, long stop){
        return nervousnetDB.getReadings(configMap.get(sensorID), start, stop);
    }

    public void deleteTableIfExists(long sensorID){
        nervousnetDB.deleteTableIfExists(sensorID);
    }

    public void createTableIfNotExists(long sensorID){
        nervousnetDB.createTableIfNotExists(configMap.get(sensorID));
    }

    public void removeOldReadings(long sensorID, long threshold){
        nervousnetDB.removeOldReadings(sensorID, threshold);
    }

    private void initSensor(Context context, BasicSensorConfiguration sensorConf)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        if (wrappers.containsKey(sensorConf.getSensorID())){
            wrappers.get(sensorConf.getSensorID()).stop();
        }

        if (sensorConf.getSamplingRate() < 0){
            // Do not run
            return;
        } else {
            String wrapperName = sensorConf.getWrapperName();
            String packageName = BaseSensor.class.getPackage().getName();
            String className = packageName + "." + wrapperName;

            // Automatically get the class from the config file
            BaseSensor wrapper = (BaseSensor) Class.forName(className)
                    .getConstructor(Context.class, BasicSensorConfiguration.class)
                    .newInstance(context, sensorConf);

            wrappers.put(sensorConf.getSensorID(), wrapper);
            //Log.d("NERVOUSNET-MAIN", sensorConf.getSensorName() + " initialized");
            wrapper.start();
        }

    }

    public void startAllSensors(){
        for (GeneralSensorConfiguration conf : configMap.values())
            try {
                initSensor(context, (BasicSensorConfiguration) conf);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
    }

    public void startSensor(long sensorID){
        if (wrappers.containsKey(sensorID)){
            wrappers.get(sensorID).start();
        } else {
            restartSensor(sensorID);
        }
    }

    public void stopSensor(long sensorID){
        if (wrappers.containsKey(sensorID)){
            wrappers.get(sensorID).stop();
            //wrappers.remove(sensorID);
        }
    }

    public void stopAllSensors(){
        for (GeneralSensorConfiguration conf : configMap.values()){
            stopSensor(conf.getSensorID());
        }
    }

    public void restartSensor(long sensorID){

        try {
            initSensor(context, (BasicSensorConfiguration) configMap.get(sensorID));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public void updateSamplingRate(long sensorID, long newSamplingRate){
        // Update database
        //nervousnetDB.storeState(sensorID, (int)newSamplingRate);
        // Update sensor
        //restartSensor(sensorID);
    }

    public void updateSamplingRateAll(long newSamplingRate){
        for (GeneralSensorConfiguration conf : configMap.values())
            updateSamplingRate(conf.getSensorID(), newSamplingRate);
    }

    // NERVOUSNETGEN
    public void deleteDatabase(){
        String[] dblist = context.databaseList();
        for (String db : dblist){
            context.deleteDatabase(db);
        }
    }

    public void registerSensor(GeneralSensorConfiguration config){
        configMap.put(config.getSensorID(), config);
        nervousnetDB.createTableIfNotExists(config);
    }


    public GeneralSensorConfiguration getConf(long sensorID) throws ConfigurationError {
        if (configMap.containsKey(sensorID)) {
            return configMap.get(sensorID);
        } else {
            throw new ConfigurationError("Sensor " + sensorID + " is not registered.");
        }
    }
}
