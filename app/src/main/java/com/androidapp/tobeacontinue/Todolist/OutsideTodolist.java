package com.androidapp.tobeacontinue.Todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.database.MemoDBHelper;

import java.util.List;


public class OutsideTodolist extends AppCompatActivity {
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티
    //HouseTodolist와 구조 같음
    // 다른 것: 데이터 저장, 삭제, 조회 이름 다르고, requestCode로 2 넣었음

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;
    Toolbar toolbar;

    MemoDBHelper DBHelper;
    List<Memo> memoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_todolist);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBHelper = new MemoDBHelper(OutsideTodolist.this);
        memoList = DBHelper.selectAll2();

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
                Intent intent=new Intent(OutsideTodolist.this, MemoWrite.class);
                intent.putExtra("num", "2");
                startActivityForResult(intent,2);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== resultCode){
            if(resultCode == 2) {
                String strMain = data.getStringExtra("main");
                String strSub = data.getStringExtra("sub");

                Memo memo = new Memo(0, strMain, strSub,0);
                recyclerAdapter.addItem(memo);
                recyclerAdapter.notifyDataSetChanged();

                DBHelper.insertMemo2(memo);
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
        private List<Memo> listdata;

        AlertDialog.Builder builder;

        public RecyclerAdapter(List<Memo> listdata){
            this.listdata=listdata;
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }

        public List<Memo> getListdata() {
            return listdata;
        }

        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.memo_item,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            final Memo memo=listdata.get(i);

            itemViewHolder.maintext.setTag(memo.getId());

            itemViewHolder.maintext.setText(memo.getContents());
            itemViewHolder.subtext.setText(memo.getCreateDateStr());
            itemViewHolder.chkSelected.setChecked(memo.isSelected());
            itemViewHolder.chkSelected.setTag(memo);

            itemViewHolder.chkSelected.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckBox cb = (CheckBox)view;
                    Memo check = (Memo)cb.getTag();

                    check.setSelected(cb.isChecked());
                    memo.setSelected(cb.isChecked());

                }
            });
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
            public CheckBox chkSelected;

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.contentsTextView);
                subtext=itemView.findViewById(R.id.dateTextView);
                chkSelected = itemView.findViewById(R.id.checkbox);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final int position = getAdapterPosition();
                        final int id = (int)maintext.getTag();

                        builder = new AlertDialog.Builder(OutsideTodolist.this);
                        builder.setTitle("메모를 삭제하시겠습니까? ");
                        builder.setMessage("\n");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(position != RecyclerView.NO_POSITION){
                                    DBHelper.deleteMemo2(id);
                                    removeItem(position);
                                    notifyDataSetChanged();
                                }
                            }
                        });

                        builder.setNegativeButton("아니오", null);
                        builder.create().show();

                        return false;
                    }
                });
            }
        }
    }

}
