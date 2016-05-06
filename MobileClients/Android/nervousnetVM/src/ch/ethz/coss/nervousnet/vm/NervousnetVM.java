package ch.ethz.coss.nervousnet.vm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
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
import ch.ethz.coss.nervousnet.vm.storage.SensorConfigDao;
import ch.ethz.coss.nervousnet.vm.sensors.AccelerometerSensor;
import ch.ethz.coss.nervousnet.vm.sensors.BatterySensor;
import ch.ethz.coss.nervousnet.vm.sensors.ConnectivitySensor;
import ch.ethz.coss.nervousnet.vm.sensors.LocationSensor;
import ch.ethz.coss.nervousnet.vm.sensors.NoiseSensor;
import ch.ethz.coss.nervousnet.vm.storage.AccelData;
import ch.ethz.coss.nervousnet.vm.storage.AccelDataDao;
import ch.ethz.coss.nervousnet.vm.storage.BatteryData;
import ch.ethz.coss.nervousnet.vm.storage.BatteryDataDao;
import ch.ethz.coss.nervousnet.vm.storage.Config;
import ch.ethz.coss.nervousnet.vm.storage.ConfigDao;
import ch.ethz.coss.nervousnet.vm.storage.ConnectivityData;
import ch.ethz.coss.nervousnet.vm.storage.ConnectivityDataDao;
import ch.ethz.coss.nervousnet.vm.storage.DaoMaster;
import ch.ethz.coss.nervousnet.vm.storage.DaoSession;
import ch.ethz.coss.nervousnet.vm.storage.GyroData;
import ch.ethz.coss.nervousnet.vm.storage.GyroDataDao;
import ch.ethz.coss.nervousnet.vm.storage.LightData;
import ch.ethz.coss.nervousnet.vm.storage.LightDataDao;
import ch.ethz.coss.nervousnet.vm.storage.LocationData;
import ch.ethz.coss.nervousnet.vm.storage.LocationDataDao;
import ch.ethz.coss.nervousnet.vm.storage.NoiseData;
import ch.ethz.coss.nervousnet.vm.storage.NoiseDataDao;
import ch.ethz.coss.nervousnet.vm.storage.PressureData;
import ch.ethz.coss.nervousnet.vm.storage.PressureDataDao;
import ch.ethz.coss.nervousnet.vm.storage.SensorConfig;
import ch.ethz.coss.nervousnet.vm.storage.SensorDataImpl;
import de.greenrobot.dao.query.QueryBuilder;
import ch.ethz.coss.nervousnet.vm.storage.DaoMaster.DevOpenHelper;


public class NervousnetVM {


	private static final String LOG_TAG = NervousnetVM.class.getSimpleName();
	private static final String DB_NAME = "NN-DB";
	
	private SensorManager sensorManager = null;
	
	private Lock storeMutex;

	private AccelerometerSensor sensorAccelerometer = null;
	private BatterySensor sensorBattery = null;
	private ConnectivitySensor sensorConnectivity = null;
	private NoiseSensor sensorNoise = null;
	private LocationSensor sensorLocation = null;
	
	private byte state = NervousnetConstants.STATE_PAUSED;
	private UUID uuid;
	private Context context;
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

	public NervousnetVM(Context context) {
		initDao();
		initSensors(context);
		
		
	}

	private void initSensors(Context context) {
		storeMutex = new ReentrantLock();
				// Initialize sensor manager
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		LocationManager locManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		PackageManager manager = context.getPackageManager();
		
	
		// Get references to android default sensors
		sensorAccelerometer = new AccelerometerSensor(1, sensorManager,  manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER));
		
//		sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
//		sensorMagnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
//		sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
//		sensorGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//		sensorTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
//		sensorHumidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
//		sensorPressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//	
		
