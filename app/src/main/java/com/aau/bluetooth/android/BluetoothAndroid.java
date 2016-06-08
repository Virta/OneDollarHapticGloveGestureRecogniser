package com.aau.bluetooth.android;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

//import org.slf4j.Logger;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.content.Context;
import android.content.Intent;

import com.aau.bluetooth.IBluetooth;
import com.aau.bluetooth.IBluetoothDevice;
import com.aau.bluetooth.IBluetoothLocalDevice;
import com.aau.bluetooth.IBluetoothServerSocket;


public class BluetoothAndroid implements IBluetooth {
    public static final String TAG = "BluetoothAndroid";

//    private final Logger mLogger;
    private final Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;
    private final Intent mDiscoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);

    public BluetoothAndroid(Context context) {
        mContext = context;
//        mLogger  = logger;

        // Get a Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
//            mLogger.error("BluetoothAndroid: Fatal error: Device does not support Bluetooth");
            throw new RuntimeException("BluetoothAndroid: Fatal error: Device does not support Bluetooth");
        }

        // Cancel discovery because it will slow down the connection
        mBluetoothAdapter.cancelDiscovery();

        // Switch on Bluetooth?
        if (!mBluetoothAdapter.isEnabled()) {
//            mLogger.error("BluetoothAndroid: Error: Bluetooth not enabled");
        }
    }

    @Override
    public boolean isEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    @Override
    public IBluetoothLocalDevice getLocalDevice() {
        return new BluetoothAndroidLocalDevice(mBluetoothAdapter);
    }

    @Override
    public IBluetoothDevice getPairedDeviceByName(String name) {
        // Fetch a list of already paired devices
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        // Search for the given Bluetooth name
        for (BluetoothDevice pairedDevice : pairedDevices) {
            if (pairedDevice.getName().equals(name)) {
                return new BluetoothAndroidDevice(pairedDevice);
            }
        }
        return null;
    }

    @Override
    public IBluetoothServerSocket getServerSocket(String name, UUID uuid) throws IOException {
        BluetoothServerSocket tmp =
                mBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, uuid);

        if (tmp == null) {
            throw new IOException("Could not get server socket");
        }

        return new BluetoothAndroidServerSocket(tmp);
    }

    @Override
    public boolean isDiscoverable() {
        return false;
    }

    @Override
    public void setDiscoverable(int durationSecs) throws IOException {
        mDiscoverableIntent
            .putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, durationSecs);
        mContext.startActivity(mDiscoverableIntent);
    }
}

