package ch.ethz.coss.nervousnet.vm.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.hardware.SensorManager;
import android.util.Log;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public abstract class BaseSensor {
	private static final String LOG_TAG = BaseSensor.class.getSimpleName();
	
	protected SensorReading reading;
	protected byte sensorState; 
	
	protected List<BaseSensorListener> listenerList = new ArrayList<BaseSensorListener>();
	protected Lock listenerMutex = new ReentrantLock();
	
	public interface BaseSensorListener {
		public void sensorDataReady(SensorReading reading);
	}


	public void addListener(BaseSensorListener listener) {
		listenerMutex.lock();
		listenerList.add(listener);
		listenerMutex.unlock();
	}

	public void removeListener(BaseSensorListener listener) {
		listenerMutex.lock();
		listenerList.remove(listener);
		listenerMutex.unlock();
	}

	public void clearListeners() {
		listenerMutex.lock();
		listenerList.clear();
		listenerMutex.unlock();
	}
	
	public void dataReady(SensorReading reading) {
		listenerMutex.lock();
		for (BaseSensorListener listener : listenerList) {
			listener.sensorDataReady(reading);
		}
		listenerMutex.unlock();
	}

	public byte getSensorState() {
		return sensorState;
	}

	public void setSensorState(byte sensorState) {
		this.sensorState = sensorState;
	}
	
	
	public abstract boolean start(SensorManager sensorManager);
	
	public abstract boolean updateAndRestart(SensorManager sensorManager, byte state);
	
	public abstract boolean stop(SensorManager sensorManager);
	
	
	public SensorReading getReading() {
		Log.d(LOG_TAG, "getReading called ");
		
		if(sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
			Log.d(LOG_TAG, "Error 101 : Sensor not available.");
			return new ErrorReading(new String[] { "101", "Sensor not available on phone." });
		} else if(sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			Log.d(LOG_TAG, "Error 102 : Sensor available but switched off");
			return new ErrorReading(new String[] { "102", "Sensor is switched off." });
		} else if(sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
			Log.d(LOG_TAG, "Error 103 : Sensor available but Permission Denied");
			return new ErrorReading(new String[] { "103", "Sensor permission denied by user." });
		}

		if (reading == null) {
			Log.d(LOG_TAG, "Error 104 : Sensor reading object is null");
			return new ErrorReading(new String[] { "104", "Sensor object is null." });
		}
		return reading;

	}
	
	
}
