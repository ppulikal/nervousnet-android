package ch.ethz.coss.nervousnet.vm.nervousnet.configuration;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.AndroidSensor;

/**
 * Created by ales on 21/09/16.
 */
public class ConfigurationBasicSensor extends ConfigurationGeneralSensor{

    private int androidSensorType;
    private int[] androidParametersPositions;
    private long samplingRate;
    private ArrayList<Long> samplingRates;
    private String wrapperName;

    public ConfigurationBasicSensor(int sensorID, String sensorName, int androidSensorType,
                                    ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes,
                                    int[] androidParametersPositions,
                                    ArrayList<Long> samplingRates,
                                    int selectedSamplingRateIndex) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
        this.androidSensorType = androidSensorType;
        this.androidParametersPositions = androidParametersPositions;
        this.samplingRate = samplingRates.get(selectedSamplingRateIndex);
        this.samplingRates = samplingRates;
        this.wrapperName = AndroidSensor.class.getSimpleName();
    }

    public ConfigurationBasicSensor(int sensorID, String sensorName, ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes,
                                    String wrapperName,
                                    ArrayList<Long> samplingRates,
                                    int selectedSamplingRateIndex) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
        this.samplingRate = samplingRates.get(selectedSamplingRateIndex);
        this.samplingRates = samplingRates;
        this.wrapperName = wrapperName;
    }

    public String getWrapperName() {
        return wrapperName;
    }

    public int getAndroidSensorType() {
        return androidSensorType;
    }

    public int[] getAndroidParametersPositions() {
        return androidParametersPositions;
    }

    public long getSamplingRate() {
        return samplingRate;
    }

    public void setSamplingRate(long samplingRate) {
        this.samplingRate = samplingRate;
    }

    @Override
    public String toString() {
        return "ConfigurationClass{" +
                "sensorName='" + sensorName + '\'' +
                ", androidSensorType=" + androidSensorType +
                ", parametersNames=" + TextUtils.join(", ", parametersNames) +
                ", parametersTypes=" + TextUtils.join(", ", parametersTypes) +
                ", androidParametersPositions=" + Arrays.toString(androidParametersPositions) +
                ", samplingPeriod=" + samplingRate +
                '}';
    }
}
