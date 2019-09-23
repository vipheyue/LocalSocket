package com.bee.localsocket.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.bee.localsocket.ILocalSocketAidlInterface;
import com.beeboxes.sdklocalserver.LocalSocketCallback;
import com.beeboxes.sdklocalserver.LocalSocketServer;
import com.beeboxes.sdklocalserver.SendData2PCUtil;


public class CommandAidlService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return ibind;
    }

    @Override
    public void onCreate() {

        final SendData2PCUtil sendData2PCUtil = SendData2PCUtil.instance();
        sendData2PCUtil.setCallback(new LocalSocketCallback() {
            @Override
            public void onReceiveData(byte[] buffer) {
                LocalSocketServer.getInstance().replyClient(buffer);
            }
        });
        sendData2PCUtil.reconnect();
        sendData2PCUtil.reconnect();
        listenReceive(sendData2PCUtil);
        super.onCreate();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private final ILocalSocketAidlInterface.Stub ibind = new ILocalSocketAidlInterface.Stub() {
        @Override
        public void startSocket() throws RemoteException {
        }

        @Override
        public void stopSocket() throws RemoteException {

        }
    };

    /**
     * 服务端收到客户端发来的数据
     */
    private void listenReceive(final SendData2PCUtil sendData2PCUtil) {
        LocalSocketServer.getInstance().listenReceive(new LocalSocketCallback() {
            @Override
            public void onReceiveData(final byte[] bytesBuffer) {
                sendData2PCUtil.reconnect();
                sendData2PCUtil.sendBytes(bytesBuffer);
//                    LocalSocketServer.getInstance().replyClient(bytesBuffer);// TODO: 2019/4/9 测试代码 需要去除

            }
        });
    }

}
