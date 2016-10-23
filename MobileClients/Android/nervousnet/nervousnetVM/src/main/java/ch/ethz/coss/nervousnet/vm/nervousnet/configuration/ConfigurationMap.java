package ch.ethz.coss.nervousnet.vm.nervousnet.configuration;

import java.util.HashMap;

/**
 * Created by ales on 18/10/16.
 */
public class ConfigurationMap {

    private static HashMap<String, ConfigurationGeneralSensor> CONFIGURATION = new HashMap<>();

    public static void addSensorConfig(ConfigurationGeneralSensor config){
        CONFIGURATION.put(config.getSensorName(), config);
    }

    public static ConfigurationGeneralSensor getSensorConfig(String name){
        return CONFIGURATION.get(name);
    }
}
