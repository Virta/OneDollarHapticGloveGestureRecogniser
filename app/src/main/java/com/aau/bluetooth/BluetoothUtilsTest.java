package com.aau.bluetooth;

import java.nio.charset.Charset;


public class BluetoothUtilsTest {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";

    public static void printOK(String message) {
        System.out.println(ANSI_GREEN + message + ANSI_RESET);
    }
    public static void printWarn(String message) {
        System.out.println(ANSI_YELLOW + message + ANSI_RESET);
    }
    public static void printError(String message) {
        System.out.println(ANSI_RED + message + ANSI_RESET);
    }
    public static void printBuffer2(Short[] buf, int f, int t) {
        for (int i=f; i<t; i++) {
            System.out.print(String.format("%02X|", buf[i]));
            System.out.print(String.format("%8s", Integer.toBinaryString(buf[i])).replace(' ', '0'));
            System.out.print(" ");
        }
        System.out.print("\n");
    }
    public static void printBuffer4(Short[] buf, int f, int t) {
        for (int i=f; i<t; i++) {
            System.out.print(String.format("%04X|", buf[i]));
            System.out.print(String.format("%8s", Integer.toBinaryString(buf[i])).replace(' ', '0'));
            System.out.print(" ");
        }
        System.out.print("\n");
    }
    public static void printBuffer8(Short[] buf, int f, int t) {
        for (int i=f; i<t; i++) {
            System.out.print(String.format("%08X|", buf[i]));
            System.out.print(String.format("%16s", Integer.toBinaryString(buf[i])).replace(' ', '0'));
            System.out.print(" ");
        }
        System.out.print("\n");
    }
    public static void printBytes(long b, String prefix) {
        System.out.print(String.format("%s%04X|", prefix, b));
        System.out.print(String.format("%8s", Long.toBinaryString(b)).replace(' ', '0'));
        System.out.print(" ");
    }

    public static final short[] uint8Cases = {
        123,
        Byte.MAX_VALUE,
        Byte.MAX_VALUE - 1,
        Byte.MAX_VALUE + 1,
        0xFF,
        1,
        0
    };

    public static final int[] uint16Cases = {
        123,
        Short.MAX_VALUE,
        Short.MAX_VALUE - 1,
        Short.MAX_VALUE + 1,
        0xFFFF,
        1,
        0
    };
    public static final long[] uint32Cases = {
        123,
        Short.MAX_VALUE,
        Short.MAX_VALUE - 1,
        Short.MAX_VALUE + 1,
        Integer.MAX_VALUE,
        Integer.MAX_VALUE - 1,
        Integer.MAX_VALUE + 1,
        0xFFFFFFFF,
        1,
        0
    };
    public static final float[] floatCases = {
        123.4f,
        Float.MIN_VALUE,
        Float.MIN_VALUE + 1,
        Float.MAX_VALUE,
        Float.MAX_VALUE - 1,
        1,
        0
    };
    public static final String[] stringCases = {
        "Hello",
        "Goodbye!",
        "Harri Hämäläinen",
        ""
    };

    public static void main(String[] argv) {
        testUint8();
        testUint16();
        testUint32();
        testFloat();
        testString();
    }

    private static void testString() {
        System.out.println("String:");
        System.out.println("---------------------------------------");
        Short[] bufS = new Short[1024];
        Charset charset = Charset.forName("UTF-8");

        int j = 0, k = 0;
        for (int i=0; i<stringCases.length; i++) {
            k = stringCases[i].getBytes(charset).length;
            BluetoothUtils.serializeString(stringCases[i], bufS, j, charset);
            printBuffer4(bufS, j, k);
            j += k;
        }

        System.out.println("bufD length: " + j);
        byte[] bufD = new byte[j];
        for (int i=0; i<j; i++) {
            bufD[i] = bufS[i].byteValue();
        }

        j = 0; k = 0;
        for (int i=0; i<stringCases.length; i++) {
            k = stringCases[i].getBytes(charset).length;
            String x = new String(bufD, j, k, charset);
            if (x.equals(stringCases[i])) {
                printOK("PASSED: (" + i + "): " + stringCases[i] + " / " + x);
            }
            else {
                printError("FAILED: (" + i + "): " + stringCases[i] + " / " + x);
            }
            j += k;
        }
    }

