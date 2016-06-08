package com.aau.bluetooth.android;

import android.os.Handler;

import com.aau.bluetooth.data.IDataListener;

public interface IUiDataListener extends IDataListener {
    public abstract Handler getHandler();
    public abstract void setHandler(Handler handler);
}

