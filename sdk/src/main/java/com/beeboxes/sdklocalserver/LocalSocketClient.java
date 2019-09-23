package com.beeboxes.sdklocalserver;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.Executors;

import static com.beeboxes.sdklocalserver.SocketHeader.PICTURE_PACKAGE_HEAD;

public class LocalSocketClient {

    private LocalSocket localSocket;

    private LocalSocketClient() {
        connectRemote();
    }

    public boolean connectRemote() {
        LocalSocket localSocket = new LocalSocket();
        boolean connected = true;
        if (!localSocket.isConnected()) {
            try {
                localSocket.connect(new LocalSocketAddress(LocalSocketServer.LOCALSOCKET_ADDRESS, LocalSocketAddress.Namespace.ABSTRACT));
            } catch (IOException e) {
                e.printStackTrace();
                connected = false;
            }
        }
        this.localSocket = localSocket;
        return connected;
    }

    private static LocalSocketClient mLocalSocketClient;

    public static LocalSocketClient getInstance() {
        if (mLocalSocketClient == null) {
            synchronized (LocalSocketClient.class) {
                if (mLocalSocketClient == null)
                    mLocalSocketClient = new LocalSocketClient();
            }
        }
        return mLocalSocketClient;
    }

    public void listenReceive(LocalSocketCallback mLocalSocketCallback) {
        Executors.newSingleThreadExecutor().execute(new ReceiveRunnable(localSocket, mLocalSocketCallback));
    }

    public void sendInfo(final byte[] sendBytes) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 发送给服务端
                    OutputStream outputStream = localSocket.getOutputStream();
                    localSocket.setSoTimeout(3000);
//                    outputStream.write(sendBytes);
//                    outputStream.flush();
//                    localSocket.shutdownOutput();


                    DataOutputStream dout = new DataOutputStream(outputStream);
                    dout.write(PICTURE_PACKAGE_HEAD);// 发送协议头
                    dout.writeLong(sendBytes.length);
                    dout.write(sendBytes);
                    dout.flush();


//                    outputStream.write("你好".getBytes());
//                    outputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }


}
