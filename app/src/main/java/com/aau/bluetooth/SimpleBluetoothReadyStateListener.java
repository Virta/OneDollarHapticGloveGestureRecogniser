package com.aau.bluetooth;




public abstract class SimpleBluetoothReadyStateListener implements IBluetoothReadyStateListener {
    @Override
    public void onReadyStateChange(IBluetoothConnection connection, BluetoothReadyState state) { }

    @Override
    public void onError(IBluetoothConnection connection, BluetoothException bluetoothException) { }
}
