package com.aau.bluetooth.android;

import java.io.IOException;
import java.util.UUID;

//import org.slf4j.Logger;

import android.bluetooth.BluetoothDevice;

import com.aau.bluetooth.IBluetoothDevice;
import com.aau.bluetooth.IBluetoothSocket;


public class BluetoothAndroidDevice implements IBluetoothDevice {
    private final BluetoothDevice mRep;
//    private final Logger mLogger;

    public BluetoothAndroidDevice(BluetoothDevice bluetoothDevice) {
        mRep = bluetoothDevice;
//        mLogger = logger;
    }

    @Override
    public IBluetoothSocket createRfcommSocket(UUID uuid) throws IOException {
        return new BluetoothAndroidSocket(mRep.createRfcommSocketToServiceRecord(uuid));
    }

    @Override
    public String getBluetoothAddress() {
        return mRep.getAddress();
    }

    @Override
    public String getName() {
        return mRep.getName();
    }
}
