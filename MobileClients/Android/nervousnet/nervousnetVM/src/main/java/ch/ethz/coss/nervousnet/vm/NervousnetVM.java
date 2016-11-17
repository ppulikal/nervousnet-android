package ch.ethz.coss.nervousnet.vm;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.RemoteException;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.UUID;

import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.configuration.ConfigurationManager;
import ch.ethz.coss.nervousnet.vm.configuration.GeneralSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.configuration.JsonConfigurationLoader;
import ch.ethz.coss.nervousnet.vm.configuration.StateDBManager;
import ch.ethz.coss.nervousnet.vm.configuration.iConfigurationManager;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;
import ch.ethz.coss.nervousnet.vm.nervousnet.ConfigurationError;
import ch.ethz.coss.nervousnet.vm.nervousnet.NervousnetMain;
import ch.ethz.coss.nervousnet.vm.nervousnet.database.NoSuchElementInDBException;
import ch.ethz.coss.nervousnet.vm.nervousnet.iNervousnetMain;


public class NervousnetVM {

    private static final String LOG_TAG = NervousnetVM.class.getSimpleName();
    private static final String DB_NAME = "NN-DB";

    //private Hashtable<Long, BaseSensor> hSensors = null;
    //private Hashtable<Long, SensorConfig> hSensorConfig = null;

    private UUID uuid;
    private Context context;
    private byte state = NervousnetVMConstants.STATE_PAUSED;
    private Handler dataCollectionHandler = new Handler();

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            NNLog.d(LOG_TAG, "Collect data now. " + this.hashCode() + ", " + dataCollectionHandler.hashCode());

            dataCollectionHandler.postDelayed(this, 1000);
        }
    };
    private iNervousnetMain nervousnetMain;
    private iConfigurationManager configurationManager;

    public NervousnetVM(Context context) {
        this.context = context;
        this.nervousnetMain = new NervousnetMain(context);
        this.configurationManager = new ConfigurationManager(context);

        for (GeneralSensorConfiguration conf : configurationManager.getAllConfigurations()){
            try {
                nervousnetMain.registerSensor((BasicSensorConfiguration)conf);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        initSensors();

        if (state == NervousnetVMConstants.STATE_RUNNING)
            startSensors();

        EventBus.getDefault().register(this);
    }


    private void initSensors() {

    }

    public void startSensors() {
        nervousnetMain.startAllSensors();
        dataCollectionHandler.postDelayed(runnable, 1000);

    }

    public void stopSensors() {
        nervousnetMain.stopAllSensors();
        dataCollectionHandler.removeCallbacks(runnable);

    }

    public void startSensor(long sensorID) {

    }

    public void stopSensor(long sensorID, boolean changeStateFlag) {

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
        configurationManager.setNervousnetState(state);
    }

    public synchronized void updateSensorConfig(long id, byte state) {
        try {
            int oldState = configurationManager.getSensorState(id);
            if (state != oldState) {
                configurationManager.setSensorState(id, state);
                nervousnetMain.restartSensor(id);
            }
        } catch (NoSuchElementException e){
            e.printStackTrace();
        }
    }


/*    public synchronized void updateAllSensorConfig(long rate) {
        //configStoreManager.storeSensorState();
    }*/


    public byte getNervousnetState() {
        //return state;
        try {
            return (byte) configurationManager.getNervousnetState();
        } catch (NoSuchElementInDBException e) {
            e.printStackTrace();
            return NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE;
        }
    }




    public synchronized SensorReading getLatestReading(long sensorID) {
        return nervousnetMain.getLatestReading(sensorID);
    }

    public synchronized void getReading(long sensorID, RemoteCallback cb) {
        NNLog.d(LOG_TAG, "getReading with callback " + cb);

        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {

            ArrayList<SensorReading> readings = nervousnetMain.getReadings(sensorID);
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

    public synchronized void getReadings(long sensorID, long startTime, long endTime, RemoteCallback cb) {

        if (state == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {

            ArrayList<SensorReading> readings = nervousnetMain.getReadings(sensorID, startTime, endTime);
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

    public byte getSensorState(long id) {
        try {
            return (byte) configurationManager.getSensorState(id);
        } catch (NoSuchElementException e){
            return NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF;
        }
    }


    @Subscribe
    public void onNNEvent(NNEvent event) {
        Log.d(LOG_TAG, "onSensorStateEvent called ");

        if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST) {
            updateSensorConfig(event.sensorID, event.state);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST) {
            //TODO nervousnetMain.updateSamplingRateAll(event.state);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_PAUSE_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_PAUSED);
            nervousnetMain.stopAllSensors();
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_RUNNING);
            nervousnetMain.startAllSensors();
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        }

    }

}
