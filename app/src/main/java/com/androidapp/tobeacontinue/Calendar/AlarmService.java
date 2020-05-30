package com.androidapp.tobeacontinue.Calendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.androidapp.tobeacontinue.R;

public class AlarmService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public int onStartCommand(Intent intent, int flags, int startid) {
        //ForeGround에서 실행되면 Notification을 보여줘야 한다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelid = createNotificationChannel();

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelid);
            Notification notification = builder.setOngoing(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .build();

            startForeground(1, notification);

        }

        //알림창 호출
        Intent intent1=new Intent(this,AlarmRingActivity.class);

        //새로운 TASK를 생성해서 Activity를 최상위로 올림.
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent1);

        Log.d("AlarmService","Alarm");

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
           stopForeground(true);
        }

        stopSelf();
       return START_NOT_STICKY;


    }



    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(){

        String channelid = "Alarm";
        String channelName = getString(R.string.app_name);
        NotificationChannel channel = new NotificationChannel(channelid,channelName, NotificationManager.IMPORTANCE_NONE);
        channel.setSound(null,null);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);

        return channelid;
    }
}
