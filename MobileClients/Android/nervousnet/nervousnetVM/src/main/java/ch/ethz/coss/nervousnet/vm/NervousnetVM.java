package ch.ethz.coss.nervousnet.vm;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;

import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.configuration.BasicSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.configuration.ConfigurationManager;
import ch.ethz.coss.nervousnet.vm.configuration.GeneralSensorConfiguration;
import ch.ethz.coss.nervousnet.vm.configuration.iConfigurationManager;
import ch.ethz.coss.nervousnet.vm.database.NervousnetDBManager;
import ch.ethz.coss.nervousnet.vm.events.NNEvent;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor;


public class NervousnetVM {

    private static final String LOG_TAG = NervousnetVM.class.getSimpleName();
    private UUID uuid;
    private Context context;

    // Manager for storing and retrieving of configuration
    private iConfigurationManager configurationManager;

    // Storage for sensor data
    private NervousnetDBManager nervousnetDB;

    // A list of all initialized sensor wrappers. In fact it is a hash map
    // for easier query based on a sensor id.
    private HashMap<Long, BaseSensor> sensorWrappersMap = new HashMap();


    public NervousnetVM(Context context) {
        this.context = context;
        this.configurationManager = new ConfigurationManager(context);
        this.nervousnetDB = NervousnetDBManager.getInstance(context);
        for (GeneralSensorConfiguration conf : configurationManager.getAllConfigurations()){
            try {
                initSensor((BasicSensorConfiguration) conf);
                //TODO If registration doesn't succeed, we ignore it for now
            } catch (Exception e){
                Log.d(LOG_TAG, e.getMessage());
            }
        }
        if (configurationManager.getNervousnetState() == NervousnetVMConstants.STATE_RUNNING)
            startSensors();
        EventBus.getDefault().register(this);
    }


    //####################################################################
    //REGISTER SENSOR
    //####################################################################

    /**
     * Register new sensor by providing configuration. TODO not implemented yet
     */
    public void registerSensor(GeneralSensorConfiguration config){
        // TODO: configuration manager has to accept new registration
        //configurationManager.setConf????
        //configMap.put(config.getSensorID(), config);
        //nervousnetDB.createTableIfNotExists(config);
    }


    //####################################################################
    //SENSOR INITIALIZATION
    //####################################################################

    /**
     * Initialize sensor with a given configuration.
     * @param sensorConf
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     */
    private BaseSensor initSensor(BasicSensorConfiguration sensorConf)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException, SensorIsOffException {
        if (sensorWrappersMap.containsKey(sensorConf.getSensorID())){
            sensorWrappersMap.get(sensorConf.getSensorID()).stop();
        }
        if (sensorConf.getSamplingRate() < 0){
            throw new SensorIsOffException("Sensor rate is negative, so we assume it's off");
        } else {
            String wrapperName = sensorConf.getWrapperName();
            String packageName = BaseSensor.class.getPackage().getName();
            String className = packageName + "." + wrapperName;
            // Automatically get the wrapper for the sensor
            BaseSensor sensorListener = (BaseSensor) Class.forName(className)
                    .getConstructor(Context.class, BasicSensorConfiguration.class)
                    .newInstance(context, sensorConf);
            sensorWrappersMap.put(sensorConf.getSensorID(), sensorListener);
            nervousnetDB.createTableIfNotExists(sensorConf);
            //sensorListener.start();
            return sensorListener;
        }
    }


    //####################################################################
    //SENSOR ACTIVATION
    //####################################################################

    /**
     * Starts all configured sensors. If a sensor does not work properly, will
     * be ignored.
     */
    public void startSensors() {
        for (Long sensorID : configurationManager.getSensorIDs())
            startSensor(sensorID);
    }


    /**
     * Stops all configured sensors.
     */
    public void stopSensors() {
        for (Long sensorID : configurationManager.getSensorIDs())
            stopSensor(sensorID);
    }


