package com.aau.bluetooth.data;



public interface ITypedDataListener extends IDataListener {
    public abstract char getType();
    public abstract void setType(char type);
}

