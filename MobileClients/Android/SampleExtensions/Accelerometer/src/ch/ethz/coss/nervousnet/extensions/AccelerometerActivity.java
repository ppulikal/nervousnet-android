package ch.ethz.coss.nervousnet.extensions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NervousnetRemote;
import ch.ethz.coss.nervousnet.lib.Utils;


public class AccelerometerActivity extends Activity {

	/**********Step 1 for nervousnet HUB API's**********/
	protected NervousnetRemote mService;
	private ServiceConnection mServiceConnection;
	private Boolean bindFlag;
	/***********END OF STEP 1**************/
	
	int m_interval = 100; // 100 milliseconds by default, can be changed later
	Handler m_handler = new Handler();
	Runnable m_statusChecker;

	TextView accel_X, accel_Y, accel_Z, errorView;
	LinearLayout reading, error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_accelerometer);

		Button aboutButton = (Button) findViewById(R.id.about_button);
		aboutButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AccelerometerActivity.this, AboutActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		});

		Button startButton = (Button) findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				startActivity(getPackageManager().getLaunchIntentForPackage("ch.ethz.coss.nervousnet.hub"));
				System.exit(0);
			}
		});

		reading = (LinearLayout) findViewById(R.id.reading);
		error = (LinearLayout) findViewById(R.id.error);
		accel_X = (TextView) findViewById(R.id.accel_x);
		accel_Y = (TextView) findViewById(R.id.accel_y);
		accel_Z = (TextView) findViewById(R.id.accel_z);
		errorView = (TextView) findViewById(R.id.error_tv);
		
		/***********STEP 2 for nervousnet HUB API's************/
		if (mServiceConnection == null) {
			initConnection();
		}

		if (mService == null) {
			try {

				doBindService();
				Log.d("AccelerometerActivity", bindFlag.toString()); // will
																		// return
																		// "true"
				if (!bindFlag) {

					Utils.displayAlert(AccelerometerActivity.this, "Alert",
							"Nervousnet HUB application is required to be installed and running to use this app. If not installed please download it from the App Store. If already installed, please turn on the Data Collection option inside the Nervousnet HUB application.",
							"Download Now", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									try {
										startActivity(new Intent(Intent.ACTION_VIEW,
												Uri.parse("market://details?id=ch.ethz.coss.nervousnet.hub")));
									} catch (android.content.ActivityNotFoundException anfe) {
										startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(
												"https://play.google.com/store/apps/details?id=ch.ethz.coss.nervousnet.hub")));
									}

								}
							}, "Exit", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									System.exit(0);
								}
							});
				} else {
					// if(mService == null) {
					// Utils.displayAlert(AccelerometerActivity.this, "Alert",
					// "Nervousnet HUB application is required running to use
					// this app. If already installed, please turn on the Data
					// Collection option inside the Nervousnet HUB
					// application.",
					// "Start HUB app", new DialogInterface.OnClickListener() {
					// public void onClick(DialogInterface dialog, int id) {
					// startActivity(getPackageManager().getLaunchIntentForPackage("ch.ethz.coss.nervousnet.hub"));
					// }
					// }, "Exit", new DialogInterface.OnClickListener() {
					// public void onClick(DialogInterface dialog, int id) {
					// System.exit(0);
					// }
					// });
					//
					// return;
					// }
					startRepeatingTask();

				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.e("SensorDisplayActivity", "not able to bind ! ");
			}

			// //binding to remote service
			// boolean flag = bindService(it, mServiceConnection,
			// Service.BIND_AUTO_CREATE);
			//
			//

		}
		/**********END OF STEP 2**************/
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}
	
	@Override
	public void onBackPressed() {

		doUnbindService();
		finish();
		System.exit(0);
	}

	/*********STEP3 for nervousnet HUB API's********/
	void initConnection() {

		Log.d("AccelerometerActivity", "Inside initConnection");
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.d("AccelerometerActivity", "Inside onServiceDisconnected 2");
				System.out.println("onServiceDisconnected");
				// TODO Auto-generated method stub
				mService = null;
				mServiceConnection = null;
				Toast.makeText(getApplicationContext(), "NervousnetRemote Service not connected", Toast.LENGTH_SHORT)
						.show();
				Log.d("AccelerometerActivity", "Binding - Service disconnected");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d("AccelerometerActivity", "onServiceConnected");

				mService = NervousnetRemote.Stub.asInterface(service);
//				try {
//					ArrayList list = new ArrayList();
//				Used for testing getReadings method
//					mService.getReadings(LibConstants.SENSOR_ACCELEROMETER, System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS), System.currentTimeMillis(), list);
//					Log.d("AccelerometerActivity", "list size = "+list.size());
//				} catch (RemoteException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
					startRepeatingTask();
				Toast.makeText(getApplicationContext(), "Nervousnet Remote Service Connected", Toast.LENGTH_SHORT)
						.show();
				Log.d("AccelerometerActivity", "Binding is done - Service connected");
			}
		};

	}

	void startRepeatingTask() {
		m_statusChecker = new Runnable() {
			@Override
			public void run() {

				Log.d("AccelerometerActivity", "before updating");

				try {
					update();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				m_handler.postDelayed(m_statusChecker, m_interval);
			}
		};

		m_statusChecker.run();
	}

	void stopRepeatingTask() {
		m_handler.removeCallbacks(m_statusChecker);
	}

	protected void update() throws RemoteException {

		if (mService != null) {
			AccelerometerReading aReading = null;
			try {	
			aReading = (AccelerometerReading) mService.getReading(LibConstants.SENSOR_ACCELEROMETER);

			accel_X.setText("" + aReading.getX());
			accel_Y.setText("" + aReading.getY());
			accel_Z.setText("" + aReading.getZ());
			reading.setVisibility(View.VISIBLE);
			error.setVisibility(View.INVISIBLE);
			} catch (DeadObjectException doe) {
				// TODO Auto-generated catch block
				doe.printStackTrace();
			} 
		} else {
			error.setVisibility(View.VISIBLE);
			reading.setVisibility(View.INVISIBLE);
		}

	}

	protected void doBindService() {
		Log.d("AccelerometerActivity", "doBindService successfull");
		Intent it = new Intent();
		it.setClassName("ch.ethz.coss.nervousnet.hub", "ch.ethz.coss.nervousnet.hub.NervousnetHubApiService");
		bindFlag = getApplicationContext().bindService(it, mServiceConnection, 0);

	}

	protected void doUnbindService() {
		getApplicationContext().unbindService(mServiceConnection);
		bindFlag = false;
		Log.d("AccelerometerActivity ", "doUnbindService successfull");
	}
	/*********END OF STEP3********/

}
