package ch.ethz.coss.nervousnet.vm.nervousnet;

import ch.ethz.coss.nervousnet.vm.configuration.ConfigurationBasicSensor;

/**
 * Created by ales on 16/11/16.
 */
public class ConfigurationError extends Exception {

    public ConfigurationError(String msg){
        super(msg);
    }
}
