package com.aau.bluetooth;

import java.io.IOException;


public class BluetoothSocketException extends BluetoothException {
    private static final long serialVersionUID = -6541671190387096168L;

    private final Exception mException;

    public BluetoothSocketException(IOException ioException) {
        super(ioException.getMessage());
        mException = ioException;
    }

    public BluetoothSocketException(Exception exception) {
        super(exception.getMessage());
        mException = exception;
    }

    public Exception getException() {
        return mException;
    }
}
