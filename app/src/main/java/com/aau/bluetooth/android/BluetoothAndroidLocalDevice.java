package com.aau.bluetooth.android;

//import org.slf4j.Logger;

import android.bluetooth.BluetoothAdapter;

import com.aau.bluetooth.IBluetoothLocalDevice;


public class BluetoothAndroidLocalDevice implements IBluetoothLocalDevice {
    private final BluetoothAdapter mRep;
    @SuppressWarnings("unused")
//    private final Logger mLogger;

    public BluetoothAndroidLocalDevice(BluetoothAdapter bluetoothAdapter) {
        mRep = bluetoothAdapter;
//        mLogger  = logger;
    }

    @Override
    public String getBluetoothAddress() {
        return mRep.getAddress();
    }

    @Override
    public String getName() {
        return mRep.getName();
    }

    @Override
    public boolean isEnabled() {
        return mRep.isEnabled();
    }
}
