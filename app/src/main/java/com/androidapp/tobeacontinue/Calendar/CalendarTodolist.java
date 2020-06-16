package com.androidapp.tobeacontinue.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.database.CalendarDBHelper;

import java.util.List;

public class CalendarTodolist extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button btnAlarm;
    Toolbar toolbar;

    CalendarDBHelper dbHelper;
    List<CalendarMemo> memoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper=new CalendarDBHelper(CalendarTodolist.this);
        memoList=dbHelper.selectAll();

        recyclerView=findViewById(R.id.recyclerview);

        //리사이클러뷰는 리니어레이아웃매니저를 사용해야함
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CalendarTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter=new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);

        btnAlarm=findViewById(R.id.btnAlarm);

        btnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(CalendarTodolist.this, AlarmActivity.class);
                startActivityForResult(intent,5);
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

            if(requestCode == resultCode) {
                if (resultCode == 5) {
                    String context = data.getStringExtra("context");
                    String date = data.getStringExtra("date");
                    String time = data.getStringExtra("time");

                    CalendarMemo memo = new CalendarMemo(context, date, time, 0);
                    recyclerAdapter.addItem(memo);
                    recyclerAdapter.notifyDataSetChanged();

                    dbHelper.insertMemo(memo);
                }
            }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:{
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
        private List<CalendarMemo> listdata;

        AlertDialog.Builder builder;

        public RecyclerAdapter(List<CalendarMemo> listdata){
            this.listdata=listdata;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.calendar_memo,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }


        @Override
        //onBindViewHolder는 데이터를 레이아웃에 어떻게 넣어줄지를 정함.
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            CalendarMemo memo=listdata.get(i);

            //seq가져오기
            itemViewHolder.maintext.setTag(memo.getSeq());
            itemViewHolder.maintext.setText(memo.getMaintext());
            itemViewHolder.subtext.setText(memo.getSubtext());
            itemViewHolder.timetext.setText(memo.getTimetext());

        }

        //리스트추가
        void addItem(CalendarMemo memo){
            listdata.add(memo);
        }

        //리스트 삭제
        void removeItem(int position){
            listdata.remove(position);
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            private TextView maintext;
            private TextView subtext;
            private TextView timetext;

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.contentsTextView);
                subtext=itemView.findViewById(R.id.dateTextView);
                timetext=itemView.findViewById(R.id.timeTextView);

                itemView.setOnLongClickListener(new View.OnLongClickListener(){

                    @Override
                    public boolean onLongClick(View view) {
                        //메모하나를 길게 눌렀을 때 해당 메모의 포지션을 가져온다.
                        //이때 포지션은 DB의 포지션이 아니라 현재 화면에 보이는 리스트 중 몇번쨰인가를 가져오는것->seq가져오기
                        final int position =getAdapterPosition();
                        final int seq=(int)maintext.getTag();

                        builder = new AlertDialog.Builder(CalendarTodolist.this);
                        builder.setTitle(getString(R.string.delete_Memo));
                        builder.setMessage("\n");
                        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(position != RecyclerView.NO_POSITION){
                                    dbHelper.deleteMemo(seq);
                                    removeItem(position);
                                    notifyDataSetChanged();
                                }
                            }
                        });

                        builder.setNegativeButton(getString(R.string.no), null);
                        builder.create().show();
                        return false;
                    }
                });
            }
        }
    }



}



