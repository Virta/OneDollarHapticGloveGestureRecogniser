package com.aau.bluetooth;



public class BluetoothException extends Exception {
    private static final long serialVersionUID = -7568104029094261395L;

    public BluetoothException(String detailMessage) {
        super(detailMessage);
    }

    public BluetoothException(Throwable ex) {
        super(ex);
    }
}
