package com.aau.bluetooth.data;

import com.aau.bluetooth.BluetoothClient;


public interface IDataListener {
    void onData(BluetoothClient connection, byte[] data, int len);
    void process(byte[] data, int len);
}

