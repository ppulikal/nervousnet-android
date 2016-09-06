package virtual;

import android.os.RemoteException;
import android.util.Log;
import java.util.ArrayList;


import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.GyroReading;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.ProximityReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import data.iDataSource;
import sensor.ActiveSensors;

/**
 * Created by ales on 03/08/16.
 */
public class DataSourceHelper {

    private static boolean bLight = true;
    private static boolean bBattery = false;
    private static boolean bNoise = false;

    private static final long oneDayMiliseconds = 86400; //000
    private static final long initWindowSizeMiliseconds = 10000;

    public static OriginalVirtualSensorPoint getNextVirtualSensorPoint(iDataSource dataSource) throws RemoteException {

        OriginalVirtualSensorPoint original = new OriginalVirtualSensorPoint();

        if (bLight) {
            LightReading light = dataSource.getLatestLightValue();
            original.setLight( light.getLuxValue() );
        }

        if (bNoise) {
            NoiseReading noise = dataSource.getLatestNoiseValue();
            original.setNoise( noise.getdbValue() );
        }

        if (bBattery) {
            BatteryReading battery = dataSource.getLatestBatteryValue();
            original.setBattery( battery.getPercent() );
        }

        // Set current time
        original.setTimestamp(System.currentTimeMillis());

        return original;
    }

    public static ArrayList<OriginalVirtualSensorPoint> getInitData(iDataSource dataSource) throws RemoteException {

        long stop = System.currentTimeMillis();
        long start = stop - oneDayMiliseconds;

        ArrayList<ArrayList<SensorReading>> arr = new ArrayList<>();

        if (bLight) {
            ArrayList<SensorReading> light = dataSource.getLightValues( start, stop );
            if (light != null) {
                arr.add(light);

                for (SensorReading point : light) {
                    Log.d("TIMESTAMP-LIGHT", "" + point.timestamp + " " + ((LightReading) point).getLuxValue());
                }
            }
            else {

            }
        }

        if (bNoise) {
            ArrayList<SensorReading> noise = dataSource.getNoiseValues( start, stop );
            if (noise != null) {
                arr.add(noise);
                //Log.d("TIMESTAMP-NOISE", "Size: " + arr.size());
                for (SensorReading point : noise) {
                    Log.d("TIMESTAMP-NOISE", "" + point.timestamp + " " + ((NoiseReading) point).getdbValue());
                }
            }
            else{

            }
        }

        if (bBattery) {
            ArrayList<SensorReading> battery = dataSource.getBatteryValues( start, stop );
            if (battery != null) {
                arr.add(battery);
                //Log.d("TIMESTAMP-BATTERY", "Size: " + arr.size());
                for (SensorReading point : battery) {
                    Log.d("TIMESTAMP-BATTERY", "" + point.timestamp + " " + ((BatteryReading) point).getPercent());
                }
            }
            else {

            }
        }

        // COMBINE
        ArrayList<OriginalVirtualSensorPoint> vsparr = combine(arr);

        return vsparr;
    }

    private static ArrayList<OriginalVirtualSensorPoint> combine(ArrayList<ArrayList<SensorReading>> listOfSensorsReadings){

        long startTimestamp = Long.MIN_VALUE;
        long stopTimestamp = Long.MIN_VALUE;

        //1. get the beginning and end point of the frame, used for cuting into intervals
        for( ArrayList<SensorReading> arr : listOfSensorsReadings ){
            long tmpStart = arr.get(0).timestamp;
            if (tmpStart > startTimestamp)
                startTimestamp = tmpStart;
            long tmpStop = arr.get(arr.size() - 1).timestamp;
            if (tmpStop > stopTimestamp)
                stopTimestamp = tmpStop;
        }

        // Let's take interval as 1s:
        long start = startTimestamp;
        long step = initWindowSizeMiliseconds;

        // Initialize pointes which will run through all arrays
        int[] pointers = new int[listOfSensorsReadings.size()];
        for( int i = 0; i < listOfSensorsReadings.size(); i++ ){
            pointers[i++] = -1;
        }

        ArrayList<OriginalVirtualSensorPoint> vsparr = new ArrayList<>();

        while ( start <= stopTimestamp ) {

            for (int i = 0; i < pointers.length; i++) {
                ArrayList<SensorReading> readings = listOfSensorsReadings.get(i);
                int sizeI = readings.size();
                while (pointers[i]+1 < sizeI && readings.get(pointers[i]+1).timestamp <= start) {
                    pointers[i]++;
                }
            }

            OriginalVirtualSensorPoint original = new OriginalVirtualSensorPoint();

            // Set timestamp of the combined virtual point

            original.setTimestamp(start);
            // Fill the VirtualSensor
            for (int i = 0; i < pointers.length; i++) {
                SensorReading reading = listOfSensorsReadings.get(i).get(pointers[i]);
                if (reading instanceof NoiseReading) {
                    original.setNoise(((NoiseReading) reading).getdbValue());
                } else if (reading instanceof LightReading) {
                    original.setLight(((LightReading) reading).getLuxValue());
                } else if (reading instanceof AccelerometerReading) {
                    original.setAccelerometer(((AccelerometerReading) reading).getX(),
                            ((AccelerometerReading) reading).getY(),
                            ((AccelerometerReading) reading).getZ());
                } else if (reading instanceof GyroReading) {
                    original.setGyrometer(((GyroReading) reading).getGyroX(),
                            ((GyroReading) reading).getGyroY(),
                            ((GyroReading) reading).getGyroZ());
                } else if (reading instanceof ProximityReading) {
                    original.setProximity(((ProximityReading) reading).getProximity());
                }
            }
            vsparr.add(original);
            start += step;
        }
        return vsparr;
    }
}
