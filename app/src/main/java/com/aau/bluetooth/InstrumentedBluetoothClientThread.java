package com.aau.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//import org.slf4j.Logger;


public class InstrumentedBluetoothClientThread extends BluetoothClientThread
                                               implements IInstrumentationProvider {

    private final List<IInstrumentationListener> mListeners;

    public InstrumentedBluetoothClientThread(BluetoothClient bluetoothClient) {
        super(bluetoothClient);
        mListeners = new ArrayList<IInstrumentationListener>();
    }

    /*------------------------------------------------------------------------
     * BluetoothClientThread methods {{{
     */
    @Override
    protected int readIntoBytes(byte[] buf) throws IOException {
        int bytesRead = super.readIntoBytes(buf);
        triggerInstrumentationListenersRead((new Date()).getTime(), bytesRead);
        return bytesRead;
    }

    @Override
    protected int writeFromBytes(byte[] buf) throws IOException {
        int bytesWritten = super.writeFromBytes(buf);
        triggerInstrumentationListenersWrite((new Date()).getTime(), bytesWritten);
        return bytesWritten;
    }
    /* }}} */

    /*------------------------------------------------------------------------
     * IInstrumentationProvider methods {{{
     */
    @Override
    public void addInstrumentationListener(IInstrumentationListener listener) {
        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeInstrumentationListener(IInstrumentationListener listener) {
        mListeners.remove(listener);
    }
    /* }}} */

    private void triggerInstrumentationListenersRead(long ts, long byteCount) {
        for (IInstrumentationListener listener : mListeners) {
            listener.updateRead(ts, byteCount);
        }
    }

    private void triggerInstrumentationListenersWrite(long ts, long byteCount) {
        for (IInstrumentationListener listener : mListeners) {
            listener.updateWrite(ts, byteCount);
        }
    }
}

