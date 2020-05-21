package com.androidapp.tobeacontinue.Todolist;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.database.CalendarDBHelper;

import java.util.List;

public class CalendarTodolist extends AppCompatActivity {

    CalendarDBHelper dbHelper;

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;
    List<CalendarMemo> memoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_todolist);

        dbHelper=new CalendarDBHelper(CalendarTodolist.this);
        memoList = dbHelper.selectAll(); //

        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CalendarTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter=new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);


        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(CalendarTodolist.this,CalendarAddActivity.class);
                startActivityForResult(intent,0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== 0){
            String strMain=data.getStringExtra("main");
            String strSub=data.getStringExtra("sub");
            String strTime=data.getStringExtra("time");

            CalendarMemo memo=new CalendarMemo(strMain,strSub,strTime,0);
            recyclerAdapter.addItem(memo);
            recyclerAdapter.notifyDataSetChanged();

            dbHelper.insertMemo(memo);
        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
        private List<CalendarMemo> listdata;

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
//
//            //imageview는 0일때와 1일때의 색상이 다름,
//            if(memo.getIsdone()==0){ //0일때는 회색
//                itemViewHolder.img.setBackgroundColor(Color.LTGRAY);
//            }
//            else{ //1일때는 녹색
//                itemViewHolder.img.setBackgroundColor(Color.GREEN);
//            }
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
            //private ImageView img;

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.item_maintext);
                subtext=itemView.findViewById(R.id.item_subtext);
                timetext=itemView.findViewById(R.id.item_time);
                //img=itemView.findViewById(R.id.item_image);

                itemView.setOnLongClickListener(new View.OnLongClickListener(){

                    @Override
                    public boolean onLongClick(View view) {
                        //메모하나를 길게 눌렀을 때 해당 메모의 포지션을 가져온다.
                        //이때 포지션은 DB의 포지션이 아니라 현재 화면에 보이는 리스트 중 몇번쨰인가를 가져오는것->seq가져오기
                        int position =getAdapterPosition();
                        int seq=(int)maintext.getTag();

                        if(position!=RecyclerView.NO_POSITION){
                            dbHelper.deleteMemo(seq);
                            removeItem(position);
                            notifyDataSetChanged();
                        }
                        return false;
                    }
                });
            }
        }
    }
}