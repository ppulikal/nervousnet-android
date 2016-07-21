package ch.ethz.coss.nervousnet.extensions.lightmeter;

import ch.ethz.coss.nervousnet.extensions.lightmeter.R;
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

import java.util.List;

import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.NervousnetRemote;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceConnectionListener;
import ch.ethz.coss.nervousnet.lib.NervousnetServiceController;
import ch.ethz.coss.nervousnet.lib.RemoteCallback;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;


public class LightmeterActivity extends Activity implements NervousnetServiceConnectionListener {

	int m_interval = 100; // 100 milliseconds by default, can be changed later
	Handler m_handler = new Handler();
	Runnable m_statusChecker;

	NervousnetServiceController nervousnetServiceController;
	TextView lux, errorView;
	LinearLayout reading, error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nervousnetServiceController = new NervousnetServiceController(LightmeterActivity.this, this);
		nervousnetServiceController.connect();
		setContentView(R.layout.activity_lightmeter);


		Button aboutButton = (Button)findViewById(R.id.about_button);
		aboutButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	Intent intent = new Intent(LightmeterActivity.this, AboutActivity.class);
        		intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        		startActivity(intent);
            }
        });

		Button startButton = (Button)findViewById(R.id.startButton);
		startButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
            	startActivity(getPackageManager().getLaunchIntentForPackage("ch.ethz.coss.nervousnet.hub"));
            	System.exit(0);
            }
        });



		reading = (LinearLayout) findViewById(R.id.reading);
		error = (LinearLayout) findViewById(R.id.error);
		lux = (TextView) findViewById(R.id.lux);
		errorView = (TextView) findViewById(R.id.error_tv);


	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);


	}

	@Override
	public void onBackPressed() {
		nervousnetServiceController.disconnect();
		finish();
		System.exit(0);
	}


	protected void update(NervousnetRemote mService) throws RemoteException{
		if (mService != null) {
			Log.d("LightmeterActivity", "before updating 1");
			SensorReading lReading = mService.getLatestReading(LibConstants.SENSOR_LIGHT);
			if(lReading != null) {
					if(lReading instanceof LightReading)
						Log.d("LightmeterActivity", "before updating 3");
					lux.setText("" + ((LightReading) lReading).getLuxValue());
					reading.setVisibility(View.VISIBLE);
					error.setVisibility(View.INVISIBLE);
				}else  {
					lux.setText("Light object is null");
					reading.setVisibility(View.INVISIBLE);
					error.setVisibility(View.VISIBLE);
				}
		}else {
			Toast.makeText(getApplicationContext(), "mService null", Toast.LENGTH_SHORT)
					.show();
			 error.setVisibility(View.VISIBLE);
			 reading.setVisibility(View.INVISIBLE);
		}


	}

//	protected void update() throws RemoteException {
//
//		if (mService != null) {
//			Log.d("LightmeterActivity", "before updating 1");
////			SensorReading lReading = null;
////			try {
//				Log.d("LightmeterActivity", "before updating 2 ");
//				 final RemoteCallback.Stub cbBinder = new RemoteCallback.Stub() {
//
//					@Override
//					public void success(List list) throws RemoteException {
//						SensorReading lReading = null;
//						if(list.size() > 0)
//							lReading = (SensorReading) list.get(0);
//						if(lReading != null) {
//							if(lReading instanceof LightReading)
//								Log.d("LightmeterActivity", "before updating 3");
//							lux.setText("" + ((LightReading) lReading).getLuxValue());
//							reading.setVisibility(View.VISIBLE);
//							error.setVisibility(View.INVISIBLE);
//						}else  {
//							Log.d("LightmeterActivity", "before updating 4 "+list.size());
//							lux.setText("Light object is null");
//							reading.setVisibility(View.INVISIBLE);
//							error.setVisibility(View.VISIBLE);
//						}
//					}
//
//					@Override
//					public void failure(ErrorReading reading) throws RemoteException {
//
//					}
//				};
//
////			lReading = (SensorReading)
//					mService.getReading(LibConstants.SENSOR_ACCELEROMETER, cbBinder);
//
//
////				lReading =
////				SensorReading lReading = null;
////				Log.d("LightmeterActivity", "before updating 5 "+cbBinder);
////				lReading =	(SensorReading) mService.getLatestReading(LibConstants.SENSOR_LIGHT);
////				if(lReading != null) {
////					if(lReading instanceof LightReading)
////						Log.d("LightmeterActivity", "before updating 3");
////					lux.setText("" + ((LightReading) lReading).getLuxValue());
////					reading.setVisibility(View.VISIBLE);
////					error.setVisibility(View.INVISIBLE);
////				}else  {
////					lux.setText("Light object is null");
////					reading.setVisibility(View.INVISIBLE);
////					error.setVisibility(View.VISIBLE);
////				}
////				mService.getReading(LibConstants.SENSOR_LIGHT,cbBinder);
//
////			} catch (DeadObjectException doe) {
////				// TODO Auto-generated catch block
////				doe.printStackTrace();
////			}
//		} else {
//			Toast.makeText(getApplicationContext(), "mService null", Toast.LENGTH_SHORT)
//					.show();
//			 error.setVisibility(View.VISIBLE);
//			 reading.setVisibility(View.INVISIBLE);
//		}
//
//	}


	void startRepeatingTask(final NervousnetRemote mService) {
		m_statusChecker = new Runnable() {
			@Override
			public void run() {

				Log.d("NervousnetServiceUtil", "before updating");

				try {
					update(mService);
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

	@Override
	public void onServiceConnected(NervousnetRemote mService) {
		startRepeatingTask(mService);
	}

	@Override
	public void onServiceDisconnected() {
		stopRepeatingTask();
	}





class Callback extends RemoteCallback.Stub {

		@Override
		public void success(List list) throws RemoteException {

		}

		@Override
		public void failure(ErrorReading reading) throws RemoteException {

		}

		@Override
		public IBinder asBinder() {
			return null;
		}
	}


}
