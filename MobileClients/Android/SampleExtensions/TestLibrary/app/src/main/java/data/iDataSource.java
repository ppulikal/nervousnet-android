package data;

import android.os.RemoteException;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;

/**
 * Created by ales on 03/08/16.
 */
public interface iDataSource {
    public LightReading getLatestLightValue() throws RemoteException;
    public ArrayList<SensorReading> getLightValues(long startTime, long stopTime) throws RemoteException;

    public AccelerometerReading getLatestAccValue() throws RemoteException;
    public ArrayList<SensorReading> getAccValues(long startTime, long stopTime) throws RemoteException;

    public BatteryReading getLatestBatteryValue() throws RemoteException;
    public ArrayList<SensorReading> getBatteryValues(long startTime, long stopTime) throws RemoteException;

    public NoiseReading getLatestNoiseValue() throws RemoteException;
    public ArrayList<SensorReading> getNoiseValues(long startTime, long stopTime) throws RemoteException;
}
