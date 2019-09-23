# LocalSocket
localsocket 双向连续发送文件

* 加入依赖 implementation project(':sdk')
* AIDL bindService 连上远程app service

    


* socket接受数据(需要在连上AIDL 连上socket之后调用)
    * 连接上AIDL之后 在onServiceConnected 函数里面注册 socket 注册监听 实例化时会自动连接上socket
        >    LocalSocketClient.getInstance().listenReceive(new LocalSocketCallback() {}
* socket发送数据(需要在连上AIDL 连上socket之后调用)
    *                     LocalSocketClient.getInstance().sendInfo("你好".getBytes());// 发送数据到loca socket 服务端




* 串口发送数据与接受数据
```
        final SendData2PCUtil sendData2PCUtil = SendData2PCUtil.instance();
                sendData2PCUtil.reconnect();
                sendData2PCUtil.sendBytes(bytes);


```

* 串口接受数据
```
  sendData2PCUtil.setCallback(new LocalSocketCallback() {
            @Override
            public void onReceiveData(byte[] buffer) {
            }
        });
```


DEMO [https://github.com/vipheyue/LocalSocket](https://github.com/vipheyue/LocalSocket)
