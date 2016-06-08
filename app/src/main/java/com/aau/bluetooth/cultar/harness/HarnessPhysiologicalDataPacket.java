package com.aau.bluetooth.cultar.harness;

import com.aau.bluetooth.BluetoothUtils;
import com.aau.bluetooth.data.IDataPacket;


/**
 * This is the data packet which the harness sends once per second to the application.<br/>
 *
 * @author Walther Jensen
 * @author Konrad Markus
 *
 */
public class HarnessPhysiologicalDataPacket implements IDataPacket {
    public static final int MINUTE_MS = 60000;
    public static final int SECOND_MS = 1000;

    public static final int INDEX_TIMESTAMP = 1;
    public static final int INDEX_HEART_RATE = 5;
    public static final int INDEX_BREATH_RATE = 7;
    public static final int INDEX_STANCE = 9;
    public static final int INDEX_RR_INTERVAL = 11;
    /*[XXX: not used yet?]    
    public static final int INDEX_LIGHT_LEVEL = 13;
    public static final int INDEX_NOISE_LEVEL = 15;
    */

    public static final char TYPE = 'P';

    public long timestamp;

    /**
     * This number is primarily used by the AdminPanel to draw graphs as they need both x and y values.
     */
    public int hour;
    public int minute;
    public int second;

    /**
     * This is the beat per minute and in the range of 40-240.
     */
    public int heartRate;

    /**
     * The BioHarness does ECG readings in the background and registers the interval between R peaks in milliseconds.
     */
    public int rrInterval;

    /**
     * Breadth rate is in the range of 0 - 120.
     */
    public int breathRate;

    /**
     * A reading of the stance of the BioHarness. The range if from -180 to 180 and can be used as an indicator if the person is bent over.
     */
    public int stance;

    /*[XXX: not used yet?]
    public int lightLevel;
    public int noiseLevel;
    */

    public void parsePacket(byte[] data, int len) {
        if (data[0] == TYPE) {
            timestamp = BluetoothUtils.deserializeUint32(data, INDEX_TIMESTAMP);
            hour = (int) (timestamp / MINUTE_MS / 60);
            minute =(int) (timestamp / MINUTE_MS % 60);
            second =(int) (timestamp / SECOND_MS % 60);

            heartRate = BluetoothUtils.deserializeUint16(data, INDEX_HEART_RATE);
            breathRate = BluetoothUtils.deserializeUint16(data, INDEX_BREATH_RATE);
            stance = BluetoothUtils.deserializeUint16(data, INDEX_STANCE);
            rrInterval = BluetoothUtils.deserializeInt16(data, INDEX_RR_INTERVAL);

            /*[XXX: not used yet?]
            lightLevel = BluetoothUtils.deserialize_uint16_t(data, INDEX_LIGHT_LEVEL);
            noiseLevel = BluetoothUtils.deserialize_uint16_t(data, INDEX_NOISE_LEVEL);
            */
        }
    }


    public String toString() {
        StringBuffer ret = new StringBuffer(TYPE);
        ret.append("[");
        ret.append("hour: ").append(hour).append(", ");
        ret.append("minute: ").append(minute).append(", ");
        ret.append("second: ").append(second).append(", ");
        ret.append("heartRate: ").append(heartRate).append(", ");
        ret.append("rrInterval: ").append(rrInterval).append(", ");
        ret.append("breathRate: ").append(breathRate).append(", ");
        ret.append("stance: ").append(stance);
        /*[XXX: not used yet?]
        .append(", ");
        ret.append("lightLevel: ").append(lightLevel).append(", ");
        ret.append("noiseLevel: ").append(noiseLevel);
        */
        ret.append("]");

        return ret.toString();
    }
}
