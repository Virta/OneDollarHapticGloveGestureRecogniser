package com.aau.bluetooth.cultar.harness.android;

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
import com.aau.bluetooth.cultar.harness.HarnessCmdPacket;
import com.aau.bluetooth.cultar.harness.HarnessErrorCodePacket;
import com.aau.bluetooth.cultar.harness.HarnessImuDataPacket;
import com.aau.bluetooth.cultar.harness.HarnessPhysiologicalDataPacket;
import com.aau.bluetooth.data.LineDataListener;
import com.aau.bluetooth.data.LineDataListener.LineDelim;

public class Harness {
	public static final String TAG = "Harness";

	private static final int DEFAULT_NUM_RETRIES = 3;
	private static final long DEFAULT_RETRY_INTERVAL = 2000;

//	private Logger mLogger;

	private String mBluetoothName;
	private BluetoothClient mBluetoothClient;
	private UiBluetoothReadyStateListener mBluetoothReadyStateListener;
	private IUiDataListener mDataListener;

	private HarnessCmdPacket mCmdPacket;
	private HarnessImuDataPacket mImuPacket;
	private HarnessPhysiologicalDataPacket mPhysPacket;
	private HarnessErrorCodePacket mErrorPacket;

	private Handler mUiHandler;

	public Harness() {};
	public void init(IBluetooth bluetooth, String bluetoothName) throws BluetoothException {
		mBluetoothName = bluetoothName;
//		mLogger = logger;

		mCmdPacket = new HarnessCmdPacket();
		mImuPacket = new HarnessImuDataPacket();
		mPhysPacket = new HarnessPhysiologicalDataPacket();
		mErrorPacket = new HarnessErrorCodePacket();

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
		}.init(LineDataListener.DEFAULT_BUF_LEN, LineDelim.LINE_CRLF, mImuPacket, mUiHandler);

		// Find the device from already paired devices
		IBluetoothDevice vestDevice = bluetooth.getPairedDeviceByName(mBluetoothName);

		if (vestDevice == null) {
			/* [TODO: how to handle?] */
			throw new BluetoothException("No paired device found for id: " + mBluetoothName);
		}

		// Connection object
		mBluetoothClient = new BluetoothClient(vestDevice, IBluetooth.RFCOMM_UUID, DEFAULT_NUM_RETRIES, DEFAULT_RETRY_INTERVAL);

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

}
