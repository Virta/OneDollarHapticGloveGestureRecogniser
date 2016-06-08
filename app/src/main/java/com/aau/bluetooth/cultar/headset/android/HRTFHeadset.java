package com.aau.bluetooth.cultar.headset.android;

//import org.slf4j.Logger;

import com.aau.bluetooth.BluetoothClient;
import com.aau.bluetooth.BluetoothException;
import com.aau.bluetooth.BluetoothReadyState;
import com.aau.bluetooth.IBluetooth;
import com.aau.bluetooth.IBluetoothConnection;
import com.aau.bluetooth.IBluetoothDevice;
import com.aau.bluetooth.android.IUiDataListener;
import com.aau.bluetooth.android.UiBluetoothReadyStateListener;
import com.aau.bluetooth.android.UiParsedLineDataListener;
import com.aau.bluetooth.cultar.headset.HeadTestPacket;
import com.aau.bluetooth.data.LineDataListener;
import com.aau.bluetooth.data.LineDataListener.LineDelim;
import com.aau.openalrenderer.openal.OALCultAR;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;

@SuppressLint("UseSparseArrays")
public class HRTFHeadset {
	public static final String TAG = "HeadsetHRTF";

	private static final int DEFAULT_NUM_RETRIES = 3;
	private static final long DEFAULT_RETRY_INTERVAL = 2000;

//	private Logger mLogger;
	private String mBluetoothName;
	private BluetoothClient mBluetoothClient;
	private UiBluetoothReadyStateListener mBluetoothReadyStateListener;
	private IUiDataListener mDataListener;
	public OALCultAR mHRTF;
	private HeadTestPacket mIMUPacket;
	private Handler mUiHandler;

	public void init(Activity activity, IBluetooth bluetooth, String bluetoothName, String mediaPath, boolean hrtfDebug) throws BluetoothException {
		mBluetoothName = bluetoothName;
//		mLogger = logger;
		mIMUPacket = new HeadTestPacket();
		mHRTF = OALCultAR.getInstance(activity, mediaPath, hrtfDebug);


		// State listener
		mBluetoothReadyStateListener = new UiBluetoothReadyStateListener(mUiHandler) {
			@Override
			public void onReadyStateChange(IBluetoothConnection connection, BluetoothReadyState state) {
				super.onReadyStateChange(connection, state);
				Log.i(TAG, state.toString());
			}

			@Override
			public void onError(IBluetoothConnection connection, BluetoothException bluetoothException) {
				super.onError(connection, bluetoothException);
				Log.i(TAG, bluetoothException.toString());
			}
		};

		mDataListener = new UiParsedLineDataListener() {
			@Override
			public void process(byte[] data, int len) {
				super.process(data, len);
			}
		}.init(LineDataListener.DEFAULT_BUF_LEN, LineDelim.LINE_CRLF, mIMUPacket, mUiHandler);

		// Find the device from already paired devices
		IBluetoothDevice headsetDevice = bluetooth.getPairedDeviceByName(mBluetoothName);

		if (headsetDevice == null) {
			/* [TODO: how to handle?] */
			throw new BluetoothException("No paired device found for id: " + mBluetoothName);
		}

		// Connection object
		mBluetoothClient = new BluetoothClient(headsetDevice, IBluetooth.RFCOMM_UUID, DEFAULT_NUM_RETRIES, DEFAULT_RETRY_INTERVAL);

		// Add listeners to the connection
		mBluetoothClient.addReadyStateListener(mBluetoothReadyStateListener).addDataListener(mDataListener);
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

	
	
	
	
//	/**
//	 * Use this function to load wave data into a channel. For now it only supports .wav and no streaming.
//	 * Future implementations should be .ogg support and streaming support to better memory control
//	 * @param channelId - Which channel to load wavedata into
//	 * @param sourceFileName - Filename for .wav file. Don't add .wav for now
//	 * @param minDistance - The sources min distance (not supported yet)
//	 * @param maxDistance - The sources max distance (not supported yet)
//	 * @return return true if successful otherwise false
//	 */
//
//	public boolean setChannelSource(int channelId, String sourceFileName, int minDistance, int maxDistance) {
//		if (mSourceList.containsKey(channelId)){
//			return true;
//		} else {
//			try {
//				Buffer tempBuffer = mOpenALRender.addBuffer(sourceFileName);
//				Source tempSource = mOpenALRender.addSource(tempBuffer);
//				tempSource.setRolloffFactor(0.5f);
//				mSourceList.put(channelId, tempSource);
//				return true;
//			} catch (IOException e) {
//				e.printStackTrace();
//				return false;
//			}
//		}
//	}
//
//
//	/**
//	 * Update OpenAL channels position based on distance and angle.
//	 * If channel is not present in our sourcelist then nothing happens.
//	 * Use setChannelSource to make sure channel is present.
//	 * @param channelId - Which channel we want to update
//	 * @param distance - Distance in meters
//	 * @param angle - Angle in degrees
//	 */
////	@Override
//	public void updateChannel(int channelId, double distance, double angle) {
//		// Only calculate and set info if channel is set
//		if (mSourceList.containsKey(channelId)){
//	        double x = distance * Math.cos(Math.toRadians((-90) + angle));
//	        double y = distance * Math.sin(Math.toRadians((-90) + angle));
//			// We are working in 3D cartesian coordinates here thus gps x,y goes into x and z axes in OpenALRenderer.
//			mSourceList.get(channelId).setPosition((float)x, 0, (float)y);
//		}
//	}
//
//	/**
//	 * Update the OpenAL listeners orientation. Consider this object the same as our head.
//	 * @param angle - Angle in degrees
//	 */
////	@Override
//	public void setHeadOrientation(float angle) {
//		mOpenALRender.setListenerOrientation(angle);
//	}
//
//	/**
//	 * Play audio for channel
//	 * @param id - Which channel to play
//	 * @param loop - loop playback
//	 */
////	@Override
//	public void play(int id, boolean loop) {
//		if (mSourceList.containsKey(id)){
//			mSourceList.get(id).play(loop);
//		}
//	}
//
//	/**
//	 * Stop audio for channel
//	 * @param id - Which channel to stop
//	 */
////	@Override
//	public void stop(int id) {
//		if (mSourceList.containsKey(id)){
//			mSourceList.get(id).stop();
//		}
//	}
//
////	@Override
//	public void beacon(float volume, int directionality, boolean on) {
//		// TODO Auto-generated method stub
//
//	}



}
