package ch.ethz.coss.nervousnet.vm.storage;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.ConnectivityReading;
import ch.ethz.coss.nervousnet.lib.GyroReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.LocationReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVM;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.sensors.BaseSensor.BaseSensorListener;
import ch.ethz.coss.nervousnet.vm.storage.DaoMaster.DevOpenHelper;
import de.greenrobot.dao.query.QueryBuilder;

public class SQLHelper implements BaseSensorListener{
	
	private static final String LOG_TAG = SQLHelper.class.getSimpleName();
	
	Config config = null;
	
	DaoMaster daoMaster;
	DaoSession daoSession;
	SQLiteDatabase sqlDB;
	ConfigDao configDao;
	SensorConfigDao sensorConfigDao;
	AccelDataDao accDao;
	BatteryDataDao battDao;
	LightDataDao lightDao;
	NoiseDataDao noiseDao;
	LocationDataDao locDao;
	ConnectivityDataDao connDao;
	GyroDataDao gyroDao;
	PressureDataDao pressureDao;
	
	public SQLHelper(Context context, String DB_NAME) {
		initDao( context, DB_NAME);
	}
	
	private void initDao(Context context, String DB_NAME) {
		NNLog.d(LOG_TAG, "Inside initDao");
		try {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			sqlDB = helper.getWritableDatabase();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Inside constructor and creating DB = " + DB_NAME, e);
		}
		
		
		daoMaster = new DaoMaster(sqlDB);
		daoSession = daoMaster.newSession();
		configDao = daoSession.getConfigDao();
		sensorConfigDao = daoSession.getSensorConfigDao();
		accDao = daoSession.getAccelDataDao();
		locDao = daoSession.getLocationDataDao();
		connDao = daoSession.getConnectivityDataDao();
		gyroDao = daoSession.getGyroDataDao();
		lightDao = daoSession.getLightDataDao();
		noiseDao = daoSession.getNoiseDataDao();
		pressureDao = daoSession.getPressureDataDao();

		populateSensorConfig();
	}

	public synchronized void populateSensorConfig() {
		NNLog.d(LOG_TAG, "Inside populateSensorConfig");
		boolean success = true;
		SensorConfig sensorconfig = null;

		NNLog.d(LOG_TAG, "sensorConfigDao - count = " + sensorConfigDao.queryBuilder().count());
		if (sensorConfigDao.queryBuilder().count() == 0) {

			for (int i = 0; i < NervousnetVMConstants.sensor_ids.length; i++)
				sensorConfigDao.insert(new SensorConfig(NervousnetVMConstants.sensor_ids[i],
						NervousnetVMConstants.sensor_labels[i], (byte) 0));
		}
	}
	
	
	public synchronized Config loadVMConfig() {
		NNLog.d(LOG_TAG, "Inside loadVMConfig");

		NNLog.d(LOG_TAG, "Config - count = " + configDao.queryBuilder().count());
		if (configDao.queryBuilder().count() != 0) {
			config = configDao.queryBuilder().unique();
		} 

		return config;
	}

	public synchronized void storeVMConfig(byte state, UUID uuid) {
		NNLog.d(LOG_TAG, "Inside storeVMConfig");
		Config config = null;

		if (configDao.queryBuilder().count() == 0) {
			NNLog.d(LOG_TAG, "Config DB Is empty.");
			config = new Config(state, uuid.toString(), Build.MANUFACTURER, Build.MODEL, "Android",
					Build.VERSION.RELEASE, System.currentTimeMillis());
			configDao.insert(config);
		} else if (configDao.queryBuilder().count() == 1) {
			NNLog.d(LOG_TAG, "Config DB exists.");
			config = configDao.queryBuilder().unique();
			configDao.deleteAll();
			config.setState(state);
			configDao.insert(config);
			config = configDao.queryBuilder().unique();
			NNLog.d(LOG_TAG, "state = " + config.getState());
		} else
			Log.e(LOG_TAG, "Config DB count is more than 1. There is something wrong.");

	}
	
	public synchronized void updateSensorConfig(SensorConfig config) throws Exception {

		sensorConfigDao.insertOrReplace(config);
	}
	
