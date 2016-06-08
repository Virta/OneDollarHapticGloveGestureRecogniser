package com.aau.bluetooth;

import java.io.IOException;
import java.util.UUID;


public interface IBluetoothServerHandler {
    public void handleClient(IBluetoothSocket clientSocket);
    public String getName();
    public UUID getUuid();
    public void close() throws IOException;
}
