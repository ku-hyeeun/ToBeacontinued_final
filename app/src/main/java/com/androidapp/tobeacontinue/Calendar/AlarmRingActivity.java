package com.androidapp.tobeacontinue.Calendar;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import com.androidapp.tobeacontinue.R;

//알람종료
public class AlarmRingActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmclose);

        //알림음 재생
        this.mediaPlayer= MediaPlayer.create(this, R.raw.alarm);
        this.mediaPlayer.start();

        findViewById(R.id.btnClose).setOnClickListener(mClickListener);

    }


    protected void onDestroy(){
        super.onDestroy();


        if(this.mediaPlayer != null){
            this.mediaPlayer.release();
            this.mediaPlayer=null;
        }
    }


    //알람종료
    private  void close(){
        if(this.mediaPlayer.isPlaying()){
            this.mediaPlayer.stop();
            this.mediaPlayer.release();
            this.mediaPlayer=null;

        }

        // finish();
    }



    View.OnClickListener mClickListener=new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btnClose:
                    //알람종료
                    finish();
                    break;


            }
        }
    };

}
