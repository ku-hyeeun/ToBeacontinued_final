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
import com.androidapp.tobeacontinue.database.MemoDBHelper;

import java.util.List;


public class HouseTodolist extends AppCompatActivity {
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티

    RecyclerView recyclerView;                  //리사이클러뷰
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;                              //작성 버튼

    MemoDBHelper DBHelper;                     //DB 만들기

    List<Memo> memoList;                        //Memo 리스트 만들기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_todolist);

        DBHelper = new MemoDBHelper(HouseTodolist.this);
        memoList = DBHelper.selectAll1();       //메모리스트 -> DB 조회

        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HouseTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter=new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);

        btnAdd.setOnClickListener(new View.OnClickListener(){       //작성 버튼 누를시 memowrite.class로 이동!!
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(HouseTodolist.this, MemoWrite.class);
                intent.putExtra("num", "1");                       //requestCode로 4개 액티비티 구분 지어서 resultCode 나눠 받기 위해 num에 1이란 데이터 넣어서 전달
                startActivityForResult(intent,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== resultCode){
            if(resultCode==1) {                             //resultCode 1로 받을 때
                String strMain = data.getStringExtra("main");   //내용 받기
                String strSub = data.getStringExtra("sub");     //날짜 받기

                Memo memo = new Memo(0, strMain, strSub, 0);    //id=0,완료 여부 = 0
                recyclerAdapter.addItem(memo);
                recyclerAdapter.notifyDataSetChanged();

                DBHelper.insertMemo1(memo);                 //memowrite.class에서 데이터 받은 것 DB에 저장
            }
        }
    }

//리사이클러 어댑터 클래스
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
        private List<Memo> listdata;                        //메모리스트

        public RecyclerAdapter(List<Memo> listdata){
            this.listdata=listdata;
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.memo_item,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            Memo memo=listdata.get(i);

            itemViewHolder.maintext.setTag(memo.getId());

            itemViewHolder.maintext.setText(memo.getContents());
            itemViewHolder.subtext.setText(memo.getCreateDateStr());


        }

        void addItem(Memo memo){
            listdata.add(memo);
        }

        void removeItem(int position){
            listdata.remove(position);
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            private TextView maintext;
            private TextView subtext;

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.contentsTextView);
                subtext=itemView.findViewById(R.id.dateTextView);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {             //메모 리스트에서 원하는 아이템 길게 누르면 데이터 삭제
                        int position = getAdapterPosition();
                        int id = (int)maintext.getTag();

                        if(position != RecyclerView.NO_POSITION){
                            DBHelper.deleteMemo1(id);
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
