package com.aau.bluetooth.cultar.glove.android;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

//import org.slf4j.Logger;

import android.os.Handler;
import android.util.Log;

import com.aau.bluetooth.BluetoothClient;
import com.aau.bluetooth.BluetoothException;
import com.aau.bluetooth.BluetoothReadyState;
import com.aau.bluetooth.IBluetooth;
import com.aau.bluetooth.IBluetoothConnection;
import com.aau.bluetooth.IBluetoothDevice;
import com.aau.bluetooth.android.IUiDataListener;
import com.aau.bluetooth.android.UiBluetoothReadyStateListener;
import com.aau.bluetooth.android.UiParsedLineDataListener;
import com.aau.bluetooth.cultar.glove.GloveCmdPacket;
import com.aau.bluetooth.cultar.glove.GloveDataPacket;
import com.aau.bluetooth.cultar.glove.IGloveFingerListener;
import com.aau.bluetooth.cultar.glove.IGloveGestureListener;
import com.aau.bluetooth.data.LineDataListener;
import com.aau.bluetooth.data.LineDataListener.LineDelim;

/*
TODO:
    - bluetooth connection stuff
        - how is this going to work with the UI?
        - can the UI add listeners which are proxied to the devices own connection?

    - Glove state tracking
        - finger state
            - trigger events for finger actions?
        - gestures
            - what actually are these?
            - trigger event for these?
        - vib states
        - "position" of the glove?

    - POI management
        - List of POIs
            - position
            - name
            - desc
            - type
        - List of POIs which are being tracked
            - [0, all]

    - Tracking
        - guide to POI
            - works with single POI
            - left/right signals
            - (Up/Down?)
            - can select a POI for further information
                - visual/audio/etc on Android device

        - "feel" POIs
            - works for [1,all] POIs
            - haptic feedback when near/over/pointing-at a POI
            - different feedbacks for different types for POI?
            - different feedbacks for proximity to POI?
            - can select a POI for further information
                - visual/audio/etc on Android device

        - "explore all POIs"
            - works with [1,all] POIs
            - single haptic feedback when pointing at a POI
                - possibly in conjunciton with a visual "map"?
                    - see existing in Yi-Ta's Android app
            - can select a POI for further information
                - visual/audio/etc on Android device

    - POI "feeling"
        - can vibrate 3 fingers independently
        - can vibrate in discrete pulse modes, [0,9]
            - 0: off
            - 9: continuous vibration

        - Proximity:
            - first calculate "distance" from glove to POI
                - angle in (x,z) plane?
                - need to know glove position
                - POI has position
                - calculate angle in (x,z) plane from glove to POI
                - units?
                    - GPS coords?
                    - what is being used by S5?

            - when distance in within threshold OVER
                - all vibrate constantly?
            - when distance is within threshold -NEAR1
                - vibrate f1 and f2
            - when distance is within threshold +NEAR1
                - vibrate f0 and f1
            - when threshold is within threshold -NEAR2
                - vibrate f2
            - when distance is within threshold +NEAR2
                - vibrate f0

        - Calibration support?

*/

public class Glove {
    public static final String TAG = "Glove";
    public static final double DEFAULT_DIRECTION_HIT_THRESHOLD = 20;

    public static final int X = 0;
    public static final int Y = 1;
    public static final int Z = 2;

    private static final int DEFAULT_NUM_RETRIES = 3;
    private static final long DEFAULT_RETRY_INTERVAL = 2000;

    /*[TODO: move to GloveDataPacket]*/
    public enum GloveGesture {
        FOO, BAR;

        /*[TODO: to/from int values]*/
    }

    public enum TrackingMode {
        TRACKING_DIRECTION_GUIDE, TRACKING_PROXIMITY
    }

    public enum TrackingDirection {
        LEFT, RIGHT;
    }

//    private Logger mLogger;

    private String mBluetoothName;
    private BluetoothClient mBluetoothClient;
    private UiBluetoothReadyStateListener mBluetoothReadyStateListener;
    private IUiDataListener mDataListener;

    private GloveCmdPacket mCmdPacket;
    private GloveDataPacket mCurrentHardwareState;
    private int mFinger0VibrationLevel;
    private int mFinger1VibrationLevel;
    private int mFinger2VibrationLevel;
    private int mHardwareFinger0VibrationLevel;
    private int mHardwareFinger1VibrationLevel;
    private int mHardwareFinger2VibrationLevel;

    private double[] mPosition;

