package com.aau.bluetooth;

import java.io.IOException;
import java.util.UUID;


public interface IBluetoothDevice {
    public IBluetoothSocket createRfcommSocket(UUID uuid) throws IOException;
    public String getBluetoothAddress();
    public String getName();
}
