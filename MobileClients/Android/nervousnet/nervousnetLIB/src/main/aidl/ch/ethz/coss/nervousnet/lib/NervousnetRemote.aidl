package ch.ethz.coss.nervousnet.lib;

import ch.ethz.coss.nervousnet.lib.BatteryReading;
import ch.ethz.coss.nervousnet.lib.LocationReading;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.ConnectivityReading;
import ch.ethz.coss.nervousnet.lib.GyroReading;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.NoiseReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;

import java.util.List;

interface NervousnetRemote
	{
	
		/*
	 	* Returns latest Sensor values.
	    * sensorType = type of Sensor. Check LibConstants for types.
	    * startTime = from time , endTime = to time
	    * returns SensorReading object
	    */
	    SensorReading getReading(int sensorType);
	    
	    /*
	 	* Returns Sensor values in a List of SensorReading Objects.
	    * sensorType = type of Sensors. Check LibConstants for types.
	    * startTime = from time , endTime = to time
	    * list = list that will contain the returned objects of SensorReadings
	    */
	    void getReadings(int sensorType, long startTime, long endTime, out List list);
	    
	       
	   
	}
	/*Methods to be added
	getAverage, getCorrelation, getEntropy, getKMeans,
	 getLargest, getMaxValue, getMeanSquare, getMedian,
	getMinValue, getRankLargest, getRankSmallest, getRms,
	 getRmsError, getSensorDescriptorList, getSmallest, getSum,
	  getSumSquare, getTimeRange, sd, var*/