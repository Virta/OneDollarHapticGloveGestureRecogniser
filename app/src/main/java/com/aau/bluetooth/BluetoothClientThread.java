package com.aau.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.concurrent.atomic.AtomicBoolean;

//import org.slf4j.Logger;


public class BluetoothClientThread extends Thread {
    public static final int BUF_SIZE = 1024;

//    protected Logger mLogger;

    protected IBluetoothSocket mSocket;
    protected InputStream mInStream;
    protected OutputStream mOutStream;
    protected ReadableByteChannel mInChannel;
    protected WritableByteChannel mOutChannel;
    protected BluetoothClient mBluetoothClient;
    protected long mConnectDelay;

    private final AtomicBoolean mPaused;
    private final AtomicBoolean mRunning;
    private final AtomicBoolean mConnected;

    public BluetoothClientThread(BluetoothClient bluetoothClient) {
        mSocket = null;
//        mLogger = logger;
        mBluetoothClient = bluetoothClient;
        mConnectDelay = 0;

        mPaused = new AtomicBoolean(false);
        mRunning = new AtomicBoolean(false);
        mConnected = new AtomicBoolean(false);
    }

    public BluetoothClientThread(BluetoothClient bluetoothClient, IBluetoothSocket bluetoothSocket) {
        mSocket = bluetoothSocket;
//        mLogger = logger;
        mBluetoothClient = bluetoothClient;
        mConnectDelay = 0;

        mPaused = new AtomicBoolean(false);
        mRunning = new AtomicBoolean(false);
        mConnected = new AtomicBoolean(false);
    }

    protected void _runConnect() {
        mRunning.set(true);

        // Delay if necessary
        if (mConnectDelay > 0) {
            try {
                sleep(mConnectDelay);
            }
            catch (InterruptedException ex) { /* Oh well, ignore */ }
        }

        mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CONNECTING);

        // Get a BluetoothSocket to connect with the device
        try {
            mSocket = mBluetoothClient.makeSocket();
            if (mSocket == null) {
                throw new IOException("Could not get socket");
            }
        }
        catch (IOException ex) {
            mBluetoothClient.notifyError(new BluetoothSocketException(ex));
            mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CONNECTION_FAILED);

            // Exit here because there is a problem
            _close_exec();
            return;
        }

        // Connect the device through the socket
        // This will block until it succeeds or throws an exception
        try {
            mSocket.connect();
            mConnected.set(true);
        }
        catch (IOException ex) {
            mBluetoothClient.notifyError(new BluetoothConnectionException(ex));
            mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CONNECTION_FAILED);

            // Exit here because there is a problem
            _close_exec();
            return;
        }

        // Get the input and output streams
        try {
            mInStream = mSocket.getInputStream();
            mOutStream = mSocket.getOutputStream();

            mInChannel = Channels.newChannel(mInStream);
            mOutChannel = Channels.newChannel(mOutStream);
        }
        catch (IOException ex) {
            mBluetoothClient.notifyError(new BluetoothSocketException(ex));
            mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CONNECTION_FAILED);

            // Exit here because there is a problem
            _close_exec();
            return;
        }

        // Notify listeners of the CONNECTED state
        mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CONNECTED);
    }

    protected void _runConnected() {
        mConnected.set(true);

        // Get the input and output streams
        try {
            mInStream = mSocket.getInputStream();
            mOutStream = mSocket.getOutputStream();

            mInChannel = Channels.newChannel(mInStream);
            mOutChannel = Channels.newChannel(mOutStream);
        }
        catch (IOException ex) {
            mBluetoothClient.notifyError(new BluetoothSocketException(ex));
            mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CONNECTION_FAILED);

            // Exit here because there is a problem
            _close_exec();
            return;
        }
    }

    @Override
    public void run() {
        if (mSocket == null) {
            _runConnect();
        }
        else {
            _runConnected();
        }

        byte[] buffer = new byte[BluetoothClientThread.BUF_SIZE];
        int bytes;

        // Keep listening to the InputStream until an exception occurs
        while (mRunning.get()) {
            try {
                synchronized (this) {
                    if (mPaused.get()) {
                        try {
                            wait();
                        }
                        catch (InterruptedException ex) { /* wake up */ }
                    }
                }

                bytes = readIntoBytes(buffer);
                mBluetoothClient.notifyData(buffer, bytes);
            }
            catch (Exception ex) {
                if (mRunning.get()) {
                    // Only raise an error if we are supposed to be running.
                    // A call to close() to deliberately end the thread will unset mRunning
                    mBluetoothClient.notifyError(new BluetoothSocketException(ex));
                    close();
                }
                break;
            }
        }
    }

    public long getConnectDelay() {
        return mConnectDelay;
    }

    public void setConnectDelay(long connectDelay) {
        mConnectDelay = connectDelay;
    }

    protected int readIntoBytes(byte[] buf) throws IOException {
        if (mInStream == null) {
            throw new IOException("No input stream");
        }
        int ret = mInStream.read(buf);
//        if (mLogger != null) mLogger.debug(".xREAD: " + ret);
        return ret;
    }

    protected int writeFromBytes(byte[] buf) throws IOException {
        if (mOutStream == null) {
            throw new IOException("No output stream");
        }
        mOutStream.write(buf);
//        if (mLogger != null) mLogger.debug(".xWRITE: " + buf.length);
        return buf.length; //[XXX: assumes that if there is no error then all bytes were written]
    }

    protected int readIntoBuf(ByteBuffer buf) throws IOException {
        if (mInChannel == null) {
            throw new IOException("No input channel");
        }
        int ret = mInChannel.read(buf);
//        if (mLogger != null) mLogger.debug(".xBUFREAD: " + ret);
        return ret;
    }

    protected int writeFromBuf(ByteBuffer buf) throws IOException {
        if (mOutChannel == null) {
            throw new IOException("No output channel");
        }
        int ret = mOutChannel.write(buf);
//        if (mLogger != null) mLogger.debug(".xBUFWRITE: " + ret);
        return ret;
    }

    protected void flush() throws IOException {
        mOutStream.flush();
    }

    public synchronized boolean isConnected() {
        return mConnected.get();
    }

    public synchronized boolean isPaused() {
        return mPaused.get();
    }

    public synchronized void setPaused(boolean paused) {
        mPaused.set(paused);

        // Wake up the thread if un-pausing
        if (!mPaused.get()) {
            interrupt();
        }
    }

    public void close() {
        mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CLOSING);
        _close_exec();
        mBluetoothClient.notifyReadyStateChange(BluetoothReadyState.CLOSED);
    }

    private void _close_exec() {
        try {
            mRunning.set(false);
            if (mSocket != null) {
                mSocket.close();
            }
            mConnected.set(false);
        }
        catch (IOException ex) { /* Ignore */ }
        finally {
            mRunning.set(false);
            mConnected.set(false);
        }
    }
}

