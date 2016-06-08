package com.aau.bluetooth.android;

import android.os.Handler;

import com.aau.bluetooth.data.IDataPacket;
import com.aau.bluetooth.data.ILineDataListener;
import com.aau.bluetooth.data.ITypedDataListener;


public class TypedUiParsedLineDataListener
                extends UiParsedLineDataListener
                implements ILineDataListener, IUiDataListener, ITypedDataListener
{
    protected char mType;

    public TypedUiParsedLineDataListener() {
        super();
    }

    public TypedUiParsedLineDataListener(int bufLen, LineDelim lineDelim, IDataPacket dataPacket, Handler handler, char type) {
        init(bufLen, lineDelim, dataPacket, handler, type);
    }

    public TypedUiParsedLineDataListener init(int bufLen, LineDelim lineDelim, IDataPacket dataPacket, Handler handler, char type) {
        super.init(bufLen, lineDelim, dataPacket, handler);
        mType = type;
        return this;
    }

    @Override
    public char getType() {
        return mType;
    }

    @Override
    public void setType(char type) {
        mType = type;
    }

    @Override
    public void process(byte[] data, int len) {
        if (data[0] == mType) {
            super.process(data, len);
        }
    }
}
