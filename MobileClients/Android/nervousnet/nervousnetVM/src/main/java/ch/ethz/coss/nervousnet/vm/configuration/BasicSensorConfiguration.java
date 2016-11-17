package ch.ethz.coss.nervousnet.vm.configuration;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.nervousnet.sensors.AndroidSensor;

/**
 * Created by ales on 21/09/16.
 */
public class BasicSensorConfiguration extends GeneralSensorConfiguration {

    private int androidSensorType;
    private int[] androidParametersPositions;
    private int state;
    private long samplingRate;
    private ArrayList<Long> samplingRates;
    private String wrapperName;

    public BasicSensorConfiguration(int sensorID, String sensorName, int androidSensorType,
                                    ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes,
                                    int[] androidParametersPositions,
                                    ArrayList<Long> samplingRates,
                                    int state) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
        this.androidSensorType = androidSensorType;
        this.androidParametersPositions = androidParametersPositions;
        this.samplingRates = samplingRates;
        this.wrapperName = AndroidSensor.class.getSimpleName();
        setState(state);
    }

    public BasicSensorConfiguration(int sensorID, String sensorName, ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes,
                                    String wrapperName,
                                    ArrayList<Long> samplingRates,
                                    int state) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
        this.samplingRates = samplingRates;
        this.wrapperName = wrapperName;
        setState(state);
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

    public void setState(int state){
        this.state = state;
        switch (state){
            case NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF:
                this.samplingRate = -1; break;
            case NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_LOW:
                this.samplingRate = samplingRates.get(0); break;
            case NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_MED:
                this.samplingRate = samplingRates.get(1); break;
            case NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_HIGH:
                this.samplingRate = samplingRates.get(2); break;
            default:
                this.samplingRate = -1; break;
        }
    }

    public ArrayList<Long> getSamplingRates() {
        return samplingRates;
    }

    public int getState() {
        return state;
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