	public synchronized List<SensorConfig> getSensorConfigList() {
		return sensorConfigDao.queryBuilder().list();
	}
	
	public void storeSensorAsync(SensorDataImpl sensorData) {

//		new StoreTask().execute(sensorData);
	}

	class StoreTask extends AsyncTask<SensorDataImpl, Void, Void> {

		public StoreTask() {
		}

		@Override
		protected Void doInBackground(SensorDataImpl... params) {

			if (params != null && params.length > 0) {

//				for (int i = 0; i < params.length; i++) {
//					Log.d(LOG_TAG, "doInBackground (params[i] = " + params[i] + ")");
					storeSensor(params[0]);
//				}
			}
			return null;
		}

	}
	
	public synchronized boolean storeSensor(SensorDataImpl sensorData) {
		NNLog.d(LOG_TAG, "Inside storeSensor ");

		if (sensorData == null) {
			Log.e(LOG_TAG, "SensorData is null. please check it");
			return false;
		} 
			NNLog.d(LOG_TAG, "SensorData (Type = " + sensorData.getType() + ")"); // ,
																						// Timestamp
																						// =
																						// "+sensorData.getTimeStamp()+",
																						// Volatility
																						// =
																						// "+sensorData.getVolatility());
		
		switch (sensorData.getType()) {
		case LibConstants.SENSOR_ACCELEROMETER:

			AccelData accelData = (AccelData) sensorData;
			NNLog.d(LOG_TAG, "ACCEL_DATA table count = " + accDao.count());
			NNLog.d(LOG_TAG, "Inside Switch, AccelData Type = (Type = " + accelData.getType() + ", Timestamp = "
					+ accelData.getTimeStamp() + ", Volatility = " + accelData.getVolatility());
			NNLog.d(LOG_TAG, "Inside Switch, AccelData Type = (X = " + accelData.getX() + ", Y = " + accelData.getY()
					+ ", Z = " + accelData.getZ());

			accDao.insert(accelData);
			
			return true;

		case LibConstants.SENSOR_BATTERY:
//			BatteryData battData = (BatteryData) sensorData;
//			NNLog.d(LOG_TAG, "BATTERY_DATA table count = " + battDao.count());
//			NNLog.d(LOG_TAG, "Inside Switch, BatteryData Type = (Type = " + battData.getType() + ", Timestamp = "
//					+ battData.getTimeStamp() + ", Volatility = " + battData.getVolatility());
//			NNLog.d(LOG_TAG, "Inside Switch, BatteryData Type = (Percent = " + battData.getPercent() + "%, Health = "
//					+ battData.getHealth());
//			battDao.insert(battData);
			return true;

		case LibConstants.DEVICE_INFO:
			return true;

		case LibConstants.SENSOR_LOCATION:
			LocationData locData = (LocationData) sensorData;
			NNLog.d(LOG_TAG, "LOCATION_DATA table count = " + locDao.count());
			NNLog.d(LOG_TAG, "Inside Switch, LocationData Type = (Type = " + locData.getType() + ", Timestamp = "
					+ locData.getTimeStamp() + ", Volatility = " + locData.getVolatility());
			NNLog.d(LOG_TAG, "Inside Switch, LocationData Type = (Latitude = " + locData.getLatitude() + ", Longitude = "
					+ locData.getLongitude() + ", ALtitude = " + locData.getAltitude());

			locDao.insert(locData);
			return true;

		case LibConstants.SENSOR_BLEBEACON:
			return true;

		case LibConstants.SENSOR_CONNECTIVITY:
			ConnectivityData connData = (ConnectivityData) sensorData;
			NNLog.d(LOG_TAG, "Connectivity_DATA table count = " + connDao.count());
			NNLog.d(LOG_TAG, "Inside Switch, ConnectivityData Type = (Type = " + connData.getType() + ", Timestamp = "
					+ connData.getTimeStamp() + ", Volatility = " + connData.getVolatility());

			connDao.insert(connData);
			return true;
		case LibConstants.SENSOR_GYROSCOPE:
			GyroData gyroData = (GyroData) sensorData;
			NNLog.d(LOG_TAG, "GYRO_DATA table count = " + gyroDao.count());
			NNLog.d(LOG_TAG, "Inside Switch, GyroData Type = (Type = " + gyroData.getType() + ", Timestamp = "
					+ gyroData.getTimeStamp() + ", Volatility = " + gyroData.getVolatility());
			gyroDao.insert(gyroData);
			return true;
		case LibConstants.SENSOR_HUMIDITY:
			return true;
		case LibConstants.SENSOR_LIGHT:
			LightData lightData = (LightData) sensorData;
			NNLog.d(LOG_TAG, "LIGHT_DATA table count = " + lightDao.count());
			NNLog.d(LOG_TAG, "Inside Switch, LightData Type = (Type = " + lightData.getType() + ", Timestamp = "
					+ lightData.getTimeStamp() + ", Volatility = " + lightData.getVolatility());
			lightDao.insert(lightData);
			return true;

		case LibConstants.SENSOR_MAGNETIC:
			return true;
		case LibConstants.SENSOR_NOISE:
			NoiseData noiseData = (NoiseData) sensorData;
			NNLog.d(LOG_TAG, "NoiseData table count = " + noiseDao.count());
			NNLog.d(LOG_TAG, "Inside Switch, noiseData Type = (Type = " + noiseData.getType() + ", Timestamp = "
					+ noiseData.getTimeStamp() + ", Volatility = " + noiseData.getVolatility());
			noiseDao.insert(noiseData);
			return true;
		case LibConstants.SENSOR_PRESSURE:
			PressureData pressureData = (PressureData) sensorData;
			NNLog.d(LOG_TAG, "PressureData table count = " + pressureDao.count());
			NNLog.d(LOG_TAG, "Inside Switch, pressureData Type = (Type = " + pressureData.getType() + ", Timestamp = "
					+ pressureData.getTimeStamp() + ", Volatility = " + pressureData.getVolatility());
			pressureDao.insert(pressureData);
			return true;
		case LibConstants.SENSOR_PROXIMITY:
			return true;
		case LibConstants.SENSOR_TEMPERATURE:
			return true;

		}
		return false;
	}
	
	
	public synchronized void getSensorReadings(int type, long startTime, long endTime, ArrayList<SensorReading> list) {
		QueryBuilder qb = null;

		switch (type) {
		case LibConstants.SENSOR_ACCELEROMETER:
			qb = accDao.queryBuilder();
			qb.where(AccelDataDao.Properties.TimeStamp.between(startTime, endTime));
			@SuppressWarnings("unchecked")
			ArrayList<SensorDataImpl> aList = (ArrayList<SensorDataImpl>) qb.list();
			Iterator<SensorDataImpl> iterator = aList.iterator();
			while (iterator.hasNext()) {
				list.add(convertSensorDataToSensorReading(iterator.next()));
			}
			
			NNLog.d(LOG_TAG, "List size = " + list.size());

			return;
		case LibConstants.SENSOR_BATTERY:
			qb = battDao.queryBuilder();
			qb.where(BatteryDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.DEVICE_INFO:

			// TODO
			return;
		case LibConstants.SENSOR_LOCATION:
			qb = locDao.queryBuilder();
			qb.where(LocationDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_BLEBEACON:
			// TODO
			return;
		case LibConstants.SENSOR_CONNECTIVITY:
			qb = connDao.queryBuilder();
			qb.where(ConnectivityDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_GYROSCOPE:
			qb = gyroDao.queryBuilder();
			qb.where(GyroDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_HUMIDITY:
			// TODO
			return;
		case LibConstants.SENSOR_LIGHT:
			qb = lightDao.queryBuilder();
			qb.where(LightDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_MAGNETIC:
			// TODO
			return;
		case LibConstants.SENSOR_NOISE:
			qb = noiseDao.queryBuilder();
			qb.where(NoiseDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_PRESSURE:
			qb = pressureDao.queryBuilder();
			qb.where(PressureDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_PROXIMITY:
			// TODO
			return;
		case LibConstants.SENSOR_TEMPERATURE:
			// TODO
			return;

		}

	}
	
	private synchronized SensorReading convertSensorDataToSensorReading(SensorDataImpl data) {
		NNLog.d(LOG_TAG, "convertSensorDataToSensorReading reading Type = " + data.getType());
		SensorReading reading = null;

		switch (data.getType()) {
		case LibConstants.SENSOR_ACCELEROMETER:
			AccelData adata = (AccelData) data;
			reading = new AccelerometerReading(adata.getTimeStamp(),
					new float[] { adata.getX(), adata.getY(), adata.getZ() });
			reading.type = LibConstants.SENSOR_ACCELEROMETER;

			return reading;

		case LibConstants.SENSOR_BATTERY:

			// BatteryData bdata = (BatteryData) data;
			// reading = new BatteryReading(bdata.getTimeStamp(),
			// bdata.getPercent(), false, false, false, state, state, state,
			// null});
			// reading.type = LibConstants.SENSOR_BATTERY;

			return reading;

		case LibConstants.SENSOR_GYROSCOPE:

			return reading;

		case LibConstants.SENSOR_CONNECTIVITY:

			return reading;

		case LibConstants.SENSOR_LIGHT:
			return reading;

		case LibConstants.SENSOR_LOCATION:

			return reading;

		default:
			return null;

		}
	}
	

	@Override
	public void sensorDataReady(SensorReading reading) {
			storeSensorAsync(convertSensorReadingToSensorData(reading));
	}
	
	
	private synchronized SensorDataImpl convertSensorReadingToSensorData(SensorReading reading) {
		NNLog.d(LOG_TAG, "convertSensorReadingToSensorData reading Type = " + reading.type);
		SensorDataImpl sensorData;

		switch (reading.type) {
		case LibConstants.SENSOR_ACCELEROMETER:
			AccelerometerReading areading = (AccelerometerReading) reading;
			sensorData = new AccelData(null, reading.timestamp, areading.getX(), areading.getY(), areading.getZ(), 0l,
					true);
			sensorData.setType(LibConstants.SENSOR_ACCELEROMETER);
			return sensorData;
			
		case LibConstants.SENSOR_BATTERY:
			BatteryReading breading = (BatteryReading) reading;
			sensorData = new BatteryData(null, breading.timestamp, breading.getPercent(), breading.getCharging_type(), breading.getHealth(), breading.getTemp(), breading.getVolt(), breading.volatility, breading.isShare);
			sensorData.setType(LibConstants.SENSOR_BATTERY);
			return sensorData;
			

		case LibConstants.SENSOR_GYROSCOPE:
			GyroReading greading = (GyroReading) reading;
			sensorData = new GyroData(null, reading.timestamp, greading.getGyroX(), greading.getGyroY(),
					greading.getGyroZ(), 0l, true);
			sensorData.setType(LibConstants.SENSOR_GYROSCOPE);
			return sensorData;

		case LibConstants.SENSOR_CONNECTIVITY:
			ConnectivityReading connReading = (ConnectivityReading) reading;
			sensorData = new ConnectivityData(null, connReading.timestamp, connReading.isConnected(),
					connReading.getNetworkType(), connReading.isRoaming(), connReading.getWifiHashId(),
					connReading.getWifiStrength(), connReading.getMobileHashId(), connReading.volatility,
					connReading.isShare);
			sensorData.setType(LibConstants.SENSOR_CONNECTIVITY);
			return sensorData;

		case LibConstants.SENSOR_LIGHT:
			LightReading lreading = (LightReading) reading;
			sensorData = new LightData(null, lreading.timestamp, lreading.getLuxValue(), lreading.volatility,
					lreading.isShare);
			sensorData.setType(LibConstants.SENSOR_LIGHT);
			return sensorData;

		case LibConstants.SENSOR_LOCATION:
			LocationReading locReading = (LocationReading) reading;
			sensorData = new LocationData(null, reading.timestamp, locReading.getLatnLong()[0],
					locReading.getLatnLong()[1], 0.0, 0l, true);
			sensorData.setType(LibConstants.SENSOR_LOCATION);
			return sensorData;

		default:
			return null;

		}
	}


}
