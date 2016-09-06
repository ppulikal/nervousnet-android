package data;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.NervousnetSensorDataListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;

/**
 * Created by ales on 28/06/16.
 *
 * Nervousnet queries the data in Nervousnet application. This class is source of the data.
 */
public class Nervousnet implements iDataSource, NervousnetServiceConnectionListener, NervousnetSensorDataListener {

    private enum SensorType{
        ACC,
        BATTERY,
        GYRO,
        LIGHT,
        LOC,
        NOISE
    }

    // We need context to get connections and sensor data
    private Context context;

    // Connection to the service
    NervousnetServiceController nervousnetServiceController;

    // Constructor
    public Nervousnet(Context context){
        this.context = context;
    }

    // Connect
    public void connect(){
        Log.d("NERVOUSNET", "Connecting ...");
        nervousnetServiceController = new NervousnetServiceController(this.context, this);
        Log.d("NERVOUSNET", "Connecting2 ..." + nervousnetServiceController);
        try {
            nervousnetServiceController.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /////////////////////////////////////////////////////////////////////////
    // LATEST DATA
    /////////////////////////////////////////////////////////////////////////

    public AccelerometerReading getLatestAccValue() throws RemoteException {
        AccelerometerReading lReading  = (AccelerometerReading) nervousnetServiceController.getLatestReading(LibConstants.SENSOR_ACCELEROMETER);
        return lReading;
    }


    public BatteryReading getLatestBatteryValue() throws RemoteException {
        BatteryReading lReading  = (BatteryReading) nervousnetServiceController.getLatestReading(LibConstants.SENSOR_BATTERY);
        return lReading;
    }


    public LightReading getLatestLightValue() throws RemoteException {
        LightReading reading = (LightReading) nervousnetServiceController.getLatestReading(LibConstants.SENSOR_LIGHT);
        return reading;
    }


    public NoiseReading getLatestNoiseValue() throws RemoteException {
        NoiseReading lReading  = (NoiseReading) nervousnetServiceController.getLatestReading(LibConstants.SENSOR_NOISE);
        return lReading;
    }


    /////////////////////////////////////////////////////////////////////////
    // RANGE
    /////////////////////////////////////////////////////////////////////////

    @Override
    public ArrayList<SensorReading> getAccValues(long startTime, long stopTime) throws RemoteException {
        Callback cb = new Callback(SensorType.ACC);
        nervousnetServiceController.getReadings(LibConstants.SENSOR_ACCELEROMETER, startTime, stopTime, cb);
        return (ArrayList<SensorReading>) cb.getList();
    }

    @Override
    public ArrayList<SensorReading> getBatteryValues(long startTime, long stopTime) throws RemoteException {
        Callback cb = new Callback(SensorType.BATTERY);
        Log.d("NERVOUSNET-BATTERY", "battery getReadings start ...");
        nervousnetServiceController.getReadings(LibConstants.SENSOR_BATTERY, startTime, stopTime, cb);
        Log.d("NERVOUSNET-BATTERY", "battery getReadings stop ...");
        return (ArrayList<SensorReading>) cb.getList();
    }

    @Override
    public ArrayList<SensorReading> getLightValues(long startTime, long stopTime) throws RemoteException {

        // TEST
        List list = nervousnetServiceController.getAverage(4); // light is 4
        Log.d("NERVOSNET-TEST", "Average " + list.get(0));



        Callback cb = new Callback(SensorType.LIGHT);
        Log.d("NERVOUSNET-LIGHT", "light getReadings start ...");
        nervousnetServiceController.getReadings(LibConstants.SENSOR_LIGHT, startTime, stopTime, cb);
        Log.d("NERVOUSNET-LIGHT", "light getReadings stop ...");
        return (ArrayList<SensorReading>) cb.getList();
    }

    @Override
    public ArrayList<SensorReading> getNoiseValues(long startTime, long stopTime) throws RemoteException {
        Callback cb = new Callback(SensorType.NOISE);
        Log.d("NERVOUSNET-NOISE", "noise getReadings start ...");
        nervousnetServiceController.getReadings(LibConstants.SENSOR_NOISE, startTime, stopTime, cb);
        Log.d("NERVOUSNET-NOISE", "noise getReadings stop ...");
        return (ArrayList<SensorReading>) cb.getList();
    }


    /////////////////////////////////////////////////////////////////////////
    // CALLBACK
    /////////////////////////////////////////////////////////////////////////

    class Callback extends RemoteCallback.Stub {
        private SensorType sType;
        private List list;

        public Callback(SensorType sType){
            this.sType = sType;
        }

        @Override
        public void success(final List<SensorReading> list) throws RemoteException {
            Log.d("NERVOUSNET CALLBACK", sType + " callback success " + list.size());
            this.list = list;
        }

        @Override
        public void failure(final ErrorReading reading) throws RemoteException {
            Log.d("NERVOUSNET CALLBACK", sType + "callback failure "+reading.getErrorString());
        }

        public List getList() { return this.list; }
    }


    /////////////////////////////////////////////////////////////////////////
    // OVERWRITE
    /////////////////////////////////////////////////////////////////////////

    @Override
    public void onSensorDataReady(SensorReading reading) {

    }

    @Override
    public void onServiceConnected() {

    }

    @Override
    public void onServiceDisconnected() {

    }

    @Override
    public void onServiceConnectionFailed(ErrorReading errorReading) {

    }



/*

    public SensorPoint getLatestGyroValue(){
        GyroReading lReading = null;
        try {
            lReading  = (GyroReading) nervousnetServiceController.getLatestReading(LibConstants.SENSOR_GYROSCOPE);
            long timestamp = lReading.timestamp;
            double[] values = {lReading.getGyroX(), lReading.getGyroY(), lReading.getGyroZ()};
            int type = lReading.type;
            Log.d("NERVOUSNET", "Getting battery value ... " + values[0]);
            return new SensorPoint(type, timestamp, values);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    public SensorPoint getLatestLocValue(){
        // TODO
        return null;
    }*/
}
