package com.aau.bluetooth.cultar.glove;

import java.util.Timer;
import java.util.TimerTask;

//import org.slf4j.Logger;

import com.aau.bluetooth.BluetoothClient;

/**
 * An message queue for sending Bluetooth Glove commands.
 * The queue is intended to manage listening for ACKs messages from the glove,
 * and re-sending messages if need be.
 *
 * @author Konrad Markus <konker@luxvelocitas.com>
 *
 */
public class GloveMessageQueue {
//    private final Logger mLogger;
    private final BluetoothClient mConnection;
    private final int mRetryTimeoutMs;

    private Timer mTimer;

    public GloveMessageQueue(BluetoothClient connection, int retryTimtoutMs) {
//        mLogger = logger;
        mConnection = connection;
        mRetryTimeoutMs = retryTimtoutMs;

        mTimer = null;
    }

    public synchronized void send(byte[] item) {
//        mLogger.debug("GloveEventQueue: ENQUEUE");

        // Get rid of the current item if any
        _popCurrent();

        // Send the new item instead
        _sendItem(item);
    }

    /*
     * Notify the timer that an ACK has been received, and therefore not need to retry.
     */
    public synchronized void ack() {
//        mLogger.debug("GloveEventQueue: ACK");
        _popCurrent();
    }

    /**
     * Send the given item. Keep retrying until an ACK is received,
     * or another message is sent
     *
     * @param item  The message to send
     */
    private synchronized void _sendItem(final byte[] item) {
        mConnection.write(item);
//        mLogger.debug("GloveEventQueue: SENT");

        // Start a timer to timeout and re-send the command
        // until an ACK cancels it, or another message supersedes it
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
//                mLogger.debug("GloveEventQueue: RETRY...");
                _sendItem(item);
            }
        },
        mRetryTimeoutMs);
    }

    /**
     * Cancel the current retry timer, if any
     */
    private synchronized void _popCurrent() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }
}