    private TrackingMode mTrackingMode;
    private TrackingDirection mLastDirection;
    private double mDirectionHitThreshold;
    private double mProximityThreshold1;
    private double mProximityThreshold2;
    private double mProximityThreshold3;

    //[XXX] private List<Poi> mPois;
    //private Set<Integer> mActivePois;

    private Handler mUiHandler;

    private Set<IGloveGestureListener> mGestureListeners;
    private Set<IGloveFingerListener> mFingerListeners;

    public Glove() {
    }

    public void init(IBluetooth bluetooth, String bluetoothName) throws BluetoothException {
        mBluetoothName = bluetoothName;
//        mLogger = logger;
        mPosition = new double[3];

        //[XXX] mPois = new ArrayList<Poi>();
        //mActivePois = new CopyOnWriteArraySet<Integer>();

        mCmdPacket = new GloveCmdPacket();
        mCurrentHardwareState = new GloveDataPacket();

        mGestureListeners = new CopyOnWriteArraySet<IGloveGestureListener>();
        mFingerListeners = new CopyOnWriteArraySet<IGloveFingerListener>();

        mDirectionHitThreshold = DEFAULT_DIRECTION_HIT_THRESHOLD;

        // State listener
        mBluetoothReadyStateListener = new UiBluetoothReadyStateListener(mUiHandler) {
            @Override
            public void onReadyStateChange(IBluetoothConnection connection,
                                           BluetoothReadyState state) {
                super.onReadyStateChange(connection, state);
                Log.i(TAG, state.toString());
            }

            @Override
            public void onError(IBluetoothConnection connection,
                                BluetoothException bluetoothException) {
                super.onError(connection, bluetoothException);
                Log.i(TAG, bluetoothException.toString());
            }
        };

        mDataListener =
            new UiParsedLineDataListener() {
                @Override
                public void process(byte[] data, int len) {
                    super.process(data, len);
                    _track();

                    if (mCurrentHardwareState.finger0 > 0) {
                        notifyFinger(0, mCurrentHardwareState.finger0);
                    }
                    if (mCurrentHardwareState.finger1 > 0) {
                        notifyFinger(1, mCurrentHardwareState.finger1);
                    }
                    if (mCurrentHardwareState.finger2 > 0) {
                        notifyFinger(2, mCurrentHardwareState.finger2);
                    }

                    if (mCurrentHardwareState.gesture > 0) {
                        /*[FIXME]
                        notifyGesture(mData.gesture);
                        */
                    }
                }
        }
        .init(LineDataListener.DEFAULT_BUF_LEN,
              LineDelim.LINE_CRLF,
              mCurrentHardwareState,
              mUiHandler);

        // Find the device from already paired devices
        IBluetoothDevice gloveDevice =
            bluetooth.getPairedDeviceByName(mBluetoothName);

        if (gloveDevice == null) {
            /*[TODO: how to handle?]*/
            throw new BluetoothException("No paired device found for id: " + mBluetoothName);
        }

        // Connection object
        mBluetoothClient = new BluetoothClient(
                                               gloveDevice,
                                               IBluetooth.RFCOMM_UUID,
                                               DEFAULT_NUM_RETRIES,
                                               DEFAULT_RETRY_INTERVAL);

        // Add listeners to the connection
        mBluetoothClient
            .addReadyStateListener(mBluetoothReadyStateListener)
            .addDataListener(mDataListener);
    }

    public BluetoothClient getBluetoothClient() {
        return mBluetoothClient;
    }

    public Handler getUiHandler() {
        return mUiHandler;
    }

    public void setUiHandler(Handler uiHandler) {
        mUiHandler = uiHandler;
        mBluetoothReadyStateListener.setHandler(mUiHandler);
        mDataListener.setHandler(mUiHandler);
    }

    public double[] getPosition() {
        return mPosition;
    }

    public void setPosition(double x, double y, double z) {
        mPosition[X] = x;
        mPosition[Y] = y;
        mPosition[Z] = z;
    }

    public double getPositionX() {
        return mPosition[X];
    }

    public double getPositionY() {
        return mPosition[Y];
    }

    public double getPositionZ() {
        return mPosition[Z];
    }

    public Glove addGestureListener(IGloveGestureListener listener) {
        mGestureListeners.add(listener);
        return this;
    }

    public Glove removeGestureListener(IGloveGestureListener listenerToRemove) {
        mGestureListeners.remove(listenerToRemove);
        return this;
    }

    public void notifyGesture(GloveGesture gesture) {
        for (IGloveGestureListener listener : mGestureListeners) {
            listener.onGesture(this, gesture);
        }
    }

