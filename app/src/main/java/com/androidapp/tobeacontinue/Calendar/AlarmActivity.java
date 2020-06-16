package com.androidapp.tobeacontinue.Calendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.androidapp.tobeacontinue.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

//알람 설정하는 액티비티
public class AlarmActivity extends AppCompatActivity {

    private Calendar calendar;          //캘린더 선언 (날짜 설정)
    private TimePicker timePicker;      //timepicker 선언 (시간 설정)

    EditText edtText;                   //할일을 입력하세요
    TextView edtDate;                   //날짜가져옴


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        this.calendar=Calendar.getInstance();
        //현재 날짜 표시
        displayDate();

        this.timePicker=findViewById(R.id.timePicker);

        edtText = findViewById(R.id.edtMemo);

        findViewById(R.id.btnCalendar).setOnClickListener(mClickListener);
        findViewById(R.id.btnAlarm).setOnClickListener(mClickListener);

    }


    //날짜표시
    private void displayDate(){
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        edtDate = findViewById(R.id.txtDate);
        edtDate.setText(format.format(this.calendar.getTime()));

    }


    //DatePickerDialog 호출
    private  void showDatePicker(){
        DatePickerDialog dialog=new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                //알람날짜 설정
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                //날짜 표시
                displayDate();
            }
        } ,this.calendar.get(Calendar.YEAR), this.calendar.get(Calendar.MONTH),this.calendar.get(Calendar.DAY_OF_MONTH));

        dialog.show();
    }

    //알람등록
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm(){
        this.calendar.set(Calendar.HOUR_OF_DAY, this.timePicker.getHour());
        this.calendar.set(Calendar.MINUTE, this.timePicker.getMinute());
        this.calendar.set(Calendar.SECOND,0);


        //현재일보다 이전이면 등록 실패
        if(this.calendar.before(Calendar.getInstance())){
            Toast.makeText(this,getString(R.string.alarm_toast),Toast.LENGTH_LONG).show();
            return;
        }

        //Receiver 설정
        Intent intent =new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        //알람설정
        AlarmManager alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, this.calendar.getTimeInMillis(),pendingIntent);


        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
        SimpleDateFormat format1 = new SimpleDateFormat("HH:mm",Locale.getDefault());

        //메모로 넘기기
        String context = edtText.getText().toString();
        String date = format.format(calendar.getTime());
        String time= format1.format(calendar.getTime());

        Toast.makeText(AlarmActivity.this, context + ", " + date+", " + time, Toast.LENGTH_SHORT).show();

        if (context.length() > 0) {

            Intent intent1 = new Intent();
            intent1.putExtra("context", context);
            intent1.putExtra("date", date);
            intent1.putExtra("time", time);

            setResult(5, intent1);


            finish();

        }

    }

    View.OnClickListener mClickListener=new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View v) {

            switch (v.getId()){
                case R.id.btnCalendar:
                    showDatePicker();
                    break;

                case R.id.btnAlarm:
                    setAlarm();
                    break;

            }
        }
    };

}
