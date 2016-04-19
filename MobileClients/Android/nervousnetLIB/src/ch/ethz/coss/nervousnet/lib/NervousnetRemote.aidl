package ch.ethz.coss.nervousnet.lib;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import java.util.List;

interface NervousnetRemote
	{
		SensorReading getReading(int type);
	    
	    List getReadings(int type, long startTime, long endTime);
	   
	}