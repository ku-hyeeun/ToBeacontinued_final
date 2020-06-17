package com.androidapp.tobeacontinue.Todolist;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidapp.tobeacontinue.MenuActivity;
import com.androidapp.tobeacontinue.R;
import com.androidapp.tobeacontinue.database.MemoDBHelper;
import com.androidapp.tobeacontinue.midascon.BeaconListAdapter;
import com.hanvitsi.midascon.Beacon;
import com.hanvitsi.midascon.BeaconCallback;
import com.hanvitsi.midascon.MidasApplication;
import com.hanvitsi.midascon.manager.ContextManager;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;


public class CafeteriaTodolist extends AppCompatActivity implements BeaconCallback, Runnable{
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티
    //HouseTodolist와 구조 같음
    // 다른 것: 데이터 저장, 삭제, 조회 이름 다르고, requestCode로 4 넣었음

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    private ContextManager contextManager;
    private BeaconListAdapter adapter;

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;             // 비콘 노티 알림

    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;
    Toolbar toolbar;

    MemoDBHelper DBHelper;
    List<Memo> memoList;

    private Intent intent;
    private final String packageName = "com.everytime.v2";          //에브리타입 앱 연동


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cafeteria_todolist);

        DBHelper = new MemoDBHelper(CafeteriaTodolist.this);        //DB 설정
        memoList = DBHelper.selectAll4();                           //DB 테이블 조회해서 처음 페이지에 보여줌

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);      //toolbar 설정 , 뒤로가기 넣음

        recyclerView=findViewById(R.id.recyclerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CafeteriaTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);         //리사이클러뷰로 구성되어 있음

        recyclerAdapter=new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);

        btnAdd.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(CafeteriaTodolist.this, MemoWrite.class);
                intent.putExtra("num", "4");                    //메모 작성 창에 intent로 num = 4 보냄 ( 창 구분 )
                startActivityForResult(intent,4);
            }
        });

        intent = this.getPackageManager().getLaunchIntentForPackage(packageName);
        Button etabutton =(Button)findViewById(R.id.etabutton);

        etabutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                CafeteriaTodolist.this.startActivity(intent);       //이미지 버튼을 클릭하면 패키지이름인 에브리타임 앱으로 연동됨
            }
        });


        //비콘
        contextManager = getMidasApplication().getContextManager();
        contextManager.getBeaconSettings().setMidasScanMode(true);

        adapter = new BeaconListAdapter(getBaseContext());
        startService(new Intent(getApplicationContext(), BeaconListAdapter.class));         //startService -> 비콘 인식 + notification 옴
    }

    //startActivityForResult로 보냈을 때 onActivityResult 함수로 받을 때 처리해줌
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== resultCode){
            if(resultCode==4) {                                 //resultCode == 4로 받음
                String strMain = data.getStringExtra("main");   //메모 내용 받음
                String strSub = data.getStringExtra("sub");     //날짜 받음

                Memo memo = new Memo(0, strMain, strSub,0);
                recyclerAdapter.addItem(memo);
                recyclerAdapter.notifyDataSetChanged();         //MemoList에 집어넣어서 리사이클러뷰에 보여줌

                DBHelper.insertMemo4(memo);                     //DB에도 저장
            }
        }
    }

    //Toolbar에 뒤로가기 눌렀을 때 실행
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

    //BeaconCallback 함수
    @Override
    public void onBeaconCallback(int i, Beacon beacon) {
        if(beacon.getMac().equals("10-78-ce-30-00-7d") && memoList.size() != 0){        //각 장소에 비콘 아이디 다르게 설정해줌 & memoList size가 0이 아닐경우 비콘 울림
            if(beacon.getRssi()+70>0) {         //test를 위해 다 1m 반경으로 설정하였습니다.
                if (adapter != null)
                    adapter.addBeacon(beacon);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    showNotification(beacon);   //노티 보여주기
                }

                runOnUiThread(this);
            }
        }
    }


    //노티 함수
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
        Intent intent = new Intent(getApplicationContext(), CafeteriaTodolist.class);        //상단의 노티 클릭하면 각 장소 액티비티로 넘어감.
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
        builder.setTicker(getString(R.string.Beacon));
        builder.setContentTitle(getString(R.string.cafeteria_message));
        builder.setContentText(getString(R.string.Beacon_Alarm));

        builder.setAutoCancel(true);
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);

        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
        style.bigText(getString(R.string.Beacon_Alarm));
        style.setBigContentTitle(getString(R.string.cafeteria_message));
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

    //리사이클러어댑터 클래스
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

        private List<Memo> listdata;    //리스트 선언
        AlertDialog.Builder builder;    //알림 메세지 선언

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

            //메모 부분
            itemViewHolder.maintext.setTag(memo.getId());               //id

            itemViewHolder.maintext.setText(memo.getContents());        //내용
            itemViewHolder.subtext.setText(memo.getCreateDateStr());    //날짜

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

            public ItemViewHolder(@NonNull View itemView){
                super(itemView);

                maintext=itemView.findViewById(R.id.contentsTextView);
                subtext=itemView.findViewById(R.id.dateTextView);

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        final int position = getAdapterPosition();
                        final int id = (int)maintext.getTag();

                        builder = new AlertDialog.Builder(CafeteriaTodolist.this);
                        builder.setTitle(getString(R.string.delete_Memo));                 //알림 메세지
                        builder.setMessage("\n");
                        builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(position != RecyclerView.NO_POSITION){
                                    DBHelper.deleteMemo4(id);
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
