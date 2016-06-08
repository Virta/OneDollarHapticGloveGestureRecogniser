package com.aau.bluetooth.cultar.glove;

import com.aau.bluetooth.data.LineCmdPacket;


public class GloveCmdPacket extends LineCmdPacket {
    public static final short CMD_VIBRATE = 0;
    public static final short CMD_CALIBRATE = 1;

    public static final short CALIBRATION_TYPE_MAGNETOMETER = 1;
    public static final short CALIBRATION_TYPE_ACCELEROMETER = 2;
    public static final short CALIBRATION_TYPE_SAVE = 3;

    public static final int INDEX_CMD = 0;
    public static final int INDEX_CALIBRATION_TYPE = 1;
    public static final int INDEX_VIB1 = 1;
    public static final int INDEX_VIB2 = 3;
    public static final int INDEX_VIB3 = 5;
    public static final int INDEX_VIB4 = 7;
    public static final int INDEX_VIB5 = 9;
    public static final int INDEX_VIB6 = 11;
    public static final int INDEX_VIB7 = 13;
    public static final int INDEX_VIB8 = 15;
    private static final int GLOVE_CMD_PACKET_LEN = 17;

    /*    data[0] = command
     *     data[1-2] = int vib1
     *     data[3-4] = int vib2
     *     data[5-6] = int vib3
     *     data[7-8] = int vib4
     *     data[9-10] = int vib5
     *     data[11-12] = int vib6
     *     data[13-14] = int vib7
     *     data[15-16] = int vib8
     */

    public GloveCmdPacket() {
        super(GLOVE_CMD_PACKET_LEN);
    }

    public GloveCmdPacket setCmd(short cmd) {
        setUByte(cmd, INDEX_CMD);
        return this;
    }

    public GloveCmdPacket setVib1(int val) {
        set2UBytes(val, INDEX_VIB1);
        return this;
    }

    public GloveCmdPacket setVib2(int val) {
        set2UBytes(val, INDEX_VIB2);
        return this;
    }

    public GloveCmdPacket setVib3(int val) {
        set2UBytes(val, INDEX_VIB3);
        return this;
    }

    public GloveCmdPacket setVib4(int val) {
        set2UBytes(val, INDEX_VIB4);
        return this;
    }

    public GloveCmdPacket setVib5(int val) {
        set2UBytes(val, INDEX_VIB5);
        return this;
    }

    public GloveCmdPacket setVib6(int val) {
        set2UBytes(val, INDEX_VIB6);
        return this;
    }

    public GloveCmdPacket setVib7(int val) {
        set2UBytes(val, INDEX_VIB7);
        return this;
    }

    public GloveCmdPacket setVib8(int val) {
        set2UBytes(val, INDEX_VIB8);
        return this;
    }

    public GloveCmdPacket calibrateAccelerometer() {
        clear();
        setCmd(CMD_CALIBRATE);
        setUByte(CALIBRATION_TYPE_ACCELEROMETER, INDEX_CALIBRATION_TYPE);
        return this;
    }

    public GloveCmdPacket calibrateMagnetometer() {
        clear();
        setCmd(CMD_CALIBRATE);
        setUByte(CALIBRATION_TYPE_MAGNETOMETER, INDEX_CALIBRATION_TYPE);
        return this;
    }

    public GloveCmdPacket saveCalibration() {
        clear();
        setCmd(CMD_CALIBRATE);
        setUByte(CALIBRATION_TYPE_SAVE, INDEX_CALIBRATION_TYPE);
        return this;
    }
}
