package com.androidapp.tobeacontinue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Todolist extends AppCompatActivity implements OnTabSelectedListener {
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티
    //하단에 bottomNavigationView를 사용할 것이기 때문에 4개의 하단바에 해당하는 4개의 프레그먼트를 만들어 주었음

    Fragment fragment1;         //집
    Fragment fragment2;         //집 밖
    Fragment fragment3;         //한누리관
    Fragment fragment4;         //학생회관

    BottomNavigationView bottomNavigationView;      //하단바

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todolist);

        fragment1 = new Fragment();
        fragment2 = new Fragment();
        fragment3 = new Fragment();
        fragment4 = new Fragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragment1).commit();        //시작페이지는 fragment1로

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(                   //  하단바에 리스너 등록 -> 각 탭이 선택되면 그에 해당하는 프레그먼트를 보여줌
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.tab1:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragment1).commit();
                                return true;
                            case R.id.tab2:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragment2).commit();
                                return true;
                            case R.id.tab3:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragment3).commit();
                                return true;
                            case R.id.tab4:
                                getSupportFragmentManager().beginTransaction().replace(R.id.container1, fragment4).commit();
                                return true;
                        }
                        return false;
                    }
                });
    }

    public void OnTabSelected(int position){

        if(position == 0){
            bottomNavigationView.setSelectedItemId(R.id.tab1);
        }else if(position == 1){
            bottomNavigationView.setSelectedItemId(R.id.tab2);
        }else if(position == 2){
            bottomNavigationView.setSelectedItemId(R.id.tab3);
        }else if(position == 3){
            bottomNavigationView.setSelectedItemId(R.id.tab4);
        }

    }


}
