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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import ch.ethz.coss.nervousnet.lib.ConnectivityReading;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;
import ch.ethz.coss.nervousnet.vm.utils.ValueFormatter;

public class ConnectivitySensor extends BaseSensor {

	private static final String LOG_TAG = ConnectivitySensor.class.getSimpleName();

	private Context context;
	private Handler handler;
	private Runnable runnable;

	public ConnectivitySensor(Context context, byte sensorState) {
		this.context = context;
		this.sensorState = sensorState;

	}

	public class ConnectivityTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
			boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
			int networkType = -1;
			boolean isRoaming = false;
			if (isConnected) {
				networkType = activeNetwork.getType();
				isRoaming = activeNetwork.isRoaming();
			}

			String wifiHashId = "";
			int wifiStrength = Integer.MIN_VALUE;

			if (networkType == ConnectivityManager.TYPE_WIFI) {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wi = wm.getConnectionInfo();
				StringBuilder wifiInfoBuilder = new StringBuilder();
				wifiInfoBuilder.append(wi.getBSSID());
				wifiInfoBuilder.append(wi.getSSID());
				try {
					MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
					messageDigest.update(wifiInfoBuilder.toString().getBytes());
					wifiHashId = new String(messageDigest.digest());
				} catch (NoSuchAlgorithmException e) {
				}
				wifiStrength = wi.getRssi();
			}

			byte[] cdmaHashId = new byte[32];
			byte[] lteHashId = new byte[32];
			byte[] gsmHashId = new byte[32];
			byte[] wcdmaHashId = new byte[32];

			TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

			List<CellInfo> cis = tm.getAllCellInfo();
			if (cis != null) {
				// New method
				for (CellInfo ci : cis) {
					if (ci.isRegistered()) {
						if (ci instanceof CellInfoCdma) {
							CellInfoCdma cic = (CellInfoCdma) ci;
							cdmaHashId = generateMobileDigestId(cic.getCellIdentity().getSystemId(),
									cic.getCellIdentity().getNetworkId(), cic.getCellIdentity().getBasestationId());
						}
						if (ci instanceof CellInfoGsm) {
							CellInfoGsm cic = (CellInfoGsm) ci;
							gsmHashId = generateMobileDigestId(cic.getCellIdentity().getMcc(),
									cic.getCellIdentity().getMnc(), cic.getCellIdentity().getCid());
						}
						if (ci instanceof CellInfoLte) {
							CellInfoLte cic = (CellInfoLte) ci;
							lteHashId = generateMobileDigestId(cic.getCellIdentity().getMcc(),
									cic.getCellIdentity().getMnc(), cic.getCellIdentity().getCi());
						}
						if (ci instanceof CellInfoWcdma) {
							CellInfoWcdma cic = (CellInfoWcdma) ci;
							wcdmaHashId = generateMobileDigestId(cic.getCellIdentity().getMcc(),
									cic.getCellIdentity().getMnc(), cic.getCellIdentity().getCid());
						}
					}
				}
			} else {
				// Legacy method
				CellLocation cl = tm.getCellLocation();
				if (cl instanceof CdmaCellLocation) {
					CdmaCellLocation cic = (CdmaCellLocation) cl;
					cdmaHashId = generateMobileDigestId(cic.getSystemId(), cic.getNetworkId(), cic.getBaseStationId());
				}
				if (cl instanceof GsmCellLocation) {
					GsmCellLocation cic = (GsmCellLocation) cl;
					gsmHashId = generateMobileDigestId(cic.getLac(), 0, cic.getCid());
				}
			}

			StringBuilder mobileHashBuilder = new StringBuilder();
			mobileHashBuilder.append(new String(cdmaHashId));
			mobileHashBuilder.append(new String(lteHashId));
			mobileHashBuilder.append(new String(gsmHashId));
			mobileHashBuilder.append(new String(wcdmaHashId));
			reading = new ConnectivityReading(System.currentTimeMillis(), isConnected, networkType, isRoaming,
					wifiHashId, wifiStrength, mobileHashBuilder.toString());

			NNLog.d(LOG_TAG, "reading collected - " + ((ConnectivityReading) reading).getNetworkType());
			if(reading != null)
				dataReady(reading);
			return null;

		}
	}

	private byte[] generateMobileDigestId(int v1, int v2, int v3) {
		StringBuilder cicBuilder = new StringBuilder();
		cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(v1));
		cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(v2));
		cicBuilder.append(ValueFormatter.leadingZeroHexUpperString(v3));
		MessageDigest messageDigest;
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(cicBuilder.toString().getBytes());
			return messageDigest.digest();
		} catch (NoSuchAlgorithmException e) {
		}
		return new byte[16];
	}

	@Override
	public boolean start() {

		if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
			NNLog.d(LOG_TAG, "Cancelled Starting Connectivity sensor as Sensor is not available.");
			return false;
		} else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
			NNLog.d(LOG_TAG, "Cancelled Starting Connectivity sensor as permission denied by user.");
			return false;
		} else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			NNLog.d(LOG_TAG, "Cancelled starting Connectivity sensor as Sensor state is switched off.");
			return false;
		}

		NNLog.d(LOG_TAG, "Starting Connectivity sensor with state = " + sensorState);

		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				new ConnectivityTask().execute();
				handler.postDelayed(this, 5000);// NervousnetVMConstants.sensor_freq_constants[3][sensorState
												// - 1]); // TODO: test this
			}

		};

		boolean flag = handler.postDelayed(runnable, 0);

		return true;
	}

	@Override
	public boolean updateAndRestart(byte state) {
		if (state == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
			NNLog.d(LOG_TAG, "Cancelled Connectivity battery sensor as Sensor is not available.");
			return false;
		} else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
			NNLog.d(LOG_TAG, "Cancelled Connectivity battery sensor as permission denied by user.");
			return false;
		} else if (state == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			setSensorState(state);
			NNLog.d(LOG_TAG, "Cancelled Connectivity battery sensor as Sensor state is switched off.");
			return false;
		}

		stop(false);
		setSensorState(state);
		NNLog.d(LOG_TAG, "Restarting Connectivity sensor with state = " + sensorState);
		start();
		return true;
	}

	@Override
	public boolean stop(boolean changeStateFlag) {
		if (sensorState == NervousnetVMConstants.SENSOR_STATE_NOT_AVAILABLE) {
			NNLog.d(LOG_TAG, "Cancelled stop Connectivity sensor as Sensor state is not available ");
			return false;
		} else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_PERMISSION_DENIED) {
			NNLog.d(LOG_TAG, "Cancelled stop Connectivity sensor as permission denied by user.");
			return false;
		} else if (sensorState == NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF) {
			NNLog.d(LOG_TAG, "Cancelled stop Connectivity sensor as Sensor state is switched off ");
			return false;
		}
		setSensorState(NervousnetVMConstants.SENSOR_STATE_AVAILABLE_BUT_OFF);
		this.reading = null;
		handler.removeCallbacks(runnable);
		runnable = null;
		handler = null;
		return true;
	}

}