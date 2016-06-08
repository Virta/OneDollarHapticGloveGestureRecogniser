package com.aau.bluetooth.data;



public class LineCmdPacket extends CmdPacket {
    public static final short LF = 0x0A;
    public static final short CR = 0x0D;

    private boolean mTerminated;

    public LineCmdPacket(int len) {
        super(len, len + 2);
    }

    public LineCmdPacket(int minLen, int maxLen) {
        super(minLen, maxLen + 2);
    }

    @Override
    public void clear() {
        super.clear();
        mTerminated = false;
    }

    @Override
    public byte[] getData() {
        if (!mTerminated) {
            // Add the packet terminating characters at the current position
            mBuffer[mPos++] = CR;
            mBuffer[mPos++] = LF;
            mTerminated = true;
        }

        // Transfer the values to the byte array of the right length
        //[TODO: possibly "cache" this new byte array?]
        byte[] bytes = new byte[mPos];
        for (int i=0; i<mPos; i++) {
            bytes[i] = mBuffer[i].byteValue();
        }
        return bytes;
    }
}

