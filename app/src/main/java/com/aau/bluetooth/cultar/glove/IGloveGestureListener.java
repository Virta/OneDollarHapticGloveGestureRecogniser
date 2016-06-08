package com.aau.bluetooth.cultar.glove;

import com.aau.bluetooth.cultar.glove.android.Glove;
import com.aau.bluetooth.cultar.glove.android.Glove.GloveGesture;

public interface IGloveGestureListener {
    public void onGesture(Glove glove, GloveGesture gesture);
}
