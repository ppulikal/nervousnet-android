package ch.ethz.coss.nervousnet.vm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Handler;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.sensors.AccelerometerSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BatterySensor;
import ch.ethz.coss.nervousnet.vm.sensors.GyroSensor;
import ch.ethz.coss.nervousnet.vm.sensors.LightSensor;
import ch.ethz.coss.nervousnet.vm.sensors.LocationSensor;
import ch.ethz.coss.nervousnet.vm.sensors.NoiseSensor;
import ch.ethz.coss.nervousnet.vm.storage.Config;
import ch.ethz.coss.nervousnet.vm.storage.SQLHelper;
import ch.ethz.coss.nervousnet.vm.storage.SensorConfig;

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
			uuid = UUID.randomUUID();
			sqlHelper.storeVMConfig(state, uuid);
		}

		initSensors();
		
		if(state == NervousnetVMConstants.STATE_RUNNING)
		startSensors();
	}

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
			}
			else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[2]) { // Gyroscope
				sensor = new GyroSensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)
						? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			}
			else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[3]) { // Location
				sensor = new LocationSensor(manager.hasSystemFeature(PackageManager.FEATURE_LOCATION)
						? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE, locManager,
						context);
			} else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[4]) { // Light
				sensor = new LightSensor(sensorManager, manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT)
						? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			}
			else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[5]) { // Noise
				sensor = new NoiseSensor(manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
						? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			}
//			else if(sensorConfig.getID() ==
//			 NervousnetVMConstants.sensor_ids[6]) { //Proximity
//			 sensor = new
//			 ProximitySensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)
//			 ? sensorConfig.getState() :
//			 NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
//			 }

			if (sensor != null) {
				sensor.addListener(sqlHelper);
				hSensors.put(sensorConfig.getID(), sensor);

			}

		}

	}

	public void startSensors() {
		NNLog.d(LOG_TAG, "Inside startSensors");
		int count = 0;
		for (Long key : hSensors.keySet()) {
			NNLog.d(LOG_TAG, "Inside startSensors Sensor ID = " + key);
			BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count++]);
			if (sensor != null ) {
				sensor.start();
			}
		}
		

		dataCollectionHandler.postDelayed(runnable, 1000);
	}

	public void stopSensors() {
		NNLog.d(LOG_TAG, "Inside stopSensors");
		int count = 0;
		for (Long key : hSensors.keySet()) {
			NNLog.d(LOG_TAG, "Inside stopSensors Sensor ID = " + key);
			BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count++]);
			if (sensor != null)
				sensor.stop(false);
		}

		dataCollectionHandler.removeCallbacks(runnable);
	}

	public synchronized UUID getUUID() {
		return uuid;
	}

	public synchronized void newUUID() {
		uuid = UUID.randomUUID();
		sqlHelper.storeVMConfig(state, uuid);
	}

	public void storeNervousnetState(byte state) {
		this.state = state;
		try {
			sqlHelper.storeVMConfig(state, uuid);
		} catch (Exception e) {
			NNLog.d(LOG_TAG, "Exception while calling storeVMConfig ");
			e.printStackTrace();
		}

	}

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

//		reInitSensor(id);

		BaseSensor sensor = hSensors.get(sensorConfig.getID());
		sensor.updateAndRestart(state);

	}
	
	
	public synchronized void updateAllSensorConfig(byte state) {
		NNLog.d(LOG_TAG, "UpdateSensorConfig called with state = " + state);
		int count = 0;
		for (Long key : hSensorConfig.keySet()) {
			SensorConfig sensorConfig = hSensorConfig.get(NervousnetVMConstants.sensor_ids[count++]);
			sensorConfig.setState(state);
			hSensorConfig.put(sensorConfig.getID(), sensorConfig);
		}


		try {
			sqlHelper.updateAllSensorConfig(hSensorConfig.values());
			
		} catch (Exception e) {
			NNLog.d(LOG_TAG, "Exception while calling updateSensorConfig ");
			e.printStackTrace();
		}

		stopSensors();
		initSensors();

		if(state > NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			startSensors();
		}
		
	

	}

	private void reInitSensor(long id) {
		NNLog.d(LOG_TAG, "reInitSensor sensor after settings changed ");
		SensorConfig sensorConfig = hSensorConfig.get(id);
		BaseSensor sensor = hSensors.get(sensorConfig.getID());
		sensor.stop(false);
		sensor.setSensorState(sensorConfig.getState());
		sensor.start();

	}

	public byte getState() {
		return state;
	}

	public synchronized SensorReading getLatestReading(long sensorID) {
		NNLog.d(LOG_TAG, "getLatestReading of ID = " + sensorID + " requested ");

		if (state == NervousnetVMConstants.STATE_PAUSED) {
			NNLog.d(LOG_TAG, "Error 001 : nervousnet is paused.");
			return new ErrorReading(new String[] { "001", "nervousnet is paused." });
		}
		return hSensors.get(sensorID).getReading();
	}

	public synchronized void getSensorReadings(int type, long startTime, long endTime, ArrayList<SensorReading> list) {
		sqlHelper.getSensorReadings(type, startTime, endTime, list);
	}

	public byte getSensorState(long id) {
		return hSensorConfig.get(id).getState();
	}

	
	
	private Handler dataCollectionHandler = new Handler();
	
	private Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
			   NNLog.d(LOG_TAG, "Collect data now. "+this.hashCode()+", "+dataCollectionHandler.hashCode());
			   
		      dataCollectionHandler.postDelayed(this, 1000);
		   }
		};

}