    public Glove addFingerListener(IGloveFingerListener listener) {
        mFingerListeners.add(listener);
        return this;
    }

    public Glove removeFingerListener(IGloveFingerListener listenerToRemove) {
        mFingerListeners.remove(listenerToRemove);
        return this;
    }

    public void notifyFinger(int finger, int bend) {
        for (IGloveFingerListener listener : mFingerListeners) {
            listener.onFinger(this, finger, bend);
        }
    }

    public TrackingMode getTrackingMode() {
        return mTrackingMode;
    }

    public void setTrackingMode(TrackingMode trackingMode) {
        mTrackingMode = trackingMode;
    }
    public double getDirectionHitThreshold() {
        return mDirectionHitThreshold;
    }

    public void setDirectionHitThreshold(double directionHitThreshold) {
        mDirectionHitThreshold = directionHitThreshold;
    }

    public double getProximityThreshold1() {
        return mProximityThreshold1;
    }

    public void setProximityThreshold1(double proximityThreshold1) {
        mProximityThreshold1 = proximityThreshold1;
    }

    public double getProximityThreshold2() {
        return mProximityThreshold2;
    }

    public void setProximityThreshold2(double proximityThreshold2) {
        mProximityThreshold2 = proximityThreshold2;
    }

    public double getProximityThreshold3() {
        return mProximityThreshold3;
    }

    public void setProximityThreshold3(double proximityThreshold3) {
        mProximityThreshold3 = proximityThreshold3;
    }

    /*[XXX]
    public Glove addPoi(Poi poi) {
        mPois.add(poi);
        return this;
    }

    public Glove removePoi(Poi poiToRemove) {
        mPois.remove(poiToRemove);
        return this;
    }

    public Glove setPoiActive(Poi poi, boolean active) {
        int i = mPois.indexOf(poi);
        if (i != -1) {
            if (active) {
                mActivePois.add(i);
            }
            else {
                mActivePois.remove(i);
            }
        }
        return this;
    }
    */

    public int getFinger0VibrationLevel() {
        return mFinger0VibrationLevel;
    }

    public void setFinger0VibrationLevel(int finger0VibrationLevel) {
        mFinger0VibrationLevel = finger0VibrationLevel;
        if (mBluetoothClient.isConnected()) {
            _writeState();
        }
    }

    public int getFinger1VibrationLevel() {
        return mFinger1VibrationLevel;
    }

    public void setFinger1VibrationLevel(int finger1VibrationLevel) {
        mFinger1VibrationLevel = finger1VibrationLevel;
        if (mBluetoothClient.isConnected()) {
            _writeState();
        }
    }

    public int getFinger2VibrationLevel() {
        return mFinger2VibrationLevel;
    }

    public void setFinger2VibrationLevel(int finger2VibrationLevel) {
        mFinger2VibrationLevel = finger2VibrationLevel;
        if (mBluetoothClient.isConnected()) {
            _writeState();
        }
    }

    private void _track() {
        /*[FIXME: boolean enable/disable tracking]*/
        /*[XXX]
        if (mTrackingMode != null) {
            switch(mTrackingMode) {
                case TRACKING_DIRECTION_GUIDE:
                    directionTrack();
                    break;

                case TRACKING_PROXIMITY:
                    proximityTrack();
                    break;
            }
        }
        */
    }

    /*[XXX]
    public void directionTrack() {
        if (mPois.size() > 0) {
            // Assume the first active POI is being tracked
            Poi target = mPois.get((Integer)mActivePois.toArray()[0]);

            double angle = getAngle(getPosition(), target.getPosition());

            activateDirection(angle, mCurrentHardwareState.heading);
        }
    }

    public void proximityTrack() {
        if (mPois.size() > 0) {
            // For now assume the first active POI is being tracked
            Poi target = mPois.get((Integer)mActivePois.toArray()[0]);

            double angle = getAngle(getPosition(), target.getPosition());

            activateProximity(angle, mCurrentHardwareState.heading);
        }
    }
    */

    public double getAngle(double[] from, double[] target) {
        // Ignore y
        double dx = target[X] - from[X];
        double dz = target[Z] - from[Z];

        double ret = Math.toDegrees(Math.atan2(dz, dx));

        if (ret < 0) {
            ret = (ret + 360.0);
        }
        return ret;
    }

