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

import java.util.ArrayList;
import java.util.List;


public class OutsideTodolist extends AppCompatActivity {
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;

    List<Note> memoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_todolist);

        memoList = new ArrayList<>();
        memoList.add(new Note(0,"장보기","2020-05-10"));

        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(OutsideTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter= new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(OutsideTodolist.this,NoteWriteActivity.class);
                startActivityForResult(intent,2);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== 2){
            String strMain=data.getStringExtra("main");
            String strSub=data.getStringExtra("sub");

            Note memo=new Note(0,strMain,strSub);
            recyclerAdapter.addItem(memo);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
        private List<Note> listdata;

        public RecyclerAdapter(List<Note> listdata){
            this.listdata=listdata;
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_item,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            Note memo=listdata.get(i);
            itemViewHolder.maintext.setText(memo.getContents());
            itemViewHolder.subtext.setText(memo.getCreateDateStr());
        }

        void addItem(Note memo){
            listdata.add(memo);
        }

        void removeItem(int position){
            listdata.remove(position);
        }

        class ItemViewHolder extends RecyclerView.ViewHolder{
            private TextView maintext;
            private TextView subtext;
            //private ImageView img;

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.contentsTextView);
                subtext=itemView.findViewById(R.id.dateTextView);
                //img=itemView.findViewById(R.id.item_image);

            }
        }
    }
}
