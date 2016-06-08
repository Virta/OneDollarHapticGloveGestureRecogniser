package com.aau.bluetooth.data;

import com.aau.bluetooth.BluetoothClient;



public abstract class SimpleDataListener implements IDataListener {
    @Override
    public void onData(BluetoothClient connection, byte[] data, int len) {
        process(data, len);
    }

    @Override
    public void process(byte[] data, int len) { }
}
