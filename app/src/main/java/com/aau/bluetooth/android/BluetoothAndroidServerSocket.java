package com.aau.bluetooth.android;

import java.io.IOException;

//import org.slf4j.Logger;

import android.bluetooth.BluetoothServerSocket;

import com.aau.bluetooth.IBluetoothServerSocket;
import com.aau.bluetooth.IBluetoothSocket;


public class BluetoothAndroidServerSocket implements IBluetoothServerSocket {
    private final BluetoothServerSocket mRep;
//    private final Logger mLogger;

    public BluetoothAndroidServerSocket(BluetoothServerSocket serverSocket) {
        mRep = serverSocket;
//        mLogger = logger;
    }

    @Override
    public IBluetoothSocket accept() throws IOException {
        return new BluetoothAndroidSocket(mRep.accept());
    }

    @Override
    public void close() throws IOException {
        mRep.close();
    }

}
