package com.aau.bluetooth.data;




public class TypedParsedLineDataListener extends ParsedLineDataListener {
    protected char mType;

    public TypedParsedLineDataListener() {
        super();
    }

    public TypedParsedLineDataListener(int bufLen, LineDelim lineDelim, IDataPacket dataPacket, char type) {
        init(bufLen, lineDelim, dataPacket, type);
    }
    
    public TypedParsedLineDataListener init(int bufLen, LineDelim lineDelim, IDataPacket dataPacket, char type) {
        super.init(bufLen, lineDelim, dataPacket);
        mParsedData = dataPacket;
        return this;
    }

    @Override
    public void process(byte[] data, int len) {
        if (data[0] == mType) {
            super.process(data, len);
        }
    }
}
