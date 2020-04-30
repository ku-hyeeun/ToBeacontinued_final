package com.androidapp.tobeacontinue;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,FragmentCallback {

    HomeFragment homeFragment;
    CalendarFragment calendarFragment;

    DrawerLayout drawer;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = findViewById(R.id.toolbar);
          setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        homeFragment = new HomeFragment();
        calendarFragment = new CalendarFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, homeFragment).commit();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id==R.id.menu1){
            Toast.makeText(this,"장소로 추가", Toast.LENGTH_LONG).show();
            OnFragmentSelected(0, null);
        }
        else if(id ==R.id.menu2){
            Toast.makeText(this,"캘린더로 추가",Toast.LENGTH_LONG).show();
            OnFragmentSelected(1, null);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnFragmentSelected(int position, Bundle bundle) {
        Fragment curFragment = null;

        if(position == 0){
            curFragment = homeFragment;
        }
        else if(position ==1 ){
            curFragment = calendarFragment;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        View v = menu.findItem(R.id.menu_search).getActionView();
        if(v!=null){
            EditText editText = v.findViewById(R.id.editText);

            if(editText != null){
                editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                        Toast.makeText(getApplicationContext(),"입력됨",Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int curId = item.getItemId();
        switch (curId){
            case R.id.menu_search:
                Toast.makeText(this,"검색 메뉴가 선택되었습니다.",Toast.LENGTH_LONG).show();
                break;
            case R.id.menu_settings:
                Toast.makeText(this,"설정 메뉴가 검색되었습니다.",Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
