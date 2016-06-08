package com.aau.bluetooth.android;

import android.os.Handler;

import com.aau.bluetooth.BluetoothException;
import com.aau.bluetooth.BluetoothReadyState;
import com.aau.bluetooth.IBluetoothConnection;
import com.aau.bluetooth.SimpleBluetoothReadyStateListener;


public class UiBluetoothReadyStateListener extends SimpleBluetoothReadyStateListener {
    protected Handler mHandler;

    public UiBluetoothReadyStateListener() {
        mHandler = null;
    }

    public UiBluetoothReadyStateListener(Handler handler) {
        mHandler = handler;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void onReadyStateChange(IBluetoothConnection connection, BluetoothReadyState state) {
        if (mHandler != null) {
            mHandler.obtainMessage(BluetoothAndroid.BLUETOOTH_THREAD_READY_STATE, state).sendToTarget();
        }
    }

    @Override
    public void onError(IBluetoothConnection connection, BluetoothException bluetoothException) {
        if (mHandler != null) {
            mHandler.obtainMessage(BluetoothAndroid.BLUETOOTH_THREAD_ERROR, bluetoothException).sendToTarget();
        }
    }
}

