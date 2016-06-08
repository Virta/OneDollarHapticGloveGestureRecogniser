package com.aau.bluetooth;

import java.io.IOException;
import java.util.UUID;


public interface IBluetooth {
    public static final int APPLICATION_DATA_RECEIVED = 0;
    public static final int BLUETOOTH_THREAD_READY_STATE = 1;
    public static final int BLUETOOTH_THREAD_ERROR = 2;
    public static final UUID RFCOMM_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public IBluetoothLocalDevice getLocalDevice();
    public IBluetoothDevice getPairedDeviceByName(String bluetoothName);
    public IBluetoothServerSocket getServerSocket(String name, UUID uuid) throws IOException;
    public boolean isEnabled();
    public boolean isDiscoverable();
    public void setDiscoverable(int durationSecs) throws IOException;
}
