package com.beeboxes.sdklocalserver;

import android.text.TextUtils;
import android.util.Log;

import com.beeboxes.device.serial.transport.ILog;

public class MyLog implements ILog {
    private static final String TAG = "SerailSdk";
    @Override
    public void log(String msg) {
        if (!TextUtils.isEmpty(msg)) {
            Log.d(TAG, msg);
        }
    }
}
