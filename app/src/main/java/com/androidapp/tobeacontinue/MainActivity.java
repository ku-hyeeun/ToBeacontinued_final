package com.androidapp.tobeacontinue;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //스플래쉬 화면 첫번째 켜질때 뜨는 화면

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Handler hd = new Handler();
        hd.postDelayed(new splashhandler(), 2000);  //2초 뒤 화면 변함

    }

    private class splashhandler implements Runnable{
        public void run(){
            startActivity(new Intent(getApplication(), MenuActivity.class));
            MainActivity.this.finish(); // 로딩페이지 Activity stack에서 제거
        }
    }
}
