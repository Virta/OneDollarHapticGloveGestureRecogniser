package com.aau.bluetooth.cultar.harness;

import com.aau.bluetooth.BluetoothUtils;
import com.aau.bluetooth.data.IDataPacket;

public class HarnessErrorCodePacket implements IDataPacket{
	public static final int INDEX_ERROR = 1;

	public static final char TYPE = 'E';
	
	public int ECODE;

	
	public void parsePacket(byte[] data, int len) {
//		Log.d("ASDF", Arrays.toString(data));
		ECODE = BluetoothUtils.deserializeUint8(data, INDEX_ERROR);
	}

	public String toString() {
//		StringBuffer ret = new StringBuffer(TYPE);
//		ret.append("[");
//		ret.append("ECODE:").append(ECODE);
//		ret.append("]");
//		return ret.toString();
		return ErrorCodeText.getDescription(ECODE);
	}
}
