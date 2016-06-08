package com.aau.bluetooth.cultar.hiitimu;

import java.util.Arrays;

import com.aau.bluetooth.data.IDataPacket;


public class HiitImuDataPacket implements IDataPacket {
    private static enum Types { T, A, G, M }

    private static final String sLF = "\n";
    private static final String sDELIM = ";";
 
    public int accX;
    public int accY;
    public int accZ;
    public int magX;
    public int magY;
    public int magZ;
    public int gyroX;
    public int gyroY;
    public int gyroZ;

    //[FIXME: exception in case of error?]
    public void parsePacket(byte[] data, int len) {
        //Log.i("HIITIMU", "|" + new String(Arrays.copyOfRange(data, 0, len)) + "|");
        String[] rows = (new String(Arrays.copyOfRange(data, 0, len))).split(sLF);
        for (String r : rows) {
            String[] fields = r.split(sDELIM);
            if (fields.length < 4) {
                //[TODO: warning?]
                //[TODO: what about T]
                //Log.i("HIITIMU", "fields length != 4: " + fields.length);
                continue;
            }

            try {
                int x = Integer.valueOf(fields[1]);
                int y = Integer.valueOf(fields[2]);
                int z = Integer.valueOf(fields[3]);

                switch (Types.valueOf(fields[0])) {
                    case A:
                        accX = x; accY = y; accZ = z;
                        break;
        
                    case G:
                        gyroX = x; gyroY = y; gyroZ = z;
                        break;
        
                    case M:
                        magX = x; magY = y; magZ = z;
                        break;
        
                    default:
                        //[TODO: warning]
                }
            }
            catch (NumberFormatException ex) { /* ignore */ }
            catch (IllegalArgumentException ex) { /* ignore */ }
        }
    }

    public String toString() {
        StringBuffer ret = new StringBuffer();
        ret.append("[");
        ret.append("accX: ").append(accX).append(", ");
        ret.append("accY: ").append(accY).append(", ");
        ret.append("accZ: ").append(accZ).append(", ");
        ret.append("magX: ").append(magX).append(", ");
        ret.append("magY: ").append(magY).append(", ");
        ret.append("magZ: ").append(magZ).append(", ");
        ret.append("gyroX: ").append(gyroX).append(", ");
        ret.append("gyroY: ").append(gyroY).append(", ");
        ret.append("gyroZ: ").append(gyroZ).append(", ");
        ret.append("]");

        return ret.toString();
    }
}
