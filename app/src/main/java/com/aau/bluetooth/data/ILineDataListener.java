package com.aau.bluetooth.data;

import com.aau.bluetooth.data.LineDataListener.LineDelim;

public interface ILineDataListener extends IDataListener {
    public abstract int getBufLen();
    public abstract void setBufLen(int bufLen);
    public abstract LineDelim getLineDelim();
    public abstract void setLineDelim(LineDelim lineDelim);
}

