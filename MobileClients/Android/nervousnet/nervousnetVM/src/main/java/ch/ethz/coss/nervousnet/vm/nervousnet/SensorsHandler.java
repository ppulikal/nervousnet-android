package ch.ethz.coss.nervousnet.vm.nervousnet;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;

import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;
import ch.ethz.coss.nervousnet.vm.nervousnet.wrappers.Wrapper_v3;
import ch.ethz.coss.nervousnet.vm.nervousnet.wrappers.aWrapper;

/**
 * Created by ales on 01/11/16.
 */
public class SensorsHandler {

    private static HashMap<String, aWrapper> wrappers = new HashMap<>();

    protected static void initSensor(Context context, ConfigurationBasicSensor sensorConf){

        if (wrappers.containsKey(sensorConf.getSensorName())){
            // Ignore
        } else {

            // TODO: select right Wrapper
            String chooseWrapper = "Wrapper_v3";
            aWrapper wrapper = null;
            switch (chooseWrapper) {
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
                    wrapper = new Wrapper_v3(context, sensorConf.getSensorName());
                    break;
                default:
                    // do nothing, ignore
                    Log.d("MAIN", "ERROR - wrapper not supported in main activity class");
            }
            wrappers.put(sensorConf.getSensorName(), wrapper);
            Log.d("MAIN", sensorConf.getSensorName() + " DONE");
        }
    }

    protected static void startAllSensors(){
        for ( aWrapper wrapper : wrappers.values()) {
            wrapper.start();
        }
    }

    protected static void stopAllSensors(){
        for ( aWrapper wrapper : wrappers.values() ){
            wrapper.stop();
        }
    }

}
