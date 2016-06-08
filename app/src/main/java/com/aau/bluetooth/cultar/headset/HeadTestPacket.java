package com.aau.bluetooth.cultar.headset;

import com.aau.bluetooth.BluetoothUtils;
import com.aau.bluetooth.data.IDataPacket;

public class HeadTestPacket implements IDataPacket {
    public static final int INDEX_TIME = 1;
    public static final int INDEX_X = 5;
    public static final int INDEX_Y = 9;
    public static final int INDEX_Z = 13;
    
    
    public static final char TYPE = 'T';

    public long incTime;
    public float incX, incY, incZ;
    //[FIXME: exception in case of error?]
    @Override
    public void parsePacket(byte[] data, int len) {
        if (data[0] == TYPE) {
            incTime = BluetoothUtils.deserializeUint32(data, INDEX_TIME);
            incX = BluetoothUtils.deserializeFloat(data, INDEX_X);
            incY = BluetoothUtils.deserializeFloat(data, INDEX_Y);
            incZ = BluetoothUtils.deserializeFloat(data, INDEX_Z);
        }
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(TYPE);
        ret.append("[");
        ret.append("incData: ").append(incTime);
        ret.append(", incX: ").append(incX);
        ret.append(", incY: ").append(incY);
        ret.append(", incZ: ").append(incZ);
        ret.append("]");

        return ret.toString();
    }

    /** incData meaning:
    **    10: playback stopped
    **
    **/
}
