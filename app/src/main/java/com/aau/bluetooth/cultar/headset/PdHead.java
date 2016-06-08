package com.aau.bluetooth.cultar.headset;

import java.io.IOException;

//import org.slf4j.Logger;

import android.util.Log;

import com.aau.bluetooth.BluetoothClient;
import com.aau.bluetooth.BluetoothException;
import com.aau.bluetooth.BluetoothReadyState;
import com.aau.bluetooth.IBluetooth;
import com.aau.bluetooth.IBluetoothConnection;
import com.aau.bluetooth.IBluetoothDevice;
import com.aau.bluetooth.IBluetoothReadyStateListener;
import com.aau.bluetooth.data.LineDataListener;
import com.aau.bluetooth.data.LineDataListener.LineDelim;
import com.aau.bluetooth.data.TypedParsedLineDataListener;


public class PdHead implements IHead {
//    private Logger mLogger;

    private String mBluetoothName;
    private IBluetooth mBluetooth;
    private IBluetoothReadyStateListener mBluetoothReadyStateListener;
    private BluetoothClient mBluetoothClient;
    private HeadCmdPacket mCmdPacket;
    private HeadDataPacket mCurrentHardwareState;
    private TypedParsedLineDataListener mDataListener;


    public PdHead() { /* nothing at the moment */ }

    @Override
    public void init(IBluetooth bluetooth, String name) {
        mBluetooth = bluetooth;
        mBluetoothName = name;
//        mLogger  = logger;
        mCmdPacket = new HeadCmdPacket();

        // State listener
        mBluetoothReadyStateListener = new IBluetoothReadyStateListener() {
            @Override
            public void onReadyStateChange(IBluetoothConnection connection,
                                           BluetoothReadyState state) {
                switch (state) {
                    case CONNECTED:
                        PdHead.this.onConnected();
                        break;

                    case CLOSED:
                        PdHead.this.onDisconnected();
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onError(IBluetoothConnection connection,
                                BluetoothException bluetoothException) {
                PdHead.this.onError(bluetoothException);
            }
        };

        mDataListener =
                new TypedParsedLineDataListener() {
                    @Override
                    public void process(byte[] data, int len) {
                        super.process(data, len);

                        //[FIXME: magic number]
                        if (mCurrentHardwareState.incData == 10) {
                            PdHead.this.onAudioStopped();
                        }
                    }
                }
                .init(LineDataListener.DEFAULT_BUF_LEN,
                      LineDelim.LINE_LFCR,
                      mCurrentHardwareState,
                      HeadDataPacket.TYPE);
    }

    @Override
    public void connect() throws IOException {
        // Find the device from already paired devices
        IBluetoothDevice headDevice =
            mBluetooth.getPairedDeviceByName(mBluetoothName);

        if (headDevice == null) {
//            mLogger.error("PdHead: Connect: Could not find Bluetooth device: " + mBluetoothName);
            return;
        }

        // Connection object
        mBluetoothClient = new BluetoothClient(
                                               headDevice,
                                               IBluetooth.RFCOMM_UUID,
                                               0,
                                               0);

        // Add listeners to the connection
        mBluetoothClient
            .addReadyStateListener(mBluetoothReadyStateListener)
            .addDataListener(mDataListener);

        // Connect to the device
        mBluetoothClient.connect();
    }

    @Override
    public void disconnect() {
        if (mBluetoothClient.isConnected()) {
            mBluetoothClient.close();
        }
    }

    @Override
    public boolean isConnected() {
        return mBluetoothClient.isConnected();
    }

    @Override
    public void setMinDistance(float meters) {
        if (isConnected()) {
            mCmdPacket.clear();
            mBluetoothClient.write(mCmdPacket
                                                .setMinDistance(meters)
                                                .getData());
        }
    }

    @Override
    public void setMaxDistance(float meters) {
        if (isConnected()) {
            mCmdPacket.clear();
            mBluetoothClient.write(mCmdPacket
                                                .setMaxDistance(meters)
                                                .getData());
        }
    }

    @Override
    public void setDistance(float meters) {
        if (isConnected()) {
            mCmdPacket.clear();
            mBluetoothClient.write(mCmdPacket
                                                .setDistance(meters)
                                                .getData());
        }
    }

    @Override
    public void setOrientation(float angle) {
        if (isConnected()) {
            mCmdPacket.clear();
            mBluetoothClient.write(mCmdPacket
                                                .setOrientation(angle)
                                                .getData());
        }
    }

    @Override
    public void play(int id, boolean loop) {
        if (isConnected()) {
            mCmdPacket.clear();
            mBluetoothClient.write(mCmdPacket
                                                .play(id, loop)
                                                .getData());
        }
    }

    @Override
    public void stop(HeadCmdPacket.STOP_TYPE type) {
        if (isConnected()) {
            mCmdPacket.clear();
            mBluetoothClient.write(mCmdPacket
                                                .stop(type)
                                                .getData());
        }
    }

    @Override
    public void beacon(float volume, int directionality, boolean on) {
        if (isConnected()) {
            mCmdPacket.clear();
            mBluetoothClient.write(mCmdPacket
                                                .beacon(volume, directionality, on)
                                                .getData());
        }
    }

    @Override
    public void onConnected() {
        //[TODO]
        Log.i("PdHead", "onConnected");
    }

    @Override
    public void onDisconnected() {
        //[TODO]
        Log.i("PdHead", "onDisconnected");
    }

    @Override
    public void onError(Exception ex) {
        //[TODO]
        Log.i("PdHead", "onError: " + ex);
    }

    @Override
    public void onAudioStopped() {
        //[TODO]
        Log.i("PdHead", "onAudioStopped");
    }
}
