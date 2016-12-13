/*******************************************************************************
 *     Nervousnet - a distributed middleware software for social sensing.
 *      It is responsible for collecting and managing data in a fully de-centralised fashion
 *
 *     Copyright (C) 2016 ETH Zürich, COSS
 *
 *     This file is part of Nervousnet Framework
 *
 *     Nervousnet is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Nervousnet is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 *
 * 	Contributors:
 * 	@author Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.vm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import android.os.RemoteException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;
import ch.ethz.coss.nervousnet.vm.sensors.AccelerometerSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BatterySensor;
import ch.ethz.coss.nervousnet.vm.sensors.GyroSensor;
import ch.ethz.coss.nervousnet.vm.sensors.LightSensor;
import ch.ethz.coss.nervousnet.vm.sensors.LocationSensor;
import ch.ethz.coss.nervousnet.vm.sensors.NoiseSensor;
import ch.ethz.coss.nervousnet.vm.sensors.ProximitySensor;
import ch.ethz.coss.nervousnet.vm.storage.Config;
import ch.ethz.coss.nervousnet.vm.storage.SQLHelper;
import ch.ethz.coss.nervousnet.vm.storage.SensorConfig;

/**
 * Main class for the Nervousnet Virtual Machine.
 */
public class NervousnetVM {

    private static final String LOG_TAG = NervousnetVM.class.getSimpleName();
    private static final String DB_NAME = "NN-DB";

    private Lock storeMutex;

    private Hashtable<Long, BaseSensor> hSensors = null;
    private Hashtable<Long, SensorConfig> hSensorConfig = null;

    private UUID uuid;
    private Context context;
    private byte state = NervousnetVMConstants.STATE_PAUSED;

    private SQLHelper sqlHelper;
    private SensorManager sensorManager;
    private Handler dataCollectionHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            NNLog.d(LOG_TAG, "Collect data now. " + this.hashCode() + ", " + dataCollectionHandler.hashCode());

            dataCollectionHandler.postDelayed(this, 1000);
        }
    };

    /**
     * Constructor of the NervousnetVM object
     * @param context Application Context object
     */
    public NervousnetVM(Context context) {
        NNLog.d(LOG_TAG, "Inside constructor");
        this.context = context;

        sqlHelper = new SQLHelper(context, DB_NAME);

        Config config = sqlHelper.loadVMConfig();

        if (config != null) {
            state = config.getState();
            uuid = UUID.fromString(config.getUUID());
            NNLog.d(LOG_TAG, "Config - UUID = " + uuid);
            NNLog.d(LOG_TAG, "Config - state = " + state);
        } else {
            NNLog.d(LOG_TAG, "Inside Constructure after loadVMConfig() no config found. Create a new config.");
            newUUID();
        }

        initSensors();

        if (state == NervousnetVMConstants.STATE_RUNNING)
            startSensors();

        EventBus.getDefault().register(this);
    }


    /**
     * Register and analyse the available Sensors
     */
    private void initSensors() {
        NNLog.d(LOG_TAG, "Inside initSensors");
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

    }

    /**
     * Starts all Sensors according to their current state
     */
    public void startSensors() {
        NNLog.d(LOG_TAG, "Inside startSensors");
        int count = 0;
        for (Long key : hSensors.keySet()) {
            NNLog.d(LOG_TAG, "Inside startSensors Sensor ID = " + key);
            BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count]);
            if (sensor != null) {
                sensor.stopAndRestart(hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]).getState());
            }
        }
        dataCollectionHandler.postDelayed(runnable, 1000);
    }


    /**
     * Stops all sensors if already running
     */
    public void stopSensors() {
        NNLog.d(LOG_TAG, "Inside stopSensors");
        int count = 0;
        for (Long key : hSensors.keySet()) {
            NNLog.d(LOG_TAG, "Inside stopSensors Sensor ID = " + key);
            BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count++]);
            if (sensor != null)
                sensor.stop(true);
        }

        dataCollectionHandler.removeCallbacks(runnable);
    }

