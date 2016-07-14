package ch.ethz.coss.nervousnet.lib;

import java.util.List;
import ch.ethz.coss.nervousnet.lib.ErrorReading;

 interface RemoteCallback
	{
	
	 void success(in List list);
     void failure(in ErrorReading reading);

	}