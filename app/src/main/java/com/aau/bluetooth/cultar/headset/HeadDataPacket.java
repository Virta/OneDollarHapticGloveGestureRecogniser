package com.aau.bluetooth.cultar.headset;

import com.aau.bluetooth.BluetoothUtils;
import com.aau.bluetooth.data.IDataPacket;

public class HeadDataPacket implements IDataPacket {
    public static final int INDEX_DATA = 1;

    public static final char TYPE = 'E';

    public long incData;

    //[FIXME: exception in case of error?]
    @Override
    public void parsePacket(byte[] data, int len) {
        if (data[0] == TYPE) {
            incData = BluetoothUtils.deserializeUint32(data, INDEX_DATA);
        }
    }

    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(TYPE);
        ret.append("[");
        ret.append("incData: ").append(incData);
        ret.append("]");

        return ret.toString();
    }

    /** incData meaning:
    **    10: playback stopped
    **
    **/
}
