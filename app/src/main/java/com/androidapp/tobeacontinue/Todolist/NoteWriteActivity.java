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

public class NoteWriteActivity extends AppCompatActivity {

    EditText edtText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_write);

        edtText=findViewById(R.id.edtMemo);

        findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String str=edtText.getText().toString();

                if(str.length()>0){
                    Date date=new Date();
                    SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

                    String substr=sdf.format(date);
                    Toast.makeText(NoteWriteActivity.this,str+","+substr,Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.putExtra("main",str);
                    intent.putExtra("sub",substr);
                    setResult(0, intent);
//                    setResult(1, intent);
//                    setResult(2, intent);
//                    setResult(3, intent);
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
