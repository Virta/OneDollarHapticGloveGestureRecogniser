package com.aau.bluetooth.android;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//import org.slf4j.Logger;

import android.bluetooth.BluetoothSocket;

import com.aau.bluetooth.IBluetoothSocket;


public class BluetoothAndroidSocket implements IBluetoothSocket {
    private final BluetoothSocket mRep;
    @SuppressWarnings("unused")
//    private final Logger mLogger;

    public BluetoothAndroidSocket(BluetoothSocket bluetoothSocket) {
        mRep = bluetoothSocket;
//        mLogger = logger;
    }

    @Override
    public void connect() throws IOException {
        mRep.connect();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return mRep.getInputStream();
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return mRep.getOutputStream();
    }

    @Override
    public void close() throws IOException {
        mRep.close();
    }
}