//    public void startSensor(long sensorID) {
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
//    }


    /**
     * Method to Stop individual Sensors
     * @param sensorID - long Sensor ID of the Sensors
     * @param changeStateFlag - boolean flag for forcing stop of sensors
     */
    public void stopSensor(long sensorID, boolean changeStateFlag) {
        BaseSensor sensor = hSensors.get(sensorID);
        if (sensor != null)
            sensor.stop(true);
    }


    /**
     * gets UUID representing the installation.
     * note: remember the UUID is transient and can be reset by the user from the settinsg screen
     * @return UUID
     */
    public synchronized UUID getUUID() {
        return uuid;
    }


    /**
     * Method to generate a new UUID and store it in the database
     */
    public synchronized void newUUID() {
        uuid = UUID.randomUUID();
        sqlHelper.storeVMConfig(state, uuid);
    }

    /**
     * Method to regenerate a new UUID and update this value into the database
     */
    public synchronized void regenerateUUID() {
        newUUID();
        sqlHelper.resetDatabase();
    }


    /**
     * Method to save or update the state of Nervousnet and save it onto the database
     * @param state - new state. Can be one of NervousnetVMConstants.STATE_PAUSED or NervousnetVMConstants.STATE_RUNNING
     */
    public void storeNervousnetState(byte state) {
        this.state = state;
        try {
            sqlHelper.storeVMConfig(state, uuid);
        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling storeVMConfig ");
            e.printStackTrace();
        }

    }


    /**
     * Method to update the state of an individual sensor
     * @param id long id of the Sensors
     * @param state new state of the Sensor
     */
    public synchronized void updateSensorConfig(long id, byte state) {
        NNLog.d(LOG_TAG, "UpdateSensorConfig called with state = " + state);
        SensorConfig sensorConfig = hSensorConfig.get(id);

        sensorConfig.setState(state);
        try {
            sqlHelper.updateSensorConfig(sensorConfig);
            hSensorConfig.put(id, sensorConfig);
        } catch (Exception e) {
            NNLog.d(LOG_TAG, "Exception while calling updateSensorConfig ");
            e.printStackTrace();
        }

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

    /**
     * Method to change the state of an all available sensor
     * @param state new state of the Sensors.
     */
    public synchronized void updateAllSensorConfig(byte state) {
        NNLog.d(LOG_TAG, "updateAllSensorConfig called with state = " + state);
        int count = 0;
        for (Long key : hSensorConfig.keySet()) {
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

    }


    /**
     * Returns the state of Nervousnet
     * @return byte state of Nervousnet
     */
    public byte getState() {
        return state;
    }


    /**
     * Returns the latest readings available for a specific sensor
     * @param sensorID -  id of the sensor
     * @return SensorReading - object of SensorReading containing the Readings.
     */
    public synchronized SensorReading getLatestReading(long sensorID) {
        NNLog.d(LOG_TAG, "getLatestReading of ID = " + sensorID + " requested ");

        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");

            return Utils.getErrorReading(101);
        }
        return hSensors.get(sensorID).getReading();
    }

    /**
     *  Get reading for a specific Sensor using callback.
     * @param sensorID sensor id of specific sensors
     * @param cb RemoteCallback object
     */
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
            NNLog.d(LOG_TAG, "getReading callback with success");
            ArrayList aList = new ArrayList();
            NNLog.d(LOG_TAG, "getReading with callback called and state is not paused2");
            aList.add(hSensors.get(sensorID).getReading());
            NNLog.d(LOG_TAG, "getReading with callback called and state is not paused3 " + aList.size());
            try {
                cb.success(aList);
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

    /**
     * Gets readings for a specific sensors for a time period using callback.
     * @param sensorID sensor id of the sensor
     * @param startTime starting time for the readings
     * @param endTime end time for the readings
     * @param cb callback object
     */

    public synchronized void getReadings(long sensorID, long startTime, long endTime, RemoteCallback cb) {
        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {

            sqlHelper.getSensorReadings((int) sensorID, startTime, endTime, cb);
        }
    }

    /**
     * Gets the sensor state
     * @param id Sensor id
     * @return byte returns current state of the sensor
     */
    public byte getSensorState(long id) {
        return hSensorConfig.get(id).getState();
    }

    /**
      * Method to catch NNEvents.
      * @param event NNEvent object
     */
    @Subscribe
    public void onNNEvent(NNEvent event) {
        NNLog.d(LOG_TAG, "onSensorStateEvent called ");

        if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST) {
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
        }

    }
}
