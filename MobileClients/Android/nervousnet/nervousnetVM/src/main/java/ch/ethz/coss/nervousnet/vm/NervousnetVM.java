package ch.ethz.coss.nervousnet.vm;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.RemoteException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.locks.Lock;

import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;
import ch.ethz.coss.nervousnet.vm.nervousnet.NervousnetCore;


public class NervousnetVM {

    private static final String LOG_TAG = NervousnetVM.class.getSimpleName();
    private static final String DB_NAME = "NN-DB";

    private Lock storeMutex;

    //private Hashtable<Long, BaseSensor> hSensors = null;
    //private Hashtable<Long, SensorConfig> hSensorConfig = null;

    private UUID uuid;
    private Context context;
    private byte state = NervousnetVMConstants.STATE_PAUSED;

    private SensorManager sensorManager;
    private Handler dataCollectionHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            NNLog.d(LOG_TAG, "Collect data now. " + this.hashCode() + ", " + dataCollectionHandler.hashCode());

            dataCollectionHandler.postDelayed(this, 1000);
        }
    };


    private NervousnetCore generalNervousnet;


    public NervousnetVM(Context context) {

        // TODO: just for testing
        generalNervousnet = new NervousnetCore(context);



        NNLog.d(LOG_TAG, "Inside constructor");
        this.context = context;

        //sqlHelper = new SQLHelper(context, DB_NAME);

        //Config config = sqlHelper.loadVMConfig();

        /*if (config != null) {
            state = config.getState();
            uuid = UUID.fromString(config.getUUID());
            NNLog.d(LOG_TAG, "Config - UUID = " + uuid);
            NNLog.d(LOG_TAG, "Config - state = " + state);
        } else {
            NNLog.d(LOG_TAG, "Inside Constructure after loadVMConfig() no config found. Create a new config.");
            newUUID();
        }
*/
        initSensors();

        //if (state == NervousnetVMConstants.STATE_RUNNING)
            startSensors();

        EventBus.getDefault().register(this);
    }


    private void initSensors() {


        /*NNLog.d(LOG_TAG, "Inside initSensors");
        storeMutex = new ReentrantLock();
        // Initialize sensor manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        PackageManager manager = context.getPackageManager();

        hSensors = new Hashtable<Long, BaseSensor>();
        hSensorConfig = new Hashtable<Long, SensorConfig>();

        for (SensorConfig element : sqlHelper.getSensorConfigList()) {
            hSensorConfig.put(element.getID(), element);
        }

        int count = 0;
        for (Long key : hSensorConfig.keySet()) {
            SensorConfig sensorConfig = hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]);
            BaseSensor sensor = null;
            if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[0]) { // Accelerometer
                sensor = new AccelerometerSensor(sensorManager,
                        manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER) ? sensorConfig.getState()
                                : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            } else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[1]) { // Battery
                sensor = new BatterySensor(context, sensorConfig.getState());
            } else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[2]) { // Gyroscope
                sensor = new GyroSensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            } else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[3]) { // Location
                sensor = new LocationSensor(manager.hasSystemFeature(PackageManager.FEATURE_LOCATION)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE, locManager,
                        context);
            } else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[4]) { // Light
                sensor = new LightSensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            } else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[5]) { // Noise
                sensor = new NoiseSensor(manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE, context);
            } else if (sensorConfig.getID() ==
                    NervousnetVMConstants.sensor_ids[6]) { //Proximity
                sensor = new ProximitySensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)
                        ? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
            }

            if (sensor != null) {
                sensor.addListener(sqlHelper);
                hSensors.put(sensorConfig.getID(), sensor);

            }

        }
*/
    }

    public void startSensors() {


        generalNervousnet.startAllSensors();
        state = NervousnetVMConstants.STATE_RUNNING;



        /*NNLog.d(LOG_TAG, "Inside startSensors");
        int count = 0;
        for (Long key : hSensors.keySet()) {
            NNLog.d(LOG_TAG, "Inside startSensors Sensor ID = " + key);
            BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count]);
            if (sensor != null) {
                sensor.stopAndRestart(hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]).getState());
            }
        }
        dataCollectionHandler.postDelayed(runnable, 1000);*/
    }

    public void stopSensors() {

        generalNervousnet.stopAllSensors();
        state = NervousnetVMConstants.STATE_PAUSED;

        /*NNLog.d(LOG_TAG, "Inside stopSensors");
        int count = 0;
        for (Long key : hSensors.keySet()) {
            NNLog.d(LOG_TAG, "Inside stopSensors Sensor ID = " + key);
            BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count++]);
            if (sensor != null)
                sensor.stop(true);
        }

        dataCollectionHandler.removeCallbacks(runnable);*/
    }

    public void startSensor(long sensorID) {
//
//        BaseSensor sensor = hSensors.get(sensorID);
//        if(sensor != null) {
//            SensorConfig sensorConfig = hSensorConfig.get(sensorID);
//            sensorConfig.setState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_HIGH);
//            hSensorConfig.put(sensorConfig.getID(), sensorConfig);
//            updateSensorConfig();
//            sensor.stopAndRestart(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_HIGH);
////            sensor.setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_DELAY_HIGH);
////            sensor.start();
//        }
//
//
    }

    public void stopSensor(long sensorID, boolean changeStateFlag) {
        /*BaseSensor sensor = hSensors.get(sensorID);
        if (sensor != null)
            sensor.stop(true);*/
    }

    public synchronized UUID getUUID() {
        return uuid;
    }


    public synchronized void newUUID() {
        uuid = UUID.randomUUID();
        //sqlHelper.storeVMConfig(state, uuid);
    }


    public synchronized void regenerateUUID() {
        newUUID();
        //sqlHelper.resetDatabase();
    }


    public void storeNervousnetState(byte state) {
        this.state = state;
        try {
            //sqlHelper.storeVMConfig(state, uuid);
        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling storeVMConfig ");
            e.printStackTrace();
        }

    }

    public synchronized void updateSensorConfig(long id, byte state) {
        NNLog.d(LOG_TAG, "UpdateSensorConfig called with state = " + state);
        /*SensorConfig sensorConfig = hSensorConfig.get(id);

        sensorConfig.setState(state);
        try {
            sqlHelper.updateSensorConfig(sensorConfig);
            hSensorConfig.put(id, sensorConfig);
        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling updateSensorConfig ");
            e.printStackTrace();
        }*/

    }

