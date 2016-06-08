package com.aau.bluetooth.android;

import android.os.Handler;

import com.aau.bluetooth.data.IDataPacket;
import com.aau.bluetooth.data.ILineDataListener;
import com.aau.bluetooth.data.ParsedLineDataListener;


public class UiParsedLineDataListener
                        extends ParsedLineDataListener
                        implements ILineDataListener, IUiDataListener {

    protected Handler mHandler;

    public UiParsedLineDataListener() {
        super();
    }

    public UiParsedLineDataListener(int bufLen, LineDelim lineDelim, IDataPacket dataPacket, Handler handler) {
        init(bufLen, lineDelim, dataPacket, handler);
    }

    public UiParsedLineDataListener init(int bufLen, LineDelim lineDelim, IDataPacket dataPacket, Handler handler) {
        super.init(bufLen, lineDelim, dataPacket);
        mHandler = handler;
        return this;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public void setHandler(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void process(byte[] data, int len) {
        super.process(data, len);

        // Send received parsed data object to the UI thread via the handler
        mHandler.obtainMessage(BluetoothAndroid.APPLICATION_DATA_RECEIVED, super.mParsedData).sendToTarget();
    }
}
