package com.aau.bluetooth;


public interface IInstrumentationListener {
    public void updateRead(long timestamp, long byteCount);
    public void updateWrite(long timestamp, long byteCount);
}

