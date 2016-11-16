package ch.ethz.coss.nervousnet.vm.nervousnet;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationGeneralSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationLoader;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.StateDBManager;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetDBManager;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NoSuchElementInDBException;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.BaseSensor;

/**
 * Created by ales on 19/10/16.
 */
public class NervousnetMain implements iNervousnetMain {
    Context context;
    private NervousnetDBManager nervousnetDB;
    private StateDBManager configStoreManager;
    private HashMap<Long, BaseSensor> wrappers = new HashMap();
    ArrayList<ConfigurationBasicSensor> confList;

    public NervousnetMain(Context context){
        this.context = context;
        //deleteDatabase();
        this.nervousnetDB = NervousnetDBManager.getInstance(context);
        this.configStoreManager = new StateDBManager(context);
        ConfigurationLoader confLoader = new ConfigurationLoader(context);
        confList = confLoader.load();
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
        return nervousnetDB.getReadings(sensorID);
    }

    public ArrayList<SensorReading> getReadings(long sensorID, long start, long stop){
        return nervousnetDB.getReadings(sensorID, start, stop);
    }

    public void deleteTableIfExists(long sensorID){
        nervousnetDB.deleteTableIfExists(sensorID);
    }

    public void createTableIfNotExists(long sensorID){
        nervousnetDB.createTableIfNotExists(sensorID);
    }

    public void removeOldReadings(long sensorID, long threshold){
        nervousnetDB.removeOldReadings(sensorID, threshold);
    }

    private void initSensor(Context context, ConfigurationBasicSensor sensorConf)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {

        if (wrappers.containsKey(sensorConf.getSensorID())){
            wrappers.get(sensorConf.getSensorID()).stop();
        }

        try {
            long samplingRate = configStoreManager.getSensorState(sensorConf.getSensorID());
            sensorConf.setSamplingRate(samplingRate);
        } catch (NoSuchElementInDBException e) {
            // There is nothing in the DB so config value for the sampling rate will be used
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
                    .getConstructor(Context.class, long.class)
                    .newInstance(context, sensorConf.getSensorID());

            wrappers.put(sensorConf.getSensorID(), wrapper);
            //Log.d("NERVOUSNET-MAIN", sensorConf.getSensorName() + " initialized");
            wrapper.start();
        }

    }

    public void startAllSensors(){
        for (ConfigurationBasicSensor conf : confList)
            try {
                initSensor(context, conf);
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
            configStoreManager.storeSensorState(sensorID, 1);//TODO change 1
        } else {
            restartSensor(sensorID);
        }
    }

    public void stopSensor(long sensorID){
        if (wrappers.containsKey(sensorID)){
            wrappers.get(sensorID).stop();
            configStoreManager.storeSensorState(sensorID, NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);
            //wrappers.remove(sensorID);
        }
    }

    public void stopAllSensors(){
        for (ConfigurationBasicSensor conf : confList){
            stopSensor(conf.getSensorID());
        }
    }

    public void restartSensor(long sensorID){

        try {
            initSensor(context, (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorID));
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
        for (ConfigurationBasicSensor conf : confList)
            updateSamplingRate(conf.getSensorID(), newSamplingRate);
    }

    // NERVOUSNETGEN
    public void deleteDatabase(){
        String[] dblist = context.databaseList();
        for (String db : dblist){
            context.deleteDatabase(db);
        }
    }

    public void registerSensor(ConfigurationGeneralSensor config){
        // Add to Configuration Map
        ConfigurationMap.addSensorConfig(config);
        // Prepare database
        nervousnetDB.createTableIfNotExists(config.getSensorID());
    }


    public ConfigurationGeneralSensor getConf(long sensorID) {
        return ConfigurationMap.getSensorConfig(sensorID);
    }
}
