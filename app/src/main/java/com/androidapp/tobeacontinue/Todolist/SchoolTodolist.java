package com.androidapp.tobeacontinue.Todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
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
import com.androidapp.tobeacontinue.database.ImageDBHelper;
import com.androidapp.tobeacontinue.database.MemoDBHelper;
import com.androidapp.tobeacontinue.etc.Settings;

import java.io.ByteArrayOutputStream;
import java.util.List;


public class SchoolTodolist extends AppCompatActivity {
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티
    //HouseTodolist와 구조 같음
    // 다른 것: 데이터 저장, 삭제, 조회 이름 다르고, requestCode로 3 넣었음

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;
    Button btnSelection;

    MemoDBHelper DBHelper;                              //메모 디비
    List<Memo> memoList;                                //메모 리스트
    Bitmap scaled;                                      //사진 처리 bitmap
    public static ImageDBHelper imageDBHelper;          //이미지 저장 디비

    private static int PICK_IMAGE_REQUEST = 1;          //이미지 로드
    ImageView imgView;
    static final String TAG = "SchoolTodolist";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_todolist);

        DBHelper = new MemoDBHelper(SchoolTodolist.this);
        memoList = DBHelper.selectAll3();                       //메모DB 조회

        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(SchoolTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter= new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(SchoolTodolist.this, MemoWrite.class);
                intent.putExtra("num", "3");
                startActivityForResult(intent,3);
            }
        });

        btnSelection = findViewById(R.id.btnShow);              //selection 버튼으로 체크한 메모 보여줌
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
                Toast.makeText(SchoolTodolist.this,"Selected Memo: \n"+data,Toast.LENGTH_SHORT).show();
            }
        });

        imageDBHelper = new ImageDBHelper(this);                //이미지 저장을 위한 DB
        imageDBHelper.queryData("CREATE TABLE IF NOT EXISTS table_image (image BLOB);");        //테이블 만들기

        imgView = findViewById(R.id.imageView);                 //이미지 뷰에 DB에 저장된 데이터 띄우기
        Cursor cursor = imageDBHelper.getData("SELECT * FROM table_image;");    //조회
        while(cursor.moveToNext())
        {
            byte[] image = cursor.getBlob(0);                       //이미지를 바이트로 처리한 후
            scaled = byteArrayToBitmap(image);                      //byte를 bitmap으로 바꿔주는 함수를 통해 변환
            imgView.setImageBitmap(scaled);                         //이미지 뷰에 띄우기
        }

    }

    //갤러리로부터 이미지 불러오기
    public void loadImagefromGallery(View view){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);

        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== resultCode){                               // 메모 작성 창에서 intent 넘겨받을 때 오는 resultCode
            if(resultCode == 3){
            String strMain=data.getStringExtra("main");
            String strSub=data.getStringExtra("sub");

            Memo memo=new Memo(0,strMain,strSub,0);
            recyclerAdapter.addItem(memo);
            recyclerAdapter.notifyDataSetChanged();

            DBHelper.insertMemo3(memo);
            }
        }

        try {                                                       // 갤러리에서 이미지 로드할 때 오는 resultcode
            if(requestCode == PICK_IMAGE_REQUEST && resultCode ==RESULT_OK && null!=data) {
                Uri uri = data.getData();

                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                int nh = (int) (bitmap.getHeight() * (1024.0 / bitmap.getWidth()));
                scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);

                imgView = findViewById(R.id.imageView);
                imgView.setImageBitmap(scaled);                     //이미지 불러온 후
                imageDBHelper.insertImage(imgViewToByte(imgView));  //DB에 img를 byte로 변환 후 저장

            }else {
                Toast.makeText(this,"취소되었습니다.",Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this,"로딩에 오류가 있습니다.",Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private byte[] imgViewToByte(ImageView imageView) {             //이미지 바이트 변환 함수
        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public Bitmap byteArrayToBitmap( byte[] $byteArray ) {          //바이트 이미지 변환 함수
        Bitmap bitmap = BitmapFactory.decodeByteArray( $byteArray, 0, $byteArray.length ) ;
        return bitmap ;
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
        public void onBindViewHolder(@NonNull RecyclerAdapter.ItemViewHolder itemViewHolder, int i) {
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

                        builder = new AlertDialog.Builder(SchoolTodolist.this);
                        builder.setTitle("메모를 삭제하시겠습니까? ");
                        builder.setMessage("\n");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(position != RecyclerView.NO_POSITION){
                                    DBHelper.deleteMemo3(id);
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
