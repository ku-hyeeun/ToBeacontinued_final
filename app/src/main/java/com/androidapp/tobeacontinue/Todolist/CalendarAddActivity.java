package com.androidapp.tobeacontinue.Todolist;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.androidapp.tobeacontinue.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarAddActivity extends AppCompatActivity {
    EditText edtText; //할일을 입력하세요
    TextView edtDate; //날짜가져옴
    TextView edtTime; //시간가져옴

    private TextView textView_Date; // 오늘의날짜
    private TextView texView_Time; //현재시간

    private DatePickerDialog.OnDateSetListener callbackMethod;
    private TimePickerDialog.OnTimeSetListener callbackMethod2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_add);

        this.InitializeView();
        this.InitializeListener();

        edtText = findViewById(R.id.edtMemo);
        edtDate = findViewById(R.id.textView_date);
        edtTime = findViewById(R.id.textView_time);


        findViewById(R.id.btnDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = edtText.getText().toString();

                if (str.length() > 0) {

                    String substr = edtDate.getText().toString();
                    String timestr= edtTime.getText().toString();

                    Toast.makeText(CalendarAddActivity.this, str + "," + substr+"," + timestr, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("main", str);
                    intent.putExtra("sub", substr);
                    intent.putExtra("time",timestr);

                    setResult(0, intent);

                    finish();

                }
            }
        });

        findViewById(R.id.btnNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CalendarAddActivity.this, CalendarTodolist.class);
                startActivity(intent);
            }
        });
    }


    public void InitializeView() {
        textView_Date = (TextView) findViewById(R.id.textView_date);
        texView_Time=(TextView) findViewById(R.id.textView_time);
    }

    public void InitializeListener() {
        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                textView_Date.setText(year + "년" + (month+1)  + "월" + dayOfMonth + "일");
            }
        };

        callbackMethod2= new TimePickerDialog.OnTimeSetListener(){
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                texView_Time.setText(hourOfDay+"시"+minute+"분");
            }
        };
    }

    //DatePicker버튼 누를때 작용
    public void OnClickHandler(View view) {
        Date date = Calendar.getInstance().getTime();
        int year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.getDefault()).format(date));
        int month = Integer.parseInt(new SimpleDateFormat("MM", Locale.getDefault()).format(date));
        int day = Integer.parseInt(new SimpleDateFormat("dd", Locale.getDefault()).format(date));

        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, year, (month-1), day);
        dialog.show();

    }

    //Timepicker 버튼누를때 작용
    public void OnClickHandler2(View view){
        Date date = Calendar.getInstance().getTime();
        int hour=Integer.parseInt(new SimpleDateFormat("HH",Locale.getDefault()).format(date));
        int minute=Integer.parseInt(new SimpleDateFormat("mm",Locale.getDefault()).format(date));
        TimePickerDialog dialog2=new TimePickerDialog(this,callbackMethod2,hour,minute,true);
        dialog2.show();
    }

}