    private static void testFloat() {
        System.out.println("float:");
        System.out.println("---------------------------------------");
        Short[] bufS = new Short[1024];

        for (int i=0; i<floatCases.length; i++) {
            printBytes(Float.floatToIntBits(floatCases[i]), "<");
            BluetoothUtils.serializeFloat(floatCases[i], bufS, i*4);
            printBuffer8(bufS, i*4, i*4+4);
        }

        int l = floatCases.length*4;
        byte[] bufD = new byte[l];
        for (int i=0; i<l; i++) {
            bufD[i] = bufS[i].byteValue();
        }

        for (int i=0; i<floatCases.length; i++) {
            float x = BluetoothUtils.deserializeFloat(bufD, i*4);
            if (x == floatCases[i]) {
                printOK("PASSED: (" + i + "): " + floatCases[i] + " / " + x);
            }
            else {
                printError("FAILED: (" + i + "): " + floatCases[i] + " / " + x);
            }
        }
    }

    private static void testUint32() {
        System.out.println("uint32:");
        System.out.println("---------------------------------------");
        Short[] bufS = new Short[1024];

        for (int i=0; i<uint32Cases.length; i++) {
            printBytes(uint32Cases[i], "<");
            BluetoothUtils.serializeUint32(uint32Cases[i], bufS, i*4);
            printBuffer8(bufS, i*4, i*4+4);
        }

        int l = uint32Cases.length*4;
        byte[] bufD = new byte[l];
        for (int i=0; i<l; i++) {
            bufD[i] = bufS[i].byteValue();
        }

        for (int i=0; i<uint32Cases.length; i++) {
            long x = BluetoothUtils.deserializeUint32(bufD, i*4);
            if (x == uint32Cases[i]) {
                printOK("PASSED: (" + i + "): " + uint32Cases[i] + " / " + x);
            }
            else {
                printError("FAILED: (" + i + "): " + uint32Cases[i] + " / " + x);
            }
        }
    }

    private static void testUint16() {
        System.out.println("uint16:");
        System.out.println("---------------------------------------");
        Short[] bufS = new Short[1024];

        for (int i=0; i<uint16Cases.length; i++) {
            printBytes(uint16Cases[i], "<");
            BluetoothUtils.serializeUint16(uint16Cases[i], bufS, i*2);
            printBuffer4(bufS, i*2, i*2+2);
        }

        int l = uint16Cases.length*2;
        byte[] bufD = new byte[l];
        for (int i=0; i<l; i++) {
            bufD[i] = bufS[i].byteValue();
        }

        for (int i=0; i<uint16Cases.length; i++) {
            int x = BluetoothUtils.deserializeUint16(bufD, i*2);
            if (x == uint16Cases[i]) {
                printOK("PASSED: (" + i + "): " + uint16Cases[i] + " / " + x);
            }
            else {
                printError("FAILED: (" + i + "): " + uint16Cases[i] + " / " + x);
            }
        }
    }

    private static void testUint8() {
        System.out.println("uint8:");
        System.out.println("---------------------------------------");
        Short[] bufS = new Short[1024];

        for (int i=0; i<uint8Cases.length; i++) {
            printBytes(uint8Cases[i], "<");
            BluetoothUtils.serializeUint8(uint8Cases[i], bufS, i);
            printBuffer2(bufS, i, i+1);
        }

        int l = uint8Cases.length;
        byte[] bufD = new byte[l];
        for (int i=0; i<l; i++) {
            bufD[i] = bufS[i].byteValue();
        }

        for (int i=0; i<uint8Cases.length; i++) {
            short x = BluetoothUtils.deserializeUint8(bufD, i);
            if (x == uint8Cases[i]) {
                printOK("PASSED: (" + i + "): " + uint8Cases[i] + " / " + x);
            }
            else {
                printError("FAILED: (" + i + "): " + uint8Cases[i] + " / " + x);
            }
        }
    }
}
