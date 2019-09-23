package com.beeboxes.sdklocalserver;


public interface LocalSocketCallback {
    void onReceiveData(byte[] buffer);
}
