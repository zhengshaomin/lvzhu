package Min.app.plus.application;
import static Min.app.plus.utils.QueryBasisInfoUtils.getstoreinfo;
import static Min.app.plus.utils.QueryBasisInfoUtils.getuserinfo;
import static Min.app.plus.utils.QueryBasisInfoUtils.storeobjectid;
import static Min.app.plus.utils.QueryBasisInfoUtils.userobjectid;
import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.gson.Gson;
import org.json.JSONObject;

import Min.app.plus.MainActivity;
import Min.app.plus.R;
import Min.app.plus.bmob._User;
import Min.app.plus.utils.SoundPoolUtil;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.ai.BmobAI;
import cn.bmob.v3.realtime.Client;
import cn.bmob.v3.realtime.RealTimeDataListener;
import cn.bmob.v3.realtime.RealTimeDataManager;

/**
 * 作者：daboluo on 2023/8/21 16:50
 * Email:daboluo719@gmail.com
 */
public class App extends Application implements Application.ActivityLifecycleCallbacks{
    public static _User userInfo;
    public static JSONObject monitor_reply_data,monitor_chat_data,monitor_order_data,monitor_order_store;//评论，聊天，订单
    public static SoundPoolUtil instance;
    public static String TAG="App";
    public static int notificationid =10000;
    public static BmobAI bmobAI;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);
            getlogininfo();
        }};
    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);//禁止夜间模式

        registerActivityLifecycleCallbacks(this);
        //初始化数据
        Bmob.initialize(this, "appkey");
        Bmob.resetDomain("修改成自己的api域名");
        Bmob.resetDomain("修改成自己的sdk域名");
        //初始化AI（初始化时，会自动创建一个websocket，保持心跳连接，确保实时回复）
        bmobAI = new BmobAI();
        instance = SoundPoolUtil.getInstance(getApplicationContext());
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message msg = new Message();
                handler.sendMessage(msg);
            }
        }.start();
    }
    //查询是否登陆
    private void getlogininfo(){
        userInfo = _User.getCurrentUser(_User.class);//登陆信息
        if(userInfo!=null){
            //有登陆信息
            getuserinfo();//查询登陆信息
            getstoreinfo();//查询店铺信息
            monitor_reply();//监听评论消息
            monitor_chat();//监听聊天消息
            monitor_order();//监听订单消息
        }

    }
    //监听评论消息
    public void monitor_reply(){
        RealTimeDataManager.getInstance().start(new RealTimeDataListener() {
            @Override
            public void onConnectCompleted(Client client, Exception e) {
                if (e == null) {
                    //TODO 如果已连接，设置监听动作为：监听Chat表的更新
                    client.subTableUpdate("Reply");//
                    // }
                } else {
                    monitor_reply();
                }
            }
            @Override
            public void onDataChange(Client client, JSONObject jsonObject) {
                Gson gson = new Gson();
                String action = jsonObject.optString("action");
                String jsonString = gson.toJson(jsonObject);
                if (action.equals(Client.ACTION_UPDATE_TABLE)) {
                    //TODO 如果监听表更新
                    monitor_reply_data = jsonObject.optJSONObject("data");
                    //Toast.makeText(MainActivity.this, action+"监听到更新：" + data.optString("recipient"), Toast.LENGTH_SHORT).show();
                    if(userobjectid.equals(monitor_reply_data.optString("recipient"))){
                        instance.play(1);
                        Log.d(TAG,"监听");
                        sendNotification( "你有一条评论未读", monitor_reply_data.optString("content"));
                        //EventBus.getDefault().post(new MyEvent("Reply",monitor_reply_data.optString("recipient")));//发送消息
                    }
                }
            }
            @Override
            public void onDisconnectCompleted(Client client) {

            }
        });
    }
    //监听聊天消息
    public void monitor_chat(){
        RealTimeDataManager.getInstance().start(new RealTimeDataListener() {
            @Override
            public void onConnectCompleted(Client client, Exception e) {
                if (e == null) {
                    //TODO 如果已连接，设置监听动作为：监听Chat表的更新
                    client.subTableUpdate("Chat");//
                    // }
                } else {
                    monitor_chat();
                }
            }
            @Override
            public void onDataChange(Client client, JSONObject jsonObject) {
                Gson gson = new Gson();
                String action = jsonObject.optString("action");
                String jsonString = gson.toJson(jsonObject);
                if (action.equals(Client.ACTION_UPDATE_TABLE)) {
                    //TODO 如果监听表更新
                    monitor_chat_data = jsonObject.optJSONObject("data");
                    //Toast.makeText(MainActivity.this, action+"监听到更新：" + data.optString("recipient"), Toast.LENGTH_SHORT).show();
                    if(userobjectid.equals(monitor_chat_data.optString("msg_recipient"))){
                        Log.d(TAG,"郑绍敏");
//                        Toast.makeText(MainActivity.this, "有消息", Toast.LENGTH_SHORT).show();
                        instance.play(1);
                        sendNotification( "你有一条私信未读", monitor_chat_data.optString("msg_content"));

                    }
                }
            }
            @Override
            public void onDisconnectCompleted(Client client) {

            }
        });
    }
    //监听店铺消息
    public void monitor_order(){
        RealTimeDataManager.getInstance().start(new RealTimeDataListener() {
            @Override
            public void onConnectCompleted(Client client, Exception e) {
                if (e == null) {
                    //TODO 如果已连接，设置监听动作为：监听Chat表的更新
                    client.subTableUpdate("Order");//
                    // }
                } else {
                    monitor_order();
                }
            }
            @Override
            public void onDataChange(Client client, JSONObject jsonObject) {
                Gson gson = new Gson();
                String action = jsonObject.optString("action");
                String jsonString = gson.toJson(jsonObject);
                if (action.equals(Client.ACTION_UPDATE_TABLE)) {
                    //TODO 如果监听表更新
                    monitor_order_data = jsonObject.optJSONObject("data");
                    //Toast.makeText(MainActivity.this, action+"监听到更新：" + data.optString("recipient"), Toast.LENGTH_SHORT).show();
//                    if(userobjectid.equals(monitor_order_data.optString("consumer"))){
////                        Toast.makeText(MainActivity.this, "有消息", Toast.LENGTH_SHORT).show();
//                        instance.play(1);
//                        sendNotification( "你收到一条订单", monitor_order_data.optString("state"));
//                    }else
                        if(storeobjectid.equals(monitor_order_data.optString("store"))){
                        instance.play(1);
                        sendNotification( "你有一笔订单", monitor_order_data.optString("state"));
                    }
                }
            }
            @Override
            public void onDisconnectCompleted(Client client) {

            }
        });

    }
    public void sendNotification(String title,String message) {
        notificationid=notificationid+1;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
//        // 1. 创建一个通知(必须设置channelId)
                            Notification notification = new Notification.Builder(this, notificationid + "")
                                    .setContentTitle(title)
                                    .setContentText(message)
                                    .setWhen(System.currentTimeMillis())
                                    .setSmallIcon(R.drawable.diablos)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.drawable.diablos))
                .setContentIntent(pendingIntent) // 设置点击通知时的操作

                .setAutoCancel(true)
                .build();
        // 2. 获取系统的通知管理器
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        // 3. 创建NotificationChannel(这里传入的channelId要和创建的通知channelId一致，才能为指定通知建立通知渠道)
        NotificationChannel channel = new NotificationChannel(notificationid+"","测试渠道名称", android.app.NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        // 4. 发送通知
        notificationManager.notify(notificationid, notification);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // 当一个新的 Activity 被创建时，此方法会被调用
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // Activity 开始时调用
    }

    @Override
    public void onActivityResumed(Activity activity) {
        // Activity 恢复时调用
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // Activity 暂停时调用
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // Activity 停止时调用
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // Activity 保存状态时调用
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // Activity 被销毁时调用
    }
}
