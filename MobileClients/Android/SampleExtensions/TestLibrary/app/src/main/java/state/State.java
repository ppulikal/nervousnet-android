package state;

import android.content.Context;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The class represents the state of the node (a phone or sth else).
 */
public class State {

	private String 	clientIP;
	private String 	clientID;
	private String 	serverIP;
	private String 	serverID;
	private int 	serverPort;
	private PossibleStatePoint 	initState;
	private PossibleStatePoint selectedState;
	private ArrayList<PossibleStatePoint> possibleStates;

	public State(Context context) {
		// TODO
		clientIP = getMyIp();
		clientID = "ExampleClientID";
		serverIP = "localhost";
		serverID = "ExampleServerID";
		serverPort = 8080;  // TODO: server IP and server port are specified in manifest, and that is used directly at the sending stage in Client
		initState = null;
		//selectedState = 2;
		possibleStates = new ArrayList<>();
	}

	// GETTER FUNCTIONS
	public String getClientIP()		{ return this.clientIP;  }
	public String getClientID()		{ return this.clientID;  }
	public String getServerIP()		{ return this.serverIP;  }
	public int    getServerPort()	{ return this.serverPort;}
	public String getServerID()		{ return this.serverID;  }
	public PossibleStatePoint getInitState()	{ return this.initState; }
	public PossibleStatePoint getSelectedState (){ return this.selectedState; }
	public ArrayList<PossibleStatePoint> getPossibleStates(){ return this.possibleStates;}

	// SETTER FUNCTIONS
	public void setPossibleStates(ArrayList<PossibleStatePoint> possibleStates) { this.possibleStates = possibleStates;}
	public void addPossibleState(PossibleStatePoint point) { this.possibleStates.add(point); }
	public void clearPossibleStates() { this.possibleStates.clear(); }
	public void setSelectedStates(PossibleStatePoint selectedState ) { this.selectedState = selectedState;  }

	// HELPER FUNCTIONS
	public static String getMyIp() {
		boolean useIPv4 = true;
		try {
			ArrayList<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface intf : interfaces) {
				ArrayList<InetAddress> addrs = Collections.list(intf.getInetAddresses());
				for (InetAddress addr : addrs) {
					if (!addr.isLoopbackAddress()) {
						String sAddr = addr.getHostAddress();
						//boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
						boolean isIPv4 = sAddr.indexOf(':')<0;

						if (useIPv4) {
							if (isIPv4)
								return sAddr;
						} else {
							if (!isIPv4) {
								int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
								return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
							}
						}
					}
				}
			}
		} catch (Exception ex) { } // for now eat exceptions
		return "Can't get clientIP";
	}

}
