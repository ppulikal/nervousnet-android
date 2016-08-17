/**
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 */
/**
 *
 */
package ch.ethz.coss.nervousnet.hub.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import ch.ethz.coss.nervousnet.hub.Application;
import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.views.LightSensorView;
import ch.ethz.coss.nervousnet.lib.ErrorReading;
import ch.ethz.coss.nervousnet.lib.LibConstants;
import ch.ethz.coss.nervousnet.lib.LightReading;
import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;

public class LightFragment extends BaseFragment {

    public LightFragment() {
        super(LibConstants.SENSOR_LIGHT);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_light, container, false);
        sensorSwitch = (Switch) rootView.findViewById(R.id.lightSensorSwitch);
        sensorStatusTV = (TextView) rootView.findViewById(R.id.lightSensorStatus);
        sensorSwitch.setChecked(((((Application) ((Activity)getContext()).getApplication()).nn_VM.getSensorState(LibConstants.SENSOR_LIGHT))== 1) ? true : false);

        sensorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    ((Application) ((Activity)getContext()).getApplication()).nn_VM.startSensor(LibConstants.SENSOR_LIGHT);
                else {
                    ((Application) ((Activity)getContext()).getApplication()).nn_VM.stopSensor(LibConstants.SENSOR_LIGHT, true);

                }

            }
        });
        return rootView;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * ch.ethz.coss.nervousnet.sample.BaseFragment#updateReadings(ch.ethz.coss.
     * nervousnet.vm.SensorReading)
     */
    @Override
    public void updateReadings(SensorReading reading) {
        NNLog.d("LightFragment", "Inside updateReadings");

        if (reading instanceof ErrorReading) {

            NNLog.d("LightFragment", "Inside updateReadings - ErrorReading");
            handleError((ErrorReading) reading);
        } else {

            sensorStatusTV.setText("Service connected and sensor is running");

            TextView lux = (TextView) getActivity().findViewById(R.id.lux);
            lux.setText("" + ((LightReading) reading).getLuxValue());

        }
    }



    @Override
    public void handleError(ErrorReading reading) {
        NNLog.d("LightFragment", "handleError called");
        sensorStatusTV.setText(reading.getErrorString());
    }


}
