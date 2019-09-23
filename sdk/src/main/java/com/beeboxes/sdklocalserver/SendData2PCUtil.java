package com.beeboxes.sdklocalserver;

import com.beeboxes.device.serial.SerialPort;
import com.beeboxes.device.serial.transport.Command;
import com.beeboxes.device.serial.transport.IDataListener;
import com.beeboxes.device.serial.transport.IDataTransCallback;
import com.beeboxes.device.serial.transport.IFileReceiveListener;
import com.beeboxes.device.serial.transport.IFileTransCallback;
import com.beeboxes.device.serial.transport.SerialManager;

import java.io.File;

public class SendData2PCUtil {
    private MyLog mLoger = new MyLog();
    private final static String DEV_PATH = "/dev/";
    private SerialManager mSerialManager;
    private Boolean mFirstStart = true;
    private String mLastPort = null;
    private LocalSocketCallback mLocalSocketCallback;
    private static SendData2PCUtil  mInstance;



    public void setCallback(LocalSocketCallback mLocalSocketCallback) {
        this.mLocalSocketCallback = mLocalSocketCallback;
    }


    public static SendData2PCUtil instance() {
        if (mInstance == null) {
            synchronized (SendData2PCUtil.class) {
                if (mInstance == null)
                    mInstance = new SendData2PCUtil();
            }
        }
        return mInstance;
    }

    private SendData2PCUtil() {
        SerialManager.installLogger(mLoger);
        SerialManager.setDebugable(true);
    }

    public void reconnect() {
        String serialPath = DEV_PATH + "ttyUSB2";// default port
//        String property = SettingManagerUtils.getProperty(SettingManagerUtils.RECORD_DATA2PC_PORT);        //todo 用变量获取
        String property = null;
        if (property != null && !property.equals(mLastPort)) {// Access to the port changes after restart serial port
            mFirstStart = true;
            serialPath = DEV_PATH + property;
        }
        mLastPort = property;
        if (!mFirstStart) {
            return;
        }
        mFirstStart = false;// The first initialization
        int baudRate = 115200;
        if (mSerialManager != null) {// Stop switch port, before transmission
            mSerialManager.stopTransfer();
        }
        SerialPort serial = new SerialPort(serialPath, baudRate);
        mSerialManager = new SerialManager(serial);
        try {
            mSerialManager.startTransfer();
            mSerialManager.registerDataListener(mDataListener);
            mSerialManager.registerFileReceiveListener(fileReceiveListener);

        } catch (Exception e) {
            e.printStackTrace();
            mFirstStart = true;
        }

    }

    public void sendBytes(final byte[] buffer) {
        mSerialManager.sendAsync(buffer, 0, buffer.length, new IDataTransCallback() {
            @Override
            public void onFinished(Command.Status status) {
            }
        });

    }

    public void sendFiles(final File file) {
        mSerialManager.sendFile(file.getPath(), new IFileTransCallback() {
            long start;

            @Override
            public void onStarted(int id) {
                mLoger.log("onStarted");
                start = System.currentTimeMillis();
            }

            @Override
            public void onProgress(int id, final int progress) {
                mLoger.log("已经传输 onProgress " + progress);
            }

            @Override
            public void onFinish(int id, Command.Status status) {
                mLoger.log("onFinish " + status);
                long cost = (System.currentTimeMillis() - start);

                long seconds = cost / 1000;
                long mill = cost % 1000;

                if (cost == 0) {
                    cost = 1;
                }

                long speed = file.length() * 1000 / cost;

                final StringBuilder sb = new StringBuilder();
                sb.append("!!! " + file.getName());
                sb.append(" 传输完成, 用时: ");
                sb.append(seconds).append("秒");
                sb.append(mill).append("毫秒");
                if (speed > 1000) {
                    sb.append(" 平均速度:").append(speed / 1000).append(".").append(speed % 1000);
                    sb.append("B/S");
                } else {
                    sb.append(" 平均速度:").append(speed);
                    sb.append("B/S");
                }

                mLoger.log("onFinish " + sb.toString());
            }
        });

    }


    /**
     * byte接收
     */
    IDataListener mDataListener = new IDataListener() {
        @Override
        public void onDataReady(byte[] buffer, int offset, int len) {
            if (len > 0) {
                byte[] data = new byte[len];
                System.arraycopy(buffer, 0, data, 0, len);
                String cmd = new String(buffer, offset, len);
                mLoger.log(cmd);

                if (mLocalSocketCallback != null) {
                    mLocalSocketCallback.onReceiveData(buffer);
                }

            }
        }
    };

    IFileReceiveListener fileReceiveListener = new IFileReceiveListener() {
        @Override
        public void onStarted(final String path) {
            mLoger.log("正在接收:" + path);
        }

        @Override
        public void onProgress(final String path, final int progress) {
            mLoger.log("已经接收:　" + progress + "%");
        }

        @Override
        public void onFinish(String path, Command.Status status) {
            mLoger.log("接收完成");
        }
    };

}
