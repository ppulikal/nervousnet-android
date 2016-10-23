package ch.ethz.coss.nervousnet.vm.nervousnet.configuration;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by ales on 21/09/16.
 */
public class ConfigurationBasicSensor extends ConfigurationGeneralSensor{

    private final int androidSensorType;
    private final int[] androidParametersPositions;
    private final int samplingPeriod;

    public ConfigurationBasicSensor(String sensorName, int androidSensorType, ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes,
                                    int[] androidParametersPositions, int samplingPeriod) {
        super(sensorName, parametersNames, parametersTypes);
        this.androidSensorType = androidSensorType;
        this.androidParametersPositions = androidParametersPositions;
        this.samplingPeriod = samplingPeriod;
    }


    public int getAndroidSensorType() {
        return androidSensorType;
    }

    public int[] getAndroidParametersPositions() {
        return androidParametersPositions;
    }

    public int getSamplingPeriod() {
        return samplingPeriod;
    }

    @Override
    public String toString() {
        return "ConfigurationClass{" +
                "sensorName='" + sensorName + '\'' +
                ", androidSensorType=" + androidSensorType +
                ", parametersNames=" + TextUtils.join(", ", parametersNames) +
                ", parametersTypes=" + TextUtils.join(", ", parametersTypes) +
                ", androidParametersPositions=" + Arrays.toString(androidParametersPositions) +
                ", samplingPeriod=" + samplingPeriod +
                '}';
    }
}
