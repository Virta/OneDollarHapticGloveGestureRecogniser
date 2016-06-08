package com.aau.bluetooth.data;

import com.aau.bluetooth.BluetoothClient;



public abstract class LineDataListener
                            implements ILineDataListener {

    public static final int DEFAULT_BUF_LEN = 64;

    public static final byte LF = 0x0A;
    public static final byte CR = 0x0D;

    public static enum LineDelim { LINE_LF, LINE_LFCR, LINE_CRLF, LINE_CR };

    protected byte[] mBuf;
    protected int mBufLen;
    protected int mPos;
    protected LineDelim mLineDelim;

    public LineDataListener() {
        init(DEFAULT_BUF_LEN, LineDelim.LINE_LFCR);
    }

    public LineDataListener init(int bufLen, LineDelim lineDelim) {
        setBufLen(bufLen);
        mLineDelim = lineDelim;
        mPos = 0;

        return this;
    }

    @Override
    public int getBufLen() {
        return mBufLen;
    }

    @Override
    public void setBufLen(int bufLen) {
        mBufLen = bufLen;
        mBuf = new byte[mBufLen];
    }

    @Override
    public void setLineDelim(LineDelim lineDelim) {
        mLineDelim = lineDelim;
    }

    @Override
    public LineDelim getLineDelim() {
        return mLineDelim;
    }

    @Override
    public void onData(BluetoothClient connection, byte[] data, int len) {
        if (mPos + len >= mBufLen) {
            // [XXX: Automatically loop around. Is this a good approach?]
            mPos = 0;
        }
        if (len >= mBufLen) {
            //[XXX: skip this data]
            return;
        }

        System.arraycopy(data, 0, mBuf, mPos, len);
        mPos += len;
        switch (mLineDelim) {
            case LINE_CRLF:
                if (mPos > 2) {
                    if (mBuf[mPos - 2] == CR && mBuf[mPos - 1] == LF) {
                        // have a line
                        process(mBuf, mPos);
                        mPos = 0;
                    }
                }
                break;

            case LINE_LFCR:
                if (mPos > 2) {
                    if (mBuf[mPos - 2] == LF && mBuf[mPos - 1] == CR) {
                        // have a line
                        process(mBuf, mPos);
                        mPos = 0;
                    }
                }
                break;

            case LINE_CR:
                if (mPos > 1) {
                    if (mBuf[mPos - 1] == CR) {
                        // have a line
                        process(mBuf, mPos);
                        mPos = 0;
                    }
                }
                break;

            case LINE_LF:
                if (mPos > 1) {
                    if (mBuf[mPos - 1] == LF) {
                        // have a line
                        process(mBuf, mPos);
                        mPos = 0;
                    }
                }
                break;
        }
    }
}
