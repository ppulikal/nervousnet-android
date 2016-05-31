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
package ch.ethz.coss.nervousnet.hub.ui;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.Constants;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.fragments.AccelFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.BaseFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.BatteryFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.BeaconsFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.ConnectivityFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.DummyFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.GyroFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.HumidFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.LightFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.LocationFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.NoiseFragment;
import ch.ethz.coss.nervousnet.hub.ui.fragments.PressureFragment;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.NervousnetRemote;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.lib.Utils;
import ch.ethz.coss.nervousnet.vm.NNLog;
import ch.ethz.coss.nervousnet.vm.NervousnetVMConstants;

public class SensorDisplayActivity extends FragmentActivity implements ActionBarImplementation {
	protected NervousnetRemote mService;
	private ServiceConnection mServiceConnection;
	private Boolean bindFlag;
	private SensorDisplayPagerAdapter sapAdapter;
	private ViewPager viewPager;
	private static BaseFragment fragment;

	int m_interval = 100; // 100 seconds by default, can be changed later
	Handler m_handler = new Handler();
	Runnable m_statusChecker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateActionBar();
		setContentView(R.layout.activity_sensor_display);

		sapAdapter = new SensorDisplayPagerAdapter(getApplicationContext(), getSupportFragmentManager());

		viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(sapAdapter);

		if (mServiceConnection == null) {
			initConnection();
		}

