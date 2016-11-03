package ch.ethz.coss.nervousnet.vm;

import android.content.Context;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.RemoteException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.UUID;

import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;
import ch.ethz.coss.nervousnet.vm.nervousnet.NervousnetMain;


public class NervousnetVM {

    private static final String LOG_TAG = NervousnetVM.class.getSimpleName();
    private static final String DB_NAME = "NN-DB";

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


    private NervousnetMain generalNervousnet;


    public NervousnetVM(Context context) {
        this.context = context;
        this.generalNervousnet = new NervousnetMain(context);


        initSensors();

        if (state == NervousnetVMConstants.STATE_RUNNING)
            startSensors();

        EventBus.getDefault().register(this);
    }


    private void initSensors() {

    }

    public void startSensors() {


        generalNervousnet.startAllSensors();
        dataCollectionHandler.postDelayed(runnable, 1000);

    }

    public void stopSensors() {

        generalNervousnet.stopAllSensors();
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
        this.state = state;
    }

    public synchronized void updateSensorConfig(long id, byte state) {

    }


    public synchronized void updateAllSensorConfig(byte state) {

    }


    public byte getState() {
        //return state;
        return NervousnetVMConstants.STATE_RUNNING;
    }




    public synchronized SensorReading getLatestReading(long sensorID) {
        return generalNervousnet.getLatestReading("Light_v2");
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

        // For library

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
