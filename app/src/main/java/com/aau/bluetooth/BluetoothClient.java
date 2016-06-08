package com.aau.bluetooth;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

//import org.slf4j.Logger;

import com.aau.bluetooth.data.IDataListener;


public class BluetoothClient implements IBluetoothConnection {
//    protected Logger mLogger;

    protected UUID mRfcommUuid;
    protected BluetoothConnectionType mConnectionType;

    protected int mConnectionRetries;
    protected long mConnectionRetryDelay;
    protected int mConnectionAttempts;

    protected IBluetoothDevice mBluetoothDevice;
    protected BluetoothClientThread mBluetoothClientThread;

    protected BluetoothReadyState mCurrentReadyState;
    protected Set<IBluetoothReadyStateListener> mReadyStateListeners;
    protected Set<IDataListener> mDataListeners;
    protected AtomicInteger mDataListenersRefCount;

    private final IBluetoothReadyStateListener mInternalStateListener =
                                    new SimpleBluetoothReadyStateListener() {
        @Override
        public void onReadyStateChange(
                        IBluetoothConnection connection,
                        BluetoothReadyState state) {

            switch (state) {
                case CONNECTION_FAILED:
                    // Retry if have any attempts left
                    if (mConnectionAttempts < mConnectionRetries) {
                        mConnectionAttempts++;
                        connect(mConnectionRetryDelay);
                    }
                    break;

                case CONNECTED:
                    mConnectionAttempts = 1;
                    break;

                default:
                    break;
            }
        }
    };

    public BluetoothClient(
                           IBluetoothDevice bluetoothDevice,
                           UUID rfcommUuid,
                           int connectionRetries,
                           long connectionRetryDelayMs)
    {
        init(bluetoothDevice,
             rfcommUuid,
             BluetoothConnectionType.RFCOMM,
             connectionRetries, connectionRetryDelayMs);
    }

    public BluetoothClient(IBluetoothSocket bluetoothSocket) {
        init(null,
             null,
             BluetoothConnectionType.RFCOMM,
             0, 0);

        // Add a thread for this device to the thread list, and start it
        mBluetoothClientThread = new BluetoothClientThread(this, bluetoothSocket);
        mBluetoothClientThread.start();
    }

    protected void init(IBluetoothDevice bluetoothDevice,
                        UUID rfcommUuid,
                        BluetoothConnectionType connectionType,
                        int connectionRetries,
                        long connectionRetryDelayMs)
    {
        mBluetoothDevice = bluetoothDevice;
//        mLogger = logger;
        mRfcommUuid = rfcommUuid;
        mConnectionType = connectionType;
        mConnectionRetries = connectionRetries;
        mConnectionRetryDelay = connectionRetryDelayMs;

        mConnectionAttempts = 1;
        mCurrentReadyState = BluetoothReadyState.CLOSED;

        mDataListenersRefCount = new AtomicInteger(0);
        mReadyStateListeners = new CopyOnWriteArraySet<IBluetoothReadyStateListener>();
        mDataListeners = new CopyOnWriteArraySet<IDataListener>();

        addReadyStateListener(mInternalStateListener);
    }

    public BluetoothClient connect() {
        return connect(0);
    }

    public BluetoothClient connect(long connectDelayMs) {
        if (mBluetoothClientThread != null) {
            mBluetoothClientThread.close();
        }

        // Add a thread for this device to the thread list, and start it
        mBluetoothClientThread = new BluetoothClientThread(this);
        mBluetoothClientThread.setConnectDelay(connectDelayMs);
        mBluetoothClientThread.start();

        return this;
    }

    public int getConnectionRetries() {
        return mConnectionRetries;
    }
    public void setConnectionRetries(int connectionRetries) {
        mConnectionRetries = connectionRetries;
    }

    public void resetConnectionAttemps(){
    	mConnectionAttempts = 1;
    }
    public int getConnectionAttempts() {
        return mConnectionAttempts;
    }

    public long getConnectionRetryDelay() {
        return mConnectionRetryDelay;
    }
    public void setConnectionRetryDelay(long connectionRetryDelay) {
        mConnectionRetryDelay = connectionRetryDelay;
    }

