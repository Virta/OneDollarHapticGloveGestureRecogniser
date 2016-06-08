package com.aau.bluetooth;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;

//import org.slf4j.Logger;


public class BluetoothServer implements IBluetoothConnection {
    public static final IBluetoothServerHandler DEFAULT_BLUETOOTH_SERVER_HANDLER = new IBluetoothServerHandler() {
        @Override
        public UUID getUuid() {
            return IBluetooth.RFCOMM_UUID;
        }

        @Override
        public String getName() {
            return "DEFAULT BLUETOOTH SERVER";
        }

        @Override
        public void handleClient(IBluetoothSocket clientSocket) { /*[NOOP]*/ }


        @Override
        public void close() throws IOException { /*[NOOP]*/ }
    };

//    protected Logger mLogger;

    protected BluetoothServerThread mBluetoothServerThread;
    protected Set<IBluetoothReadyStateListener> mReadyStateListeners;
    protected BluetoothReadyState mCurrentReadyState;

    public BluetoothServer(IBluetooth bluetooth)  throws IOException {
        _init(bluetooth, DEFAULT_BLUETOOTH_SERVER_HANDLER);
    }

    public BluetoothServer(IBluetooth bluetooth, IBluetoothServerHandler serverHandler)  throws IOException {
        _init(bluetooth, serverHandler);
    }

    protected void _init(IBluetooth bluetooth, IBluetoothServerHandler serverHandler)  throws IOException {
//        mLogger  = logger;

        mReadyStateListeners = new CopyOnWriteArraySet<IBluetoothReadyStateListener>();
        mCurrentReadyState = BluetoothReadyState.CLOSED;

        final IBluetoothServerSocket serverSocket =
                bluetooth.getServerSocket(serverHandler.getName(), serverHandler.getUuid());

        if (serverSocket == null) {
            throw new IOException("BluetoothServer: Error: could not get server socket");
        }

        mBluetoothServerThread = new BluetoothServerThread(this, serverSocket, serverHandler);
    }

    public void start() {
        mBluetoothServerThread.start();
    }

    public void stop() {
//        mLogger.info("KONK2: bluetoothserver.stop()");
        mBluetoothServerThread.close();
    }

    public boolean isStarted() {
        return (mBluetoothServerThread != null && mBluetoothServerThread.isRunning());
    }

    public boolean isConnected() {
        return (mBluetoothServerThread != null && mBluetoothServerThread.isConnected());
    }

    public IBluetoothServerSocket getServerSocket() {
        return mBluetoothServerThread.getServerSocket();
    }

    @Override
    public BluetoothReadyState getCurrentReadyState() {
        return mCurrentReadyState;
    }

    @Override
    public synchronized IBluetoothConnection addReadyStateListener(IBluetoothReadyStateListener listener) {
        mReadyStateListeners.add(listener);
        // Send the new listener the current ready state
        listener.onReadyStateChange(this, mCurrentReadyState);
        return this;
    }

    @Override
    public synchronized IBluetoothConnection removeReadyStateListener(IBluetoothReadyStateListener listenerToRemove) {
        mReadyStateListeners.remove(listenerToRemove);
        return this;
    }

    @Override
    public void notifyError(BluetoothException bluetoothException) {
        for (IBluetoothReadyStateListener listener : mReadyStateListeners) {
            listener.onError(this, bluetoothException);
        }
    }

    @Override
    public void notifyReadyStateChange(BluetoothReadyState state) {
        mCurrentReadyState = state;
        for (IBluetoothReadyStateListener listener : mReadyStateListeners) {
            listener.onReadyStateChange(this, state);
        }
    }
}