		// Custom sensors
//		sensorBattery = BatterySensor.getInstance(getApplicationContext());
//		sensorConnectivity = ConnectivitySensor.getInstance(getApplicationContext());
//		sensorNoise = NoiseSensor.getInstance();
//		sensorLocation = LocationSensor.getInstance(getApplicationContext());

//		// Schedule all sensors (initially)
//		scheduleSensor(LibConstants.SENSOR_LOCATION);
//		scheduleSensor(LibConstants.SENSOR_ACCELEROMETER);
//		scheduleSensor(LibConstants.SENSOR_BATTERY);
//		scheduleSensor(LibConstants.SENSOR_LIGHT);
//		// scheduleSensor(LibConstants.SENSOR_MAGNETIC);
//		// scheduleSensor(LibConstants.SENSOR_PROXIMITY);
//		scheduleSensor(LibConstants.SENSOR_GYROSCOPE);
//		// scheduleSensor(LibConstants.SENSOR_TEMPERATURE);
//		scheduleSensor(LibConstants.SENSOR_HUMIDITY);
//		scheduleSensor(LibConstants.SENSOR_PRESSURE);
//		scheduleSensor(LibConstants.SENSOR_NOISE);
//		// scheduleSensor(LibConstants.SENSOR_BLEBEACON);
//		scheduleSensor(LibConstants.SENSOR_CONNECTIVITY);
	}

	private void initDao(){
		Log.d(LOG_TAG, "Inside constructor");
		 try {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
			sqlDB = helper.getWritableDatabase();
		} catch (Exception e) {
			Log.e(LOG_TAG, "Inside constructor and creating DB = "+DB_NAME, e);
		}
		 daoMaster = new DaoMaster(sqlDB);
		 daoSession = daoMaster.newSession();
		 configDao = daoSession.getConfigDao();
		 accDao = daoSession.getAccelDataDao();
		 locDao = daoSession.getLocationDataDao();
		 connDao = daoSession.getConnectivityDataDao();
		 gyroDao = daoSession.getGyroDataDao();
		 lightDao = daoSession.getLightDataDao();
		 noiseDao = daoSession.getNoiseDataDao();
		 pressureDao = daoSession.getPressureDataDao();
		 
		 
		populateSensorConfig();
	    boolean hasVMConfig = loadVMConfig();
		if (!hasVMConfig) {
			Log.d(LOG_TAG, "Inside Constructure after loadVMConfig() no config found. Create a new config.");
			uuid = UUID.randomUUID();
			 
			storeVMConfig();
		}
	}
	
	public synchronized UUID getUUID() {
		return uuid;
	}

	public synchronized void newUUID() {
		uuid = UUID.randomUUID();
		storeVMConfig();
	}
	
	private synchronized void populateSensorConfig() {
		Log.d(LOG_TAG, "Inside populateSensorConfig");
		boolean success = true;
		SensorConfig sensorconfig = null;
		
		Log.d(LOG_TAG, "sensorConfigDao - count = "+sensorConfigDao.queryBuilder().count());
		if(sensorConfigDao.queryBuilder().count() == 0){
			
			for(int i = 0; i < NervousnetConstants.sensor_ids.length; i++)
			sensorConfigDao.insert(new SensorConfig(NervousnetConstants.sensor_ids[i], NervousnetConstants.sensor_labels[i], false));
		}
	}

	private synchronized boolean loadVMConfig() {
		Log.d(LOG_TAG, "Inside loadVMConfig");
		boolean success = true;
		Config config = null;
		
		Log.d(LOG_TAG, "Config - count = "+configDao.queryBuilder().count());
		if(configDao.queryBuilder().count() != 0){
			config = configDao.queryBuilder().unique();
			state = config.getState();
			uuid = UUID.fromString(config.getUUID());
			Log.d(LOG_TAG, "Config - UUID = "+uuid);
			Log.d(LOG_TAG, "Config - state = "+state);
		}else 
			success = false;
		
		return success;
	}

	private synchronized void storeVMConfig() {
		Log.d(LOG_TAG, "Inside storeVMConfig");
		Config config = null;
		
		if(configDao.queryBuilder().count() == 0){

			Log.d(LOG_TAG, "Config DB Is empty.");
			config = new Config(state, uuid.toString(), Build.MANUFACTURER, Build.MODEL, "Android", Build.VERSION.RELEASE, System.currentTimeMillis()); 
			configDao.insert(config);
		} else if(configDao.queryBuilder().count() == 1){ 
			Log.d(LOG_TAG, "Config DB exists.");
			config = configDao.queryBuilder().unique();
			configDao.deleteAll();
			config.setState(state);
		    configDao.insert(config);
			config = configDao.queryBuilder().unique();
			Log.d(LOG_TAG, "state = "+config.getState());
		} else
			Log.e(LOG_TAG, "Config DB count is more than 1. There is something wrong.");
		
	}
	
	public synchronized void storeNervousnetState(byte state){
		this.state = state;
		
		try {
			storeVMConfig();
		} catch (Exception e) {
			Log.d(LOG_TAG, "Exception while calling storeVMConfig ");
			e.printStackTrace();
		}
		
	}
	
	public byte getState(){
		return state;
	}
	

	private synchronized SensorReading convertSensorDataToSensorReading(SensorDataImpl data) {
		Log.d(LOG_TAG, "convertSensorDataToSensorReading reading Type = " + data.getType());
		SensorReading reading = null;

		switch (data.getType()) {
		case LibConstants.SENSOR_ACCELEROMETER:
			AccelData adata = (AccelData) data;
			reading = new AccelerometerReading(adata.getTimeStamp(), new float[] {adata.getX(), adata.getY(), adata.getZ()});
			reading.type = LibConstants.SENSOR_ACCELEROMETER;
			
			return reading;
			
		case LibConstants.SENSOR_BATTERY:
			
//			BatteryData bdata = (BatteryData) data;
//			reading = new BatteryReading(bdata.getTimeStamp(), bdata.getPercent(), false, false, false, state, state, state, null});
//			reading.type = LibConstants.SENSOR_BATTERY;
			
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
	
	public synchronized void getSensorReadings(int type, long startTime, long endTime, ArrayList list) {
		QueryBuilder qb = null;
		
		switch(type) {
		case LibConstants.SENSOR_ACCELEROMETER:
			 qb = accDao.queryBuilder();
			 qb.where(AccelDataDao.Properties.TimeStamp.between(startTime, endTime));
			 ArrayList aList = (ArrayList) qb.list();
			 Iterator<SensorDataImpl> iterator = aList.iterator();
				while (iterator.hasNext()) {
					 list.add(convertSensorDataToSensorReading(iterator.next()));
				}
			  
			
			 Log.d(LOG_TAG, "List size = "+list.size());
			 
			 return;
		case LibConstants.SENSOR_BATTERY:
			 qb = battDao.queryBuilder();
			 qb.where(BatteryDataDao.Properties.TimeStamp.between(startTime, endTime));

			 return;
		case LibConstants.SENSOR_DEVICE:
			
			//TODO
			return;	
		case LibConstants.SENSOR_LOCATION:
			qb = locDao.queryBuilder();
			 qb.where(LocationDataDao.Properties.TimeStamp.between(startTime, endTime));

			return ;		
		case LibConstants.SENSOR_BLEBEACON:
			//TODO
			return ;
		case LibConstants.SENSOR_CONNECTIVITY:
			qb = connDao.queryBuilder();
			 qb.where(ConnectivityDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_GYROSCOPE:
			qb = gyroDao.queryBuilder();
			 qb.where(GyroDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_HUMIDITY:
			//TODO
			return;
		case LibConstants.SENSOR_LIGHT:
			qb = lightDao.queryBuilder();
			qb.where(LightDataDao.Properties.TimeStamp.between(startTime, endTime));

			return;
		case LibConstants.SENSOR_MAGNETIC:
			//TODO
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
				//TODO
			return;
		case LibConstants.SENSOR_TEMPERATURE:
				//TODO
			return;
			
		}
		
	}

	public synchronized boolean storeSensor(SensorDataImpl sensorData) {
		Log.d(LOG_TAG, "Inside storeSensor ");
		
		if(sensorData == null) {
			Log.e(LOG_TAG, "SensorData is null. please check it");
			return false;
		}else {
			Log.d(LOG_TAG, "SensorData Type = (Type = "+sensorData.getType()+")"); //, Timestamp = "+sensorData.getTimeStamp()+", Volatility = "+sensorData.getVolatility());
		}
		
		
		switch(sensorData.getType()) {
		case LibConstants.SENSOR_ACCELEROMETER:
			
			AccelData accelData = (AccelData) sensorData;
			Log.d(LOG_TAG, "ACCEL_DATA table count = "+accDao.count());
			Log.d(LOG_TAG, "Inside Switch, AccelData Type = (Type = "+accelData.getType()+", Timestamp = "+accelData.getTimeStamp()+", Volatility = "+accelData.getVolatility());
			Log.d(LOG_TAG, "Inside Switch, AccelData Type = (X = "+accelData.getX()+", Y = "+accelData.getY()+", Z = "+accelData.getZ());
			
			accDao.insert(accelData);
			return true;
			
		case LibConstants.SENSOR_BATTERY:
			BatteryData battData = (BatteryData) sensorData;
			Log.d(LOG_TAG, "BATTERY_DATA table count = "+battDao.count());
			Log.d(LOG_TAG, "Inside Switch, BatteryData Type = (Type = "+battData.getType()+", Timestamp = "+battData.getTimeStamp()+", Volatility = "+battData.getVolatility());
			Log.d(LOG_TAG, "Inside Switch, BatteryData Type = (Percent = "+battData.getPercent()+"%, Health = "+battData.getHealth());
			battDao.insert(battData);
			return true;
			
		case LibConstants.SENSOR_DEVICE:
			return true;
			
		case LibConstants.SENSOR_LOCATION:
			LocationData locData = (LocationData) sensorData;
			Log.d(LOG_TAG, "LOCATION_DATA table count = "+locDao.count());
			Log.d(LOG_TAG, "Inside Switch, LocationData Type = (Type = "+locData.getType()+", Timestamp = "+locData.getTimeStamp()+", Volatility = "+locData.getVolatility());
			Log.d(LOG_TAG, "Inside Switch, LocationData Type = (Latitude = "+locData.getLatitude()+", Longitude = "+locData.getLongitude()+", ALtitude = "+locData.getAltitude());
			
			locDao.insert(locData);
			return true;
			
		case LibConstants.SENSOR_BLEBEACON:
			return true;
			
		case LibConstants.SENSOR_CONNECTIVITY:
			ConnectivityData connData = (ConnectivityData) sensorData;
			Log.d(LOG_TAG, "Connectivity_DATA table count = "+connDao.count());
			Log.d(LOG_TAG, "Inside Switch, ConnectivityData Type = (Type = "+connData.getType()+", Timestamp = "+connData.getTimeStamp()+", Volatility = "+connData.getVolatility());
			
			connDao.insert(connData);
			return true;
		case LibConstants.SENSOR_GYROSCOPE:
			GyroData gyroData = (GyroData) sensorData;
			Log.d(LOG_TAG, "GYRO_DATA table count = "+gyroDao.count());
			Log.d(LOG_TAG, "Inside Switch, GyroData Type = (Type = "+gyroData.getType()+", Timestamp = "+gyroData.getTimeStamp()+", Volatility = "+gyroData.getVolatility());
			gyroDao.insert(gyroData);
			return true;
		case LibConstants.SENSOR_HUMIDITY:
			return true;
		case LibConstants.SENSOR_LIGHT:
			LightData lightData = (LightData) sensorData;
			Log.d(LOG_TAG, "LIGHT_DATA table count = "+lightDao.count());
			Log.d(LOG_TAG, "Inside Switch, LightData Type = (Type = "+lightData.getType()+", Timestamp = "+lightData.getTimeStamp()+", Volatility = "+lightData.getVolatility());
			lightDao.insert(lightData);
			return true;
			
		case LibConstants.SENSOR_MAGNETIC:
			return true;
		case LibConstants.SENSOR_NOISE:
			NoiseData noiseData = (NoiseData) sensorData;
			Log.d(LOG_TAG, "NoiseData table count = "+noiseDao.count());
			Log.d(LOG_TAG, "Inside Switch, noiseData Type = (Type = "+noiseData.getType()+", Timestamp = "+noiseData.getTimeStamp()+", Volatility = "+noiseData.getVolatility());
			noiseDao.insert(noiseData);
			return true;
		case LibConstants.SENSOR_PRESSURE:
			PressureData pressureData = (PressureData) sensorData;
			Log.d(LOG_TAG, "PressureData table count = "+pressureDao.count());
			Log.d(LOG_TAG, "Inside Switch, pressureData Type = (Type = "+pressureData.getType()+", Timestamp = "+pressureData.getTimeStamp()+", Volatility = "+pressureData.getVolatility());
			pressureDao.insert(pressureData);
			return true;
		case LibConstants.SENSOR_PROXIMITY:
			return true;
		case LibConstants.SENSOR_TEMPERATURE:
			return true;
			
		}
		return false;
	}
	
	
	
	public void storeSensorAsync(SensorDataImpl sensorData){
	
		new StoreTask().execute(sensorData);
	}
	
	class StoreTask extends AsyncTask<SensorDataImpl, Void, Void> {

		public StoreTask() {
		}

		@Override
		protected Void doInBackground(SensorDataImpl... params) {

			if (params != null && params.length > 0) {
				
				for (int i = 0; i < params.length; i++) {
					storeSensor(params[i]);
				}
			}
			return null;
		}

	}

	
	
//	public synchronized boolean storeSensor(long sensorID, SensorReading sensorReading) {
//		if (sensorData != null) {
//			boolean stmHasChanged = false;
//			boolean success = true;
//			SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
//
//			TreeMap<PageInterval, PageInterval> treeMap = sensorTreeMap.get(sensorID);
//			if (treeMap == null) {
//				treeMap = new TreeMap<PageInterval, PageInterval>();
//				// Open the initial interval
//				PageInterval piFirst = new PageInterval(new Interval(0, Long.MAX_VALUE), 0);
//				treeMap.put(piFirst, piFirst);
//				sensorTreeMap.put(sensorID, treeMap);
//				stmHasChanged = true;
//			}
//
//			// Reject non monotonically increasing timestamps
//			if (ssc.getLastWrittenTimestamp() - sensorData.getRecordTime() >= 0) {
//				return false;
//			}
//
//			// Add new page if the last one is full
//			if (ssc.getEntryNumber() == MAX_ENTRIES) {
//				ssc.setCurrentPage(ssc.getCurrentPage() + 1);
//				ssc.setEntryNumber(0);
//
//				// Close the last interval
//				PageInterval piLast = treeMap.get(new PageInterval(new Interval(0, 0), ssc.getCurrentPage() - 1));
//				treeMap.remove(piLast);
//				piLast.getInterval().setUpper(ssc.getLastWrittenTimestamp());
//				treeMap.put(piLast, piLast);
//				// Open the next interval
//				PageInterval piNext = new PageInterval(new Interval(ssc.getLastWrittenTimestamp() + 1, Long.MAX_VALUE), ssc.getCurrentPage());
//				treeMap.put(piNext, piNext);
//
//				// Remove old pages
//				removeOldPages(sensorID, ssc.getCurrentPage(), MAX_PAGES);
//				stmHasChanged = true;
//			}
//
//			SensorStorePage ssp = new SensorStorePage(dir, ssc.getSensorID(), ssc.getCurrentPage());
//			ssp.store(sensorData, ssc.getEntryNumber());
//
//			ssc.setEntryNumber(ssc.getEntryNumber() + 1);
//
//			ssc.setLastWrittenTimestamp(sensorData.getRecordTime());
//			ssc.store();
//			
//			if (stmHasChanged) {
//				writeSTM();
//			}
//			return success;
//		}
//		return false;
//	}

//	public long getLastUploadedTimestamp(long sensorID) {
//		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
//		return ssc.getLastUploadedTimestamp();
//	}
//
//	public void setLastUploadedTimestamp(long sensorID, long timestamp) {
//		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
//		ssc.setLastUploadedTimestamp(timestamp);
//		ssc.store();
//	}
//
//	public void deleteSensor(long sensorID) {
//		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
//		removeOldPages(sensorID, ssc.getCurrentPage(), 0);
//		ssc.delete();
//	}
//
//	public long[] getSensorStorageSize(long sensorID) {
//		long[] size = { 0, 0 };
//		SensorStoreConfig ssc = new SensorStoreConfig(dir, sensorID);
//		for (int i = 0; i < MAX_PAGES; i++) {
//			SensorStorePage ssp = new SensorStorePage(dir, sensorID, ssc.getCurrentPage() - i);
//			size[0] += ssp.getStoreSize();
//			size[1] += ssp.getIndexSize();
//		}
//		return size;
//	}
}




