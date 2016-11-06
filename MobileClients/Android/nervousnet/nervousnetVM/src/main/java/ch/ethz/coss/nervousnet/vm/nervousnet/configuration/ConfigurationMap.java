package ch.ethz.coss.nervousnet.vm.nervousnet.configuration;

import java.util.HashMap;

import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationGeneralSensor;

/**
 * Created by ales on 18/10/16.
 */
public class ConfigurationMap {

    private static HashMap<Long, ConfigurationGeneralSensor> CONFIGURATION = new HashMap<>();

    public static void addSensorConfig(ConfigurationGeneralSensor config){
        CONFIGURATION.put(config.getSensorID(), config);
    }

    public static ConfigurationGeneralSensor getSensorConfig(long sensorID){
        return CONFIGURATION.get(sensorID);
    }
}