    /**
     * Start sensor with a given ID. If sensor does not work properly, will be ignored.
     */
    public void startSensor(long sensorID){
        if (sensorWrappersMap.containsKey(sensorID)){
            sensorWrappersMap.get(sensorID).start();
        } else {
            try {
                BaseSensor listener = initSensor((BasicSensorConfiguration)
                        configurationManager.getConfiguration(sensorID));
                listener.start();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (SensorIsOffException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * Stop sensor with a given ID.
     */
    public void stopSensor(long sensorID){
        if (sensorWrappersMap.containsKey(sensorID)){
            sensorWrappersMap.get(sensorID).stop();
            //sensorWrappersMap.remove(sensorID);
        }
    }


    //####################################################################
    //GET SENSOR DATA
    //####################################################################

    /**
     * Returns latest reading that has been stored since starting the application.
     */
    public SensorReading getLatestReading(long sensorID)
            throws NoSuchElementException{
        return NervousnetDBManager.getLatestReading(sensorID);
    }

    /**
     * Calls success function of callback object if the data is successfully retrieved otherwise
     * failure function of callback object is called.
     */
    public synchronized void getReading(long sensorID, RemoteCallback cb) {
        NNLog.d(LOG_TAG, "getReading with callback " + cb);
        if (configurationManager.getNervousnetState() == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            ArrayList<SensorReading> readings = getReadings(sensorID);
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


    /**
     * Calls success function of callback object if the data is successfully retrieved otherwise
     * failure function of callback object is called. Only readings between the time range is provided.
     */
    public synchronized void getReadings(long sensorID,
                                         long startTimestamp, long endTimestamp,
                                         RemoteCallback cb) {
        if (configurationManager.getNervousnetState() == NervousnetVMConstants.STATE_PAUSED) {
            NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
            try {
                cb.failure(Utils.getErrorReading(101));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {

            ArrayList<SensorReading> readings = getReadings(sensorID, startTimestamp, endTimestamp);
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


    /**
     * Returns list of readings.
     */
    private ArrayList<SensorReading> getReadings(long sensorID){
        return nervousnetDB.getReadings(configurationManager.getConfiguration(sensorID));
    }

    /**
     * Returns list of readings between the time range.
     */
    private ArrayList<SensorReading> getReadings(long sensorID,
                                                 long startTimestamp, long endTimestamp){
        return nervousnetDB.getReadings(configurationManager.getConfiguration(sensorID),
                startTimestamp, endTimestamp);
    }




    //####################################################################
    //STORE SENSOR DATA
    //####################################################################

    public void store(SensorReading reading){
        nervousnetDB.store(reading);
    }

    public void store(ArrayList<SensorReading> readings){
        nervousnetDB.store(readings);
    }


    //####################################################################
    //TABLE AND DATABASE MANAGEMENT
    //####################################################################

    public void deleteTableIfExists(long sensorID){
        nervousnetDB.deleteTableIfExists(sensorID);
    }

    public void createTableIfNotExists(long sensorID){
        nervousnetDB.createTableIfNotExists(configurationManager.getConfiguration(sensorID));
    }

    public void deleteAllDatabases(){
        String[] dblist = context.databaseList();
        for (String db : dblist){
            context.deleteDatabase(db);
        }
    }


    //####################################################################
    //GETTERS
    //####################################################################

    public Context getContext(){
        return context;
    }


    //####################################################################
    //STATE HANDLING
    //####################################################################

    public void storeNervousnetState(byte state) {
        configurationManager.setNervousnetState(state);
    }

    public synchronized void updateSensorState(long id, byte state) {
        try {
            int oldState = configurationManager.getSensorState(id);
            if (state != oldState) {
                configurationManager.setSensorState(id, state); // update rate
                if (state == 0) stopSensor(id);                 // turn it off
                else if (oldState == 0) startSensor(id);        // turn it on
            }
        } catch (NoSuchElementException e){
            e.printStackTrace();
        }
    }



    public byte getNervousnetState() {
        return (byte) configurationManager.getNervousnetState();
    }

    public byte getSensorState(long id) {
        try {
            return (byte) configurationManager.getSensorState(id);
        } catch (NoSuchElementException e){
            return NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF;
        }
    }


    //####################################################################
    //REST
    //####################################################################

    public synchronized UUID getUUID() {
        return uuid;
    }

    public synchronized void newUUID() {
        uuid = UUID.randomUUID();
    }

    @Subscribe
    public void onNNEvent(NNEvent event) {
        Log.d(LOG_TAG, "onSensorStateEvent called ");

        if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_SENSOR_STATE_REQUEST) {
            updateSensorState(event.sensorID, event.state);
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST) {
            for(Long sensorID : configurationManager.getSensorIDs()){
                updateSensorState(sensorID, event.state);
            }
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_SENSOR_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_PAUSE_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_PAUSED);
            stopSensors();
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        } else if (event.eventType == NervousnetVMConstants.EVENT_START_NERVOUSNET_REQUEST) {
            storeNervousnetState(NervousnetVMConstants.STATE_RUNNING);
            startSensors();
            EventBus.getDefault().post(new NNEvent(NervousnetVMConstants.EVENT_NERVOUSNET_STATE_UPDATED));
        }
    }
}
