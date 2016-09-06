package ch.ethz.coss.nervousnet.lib;

import java.util.List;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;

 interface RemoteCallback
	{

	 void success(inout List<SensorReading> list);
     void failure(out ErrorReading reading);

	}