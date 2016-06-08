package com.aau.bluetooth;

import java.io.IOException;


public class BluetoothConnectionException extends BluetoothException {
    private static final long serialVersionUID = -6541671190387096168L;

    private final IOException mIOException;

    public BluetoothConnectionException(IOException ioException) {
        super(ioException.getMessage());
        mIOException = ioException;
    }

    public IOException getIOException() {
        return mIOException;
    }
}
