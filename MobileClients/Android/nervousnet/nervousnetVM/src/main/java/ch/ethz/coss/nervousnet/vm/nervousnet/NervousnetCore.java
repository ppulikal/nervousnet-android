package ch.ethz.coss.nervousnet.vm.nervousnet;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationLoader;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NervousnetManagerDB;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensor_wrappers.Wrapper_v3;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensor_wrappers.iWrapper;

/**
 * Created by ales on 19/10/16.
 */
public class NervousnetCore {

    private Context context;
    private ArrayList<iWrapper> wrappers = new ArrayList<>();
    private HashMap<String, ConfigurationBasicSensor> sensors = new HashMap<>();
    private NervousnetManagerDB nervousnetDB;


    public NervousnetCore(Context context){
        this.context = context;
        this.nervousnetDB = new NervousnetManagerDB(context);
        init();
    }


    private void init(){
        ConfigurationLoader confLoader = new ConfigurationLoader(context);
        ArrayList<ConfigurationBasicSensor> confClassList = confLoader.load();
        //databaseHelper = new DatabaseHelper(this);

        //databaseHelper.deleteTable(sensorName);

        for ( ConfigurationBasicSensor cc : confClassList) {

            // TODO: select right Wrapper
            String chooseWrapper = "Wrapper_v3";
            iWrapper wrapper = null;
            switch (chooseWrapper){
                case "Wrapper1":
                   /* wrapper = new Wrapper1(this, databaseHelper, cc.getSensorName(),
                            cc.getParametersNames(), cc.getParametersTypes(), cc.getMetadata(),
                            cc.getAndroidSensorType(), cc.getSamplingPeriod(),
                            cc.getAndroidParametersPositions());
                    wrappers.add(wrapper);*/
                    break;
                case "Wrapper_v2":

                    break;

                case "Wrapper_v3":
                    wrapper = new Wrapper_v3(this.context, cc.getSensorName());
                    break;
                default:
                    // do nothing, ignore
                    Log.d("MAIN", "ERROR - wrapper not supported in main activity class");
            }
            wrappers.add(wrapper);
            sensors.put(cc.getSensorName(), cc);
            Log.d("MAIN", cc.getSensorName() + " DONE");
        }
    }

    public void startAllSensors(){
        for ( iWrapper wrapper : wrappers) {
            wrapper.start();
        }
    }

    public void stopAllSensors(){
        for ( iWrapper wrapper : wrappers ){
            wrapper.stop();
        }
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


}
