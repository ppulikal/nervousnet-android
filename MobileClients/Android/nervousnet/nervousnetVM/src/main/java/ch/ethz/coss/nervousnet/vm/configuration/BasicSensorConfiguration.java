package ch.ethz.coss.nervousnet.vm.configuration;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;

import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.sensors.AndroidSensor;

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

    /**
     * Constructs an object that holds parameters for a basic android sensor.
     * @param sensorID - unique sensor identifier
     * @param sensorName - arbitrary sensor name
     * @param androidSensorType - check Sensor class from official android documentation
     * @param parametersNames - arbitrary persistent parameter names of the sensor
     * @param parametersTypes - arbitrary persistent parameter types of the sensor
     * @param androidParametersPositions - enables to select subset of values among all that are
     *                                   specified in SensorEvent class from official android
     *                                   documentation
     * @param samplingRates - list of possible sampling rates
     * @param state - selected sampling rate; 0 represents OFF; 1 selects the first sampling rate from
     *              samplingRates, 2 selects next one and so on;
     */
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

    /**
     * Constructor an object that holds parameters for an arbitrary sensor.
     * @param sensorID - unique sensor identifier
     * @param sensorName - arbitrary sensor name
     * @param parametersNames - arbitrary persistent parameter names of the sensor
     * @param parametersTypes - arbitrary persistent parameter types of the sensor
     * @param wrapperName - name of the class that handles sensor reading
     * @param samplingRates - list of possible sampling rates
     * @param state - selected sampling rate; 0 represents OFF; 1 selects the first sampling rate from
     *              samplingRates, 2 selects next one and so on;
     */
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

    public BasicSensorConfiguration(int sensorID, String sensorName, ArrayList<String> parametersNames,
                                    ArrayList<String> parametersTypes) {
        super(sensorID, sensorName, parametersNames, parametersTypes);
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
