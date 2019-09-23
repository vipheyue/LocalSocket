package com.beeboxes.clientlocalservre;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bee.localsocket.ILocalSocketAidlInterface;
import com.beeboxes.sdklocalserver.LocalSocketCallback;
import com.beeboxes.sdklocalserver.LocalSocketClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity {
    private boolean bindFlag;
    private ILocalSocketAidlInterface myAIDLService;
    private TextView tv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        connectAidlServer();
    }

    private void initView() {
        tv_show = findViewById(R.id.tv_show);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LocalSocketClient.getInstance().replyClient(mTestLocalSocket);// aidl收到指令发送数据到loca socket 服务端
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.ic_launcher_background);

                try {
                    File file = new File("/data/data/com.beeboxes.clientlocalservre/NewFolder/111.png");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[1024 * 10];
                    int len = 0;
                    while ((len = fis.read(buffer)) != -1) {
                        baos.write(buffer, 0, len);
                        baos.flush();
                    }
                    byte[] toByteArray = baos.toByteArray();
//                    LocalSocketClient.getInstance().sendInfo(toByteArray);// 送数据到loca socket 服务端
                    LocalSocketClient.getInstance().sendInfo("你好".getBytes());// 发送数据到loca socket 服务端
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void connectAidlServer() {
        Intent intent = new Intent();
        intent.setAction("com.bee.localsocket.server.CommandAidlService");
        // 从 Android 5.0开始 隐式Intent绑定服务的方式已不能使用,所以这里需要设置Service所在服务端的包名
        intent.setPackage("com.bee.localsocket");
        bindFlag = bindService(intent, connection, BIND_AUTO_CREATE);// 启动命令服务 aidl
    }


    /**
     * 一定要在socket连接成功后才注册监听
     */
    private void registerCallBack() {
        LocalSocketClient.getInstance().listenReceive(new LocalSocketCallback() {

            @Override
            public void onReceiveData(final byte[] bytesBuffer) {

//                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytesBuffer,0,bytesBuffer.length);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "客户端 bytesBuffer.length:" + bytesBuffer.length, Toast.LENGTH_SHORT).show();

//                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                        imageView.setImageBitmap(bitmap);
                    }
                });
            }
        });
    }
    @Override
    protected void onDestroy() {
        if (bindFlag) {
            unbindService(connection);
        }
        super.onDestroy();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myAIDLService = com.bee.localsocket.ILocalSocketAidlInterface.Stub.asInterface(service);
            try {
                myAIDLService.startSocket();
                registerCallBack();

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            myAIDLService = null;
        }
    };
}
