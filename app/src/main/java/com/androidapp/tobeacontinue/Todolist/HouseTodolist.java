package com.androidapp.tobeacontinue.Todolist;

import android.Manifest;
import android.app.Activity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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

import java.util.List;


public class HouseTodolist extends Activity implements BeaconCallback, Runnable{
    //비콘 프레그먼트에서 각 버튼을 클릭 시 열리는 새로운 액티비티

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 100;

    private ContextManager contextManager;
    private BeaconListAdapter adapter;

    private String CHANNEL_NAME = "High priority channel";
    private String CHANNEL_ID = "com.example.notifications" + CHANNEL_NAME;

    RecyclerView recyclerView;                  //리사이클러뷰
    RecyclerAdapter recyclerAdapter;
    Button btnAdd;                              //작성 버튼

    MemoDBHelper DBHelper;                      //DB 만들기

    List<Memo> memoList;                        //Memo 리스트 만들기


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_todolist);

        DBHelper = new MemoDBHelper(HouseTodolist.this);
        memoList = DBHelper.selectAll1();       //메모리스트 -> DB 조회

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HouseTodolist.this);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerAdapter=new RecyclerAdapter(memoList);
        recyclerView.setAdapter(recyclerAdapter);
        btnAdd=findViewById(R.id.writeButton);

        btnAdd.setOnClickListener(new View.OnClickListener(){       //작성 버튼 누를시 memowrite.class로 이동!!
            @Override
            public void onClick(View view) {
                //새로운 메모작성
                Intent intent=new Intent(HouseTodolist.this, MemoWrite.class);
                intent.putExtra("num", "1");                       //requestCode로 4개 액티비티 구분 지어서 resultCode 나눠 받기 위해 num에 1이란 데이터 넣어서 전달
                startActivityForResult(intent,1);
            }
        });

        contextManager = getMidasApplication().getContextManager();
        contextManager.getBeaconSettings().setMidasScanMode(true);

        adapter = new BeaconListAdapter(getBaseContext());
        startService(new Intent(getApplicationContext(), BeaconListAdapter.class));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== resultCode){
            if(resultCode==1) {                             //resultCode 1로 받을 때
                String strMain = data.getStringExtra("main");   //내용 받기
                String strSub = data.getStringExtra("sub");     //날짜 받기

                Memo memo = new Memo(0, strMain, strSub, 0);
                recyclerAdapter.addItem(memo);
                recyclerAdapter.notifyDataSetChanged();

                DBHelper.insertMemo1(memo);                 //memowrite.class에서 데이터 받은 것 DB에 저장
            }
        }
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
            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
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
            builder.setContentText(beacon.getId() + " 비콘 들어옴");

            builder.setAutoCancel(true);
            builder.setDefaults(NotificationCompat.DEFAULT_ALL);

            NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(builder);
            style.bigText(beacon.getId() + " 비콘 들어옴");
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

    //리사이클러 어댑터 클래스
    class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{
        private List<Memo> listdata;                        //메모리스트

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
                    public boolean onLongClick(View view) {             //메모 리스트에서 원하는 아이템 길게 누르면 데이터 삭제
                        final int position = getAdapterPosition();
                        final int id = (int)maintext.getTag();


                        builder = new AlertDialog.Builder(HouseTodolist.this);
                        builder.setTitle("메모를 삭제하시겠습니까? ");
                        builder.setMessage("\n");
                        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(position != RecyclerView.NO_POSITION){
                                    DBHelper.deleteMemo1(id);
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
