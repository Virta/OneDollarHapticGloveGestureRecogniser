package com.aau.bluetooth.cultar.glove;

import com.aau.bluetooth.cultar.glove.android.Glove;


public interface IGloveFingerListener {
    public void onFinger(Glove glove, int finger, int bend);
}
