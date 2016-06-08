package com.aau.bluetooth.cultar.harness;

import java.util.Arrays;

import com.aau.bluetooth.BluetoothUtils;
import com.aau.bluetooth.data.IDataPacket;

import android.util.Log;

public class HarnessImuDataPacket implements IDataPacket {
    public static final int INDEX_TIME = 1;
    public static final int INDEX_ACCX = 5;
    public static final int INDEX_ACCY = 9;
    public static final int INDEX_ACCZ = 13;
    public static final int INDEX_MAGX = 17;
    public static final int INDEX_MAGY = 21;
    public static final int INDEX_MAGZ = 25;
    public static final int INDEX_GYROX = 29;
    public static final int INDEX_GYROY = 33;
    public static final int INDEX_GYROZ = 37;
    public static final int INDEX_EULERX = 41;
    public static final int INDEX_EULERY = 45;
    public static final int INDEX_EULERZ = 49;

    public static final char TYPE = 'I';

    public long timeMillis;
    public float accX;
    public float accY;
    public float accZ;
    public float magX;
    public float magY;
    public float magZ;
    public float gyroX;
    public float gyroY;
    public float gyroZ;
    public float eulerX;
    public float eulerY;
    public float eulerZ;

    //[FIXME: exception in case of error?]
    public void parsePacket(byte[] data, int len) {
//        Log.i("IMU", Arrays.toString(data));
//        if (data[0] == TYPE) {
//            timeMillis = BluetoothUtils.deserializeUint32(data, INDEX_TIME);
//            accX = BluetoothUtils.deserializeInt16(data, INDEX_ACCX);
//            accY = BluetoothUtils.deserializeInt16(data, INDEX_ACCY);
//            accZ = BluetoothUtils.deserializeInt16(data, INDEX_ACCZ);
//            magX = BluetoothUtils.deserializeInt16(data, INDEX_MAGX);
//            magY = BluetoothUtils.deserializeInt16(data, INDEX_MAGY);
//            magZ = BluetoothUtils.deserializeInt16(data, INDEX_MAGZ);
//            gyroX = BluetoothUtils.deserializeInt16(data, INDEX_GYROX);
//            gyroY = BluetoothUtils.deserializeInt16(data, INDEX_GYROY);
//            gyroZ = BluetoothUtils.deserializeInt16(data, INDEX_GYROZ);
//            eulerX = BluetoothUtils.deserializeFloat(data, INDEX_EULERX);
//            eulerY = BluetoothUtils.deserializeFloat(data, INDEX_EULERY);
//            eulerZ = BluetoothUtils.deserializeFloat(data, INDEX_EULERZ);
//        }
        if (data[0] == TYPE) {
            timeMillis = BluetoothUtils.deserializeUint32(data, INDEX_TIME);
            accX = BluetoothUtils.deserializeFloat(data, INDEX_ACCX);
            accY = BluetoothUtils.deserializeFloat(data, INDEX_ACCY);
            accZ = BluetoothUtils.deserializeFloat(data, INDEX_ACCZ);
            magX = BluetoothUtils.deserializeFloat(data, INDEX_MAGX);
            magY = BluetoothUtils.deserializeFloat(data, INDEX_MAGY);
            magZ = BluetoothUtils.deserializeFloat(data, INDEX_MAGZ);
            gyroX = BluetoothUtils.deserializeFloat(data, INDEX_GYROX);
            gyroY = BluetoothUtils.deserializeFloat(data, INDEX_GYROY);
            gyroZ = BluetoothUtils.deserializeFloat(data, INDEX_GYROZ);
            eulerX = BluetoothUtils.deserializeFloat(data, INDEX_EULERX);
            eulerY = BluetoothUtils.deserializeFloat(data, INDEX_EULERY);
            eulerZ = BluetoothUtils.deserializeFloat(data, INDEX_EULERZ);
        }
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(TYPE);
        ret.append("[");
        ret.append("timeMillis: ").append(timeMillis).append(", ");
        ret.append("a(").append(accX).append(", ").append(accY).append(", ").append(accZ).append("), ");
        ret.append("m(").append(magX).append(", ").append(magY).append(", ").append(magZ).append(") , ");
        ret.append("g(").append(gyroX).append(", ").append(gyroY).append(", ").append(gyroZ).append("), ");
        ret.append("euler(").append(eulerX).append(", ").append(eulerY).append(", ").append(eulerZ).append(")]");

        return ret.toString();
    }
}
