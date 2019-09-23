package com.beeboxes.sdklocalserver;

import android.net.LocalServerSocket;
import android.net.LocalSocket;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.beeboxes.sdklocalserver.SocketHeader.PICTURE_PACKAGE_HEAD;


public class LocalSocketServer {

    static final String LOCALSOCKET_ADDRESS = "beeboxes_localsocket";
    private LocalServerSocket mLocalServerSocket;
    private ExecutorService receiverThread;
    private ArrayList<LocalSocket> clients = new ArrayList<LocalSocket>();

    private LocalSocketServer() {
        try {
            mLocalServerSocket = new LocalServerSocket(LOCALSOCKET_ADDRESS);

        } catch (IOException e) {
            e.printStackTrace();
        }
        receiverThread = Executors.newFixedThreadPool(20);


    }

    private static LocalSocketServer mLocalSocketServer;

    public static LocalSocketServer getInstance() {
        if (mLocalSocketServer == null) {
            synchronized (LocalSocketServer.class) {
                if (mLocalSocketServer == null)
                    mLocalSocketServer = new LocalSocketServer();
            }
        }
        return mLocalSocketServer;
    }

    public void listenReceive(final LocalSocketCallback mLocalSocketCallback) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    LocalSocket localSocket = null;
                    // 这个方法是阻塞的,有Client连接上来的时候,这里就会回调.
                    try {
                        localSocket = mLocalServerSocket.accept();
                        localSocket.setSoTimeout(5000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    clients.add(localSocket);
                    receiverThread.execute(new ReceiveRunnable(localSocket, mLocalSocketCallback)); // 当有新的socket连上来之后 启动新的线程单独处理
                }
            }
        });

    }

    /**
     * 服务器发送数据到客户端
     *
     * @param sendBytes
     */
    public void replyClient(final byte[] sendBytes) {

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                for (LocalSocket localSocket : clients) {
                    try {
                        // 发送给所有客户端
                        OutputStream outputStream = localSocket.getOutputStream();
                        localSocket.setSoTimeout(3000);
                        DataOutputStream dout = new DataOutputStream(outputStream);
                        dout.write(PICTURE_PACKAGE_HEAD);// 发送协议头
                        dout.writeLong(sendBytes.length);
                        dout.write(sendBytes);
                        dout.flush();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
