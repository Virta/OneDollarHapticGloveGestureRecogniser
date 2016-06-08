package com.aau.bluetooth;

import java.util.UUID;

//import org.slf4j.Logger;


public class InstrumentedBluetoothClient extends BluetoothClient
                                                   implements IInstrumentationProvider {

    //XXX: Shadows member in superclass
    private InstrumentedBluetoothClientThread mBluetoothClientThread;

    public InstrumentedBluetoothClient(
                                    IBluetoothDevice bluetoothDevice,

                                    UUID rfcommUuid,
                                    int connectionRetries,
                                    long connectionRetryDelayMs)
    {
        super(bluetoothDevice, rfcommUuid, connectionRetries, connectionRetryDelayMs);
    }

    @Override
    public BluetoothClient connect(long connectDelayMs) {
        if (mBluetoothClientThread != null) {
            mBluetoothClientThread.close();
        }

        // Add a thread for this device to the thread list, and start it
        mBluetoothClientThread = new InstrumentedBluetoothClientThread(this);
        mBluetoothClientThread.setConnectDelay(connectDelayMs);
        mBluetoothClientThread.start();

        return this;
    }

    /*------------------------------------------------------------------------
     * IInstrumentationProvider methods {{{
     */
    @Override
    public void addInstrumentationListener(IInstrumentationListener listener) {
        mBluetoothClientThread.addInstrumentationListener(listener);
    }

    @Override
    public void removeInstrumentationListener(IInstrumentationListener listener) {
        mBluetoothClientThread.removeInstrumentationListener(listener);
    }
    /* }}} */
}
