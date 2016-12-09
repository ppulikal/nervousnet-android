/*******************************************************************************
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
 * * 	@author Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.hub.authentication;

import java.util.Hashtable;

import ch.ethz.coss.nervousnet.lib.LibConstants;

public class SensorAuthentication {

    private Hashtable<Integer, Byte> sensorAuthList = new Hashtable<Integer, Byte>();

    public void SensorAuthentication() {
        sensorAuthList.put(LibConstants.SENSOR_ACCELEROMETER, (byte) 0);
        sensorAuthList.put(LibConstants.SENSOR_BATTERY, (byte) 0);
        sensorAuthList.put(LibConstants.DEVICE_INFO, (byte) 0);
        sensorAuthList.put(LibConstants.SENSOR_GYROSCOPE, (byte) 0);
        sensorAuthList.put(LibConstants.SENSOR_LIGHT, (byte) 0);
        sensorAuthList.put(LibConstants.SENSOR_LOCATION, (byte) 0);
        sensorAuthList.put(LibConstants.SENSOR_NOISE, (byte) 0);
        sensorAuthList.put(LibConstants.SENSOR_PROXIMITY, (byte) 0);
    }

    public void updateSensorAuthentication(int sensorID, byte accessRightsValue) {
        sensorAuthList.put(sensorID, accessRightsValue);
    }

    public String getSensorAuthenticationString() {
        String buffer = "";
        for (int key : sensorAuthList.keySet()) {
            buffer = +key + "|" + sensorAuthList.get(key);
        }

        return buffer;
    }

    public void parseSensorAuthenticationString(String sensorString) {

    }

}
