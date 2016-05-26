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
import android.util.Log;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.sensors.AccelerometerSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BatterySensor;
import ch.ethz.coss.nervousnet.vm.sensors.ConnectivitySensor;
import ch.ethz.coss.nervousnet.vm.sensors.GyroSensor;
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
		Log.d(LOG_TAG, "Inside constructor");
		this.context = context;

		sqlHelper = new SQLHelper(context, DB_NAME);

		Config config = sqlHelper.loadVMConfig();

		if (config != null) {
			state = config.getState();
			uuid = UUID.fromString(config.getUUID());
			Log.d(LOG_TAG, "Config - UUID = " + uuid);
			Log.d(LOG_TAG, "Config - state = " + state);
		} else {
			Log.d(LOG_TAG, "Inside Constructure after loadVMConfig() no config found. Create a new config.");
			uuid = UUID.randomUUID();
			sqlHelper.storeVMConfig(state, uuid);
		}

		initSensors();

	}

	private void initSensors() {
		Log.d(LOG_TAG, "Inside initSensors");
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
				sensor = new AccelerometerSensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)
						? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			} else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[1]) { // Battery
				sensor = new BatterySensor(context, sensorConfig.getState());
			}
			// else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[2]) { //Beacons
			// sensor = new
			// BLEBeaconsSensor((manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
			// ||
			// manager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))?
			// sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// }
			else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[3]) { // Connectivity
				sensor = new ConnectivitySensor(context,
						(manager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)
								|| manager.hasSystemFeature(PackageManager.FEATURE_WIFI)) ? sensorConfig.getState()
										: NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			} else if (sensorConfig.getID() == NervousnetVMConstants.sensor_ids[4]) { // Gyroscope
				sensor = new GyroSensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE)
						? sensorConfig.getState() : NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			}
			// else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[5]) { //Humidity
			// sensor = new
			// HumiditySensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY)
			// ? sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// } else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[6]) { //Location
			// sensor = new
			// LocationSensor(manager.hasSystemFeature(PackageManager.FEATURE_LOCATION)
			// ? sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// } else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[7]) { //Light
			// sensor = new
			// LightSensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT)
			// ? sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// } else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[8]) { //Magnetic
			// sensor = new
			// MagneticSensor(manager.hasSystemFeature(PackageManager.) ?
			// sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// } else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[9]) { //Noise
			// sensor = new
			// NoiseSensor(manager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
			// ? sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// } else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[10]) { //Pressure
			// sensor = new
			// PressureSensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER)
			// ? sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// } else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[11]) { //Proximity
			// sensor = new
			// ProximitySensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_PROXIMITY)
			// ? sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// } else if(sensorConfig.getID() ==
			// NervousnetVMConstants.sensor_ids[12]) { //Temperature
			// sensor = new
			// TemperatureSensor(manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE)
			// ? sensorConfig.getState() :
			// NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE);
			// }
			if (sensor != null)
				hSensors.put(sensorConfig.getID(), sensor);

		}

	}

	public void startSensors() {
		Log.d(LOG_TAG, "Inside startSensors");
		int count = 0;
		for (Long key : hSensors.keySet()) {
			Log.d(LOG_TAG, "Inside startSensors Sensor ID = " + key);
			BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count++]);
			if (sensor != null)
				sensor.start(sensorManager);
		}

	}

	public void stopSensors() {

		int count = 0;
		for (Long key : hSensors.keySet()) {
			BaseSensor sensor = hSensors.get(NervousnetVMConstants.sensor_ids[count++]);
			if (sensor != null)
				sensor.stop(sensorManager);
		}
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
			Log.d(LOG_TAG, "Exception while calling storeVMConfig ");
			e.printStackTrace();
		}

	}

	public synchronized void updateSensorConfig(long id, byte state) {
		Log.d(LOG_TAG, "UpdateSensorConfig called with state = " + state);
		SensorConfig sensorConfig = hSensorConfig.get(id);

		sensorConfig.setState(state);
		try {
			sqlHelper.updateSensorConfig(sensorConfig);
			hSensorConfig.put(id, sensorConfig);
		} catch (Exception e) {
			Log.d(LOG_TAG, "Exception while calling updateSensorConfig ");
			e.printStackTrace();
		}

		reInitSensor(id);

		BaseSensor sensor = hSensors.get(sensorConfig.getID());
		sensor.updateAndRestart(sensorManager, state);

	}

	private void reInitSensor(long id) {
		Log.d(LOG_TAG, "reInitSensor sensor after settings changed ");
		SensorConfig sensorConfig = hSensorConfig.get(id);
		BaseSensor sensor = hSensors.get(sensorConfig.getID());
		sensor.stop(sensorManager);
		sensor.setSensorState(sensorConfig.getState());
		sensor.start(sensorManager);

	}

	public byte getState() {
		return state;
	}

	public synchronized SensorReading getLatestReading(long sensorID) {
		Log.d(LOG_TAG, "getLatestReading of ID = " + sensorID + " requested ");

		if (state == NervousnetVMConstants.STATE_PAUSED) {
			Log.d(LOG_TAG, "Error 001 : nervousnet is paused.");
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

}
