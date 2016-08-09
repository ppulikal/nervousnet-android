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
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
package ch.ethz.coss.nervousnet.vm.sensors;

import android.bluetooth.BluetoothDevice;

import java.util.UUID;

import ch.ethz.coss.nervousnet.vm.utils.UnsignedArithmetic;

public class BLEBeaconRecord {

    private long tokenDetectTime;
    private BluetoothDevice bluetoothDevice;
    private int rssi;
    private byte[] scanRecord;

    private long mac;
    private UUID advertisement;
    private UUID uuid;
    private int major;
    private int minor;
    private int txpower;

    public BLEBeaconRecord(long tokenDetectTime, BluetoothDevice device, int rssi, byte[] scanRecord) {
        this.tokenDetectTime = tokenDetectTime;
        this.bluetoothDevice = device;
        this.rssi = rssi;
        this.scanRecord = scanRecord;

        // Parsing
        this.mac = UnsignedArithmetic.stringMacToLong(device.getAddress());
        this.advertisement = UnsignedArithmetic.toUUIDBigEndian(scanRecord, 0, 8);
        this.uuid = UnsignedArithmetic.toUUIDBigEndian(scanRecord, 9, 24);
        this.major = (UnsignedArithmetic.upcastToInt(scanRecord[25]) << 8) | (UnsignedArithmetic.upcastToInt(scanRecord[26]));
        this.minor = (UnsignedArithmetic.upcastToInt(scanRecord[27]) << 8) | (UnsignedArithmetic.upcastToInt(scanRecord[28]));
        this.txpower = scanRecord[29];
    }

    public long getTokenDetectTime() {
        return tokenDetectTime;
    }

    public int getRssi() {
        return rssi;
    }

    public long getMac() {
        return mac;
    }

    public UUID getAdvertisement() {
        return advertisement;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getTxpower() {
        return txpower;
    }

}