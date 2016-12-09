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
 * Event class
 */
public class NNEvent {

    public byte eventType;
    public byte state;
    public long sensorID;

    public NNEvent(long sensorID, byte state, byte type) {
        this.sensorID = sensorID;
        this.state = state;
        this.eventType = type;
    }


    public NNEvent(byte state, byte type) {
        this.eventType = type;
        this.state = state;
    }

    public NNEvent(byte type) {
        this.eventType = type;
    }


}
