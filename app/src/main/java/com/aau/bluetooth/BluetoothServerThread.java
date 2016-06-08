package com.aau.bluetooth;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

//import org.slf4j.Logger;


public class BluetoothServerThread extends Thread {
//    protected final Logger mLogger;

    protected final BluetoothServer mBluetoothServer;
    protected final IBluetoothServerSocket mServerSocket;
    protected final IBluetoothServerHandler mServerHandler;

    private final AtomicBoolean mRunning;
    private final AtomicBoolean mConnected;

    public BluetoothServerThread(BluetoothServer bluetoothServer, IBluetoothServerSocket serverSocket, IBluetoothServerHandler serverHandler) {
//        mLogger  = logger;

        mBluetoothServer = bluetoothServer;
        mServerSocket = serverSocket;
        mServerHandler = serverHandler;

        mRunning = new AtomicBoolean(false);
        mConnected = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        mRunning.set(true);

        // Keep listening until exception occurs or a socket is returned
        while (mRunning.get()) {
            try {
                mBluetoothServer.notifyReadyStateChange(BluetoothReadyState.LISTENING);
                IBluetoothSocket clientSocket = mServerSocket.accept();

                // If a connection was accepted
                if (clientSocket != null) {
                    mBluetoothServer.notifyReadyStateChange(BluetoothReadyState.CONNECTED);
                    mConnected.set(true);
                    mServerHandler.handleClient(clientSocket);
                }
            }
            catch (IOException ex) {
//                mLogger.error("Error", ex);
                if (mRunning.get()) {
                    // Only raise an error if we are supposed to be running.
                    // A call to _kill to deliberately end the thread will unset mRunning
                    mBluetoothServer.notifyError(new BluetoothSocketException(ex));
                    close();
                }
            }
        }
    }

    public void close() {
        mBluetoothServer.notifyReadyStateChange(BluetoothReadyState.CLOSING);
        try {
            mConnected.set(false);
            if (mServerHandler != null) {
                mServerHandler.close();
            }

            mRunning.set(false);
            if (mServerSocket != null) {
                mServerSocket.close();
            }
        }
        catch (IOException ex) { /* Ignore */ }
        finally {
            mConnected.set(false);
            mRunning.set(false);
            mBluetoothServer.notifyReadyStateChange(BluetoothReadyState.CLOSED);
        }
    }

    public synchronized boolean isConnected() {
        return mConnected.get();
    }

    public synchronized boolean isRunning() {
        return mRunning.get();
    }

    public IBluetoothServerSocket getServerSocket() {
        return mServerSocket;
    }
}