    public void activateDirection(double angle, double heading) {
        //[TODO]
        double d = heading - angle;
        Log.i(TAG, "activateDirection: " + angle + " -> " + heading + ": " + d);
        if (Math.abs(d) > mDirectionHitThreshold) {
            if (d < 0 || d > 180) {
                mFinger0VibrationLevel = 0;
                mFinger2VibrationLevel = 2;
                if (mLastDirection != TrackingDirection.RIGHT) {
                    mLastDirection = TrackingDirection.RIGHT;
                    _writeState();
                }
            }
            else {
                mFinger0VibrationLevel = 2;
                mFinger2VibrationLevel = 0;
                if (mLastDirection != TrackingDirection.LEFT) {
                    mLastDirection = TrackingDirection.LEFT;
                    _writeState();
                }
            }
        }
        else {
            mFinger0VibrationLevel = 0;
            mFinger0VibrationLevel = 0;
            if (mLastDirection != null) {
                mLastDirection = null;
                _writeState();
            }
        }
    }

    /*[XXX: very half-baked, more like an idea than working code]
    public void activateProximity(double angle, double heading) {
        //[TODO]
        double d = heading - angle;
        //[XXX: Erm... see http://stackoverflow.com/a/16180724/203284]
        double adist = Math.min(
                (heading-angle)<0?heading-angle+360:heading-angle,
                (angle-heading)<0?angle-heading+360:angle-heading);

        double[][] proximityPattern = {
            // Thres | L   | R   | v0  | v1  | v2
            { 20.0,    1.0,  1.0,  2.0,  2.0,  2.0 },
            { 30.0,    1.0,  0.0,  0.0,  2.0,  2.0 },
            { 40.0,    1.0,  0.0,  0.0,  0.0,  2.0 },
            { 20.0,    1.0,  1.0,  2.0,  2.0,  2.0 },
            { 30.0,    0.0,  1.0,  2.0,  2.0,  0.0 },
            { 40.0,    0.0,  1.0,  2.0,  0.0,  0.0 },
        };

        class ProximityRule {
            public double threshold;
            public int v0;
            public int v1;
            public int v2;
        }

        ProximityRule[] proximityPattern2 = {
            new ProximityRule()
        };

        Log.i(TAG, "activateProximity: " + angle + " -> " + heading + ": " + adist);
        if (adist < mProximityThreshold1) {
            // vibrate all
            mFinger0VibrationLevel = 2;
            mFinger1VibrationLevel = 2;
            mFinger2VibrationLevel = 2;
        }
        else if (adist < mProximityThreshold2) {
            if (d < 0) {
                // vibrate f1 f2
                mFinger0VibrationLevel = 0;
                mFinger1VibrationLevel = 2;
                mFinger2VibrationLevel = 2;
            }
            else {
                // vibrate f0 f1
                mFinger0VibrationLevel = 2;
                mFinger1VibrationLevel = 2;
                mFinger2VibrationLevel = 0;
            }
        }
        else if (adist < mProximityThreshold3) {
            if (d < 0) {
                // vibrate f2
                mFinger0VibrationLevel = 0;
                mFinger1VibrationLevel = 0;
                mFinger2VibrationLevel = 2;
            }
            else {
                // vibrate f0
                mFinger0VibrationLevel = 2;
                mFinger1VibrationLevel = 0;
                mFinger2VibrationLevel = 0;
            }
        }
        else {
            // all off
            mFinger0VibrationLevel = 0;
            mFinger1VibrationLevel = 0;
            mFinger2VibrationLevel = 0;
        }
        _writeState();
    }
    */

    private boolean _isDirtyState() {
        return (mFinger0VibrationLevel != mHardwareFinger0VibrationLevel ||
                mFinger1VibrationLevel != mHardwareFinger1VibrationLevel ||
                mFinger2VibrationLevel != mHardwareFinger2VibrationLevel);
    }

    private void _writeState() {
        _writeState(false);
    }

    private void _writeState(boolean force) {
        if (force || _isDirtyState()) {
            Log.i(TAG, "_writeState YES");

            mCmdPacket.clear();
            mBluetoothClient.write(
                                        mCmdPacket
                                            .setCmd(GloveCmdPacket.CMD_VIBRATE)
                                            .setVib6(mFinger0VibrationLevel)
                                            .setVib7(mFinger1VibrationLevel)
                                            .setVib8(mFinger2VibrationLevel)
                                            .getData());

            /*[TODO: wait for ack?]*/
            mHardwareFinger0VibrationLevel = mFinger0VibrationLevel;
            mHardwareFinger1VibrationLevel = mFinger1VibrationLevel;
            mHardwareFinger2VibrationLevel = mFinger2VibrationLevel;
        }
    }
}
