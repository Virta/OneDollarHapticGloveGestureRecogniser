package com.aau.bluetooth;



public interface IBluetoothReadyStateListener {
    public abstract void onReadyStateChange(IBluetoothConnection connection, BluetoothReadyState state);
    public abstract void onError(IBluetoothConnection connection, BluetoothException bluetoothException);
}
