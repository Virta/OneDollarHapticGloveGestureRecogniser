package com.aau.bluetooth;


public interface IBluetoothConnection {
    public BluetoothReadyState getCurrentReadyState();
    public IBluetoothConnection addReadyStateListener(IBluetoothReadyStateListener listener);
    public IBluetoothConnection removeReadyStateListener(IBluetoothReadyStateListener listenerToRemove);
    public void notifyReadyStateChange(BluetoothReadyState state);
    public void notifyError(BluetoothException bluetoothException);
}