    public BluetoothConnectionType getConnectionType() {
        return mConnectionType;
    }

    public void setConnectionType(BluetoothConnectionType connectionType) {
        mConnectionType = connectionType;
    }

    public synchronized BluetoothClient addDataListener(IDataListener listener) {
        mDataListenersRefCount.incrementAndGet();
        mDataListeners.add(listener);
        return this;
    }

    public synchronized BluetoothClient removeDataListener(IDataListener listenerToRemove) {
        mDataListeners.remove(listenerToRemove);
        if (mDataListenersRefCount.decrementAndGet() == 0) {
            // no more listeners, pause thread
            mBluetoothClientThread.setPaused(true);
        }
        return this;
    }

    public IBluetoothSocket makeSocket() throws IOException {
        if (mBluetoothDevice == null) {
            throw new IOException("No BluetoothDevice associated with connection");
        }
        switch (mConnectionType) {
            case RFCOMM:
                return mBluetoothDevice.createRfcommSocket(mRfcommUuid);

            default:
                return null;
        }
    }

    public int read(byte[] bytes) {
        try {
            return mBluetoothClientThread.readIntoBytes(bytes);
        }
        catch (IOException ex) {
            notifyError(new BluetoothException(ex));
        }
        return 0;
    }

    public int write(byte[] bytes) {
        try {
            return mBluetoothClientThread.writeFromBytes(bytes);
        }
        catch (IOException ex) {
            notifyError(new BluetoothException(ex));
        }
        return 0;
    }

    public int read(ByteBuffer buf) {
        try {
            return mBluetoothClientThread.readIntoBuf(buf);
        }
        catch (IOException ex) {
            notifyError(new BluetoothException(ex));
        }
        return 0;
    }

    public int write(ByteBuffer buf)  {
        try {
            return mBluetoothClientThread.writeFromBuf(buf);
        }
        catch (IOException ex) {
            notifyError(new BluetoothException(ex));
        }
        return 0;
    }

    public void flush() {
        try {
            mBluetoothClientThread.flush();
        }
        catch (IOException ex) {
            notifyError(new BluetoothException(ex));
        }
    }

    public boolean isConnected() {
        return (mBluetoothClientThread != null &&
                mBluetoothClientThread.isConnected());
    }

    public boolean isPaused() {
        return (mBluetoothClientThread != null &&
                mBluetoothClientThread.isPaused());
    }

    public BluetoothClient setPaused(boolean paused) {
        if (mBluetoothClientThread != null) {
            mBluetoothClientThread.setPaused(paused);
        }
        return this;
    }

    public BluetoothClient close() {
        if (isConnected()) {
            mBluetoothClientThread.close();
        }
        return this;
    }

    @Override
    public BluetoothReadyState getCurrentReadyState() {
        return mCurrentReadyState;
    }

    @Override
    public synchronized BluetoothClient addReadyStateListener(IBluetoothReadyStateListener listener) {
        mReadyStateListeners.add(listener);

        // Send the new listener the current ready state
        listener.onReadyStateChange(this, mCurrentReadyState);
        return this;
    }

    @Override
    public synchronized BluetoothClient removeReadyStateListener(IBluetoothReadyStateListener listenerToRemove) {
        mReadyStateListeners.remove(listenerToRemove);
        return this;
    }

    @Override
    public synchronized void notifyReadyStateChange(BluetoothReadyState state) {
        mCurrentReadyState = state;
        for (IBluetoothReadyStateListener listener : mReadyStateListeners) {
            listener.onReadyStateChange(this, state);
        }
    }

    @Override
    public synchronized void notifyError(BluetoothException bluetoothException) {
        for (IBluetoothReadyStateListener listener : mReadyStateListeners) {
            listener.onError(this, bluetoothException);
        }
    }

    public void notifyData(byte[] buffer, int bytes) {
        for (IDataListener listener : mDataListeners) {
            listener.onData(this, buffer, bytes);
        }
    }
}
