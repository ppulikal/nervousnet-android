/*******************************************************************************
 *
 *  *     Nervousnet - a distributed middleware software for social sensing. 
 *  *      It is responsible for collecting and managing data in a fully de-centralised fashion
 *  *
 *  *     Copyright (C) 2016 ETH Zürich, COSS
 *  *
 *  *     This file is part of Nervousnet Framework
 *  *
 *  *     Nervousnet is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     Nervousnet is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU General Public License
 *  *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  *
 *  * 	Contributors:
 *  * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.vm.sensors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import android.location.Location;
import android.location.LocationListener;

public class AccelerometerSensor extends BaseSensor implements SensorEventListener {

	private static final String LOG_TAG = AccelerometerSensor.class.getSimpleName();


	public AccelerometerSensor(byte sensorState) {
		this.sensorState = sensorState;
	}


	@Override
	public boolean start(SensorManager sensorManager) {
		
		if(sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
			Log.d(LOG_TAG, "Cancelled Starting accelerometer sensor as Sensor is not available.");
			return false;
		} else if(sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
			Log.d(LOG_TAG, "Cancelled Starting accelerometer sensor as permission denied by user.");
			return false;
		} else if(sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			Log.d(LOG_TAG, "Cancelled starting accelerometer sensor as Sensor state is switched off.");
			return false;
		} 
		
		Log.d(LOG_TAG, "Starting accelerometer sensor with state = " + sensorState);
		
		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				NervousnetVMConstants.sensor_freq_constants[0][sensorState - 1]);
		
		return true;
	}

	@Override
	public boolean updateAndRestart(SensorManager sensorManager, byte state) {
		
		if(state == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
			Log.d(LOG_TAG, "Cancelled Starting accelerometer sensor as Sensor is not available.");
			return false;
		} else if(state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
			Log.d(LOG_TAG, "Cancelled Starting accelerometer sensor as permission denied by user.");
			return false;
		} else if(state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			setSensorState(state);
			Log.d(LOG_TAG, "Cancelled starting accelerometer sensor as Sensor state is switched off.");
			return false;
		} 
		

		stop(sensorManager);
		
		setSensorState(state);
		Log.d(LOG_TAG, "Restarting accelerometer sensor with state = " + sensorState);
		
		start(sensorManager);
		return true;
	}

	@Override
	public boolean stop(SensorManager sensorManager) {
		if(sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
			Log.d(LOG_TAG, "Cancelled stop accelerometer sensor as Sensor state is not available ");
			return false;
		} else if(sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
			Log.d(LOG_TAG, "Cancelled stop accelerometer sensor as permission denied by user.");
			return false;
		} else if(sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			Log.d(LOG_TAG, "Cancelled stop accelerometer sensor as Sensor state is switched off ");
			return false;
		} 
		sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
		setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);
		this.reading = null;
		return true;
	}

	/**
	 * @param batteryReading
	 */
	public void dataReady(AccelerometerReading reading) {
		// Log.d(LOG_TAG, "dataReady called
		// "+listenerList.size());

		this.reading = reading;
		listenerMutex.lock();

		for (BaseSensorListener listener : listenerList) {
			// Log.d(LOG_TAG, "listener.accelSensorDataReady
			// calling ");

			listener.sensorDataReady(reading);
		}
		listenerMutex.unlock();
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		Log.d(LOG_TAG, "X = " + event.values[0]);
		reading = new AccelerometerReading(event.timestamp, event.values);
//		store(reading);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}