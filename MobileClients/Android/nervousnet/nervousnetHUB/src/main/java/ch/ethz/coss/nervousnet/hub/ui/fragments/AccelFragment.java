/*******************************************************************************
 *
 *  *     Nervousnet - a distributed middleware software for social sensing. 
 *  *     It is responsible for collecting and managing data in a fully de-centralised fashion
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
/**
 * 
 */
package ch.ethz.coss.nervousnet.hub.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.views.AccelerometerSensorView;
import ch.ethz.coss.nervousnet.lib.AccelerometerReading;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;

public class AccelFragment extends BaseFragment {
	
	AccelerometerSensorView accelView;
	
	public AccelFragment() {
	}

	public AccelFragment(int type) {
		super(type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_accel, container, false);
		accelView = (AccelerometerSensorView)rootView.findViewById(R.id.accelVizView);
		return rootView;
	}

	@Override
	public void updateReadings(SensorReading reading) {
		NNLog.d("AccelFragment", "Inside updateReadings ");

		if (reading instanceof ErrorReading) {

			NNLog.d("AccelFragment", "Inside updateReadings - ErrorReading");
			handleError((ErrorReading) reading);
		} else {
			TextView x_value = (TextView) getActivity().findViewById(R.id.accel_x);
			TextView y_value = (TextView) getActivity().findViewById(R.id.accel_y);
			TextView z_value = (TextView) getActivity().findViewById(R.id.accel_z);

			x_value.setText("" + ((AccelerometerReading) reading).getX());
			y_value.setText("" + ((AccelerometerReading) reading).getY());
			z_value.setText("" + ((AccelerometerReading) reading).getZ());
			
			float []values= new float[3];
			values[0] = ((AccelerometerReading) reading).getX();
			values[1] = ((AccelerometerReading) reading).getY();
			values[2] = ((AccelerometerReading) reading).getZ();
			accelView.setAccelerometerValues(values);
		}

	}

	@Override
	public void handleError(ErrorReading reading) {
		NNLog.d("AccelFragment", "handleError called");
		TextView status = (TextView) getActivity().findViewById(R.id.sensor_status_accel);
		status.setText("Error: code = " + reading.getErrorCode() + ", message = " + reading.getErrorString());
	}

}
