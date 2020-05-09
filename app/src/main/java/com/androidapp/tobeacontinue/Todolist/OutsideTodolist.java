package com.androidapp.tobeacontinue.Todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidapp.tobeacontinue.NoteWriteFragment;
import com.androidapp.tobeacontinue.OutsideFragment;
import com.androidapp.tobeacontinue.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class OutsideTodolist extends AppCompatActivity implements OnTabItemSelectedListener{
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티

    OutsideFragment outsideFragment;
    NoteWriteFragment noteFragment;         //작성 fragment

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_todolist);

        outsideFragment = new OutsideFragment();
        noteFragment = new NoteWriteFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container1, outsideFragment).commit();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.tab1:
                        Toast.makeText(getApplicationContext(),"첫 번째 탭 선택됨",Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container1,outsideFragment).commit();
                        return true;

                    case R.id.tab2:
                        Toast.makeText(getApplicationContext(),"두 번째 탭 선택됨",Toast.LENGTH_SHORT).show();
                        getSupportFragmentManager().beginTransaction().replace(R.id.container1,noteFragment).commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onTabSelected(int position) {
        if(position ==0 ){
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }else if(position == 1){
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        }
    }
}
