package com.aau.bluetooth;


import java.nio.charset.Charset;


public class BluetoothUtils {
    public static final String TRUE = "TRUE";
    public static final short ZERO = 0;

    public static final short SMASK_B0 = 0xFF;
    public static final int   IMASK_B0 = 0x00FF;
    public static final int   IMASK_B1 = 0xFF00;
    public static final long  LMASK_B0 = 0x000000FF;
    public static final long  LMASK_B1 = 0x0000FF00;
    public static final long  LMASK_B2 = 0x00FF0000;
    public static final long  LMASK_B3 = 0xFF000000;
    public static final Charset DEFAULT_CHARSET = Charset.forName("US-ASCII");


    private static void put_uint8(short val, Short[] buf, int index) {
        buf[index] = (short)(val & SMASK_B0);
    }

    static short get_uint8(byte[] buf, int index) {
        return (short)(buf[index] & SMASK_B0);
    }

    private static void put_uint16(int val, Short[] buf, int index) {
        buf[index++] = (short)((val & LMASK_B0));
        buf[index]   = (short)((val & LMASK_B1) >> 8);
    }

    static int get_uint16(byte[] buf, int index) {
        int ret = 0;
        ret |= ((buf[index++])    & IMASK_B0);
        ret |= ((buf[index] << 8) & IMASK_B1);
        return ret;
    }

    private static void put_uint32(long val, Short[] buf, int index) {
        buf[index++] = (short)((val & LMASK_B0));
        buf[index++] = (short)((val & LMASK_B1) >> 8);
        buf[index++] = (short)((val & LMASK_B2) >> 16);
        buf[index]   = (short)((val & LMASK_B3) >> 24);
    }

    static long get_uint32(byte[] buf, int index) {
        long ret = 0;
        ret |= ((buf[index++])       & LMASK_B0);
        ret |= ((buf[index++] << 8)  & LMASK_B1);
        ret |= ((buf[index++] << 16) & LMASK_B2);
        ret |= ((buf[index]   << 24) & LMASK_B3);
        return ret;
    }

    public static int serializeString(String s, Short[] buf, int index) {
        return serializeString(s, buf, index, DEFAULT_CHARSET);
    }

    public static int serializeString(String s, Short[] buf, int index, Charset charset) {
        if (s == null) {
            put_uint8(ZERO, buf, index);
            return 1;
        }

        /*[FIXME: do we need the tmp array here?]*/
        byte[] tmp = s.getBytes(charset);
        for (int i=0; i<tmp.length; i++) {
            put_uint8((short)tmp[i], buf, index++);
        }

        return tmp.length;
    }

    public static void serializeFloat(float s, Short[] buf, int index) {
        put_uint32(Float.floatToIntBits(s), buf, index);
    }

    public static float deserializeFloat(byte[] buf, int index) {
        return Float.intBitsToFloat((int)get_uint32(buf, index));
    }

    public static void serializeUint8(short s, Short[] buf, int index) {
        put_uint8(s, buf, index);
    }

    public static short deserializeUint8(byte[] buf, int index) {
        return get_uint8(buf, index);
    }

    public static void serializeInt8(byte s, Short[] buf, int index) {
        put_uint8(s, buf, index);
    }

    public static byte deserializeInt8(byte[] buf, int index) {
        return (byte)get_uint8(buf, index);
    }

    public static void serializeUint16(int s, Short[] buf, int index) {
        put_uint16(s, buf, index);
    }

    public static int deserializeUint16(byte[] buf, int index) {
        return get_uint16(buf, index);
    }

    public static void serializeInt16(short s, Short[] buf, int index) {
        put_uint16(s, buf, index);
    }

    public static short deserializeInt16(byte[] buf, int index) {
        return (short)get_uint16(buf, index);
    }

    public static void serializeUint32(long s, Short[] buf, int index) {
        put_uint32(s, buf, index);
    }

    public static long deserializeUint32(byte[] buf, int index) {
        return get_uint32(buf, index);
    }

    public static void serializeInt32(int s, Short[] buf, int index) {
        put_uint32(s, buf, index);
    }

    public static int deserializeInt32(byte[] buf, int index) {
        return (int)get_uint32(buf, index);
    }
}
