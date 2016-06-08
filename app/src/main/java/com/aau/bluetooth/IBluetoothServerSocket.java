package com.aau.bluetooth;

import java.io.IOException;


public interface IBluetoothServerSocket {
    public IBluetoothSocket accept() throws IOException;
    public void close() throws IOException;
}
