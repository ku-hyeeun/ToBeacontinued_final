package com.androidapp.tobeacontinue.Todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.androidapp.tobeacontinue.HouseFragment;
import com.androidapp.tobeacontinue.NoteDatabase;
import com.androidapp.tobeacontinue.NoteWriteFragment;
import com.androidapp.tobeacontinue.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;
import java.text.SimpleDateFormat;
import java.util.Date;


public class HouseTodolist extends AppCompatActivity implements OnTabItemSelectedListener,AutoPermissionListener{
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티

    private static final String TAG = "HouseTodolist";

    HouseFragment houseFragment;                //집 memo 목록
    NoteWriteFragment noteFragment;             //작성 fragment

    BottomNavigationView bottomNavigationView;      //하단바

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm");
    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH시");
    SimpleDateFormat dateFormat3 = new SimpleDateFormat("MM월 dd일");
    SimpleDateFormat dateFormat4 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    String currentDateString;
    Date currentDate;

    public static NoteDatabase mDatabase = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_todolist);

        houseFragment = new HouseFragment();
        noteFragment = new NoteWriteFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container1, houseFragment).commit();
        //시작페이지는 memo목록으로

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.tab1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container1,houseFragment).commit();
                        return true;

                    case R.id.tab2:
                        noteFragment = new NoteWriteFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container1,noteFragment).commit();
                        return true;
                }
                return false;
            }
        });

        AutoPermissions.Companion.loadAllPermissions(this, 101);
        openDatabase();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    public void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = NoteDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "Note database is open.");
        } else {
            Log.d(TAG, "Note database is not open.");
        }
    }

    @Override
    public void onTabSelected(int position) {
        if(position ==0 ){
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }else if(position == 1){
            noteFragment = new NoteWriteFragment();
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        }
    }

    public void showFragment2(Note item) {

        noteFragment = new NoteWriteFragment();
        noteFragment.setItem(item);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, noteFragment).commit();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }


}
