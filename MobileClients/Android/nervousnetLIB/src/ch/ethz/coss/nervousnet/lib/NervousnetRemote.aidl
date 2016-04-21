package ch.ethz.coss.nervousnet.lib;

import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.LocationReading;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.ConnectivityReading;
import ch.ethz.coss.nervousnet.lib.GyroReading;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.ReadingListCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;

import java.util.List;

interface NervousnetRemote
	{
		AccelerometerReading getAccelerometerReading();
		
		BatteryReading getBatteryReading();
	    
	    ConnectivityReading getConnectivityReading();
	    
	    GyroReading getGyroReading();
	 
	    LocationReading getLocationReading();
	    
	    LightReading getLightReading();
	    
	    NoiseReading getNoiseReading();
	    
	    void getReadings(int sensorType, long startTime, long endTime, out List list);
	    

	       
	   
	}