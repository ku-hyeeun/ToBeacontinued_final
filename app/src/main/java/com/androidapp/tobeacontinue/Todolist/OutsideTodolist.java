package com.androidapp.tobeacontinue.Todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.database.MemoDBHelper;
import com.androidapp.tobeacontinue.etc.Settings;

import java.util.List;


public class OutsideTodolist extends AppCompatActivity {
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티
    //HouseTodolist와 구조 같음
    // 다른 것: 데이터 저장, 삭제, 조회 이름 다르고, requestCode로 2 넣었음

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;
    Button btnSelection;

    MemoDBHelper DBHelper;
    List<Memo> memoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outside_todolist);

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

        btnSelection = (Button)findViewById(R.id.btnShow);
        btnSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data="";
                List<Memo> memoList = recyclerAdapter.getListdata();
                for(int i=0;i<memoList.size();i++){
                    Memo memo = memoList.get(i);
                    if(memo.isSelected()==true){
                        data = data+"\n"+memo.getContents();
                    }
                }
                Toast.makeText(OutsideTodolist.this,"Selected Memo: \n"+data,Toast.LENGTH_SHORT).show();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {                             //액션바에 오른쪽에 위치한 검색 메뉴
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            final SearchView v = (SearchView) menu.findItem(R.id.menu_search).getActionView();
            v.setMaxWidth(Integer.MAX_VALUE);
            v.setQueryHint("검색할 내용을 입력하세요");

            v.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    v.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId){
            case R.id.menu_search:
                Toast.makeText(this,"검색 메뉴가 선택되었습니다.",Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_settings:                //검색 메뉴와 다르게 설정 메뉴는 클릭할 경우 새로운 액티비티로 전환되게 하였음
                Toast.makeText(this,"설정 메뉴가 검색되었습니다.",Toast.LENGTH_SHORT).show();
                Intent settingIntent = new Intent(this, Settings.class);
                startActivity(settingIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
