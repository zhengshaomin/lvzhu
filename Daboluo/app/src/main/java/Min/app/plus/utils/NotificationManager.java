package Min.app.plus.utils;

import static android.content.Context.NOTIFICATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;
import static Min.app.plus.application.App.monitor_chat_data;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import org.json.JSONObject;

import Min.app.plus.MainActivity;
import Min.app.plus.R;
import Min.app.plus.StartActivity;

/**
 * 作者：daboluo on 2023/8/23 23:18
 * Email:daboluo719@gmail.com
 */
public class NotificationManager {
    public static int notificationid =10000;

    public static void replynotification(Context context,String title,String message) {
        notificationid=notificationid+1;
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//        // 1. 创建一个通知(必须设置channelId)
        Notification notification = new Notification.Builder(context, notificationid + "")
                .setContentTitle(title)
                .setContentText(message)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.diablos)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.diablos))
                .setContentIntent(pendingIntent) // 设置点击通知时的操作

                .setAutoCancel(true)
                .build();
        // 2. 获取系统的通知管理器
        android.app.NotificationManager notificationManager = (android.app.NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        // 3. 创建NotificationChannel(这里传入的channelId要和创建的通知channelId一致，才能为指定通知建立通知渠道)
        NotificationChannel channel = new NotificationChannel(notificationid+"","测试渠道名称", android.app.NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);
        // 4. 发送通知
        notificationManager.notify(notificationid, notification);
    }
}
