package com.androidapp.tobeacontinue.Todolist;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidapp.tobeacontinue.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MemoWrite extends AppCompatActivity  {

    private EditText edtText;       //내용 쓰기 위함

    Intent intent;                  //requestCode 받기 위함


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_write);

        edtText = findViewById(R.id.edtMemo);
        intent =getIntent();
        final int requestCode = Integer.parseInt(intent.getStringExtra("num"));         //num으로 받은 데이터 integer로 변환

        if(requestCode==1){                                         //각각의 경우에 따라 setResult(resultCode, intent) 나눠줌
            findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = edtText.getText().toString();

                    if (str.length() > 0) {
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        String substr = sdf.format(date);
                        Toast.makeText(MemoWrite.this, str + ", " + substr, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("main", str);
                        intent.putExtra("sub", substr);
                        setResult(1, intent);                   //resultCode == 1
                        finish();


                    }
                }
            });

            findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        else if(requestCode==2){
            findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = edtText.getText().toString();

                    if (str.length() > 0) {
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        String substr = sdf.format(date);
                        Toast.makeText(MemoWrite.this, str + ", " + substr, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("main", str);
                        intent.putExtra("sub", substr);
                        setResult(2, intent);
                        finish();


                    }
                }
            });

            findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        else if(requestCode==3){
            findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = edtText.getText().toString();

                    if (str.length() > 0) {
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        String substr = sdf.format(date);
                        Toast.makeText(MemoWrite.this, str + ", " + substr, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("main", str);
                        intent.putExtra("sub", substr);
                        setResult(3, intent);
                        finish();


                    }
                }
            });

            findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        else if(requestCode==4){
            findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String str = edtText.getText().toString();

                    if (str.length() > 0) {
                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                        String substr = sdf.format(date);
                        Toast.makeText(MemoWrite.this, str + ", " + substr, Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent();
                        intent.putExtra("main", str);
                        intent.putExtra("sub", substr);
                        setResult(4, intent);
                        finish();


                    }
                }
            });

            findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
    }


}
