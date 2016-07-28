package com.bush.android24_messenagerwordclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "--------";
    private TextView tv_result;
    private EditText editQuery;
    private Messenger messenger;
    private Messenger messenger_reply;
    private ServiceConnection conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        bindMyService();
        initMessenagerReply();
    }
    //初始化信使的返回对象，并将结果放在TextView控件上。
    private void initMessenagerReply() {
        Handler handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what){
                    case 1:
                        Bundle bundle=msg.getData();
                        String info=bundle.getString("info");
                        Log.i(TAG, "handleMessage: "+info);
                        tv_result.setText(info);
                        break;
                }
            }
        };
        //初始化信使的返回对象，要用handler机制
        messenger_reply=new Messenger(handler);
    }

    private void initView() {
        tv_result= (TextView) findViewById(R.id.text_main_result);
        editQuery= (EditText) findViewById(R.id.edit_main_query);

    }
    //绑定服务
    private void bindMyService() {
        //设置意图对象，
        Intent intent=new Intent();
        //服务所在的包
        intent.setPackage("com.bush.android24_messengerwordservice");
        //服务的标志
        intent.setAction("com.bush.android24_messengerwordservice.service.WordSearchService");
        conn=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                messenger=new Messenger(service);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                messenger=null;
            }
        };
       boolean flag= bindService(intent,conn,BIND_AUTO_CREATE);
        Log.i(TAG, "bindMyService: "+flag);
    }
    public void clickView(View view){
        switch (view.getId()){
            case R.id.btn_main_hit:
                sendMessenager();
                break;
        }
    }
    //发送信使
    private void sendMessenager() {
        try {
            Log.i(TAG, "sendMessenager: ");
            Message message=Message.obtain();
            message.what=0;
            Bundle bundle=new Bundle();
            bundle.putString("info", editQuery.getText() + "");
            message.setData(bundle);
            //确保发送和接受的消息在同一个地方
            message.replyTo=messenger_reply;
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
