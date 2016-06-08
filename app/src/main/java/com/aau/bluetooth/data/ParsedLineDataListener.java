package com.aau.bluetooth.data;




public class ParsedLineDataListener
                        extends LineDataListener
                        implements ILineDataListener {

    protected IDataPacket mParsedData;

    public ParsedLineDataListener() {
        super();
    }

    public ParsedLineDataListener(int bufLen, LineDelim lineDelim, IDataPacket dataPacket) {
        init(bufLen, lineDelim, dataPacket);
    }

    public ParsedLineDataListener init(int bufLen, LineDelim lineDelim, IDataPacket dataPacket) {
        super.init(bufLen, lineDelim);
        mParsedData = dataPacket;
        return this;
    }

    public IDataPacket getDataPacket() {
        return mParsedData;
    }

    public void setDataPacket(IDataPacket dataPacket) {
        mParsedData = dataPacket;
    }

    @Override
    public void process(byte[] data, int len) {
        mParsedData.parsePacket(data, len);
    }
}
