package com.androidapp.tobeacontinue;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.androidapp.tobeacontinue.etc.SettingActivity;
import com.androidapp.tobeacontinue.fragment.BeaconFragment;
import com.androidapp.tobeacontinue.fragment.CalendarFragment;
import com.androidapp.tobeacontinue.fragment.FragmentCallback;
import com.androidapp.tobeacontinue.midascon.BeaconListAdapter;
import com.google.android.material.navigation.NavigationView;
import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.BeaconCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

public class MenuActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, FragmentCallback, BeaconCallback, Runnable{
    //메인화면
    //메인화면은 두 부분으로 나누었다 -> 비콘을 활용한 비콘 프레그먼트 & 날짜로 저장하는 캘린더 프레그먼트

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    private ContextManager contextManager;
    private BeaconListAdapter adapter;

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;             // 비콘 노티 알림

    BeaconFragment beaconFragment;          //비콘 프레그먼트 선언
    CalendarFragment calendarFragment;      //캘린더 프레그먼트 선언

    DrawerLayout drawer;                    //메뉴액티비티는 왼쪽 상단에 네비게이션 드로어를 이용했기 때문에 선언
    Toolbar toolbar;                        //액션바

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

        beaconFragment = new BeaconFragment();
        calendarFragment = new CalendarFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container, beaconFragment).commit();        //기본 프레그먼트는 비콘프레그먼트임

        contextManager = getMidasApplication().getContextManager();
        contextManager.getBeaconSettings().setMidasScanMode(true);

        adapter = new BeaconListAdapter(getBaseContext());
        startService(new Intent(getApplicationContext(), BeaconListAdapter.class));
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {           //네비게이션 드로어 열었을 때 두 가지 메뉴
        int id = item.getItemId();

        if(id==R.id.menu1){
            OnFragmentSelected(0, null);
        }
        else if(id ==R.id.menu2){
            OnFragmentSelected(1, null);
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void OnFragmentSelected(int position, Bundle bundle) {               //프레그먼트 설정, 인터페이스 구현
        Fragment curFragment = null;

        if(position == 0){
            curFragment = beaconFragment;
        }
        else if(position ==1 ){
            curFragment = calendarFragment;
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, curFragment).commit();


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
                break;
            case R.id.menu_settings:
                Intent settingIntent = new Intent(this, SettingActivity.class);
                startActivity(settingIntent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBeaconCallback(int i, Beacon beacon) {
        if(beacon.getRssi()+80>0) {
            if (adapter != null)
                adapter.addBeacon(beacon);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                showNotification(beacon);
            }

            runOnUiThread(this);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(Beacon beacon) {
        //오레오 (API26)이상부터 채널을 추가해야 notification 사용 가능
        NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setDescription("this is the description of the channel.");
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(notificationChannel);

        if (beacon == null)
            return;
        int notify = beacon.getId().hashCode();
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);        //상단의 노티 클릭하면 menuactivity로 넘어옴.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("title", "비콘 들어옴");
        intent.putExtra("message", beacon.getId());
        intent.putExtra("notify", notify);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID);
        builder.setContentIntent(PendingIntent.getActivity(getApplicationContext(), notify, intent, PendingIntent.FLAG_UPDATE_CURRENT));

        builder.setPriority(NotificationCompat.PRIORITY_HIGH);

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker("비콘 들어옴");
        builder.setContentTitle(beacon.getId());
        builder.setContentText(" 할 일을 확인하세요. ");

        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
        style.bigText(" 할 일을 확인하세요. ");
        style.setBigContentTitle(" 비콘 들어옴");
        style.setSummaryText(getString(R.string.app_name));

        manager.notify(notify, style.build());
    }

    @Override
    public void run() {
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }

    public MidasApplication getMidasApplication() {
        return (MidasApplication) getApplication();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                // 콜백 등록
                contextManager.setBeaconCallback(this);
                contextManager.startLeScan();
            } else {
                contextManager.stopLeScan();

                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settingsIntent);
            }
        }

    }

    //뒤로 가기 눌렀을 때도 정상 작동
    @Override
    protected void onPause() {
        super.onPause();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        } else {
            if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                // 콜백 등록
                contextManager.setBeaconCallback(this);
                contextManager.startLeScan();
            } else {
                contextManager.stopLeScan();

                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(settingsIntent);
            }
        }
    }
}
