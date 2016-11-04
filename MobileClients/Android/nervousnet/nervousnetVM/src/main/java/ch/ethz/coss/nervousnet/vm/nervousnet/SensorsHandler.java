package ch.ethz.coss.nervousnet.vm.nervousnet;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.AndroidSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.BaseSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.BatterySensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.NoiseSensor;

/**
 * Created by ales on 01/11/16.
 */
public class SensorsHandler {

    private static HashMap<String, BaseSensor> wrappers = new HashMap<>();

    protected static void initSensor(Context context, ConfigurationBasicSensor sensorConf){

        if (wrappers.containsKey(sensorConf.getSensorName())){
            // Ignore
        } else {

            String chooseWrapper = sensorConf.getWrapperName();
            BaseSensor wrapper = null;
            switch (chooseWrapper) {

                case "NoiseSensor":
                    wrapper = new NoiseSensor(context, sensorConf.getSensorName());
                    break;

                case "AndroidSensor":
                    wrapper = new AndroidSensor(context, sensorConf.getSensorName());
                    break;
                case "BatterySensor":
                    wrapper = new BatterySensor(context, sensorConf.getSensorName());
                default:
                    // do nothing, ignore
                    Log.d("MAIN", "ERROR - wrapper not supported in main activity class");
            }
            wrappers.put(sensorConf.getSensorName(), wrapper);
            Log.d("MAIN", sensorConf.getSensorName() + " DONE");
        }
    }

    protected static void startAllSensors(){
        for ( BaseSensor wrapper : wrappers.values()) {
            wrapper.startListener();
        }
    }

    protected static void stopAllSensors(){
        for ( BaseSensor wrapper : wrappers.values() ){
            wrapper.stopListener();
        }
    }

}
