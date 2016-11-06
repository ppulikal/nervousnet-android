package ch.ethz.coss.nervousnet.vm.nervousnet;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationLoader;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetManagerDB;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.AndroidSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.BaseSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.BatterySensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.NoiseSensor;

/**
 * Created by ales on 19/10/16.
 */
public class NervousnetMain {
    Context context;
    private NervousnetManagerDB nervousnetDB;
    private HashMap<Long, BaseSensor> wrappers = new HashMap<>();

    public NervousnetMain(Context context){
        this.context = context;
        this.nervousnetDB = NervousnetManagerDB.getInstance(context);
        ConfigurationLoader confLoader = new ConfigurationLoader(context);
        ArrayList<ConfigurationBasicSensor> confClassList = confLoader.load();

        for (ConfigurationBasicSensor conf : confClassList)
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

    public SensorReading getLatestReading(long sensorID){
        return NervousnetManagerDB.getLatestReading(sensorID);
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
            // Ignore
        } else {

            String wrapperName = sensorConf.getWrapperName();
            String packageName = BaseSensor.class.getPackage().getName();
            String className = packageName + "." + wrapperName;

            // Automatically get the class from the config file
            BaseSensor wrapper = (BaseSensor) Class.forName(className)
                    .getConstructor(Context.class, long.class)
                    .newInstance(context, sensorConf.getSensorID());

            wrappers.put(sensorConf.getSensorID(), wrapper);
            Log.d("MAIN", sensorConf.getSensorName() + " DONE");
        }
    }

    public void startAllSensors(){
        for ( BaseSensor wrapper : wrappers.values()) {
            wrapper.start();
        }
    }

    public void stopAllSensors(){
        for ( BaseSensor wrapper : wrappers.values() ){
            wrapper.stop();
        }
    }

    public BaseSensor getSensor(long sensorID){
        return wrappers.get(sensorID);
    }

}
