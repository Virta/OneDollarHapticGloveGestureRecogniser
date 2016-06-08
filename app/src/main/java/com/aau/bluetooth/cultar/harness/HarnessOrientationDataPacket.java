package com.aau.bluetooth.cultar.harness;

import java.util.Arrays;

import com.aau.bluetooth.BluetoothUtils;
import com.aau.bluetooth.data.IDataPacket;

import android.util.Log;

public class HarnessOrientationDataPacket implements IDataPacket {
    public static final int INDEX_TIME = 1;
    public static final int INDEX_EULERX = 5;
    public static final int INDEX_EULERY = 9;
    public static final int INDEX_EULERZ = 13;
    public static final int INDEX_CAL_SYS = 17;
    public static final int INDEX_CAL_GYRO = 18;
    public static final int INDEX_CAL_ACCEL = 19;
    public static final int INDEX_CAL_MAG = 20;
    
    public static final char TYPE = 'T';

    public long timeMillis;
    public float eulerX;
    public float eulerY;
    public float eulerZ;
    public int calSys;
    public int calGyro;
    public int calAccel;
    public int calMag;
    

    //[FIXME: exception in case of error?]
    public void parsePacket(byte[] data, int len) {
    	Log.d("Orientation", "");
    	if (data[0] == TYPE) {
        	
            timeMillis = BluetoothUtils.deserializeUint32(data, INDEX_TIME);
            eulerX = BluetoothUtils.deserializeFloat(data, INDEX_EULERX);
            eulerY = BluetoothUtils.deserializeFloat(data, INDEX_EULERY);
            eulerZ = BluetoothUtils.deserializeFloat(data, INDEX_EULERZ);
            calSys = BluetoothUtils.deserializeUint8(data, INDEX_CAL_SYS);
            calGyro = BluetoothUtils.deserializeUint8(data, INDEX_CAL_GYRO);
            calAccel = BluetoothUtils.deserializeUint8(data, INDEX_CAL_ACCEL);
            calMag = BluetoothUtils.deserializeUint8(data, INDEX_CAL_MAG);
        }
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(TYPE);
        ret.append("[");
        ret.append("timeMillis: ").append(timeMillis).append(", ");
        ret.append("euler(").append(eulerX).append(", ").append(eulerY).append(", ").append(eulerZ).append(")");
        ret.append("Device status:( ").append(calSys).append(", ").append(calGyro).append(", ").append(calAccel).append(", ").append(calMag).append(")]");
        return ret.toString();
    }
}
