package ch.ethz.coss.nervousnet.vm.nervousnet.configuration;

import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by ales on 18/10/16.
 */
public class ConfigurationGeneralSensor {

    protected String sensorName;
    protected ArrayList<String> parametersNames;
    protected ArrayList<String> parametersTypes;
    protected int dimensions;

    public ConfigurationGeneralSensor(String sensorName, ArrayList<String> parametersNames,
                                      ArrayList<String> parametersTypes) {
        this.sensorName = sensorName;
        this.parametersNames = parametersNames;
        this.parametersTypes = parametersTypes;
        this.dimensions = parametersNames.size();
    }

    public String getSensorName() {
        return sensorName;
    }

    public ArrayList<String> getParametersNames() {
        return parametersNames;
    }

    public ArrayList<String> getParametersTypes() {
        return parametersTypes;
    }

    public int getDimension() {
        return dimensions;
    }
    @Override
    public String toString() {
        return "ConfigurationClass{" +
                "sensorName='" + sensorName + '\'' +
                ", parametersNames=" + TextUtils.join(", ", parametersNames) +
                ", parametersTypes=" + TextUtils.join(", ", parametersTypes) +
                '}';
    }
}