//    public synchronized void updateSensorConfig() {
//        NNLog.d(LOG_TAG, "UpdateSensorConfig called with state = " + state);
//
//        try {
//            sqlHelper.updateAllSensorConfig(hSensorConfig.values());
//
//        } catch (Exception e) {
//            NNLog.d(LOG_TAG, "Exception while calling updateSensorConfig ");
//            e.printStackTrace();
//        }
//
//
//
//    }


    public synchronized void updateAllSensorConfig(byte state) {
        NNLog.d(LOG_TAG, "updateAllSensorConfig called with state = " + state);
        int count = 0;
        /*for (Long key : hSensorConfig.keySet()) {
            SensorConfig sensorConfig = hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]);
            sensorConfig.setState(state);
            hSensorConfig.put(sensorConfig.getID(), sensorConfig);
        }


        try {
            sqlHelper.updateAllSensorConfig(hSensorConfig.values());

        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling updateAllSensorConfig ");
            e.printStackTrace();
        }
*/
    }


    public byte getState() {
        return state;
    }

    public synchronized SensorReading getLatestReading(long sensorID) {
        NNLog.d(LOG_TAG, "getLatestReading of ID = " + sensorID + " requested ");

        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            return Utils.getErrorReading(101);

        }
        //return hSensors.get(sensorID).getReading();


        // TODO fix
        if (sensorID == 4){
            return generalNervousnet.getLatestReading("Light_v2");
        }
        return null;

    }

    public synchronized void getReading(Long sensorID, RemoteCallback cb) {
        NNLog.d(LOG_TAG, "getReading with callback " + cb);

        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {

            if (sensorID == 4) {

                ArrayList<SensorReading> readings = generalNervousnet.getReadings("Light_v2");
                try {
                    cb.success(readings);
                } catch (RemoteException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    try {
                        cb.failure(Utils.getErrorReading(301));
                    } catch (RemoteException re) {
                        re.printStackTrace();
                    }
                }
            }
        }


    }

    public synchronized void getReadings(long sensorID, long startTime, long endTime, RemoteCallback cb) {
        /*if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {

            sqlHelper.getSensorReadings((int) sensorID, startTime, endTime, cb);
        }*/
    }



    public byte getSensorState(long id) {
        // TODO
        return 0;
        //return hSensorConfig.get(id).getState();
    }


    @Subscribe
    public void onNNEvent(NNEvent event) {
        NNLog.d(LOG_TAG, "onSensorStateEvent called ");

        /*if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST) {
            updateSensorConfig(event.sensorID, event.state);
            BaseSensor sensor = hSensors.get(event.sensorID);
            sensor.stopAndRestart(state);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));


        } else if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST) {
            updateAllSensorConfig(event.state);
            stopSensors();
            startSensors();
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_PAUSE_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_PAUSED);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_RUNNING);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        }*/

    }

}
