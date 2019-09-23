package com.beeboxes.sdklocalserver;

import android.net.LocalSocket;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;

import static com.beeboxes.sdklocalserver.SocketHeader.PICTURE_PACKAGE_HEAD;


/**
 * 服务器接受数据
 */


class ReceiveRunnable implements Runnable {
    LocalSocket localSocket;
    LocalSocketCallback mLocalSocketCallback;

    public ReceiveRunnable(LocalSocket localSocket, LocalSocketCallback mLocalSocketCallback) {
        this.localSocket = localSocket;
        this.mLocalSocketCallback = mLocalSocketCallback;
    }

    @Override
    public void run() {
        while (true) {
            try {
                InputStream inputStream = localSocket.getInputStream();
                boolean isHead = true;
                //循环读取PICTURE_PACKAGE_HEAD.length个字节，并判断是否和我们定义的头相同
                for (int i = 0; i < PICTURE_PACKAGE_HEAD.length; ++i) {
                    byte head = (byte) inputStream.read();
                    //如果不相同，那么结束循环，并丢弃这个字节
                    if (head != PICTURE_PACKAGE_HEAD[i]) {
                        isHead = false;
                        break;
                    }
                }
                if (!isHead) {
                    return;
                }

                DataInputStream inputData = new DataInputStream(inputStream);

                long picLeng = inputData.readLong();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = -1;
                while (picLeng > 0
                        && (len = inputData.read(buffer, 0, picLeng < buffer.length ? (int) picLeng : buffer.length)) != -1) {
                    baos.write(buffer, 0, len);
                    baos.flush();
                    //每读取后，picLeng的值要减去len个，直到picLeng = 0
                    picLeng -= len;
                }
                baos.flush();
                byte[] toByteArray = baos.toByteArray();
//                    String cmd = new String(toByteArray);
                if (mLocalSocketCallback != null && toByteArray.length > 0) {
//                        sendData2PCUtil.reconnect();
//                        sendData2PCUtil.sendBytes("我是app".getBytes());
//                        sendData2PCUtil.sendBytes(toByteArray);
//                    replyClient(toByteArray);

                    mLocalSocketCallback.onReceiveData(toByteArray);

                }
            } catch (Exception e) {// 单次奔溃不影响整体服务
//                e.printStackTrace();
            }
        }
    }


}

