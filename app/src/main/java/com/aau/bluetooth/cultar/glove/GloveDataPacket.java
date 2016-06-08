package com.aau.bluetooth.cultar.glove;

import com.aau.bluetooth.BluetoothUtils;
import com.aau.bluetooth.data.IDataPacket;

public class GloveDataPacket implements IDataPacket {
    /*
    uint8_t commandCode; // R = 0, A = 1, C = 2
    uint8_t gesture;
    uint16_t flexCode;
    uint16_t headCode;
    uint16_t pitchCode;
    uint16_t rollCode;
    */

    public static final int INDEX_TYPE    = 0;
    public static final int INDEX_GESTURE = 1;
    public static final int INDEX_FINGERS = 2;

    public static final int INDEX_HEADING = 6;
    public static final int INDEX_PITCH   = 10;
    public static final int INDEX_ROLL    = 14;

    public static final int TYPE_DATA = 0;
    public static final int TYPE_ACK = 1;
    public static final int TYPE_CALIBRATION = 2;

    public int type;
    public int gesture;
    public int finger0;
    public int finger1;
    public int finger2;
    public double heading;
    public double pitch;
    public double roll;

    //[FIXME: exception in case of error?]
    public void parsePacket(byte[] data, int len) {
        //Log.i("GLOVE", Arrays.toString(data));
        type = BluetoothUtils.deserializeUint8(data, INDEX_TYPE);
        if (type == TYPE_DATA) {
            gesture = BluetoothUtils.deserializeUint8(data, INDEX_GESTURE);

            long fingers = BluetoothUtils.deserializeUint32(data, INDEX_FINGERS);
            finger0 = (int) data[INDEX_FINGERS] & 0xFF;
            finger1 = (int) data[INDEX_FINGERS+1] & 0xFF;
            finger2 = (int) data[INDEX_FINGERS+2] & 0xFF;
//            finger0 = (int)(fingers / 100);
//            finger1 = (int)((fingers / 10) % 10);
//            finger2 = (int)(fingers % 10);

            heading = BluetoothUtils.deserializeFloat(data, INDEX_HEADING);
            heading = Math.toDegrees(heading);

            pitch = BluetoothUtils.deserializeFloat(data, INDEX_PITCH);
            pitch = Math.toDegrees(pitch);
            
            roll = BluetoothUtils.deserializeFloat(data, INDEX_ROLL);
            roll = Math.toDegrees(roll);

        }
    }

    public String toString() {
        StringBuffer ret = new StringBuffer("[");
        ret.append("type: ").append(type).append(", ");
        ret.append("gesture: ").append(gesture).append(", ");
        ret.append("finger0: ").append(finger0).append(", ");
        ret.append("finger1: ").append(finger1).append(", ");
        ret.append("finger2: ").append(finger2).append(", ");
        ret.append("heading: ").append(heading).append(", ");
        ret.append("pitch: ").append(pitch).append(", ");
        ret.append("roll: ").append(roll).append(", ");
        ret.append("]");

        return ret.toString();
    }
}
