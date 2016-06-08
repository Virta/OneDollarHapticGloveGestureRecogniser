package com.aau.bluetooth.data;


public interface IDataPacket {
    public abstract void parsePacket(byte[] data, int len);
}