		if (mService == null) {
			try {

				doBindService();
				NNLog.d("SensorDisplayActivity", bindFlag.toString()); // will
																		// return
																		// "true"
				//
				// if (!bindFlag) {
				// Utils.displayAlert(SensorDisplayActivity.this, "Alert",
				// "Please switch on the data collection option to access this
				// feature.",
				// "Switch On", new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int id) {
				// startStopSensorService(true);
				//
				// }
				// }, "Back", new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog, int id) {
				//
				// }
				// });
				// Toast.makeText(SensorDisplayActivity.this,
				// "Please check if the Nervousnet Remote Service is installed
				// and running.",
				// Toast.LENGTH_SHORT).show();
				// }
				//
				// else {
				// startRepeatingTask();
				// Toast.makeText(SensorDisplayActivity.this,
				// "Nervousnet Remote is running fine and startRepeatingTask()
				// called", Toast.LENGTH_SHORT)
				// .show();
				//
				// }

			} catch (Exception e) {
				e.printStackTrace();
				Log.e("SensorDisplayActivity", "not able to bind ! ");
			}

		}

	}

	void initConnection() {

		NNLog.d("SensorDisplayActivity", "Inside initConnection");
		mServiceConnection = new ServiceConnection() {

			@Override
			public void onServiceDisconnected(ComponentName name) {
				NNLog.d("SensorDisplayActivity", "Inside onServiceDisconnected 2");
				System.out.println("onServiceDisconnected");
				// TODO Auto-generated method stub
				mService = null;
				mServiceConnection = null;
				Toast.makeText(getApplicationContext(), "NervousnetRemote Service not connected", Toast.LENGTH_SHORT)
						.show();
				NNLog.d("SensorDisplayActivity", "Binding - Service disconnected");
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				NNLog.d("SensorDisplayActivity", "onServiceConnected");
				NNLog.d("SensorDisplayActivity", "Inside onServiceConnected 2");

				mService = NervousnetRemote.Stub.asInterface(service);

				startRepeatingTask();
				Toast.makeText(getApplicationContext(), "Nervousnet Remote Service Connected", Toast.LENGTH_SHORT)
						.show();
				NNLog.d("SensorDisplayActivity", "Binding is done - Service connected");
			}
		};

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

	@Override
	public void updateActionBar() {
		LayoutInflater inflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.ab_nn, null);
		ActionBar actionBar;
		Switch mainSwitch;

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setDisplayShowHomeEnabled(false);
		actionBar.setDisplayShowCustomEnabled(true);
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setCustomView(v);
		mainSwitch = (Switch) findViewById(R.id.mainSwitch);

		byte state = ((Application) getApplication()).getState(this);
		NNLog.d("SensorDisplayActivity", "state = " + state);
		mainSwitch.setChecked(state == 0 ? false : true);

		mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				startStopSensorService(isChecked);
			}
		});

	}

	public void startStopSensorService(boolean on) {
		if (on) {
			((Application) getApplication()).startService(this);
			initConnection();
			// startRepeatingTask();

			// UploadService.startService(this);
			// serviceRunning = true;
			//
			// // If the user wants to collect BT/BLE data, ask to enable
			// bluetooth
			// // if disabled
			// SensorConfiguration sc =
			// SensorConfiguration.getInstance(getApplicationContext());
			// SensorCollectStatus scs =
			// sc.getInitialSensorCollectStatus(Constants.SENSOR_BLEBEACON);
			// if (scs.isCollect()) {
			// // This will only work on API level 18 or higher
			// initializeBluetooth();
			// }

		} else {
			((Application) getApplication()).stopService(this);
			stopRepeatingTask();
			// UploadService.stopService(this);
			// serviceRunning = false;
		}

		((Application) getApplication()).setState(this, on ? (byte) 1 : (byte) 0);
		// updateServiceInfo();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class SensorDisplayPagerAdapter extends FragmentStatePagerAdapter {
		Context context;

		public SensorDisplayPagerAdapter(Context context, FragmentManager fm) {
			super(fm);
			this.context = context;
		}

		@Override
		public Fragment getItem(int i) {

			switch (i) {
			case 0:
				fragment = new AccelFragment(0);
				break;
			case 1:
				fragment = new BatteryFragment(1);
				break;
			case 2:
				fragment = new BeaconsFragment(2);
				break;
			case 3:
				fragment = new ConnectivityFragment(3);
				break;
			case 4:
				fragment = new GyroFragment(4);
				break;
			case 5:
				fragment = new HumidFragment(5);
				break;
			case 6:
				fragment = new LocationFragment(6);
				break;
			case 7:
				fragment = new LightFragment(7);
				break;
			case 9:
				fragment = new NoiseFragment(9);
				break;
			case 10:
				fragment = new PressureFragment(10);
				break;
			default:
				fragment = new DummyFragment(-1);
				break;
			}
			return fragment;
		}

		@Override
		public int getCount() {
			return NervousnetVMConstants.sensor_labels.length;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Drawable drawable;
			ImageSpan span;
			SpannableStringBuilder sb;
			sb = new SpannableStringBuilder("  " + NervousnetVMConstants.sensor_labels[position]);

			drawable = context.getResources().getDrawable(Constants.icon_array_sensors[position]);
			drawable.setBounds(0, 0, 40, 40);
			span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
			sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			// sb.setSpan(new ClickableSpan() {
			// @Override
			// public void onClick(View widget) {
			// Toast.makeText(context, "Clicked Span",
			// Toast.LENGTH_LONG).show();
			// }
			// }, 0, sb.length(),
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return sb;
		}

		@SuppressWarnings("unchecked")
		public Fragment getFragment(int position) {
			try {
				Field f = FragmentStatePagerAdapter.class.getDeclaredField("mFragments");
				f.setAccessible(true);
				ArrayList<Fragment> fragments = (ArrayList<Fragment>) f.get(this);
				if (fragments.size() > position) {
					return fragments.get(position);
				}
				return null;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	protected void updateStatus(SensorReading reading, int index) {

		BaseFragment fragment = (BaseFragment) sapAdapter.getFragment(index);
		NNLog.d("SensorDisplayActivity", "Inside updateStatus, index =  " + index);

		if (reading != null) {
			if (reading instanceof ErrorReading)
				fragment.handleError((ErrorReading) reading);
			else
				fragment.updateReadings(reading);
		}

		// else
		// fragment.handleError(new ErrorReading(new String[]{"100", "Reading is
		// null", ""}));
	}

	@Override
	public void onBackPressed() {
		stopRepeatingTask();
		finish();
	}

	void startRepeatingTask() {
		m_statusChecker = new Runnable() {
			@Override
			public void run() {

				NNLog.d("SensorDisplayActivity", "before updating");
				if (mService != null)
					update(); // this function can change value of m_interval.
				else {
					NNLog.d("SensorDisplayActivity", "mService is null");

					Utils.displayAlert(SensorDisplayActivity.this, "Alert",
							"Please switch on the data collection option to access this feature.", "Switch On",
							new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							startStopSensorService(true);
						}
					}, "Back", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {

							finish();
						}
					});

					stopRepeatingTask();
				}

				m_handler.postDelayed(m_statusChecker, m_interval);
			}
		};

		m_statusChecker.run();
	}

	void stopRepeatingTask() {
		m_handler.removeCallbacks(m_statusChecker);
		m_statusChecker = null;

	}

	protected void update() {
		try {
			int index = viewPager.getCurrentItem();
			NNLog.d("SensorDisplayActivity", "Inside update : index  = " + index);

			switch (index) {
			case 0:
				updateStatus(mService.getReading(LibConstants.SENSOR_ACCELEROMETER), index);
				break;
			case 1:
				updateStatus(mService.getReading(LibConstants.SENSOR_BATTERY), index);
				break;
			case 2:
				// beacons
				break;
			case 3:
				updateStatus(mService.getReading(LibConstants.SENSOR_CONNECTIVITY), index);
				break;
			case 4:
				updateStatus(mService.getReading(LibConstants.SENSOR_GYROSCOPE), index);
				break;
			case 5:
				// HUmidity
				break;
			case 6:
				updateStatus(mService.getReading(LibConstants.SENSOR_LOCATION), index);
				break;
			case 7:
				updateStatus(mService.getReading(LibConstants.SENSOR_LIGHT), index);
				break;
			case 8:
				// Magnetic
				break;
			case 9:
				updateStatus(mService.getReading(LibConstants.SENSOR_NOISE), index);
				break;
			case 10:
				// Pressure
				updateStatus(mService.getReading(LibConstants.SENSOR_PRESSURE), index);
				break;

			case 11:
				// Proximity
				break;
			}

			viewPager.getAdapter().notifyDataSetChanged();

			// }
			// catch (RemoteException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doBindService() {
		NNLog.d("SensorDisplayActivity", "doBindService successfull");

		Intent it = new Intent();
		it.setClassName("ch.ethz.coss.nervousnet.hub", "ch.ethz.coss.nervousnet.hub.NervousnetHubApiService");
		bindFlag = getApplicationContext().bindService(it, mServiceConnection, Context.BIND_AUTO_CREATE);

	}

	protected void doUnbindService() {
		getApplicationContext().unbindService(mServiceConnection);
		bindFlag = false;
		NNLog.d("SensorDisplayActivity ", "doUnbindService successfull");
	}
}
