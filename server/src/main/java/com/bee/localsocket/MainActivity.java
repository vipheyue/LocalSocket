package com.bee.localsocket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.beeboxes.sdklocalserver.LocalSocketCallback;
import com.beeboxes.sdklocalserver.LocalSocketServer;
import com.beeboxes.sdklocalserver.SendData2PCUtil;

public class MainActivity extends AppCompatActivity {

    private TextView tv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.bee.localsocket.R.layout.activity_main);
        final SendData2PCUtil sendData2PCUtil = SendData2PCUtil.instance();
        sendData2PCUtil.reconnect();

        tv_show = findViewById(R.id.tv_show);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendData2PCUtil.reconnect();
                sendData2PCUtil.sendBytes("我是app".getBytes());
            }
        });
        // 服务端自己会启动aidl服务   CommandAidlService

//        listenReceive(sendData2PCUtil);

    }


    /**
     * 服务端收到的数据
     */
    private void listenReceive(final SendData2PCUtil sendData2PCUtil) {
        LocalSocketServer.getInstance().listenReceive(new LocalSocketCallback() {
            @Override
            public void onReceiveData(final byte[] bytesBuffer) {

//                SendData2PCUtil sendData2PCUtil = new SendData2PCUtil();
//                sendData2PCUtil.reconnect();
//                sendData2PCUtil.sendBytes(bytesBuffer);

                LocalSocketServer.getInstance().replyClient(bytesBuffer);// TODO: 2019/4/9 测试代码 需要去除
                final Bitmap bitmap = BitmapFactory.decodeByteArray(bytesBuffer, 0, bytesBuffer.length);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "bytesBuffer.length:" + bytesBuffer.length, Toast.LENGTH_SHORT).show();
                        ImageView imageView = (ImageView) findViewById(R.id.imageView);
                        imageView.setImageBitmap(bitmap);
                    }
                });

            }
        });
    }


}
