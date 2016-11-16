package ch.ethz.coss.nervousnet.vm.nervousnet;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationGeneralSensor;

/**
 * Created by ales on 16/11/16.
 */
public interface iNervousnetMain {

    public SensorReading getLatestReading(long sensorID);
    public void store(SensorReading reading);
    public ArrayList<SensorReading> getReadings(long sensorID, long start, long stop);
    public ArrayList<SensorReading> getReadings(long sensorID);
    public void startSensor(long sensorID);
    public void stopSensor(long sensorID);
    public void startAllSensors();
    public void stopAllSensors();
    public void restartSensor(long sensorID);
    public void updateSamplingRate(long sensorID, long newSamplingRate);
    public void updateSamplingRateAll(long newSamplingRate);
    public void registerSensor(ConfigurationGeneralSensor config);
    public ConfigurationGeneralSensor getConf(long sensorID);
}
