package com.androidapp.tobeacontinue.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
       Intent sintent=new Intent(context,AlarmService.class);

       //Oreo 버전 이후부터는 background에서 실행을 금지하기 때문에 ForeGround에서 실행해야함
       if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
           context.startForegroundService(sintent);
       }
       else{
           context.startService(sintent);
       }

    }
}
