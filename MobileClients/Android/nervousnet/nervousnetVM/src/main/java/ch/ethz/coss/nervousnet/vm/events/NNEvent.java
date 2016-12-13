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
 * *    Contributors:
 * * 	@author Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.vm.events;

/**
 * Event class used to send and receive Events across to NervousnetHUB and NervousnetVM
 */
public class NNEvent {

    public byte eventType;
    public byte state;
    public long sensorID;

    /**
     *
     * @param sensorID - Sensor ID the event is meant to be for
     * @param state - State of the sensor
     * @param type - Type of Event
     *                Valid values are defined in NervousnetVMConstants.java
     *                 EVENT_PAUSE_NERVOUSNET_REQUEST
     *                 EVENT_START_NERVOUSNET_REQUEST
     *                 EVENT_CHANGE_SENSOR_STATE_REQUEST
     *                 EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST
     *                 EVENT_NERVOUSNET_STATE_UPDATED
     *                 EVENT_SENSOR_STATE_UPDATE
     */
    public NNEvent(long sensorID, byte state, byte type) {
        this.sensorID = sensorID;
        this.state = state;
        this.eventType = type;
    }

    /**
     *
     * @param state - State of the Sensor
     * @param type - Type of Event
     *                Valid values are defined in NervousnetVMConstants.java
     *                 EVENT_PAUSE_NERVOUSNET_REQUEST
     *                 EVENT_START_NERVOUSNET_REQUEST
     *                 EVENT_CHANGE_SENSOR_STATE_REQUEST
     *                 EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST
     *                 EVENT_NERVOUSNET_STATE_UPDATED
     *                 EVENT_SENSOR_STATE_UPDATE
     */

    public NNEvent(byte state, byte type) {
        this.eventType = type;
        this.state = state;
    }


    /**
     *
     *
     *  @param type - Type of Event
     *                Valid values are defined in NervousnetVMConstants.java
     *                 EVENT_PAUSE_NERVOUSNET_REQUEST
     *                 EVENT_START_NERVOUSNET_REQUEST
     *                 EVENT_CHANGE_SENSOR_STATE_REQUEST
     *                 EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST
     *                 EVENT_NERVOUSNET_STATE_UPDATED
     *                 EVENT_SENSOR_STATE_UPDATE
     *
     */

    public NNEvent(byte type) {
        this.eventType = type;
    }


}
