package com.androidapp.tobeacontinue.Todolist;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.tobeacontinue.MapsActivity;
import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.database.GeoDBHelper;

import java.util.ArrayList;
import java.util.List;

public class ExtraTodolist extends AppCompatActivity {

    Button btnAdd;
    RecyclerView recyclerView;                  //리사이클러뷰
    RecyclerAdapter recyclerAdapter;
    Toolbar toolbar;

    GeoDBHelper databaseHelper;
    ArrayList<GeofencingMemo> arrayList;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra_todolist);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ExtraTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        databaseHelper = new GeoDBHelper(this);
        arrayList = databaseHelper.getAllText();

        recyclerAdapter=new RecyclerAdapter(arrayList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);


        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(ExtraTodolist.this, MapsActivity.class);
                startActivityForResult(intent,1);
            }
        });

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED){
        }else {
            //Ask for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We need to show uses a dialog for display why the permission is needed and than ask for the permission

                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==requestCode){
            if(resultCode==1){
                String place =data.getStringExtra("place");
                String contents = data.getStringExtra("contents");

                GeofencingMemo memo = new GeofencingMemo(0, place, contents);
                recyclerAdapter.addItem(memo);
                recyclerAdapter.notifyDataSetChanged();

                databaseHelper.addMemo(memo);

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

        private List<GeofencingMemo> listdata;
        AlertDialog.Builder builder;    //알림 메세지 선언

        public RecyclerAdapter(List<GeofencingMemo> listdata){
            this.listdata=listdata;
        }

        @Override
        public int getItemCount() {
            return listdata.size();
        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.geo_memo_item,viewGroup,false);
            return new ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder itemViewHolder, int i) {
            GeofencingMemo memo=listdata.get(i);

            itemViewHolder.maintext.setTag(memo.getId());

            itemViewHolder.maintext.setText(memo.getPlaceText());
            itemViewHolder.subtext.setText(memo.getContentText());
        }

        void addItem(GeofencingMemo memo){
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

                maintext=itemView.findViewById(R.id.PlaceTextView);
                subtext=itemView.findViewById(R.id.ContentTextView);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {             //메모 리스트에서 원하는 아이템 길게 누르면 데이터 삭제
                        final int position = getAdapterPosition();
                        final int id = (int)maintext.getTag();

                        builder = new AlertDialog.Builder(ExtraTodolist.this);
                        builder.setTitle(getString(R.string.delete_Memo));                 //알림 메세지
                        builder.setMessage("\n");
                        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(position != RecyclerView.NO_POSITION){
                                    databaseHelper.deleteMemo(id);
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
