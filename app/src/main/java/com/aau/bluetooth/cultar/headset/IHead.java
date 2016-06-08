package com.aau.bluetooth.cultar.headset;

import java.io.IOException;

//import org.slf4j.Logger;

import com.aau.bluetooth.BluetoothException;
import com.aau.bluetooth.IBluetooth;


public interface IHead {
    /** Initialize listeners and other needed objects */
    public void init(IBluetooth bluetooth, String name);

    /** Connect to the device
     * @throws IOException */
    public void connect() throws BluetoothException, IOException;

    /** Disconnect from the device */
    public void disconnect() throws BluetoothException;

    /** Query if the device is connected */
    public boolean isConnected();

    /** Set the minimum distance parameter for the distance attenuation model */
    public void setMinDistance(float meters);

    /** Set the maximum distance parameter for the distance attenuation model */
    public void setMaxDistance(float meters);

    /** Set the distance for the currently playing audio */
    public void setDistance(float meters);

    /** Set the relative orientation of the currently playing audio */
    public void setOrientation(float angle);

    /** Play the audio with the given id */
    public void play(int id, boolean loop);

    /** Stop the audio with the given id */
    public void stop(HeadCmdPacket.STOP_TYPE type);

    /** Control an audio beacon */
    public void beacon(float volume, int directionality, boolean on);

    /** Called when device is connected */
    public void onConnected();

    /** Called when device is disconnected */
    public void onDisconnected();

    /** Called when there is a device error */
    public void onError(Exception ex);

    /** Called when the audio with the given id has stopped playing */
    public void onAudioStopped();
}
