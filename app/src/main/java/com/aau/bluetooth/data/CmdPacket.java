package com.aau.bluetooth.data;

import com.aau.bluetooth.BluetoothUtils;


public class CmdPacket {
    public static final short LF = 0x0A;
    public static final short CR = 0x0D;
    public static final short ZERO = 0x0;
    public static final short ONE = 0x1;
    public static final int TEXT_MAX_LEN = 32;

    protected Short[] mBuffer;
    protected int mPos;

    protected int mPacketMinLen;
    protected int mPacketMaxLen;

    public CmdPacket(int len) {
        mPacketMinLen = len;
        mPacketMaxLen = len;
        mBuffer = new Short[mPacketMaxLen];
        clear();
    }

    public CmdPacket(int minLen, int maxLen) {
        mPacketMinLen = minLen;
        mPacketMaxLen = maxLen;
        mBuffer = new Short[mPacketMaxLen];
        clear();
    }

    protected int setUByte(short val, int index) {
        BluetoothUtils.serializeUint8(val, mBuffer, index);
        return index + 1;
    }

    protected int setByte(byte val, int index) {
        BluetoothUtils.serializeInt8(val, mBuffer, index);
        return index + 1;
    }

    protected int set2UBytes(int val, int index) {
        BluetoothUtils.serializeUint16(val, mBuffer, index);
        return index + 2;
    }

    protected int set2Bytes(short val, int index) {
        BluetoothUtils.serializeInt16(val, mBuffer, index);
        return index + 2;
    }

    protected int set4UBytes(long val, int index) {
        BluetoothUtils.serializeUint32(val, mBuffer, index);
        return index + 4;
    }

    protected int set4Bytes(int val, int index) {
        BluetoothUtils.serializeInt32(val, mBuffer, index);
        return index + 4;
    }

    protected int set4Bytes(float val, int index) {
        BluetoothUtils.serializeFloat(val, mBuffer, index);
        return index + 4;
    }

    protected int setString(String val, int maxLen, int index) {
        int len = val.length();
        if (len > maxLen) {
            val = val.substring(0, maxLen);
        }

        // serialize_string returns the actual number of bytes used
        // to serialize the string using the default encoding
        len = BluetoothUtils.serializeString(val, mBuffer, index);
        mPos += len;
        return index + len;
    }

    protected int setByteArray(byte[] val, int maxLen, int index) {
        int len = Math.min(val.length, maxLen);

        for (int i=0, j=index; i<len; i++, j++) {
            BluetoothUtils.serializeUint8(val[i], mBuffer, j);
        }
        mPos += len;
        return index + len;
    }

    protected void clear() {
        for (int i=0; i<mBuffer.length; i++) {
            mBuffer[i] = ZERO;
        }
        mPos = mPacketMinLen;
    }

    public byte[] getData() {
        // Transfer the values to the byte array of the right length
        //[TODO: possibly "cache" this new byte array?]
        byte[] bytes = new byte[mPos];
        for (int i=0; i<mPos; i++) {
            bytes[i] = mBuffer[i].byteValue();
        }
        return bytes;
    }
}

