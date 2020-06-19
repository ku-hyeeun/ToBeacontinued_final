package com.androidapp.tobeacontinue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidapp.tobeacontinue.Todolist.CafeteriaTodolist;
import com.androidapp.tobeacontinue.database.GeoDBHelper;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    GeoDBHelper databaseHelper;

    Button AddressMap_Button, save_Button;
    TextView result_textView;
    EditText address_editText, todo_editText;

    final Geocoder geocoder = new Geocoder(this);

    final MarkerOptions markerOptions = new MarkerOptions();    //마커 옵션(title, snippet, 반경 등)

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeoFenceHelper geoFenceHelper;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 1001;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 1002;

    private float GEOFENCE_RADIUS = 100;        //마커 반경(100m)

    private String GEOFENCE_ID = "JC_GEOFENCE_ID";      //Geofence ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        save_Button = findViewById(R.id.save_Button);       //저장버튼
        AddressMap_Button = findViewById(R.id.AddressMap_Button);   //확인버튼
        result_textView = findViewById(R.id.result_textView);       //주소결과창
        address_editText = findViewById(R.id.address_editText);     //주소입력창
        todo_editText = findViewById(R.id.todo_editText);       //할일입력창

        //Location API 사용을 위하여 Geofencing Client 인스턴스를 생성
        geofencingClient = LocationServices.getGeofencingClient(this);
        geoFenceHelper = new GeoFenceHelper(this);

        //데베정의
        databaseHelper = new GeoDBHelper(MapsActivity.this);

        //지오코딩 버튼처리----------------------------------

        //주소를 지도에 표시
        AddressMap_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 주소입력 후 지도버튼 클릭시 해당 위도경도값의 지도화면으로 이동
                List<Address> list = null;

                String str = address_editText.getText().toString();

                try {
                    list = geocoder.getFromLocationName
                            (str, // 지역 이름
                                    10); // 읽을 개수

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test","입출력 오류 - 서버에서 주소변환시 에러발생");
                }

                if (list != null) {
                    if (list.size() == 0) {
                        result_textView.setText(getString(R.string.no_address));
                    } else {

                        //입력한 주소 결과창에 표시
                        result_textView.setText(list.get(0).getAddressLine(0).toString());

                        // 해당되는 주소로 카메라이동
                        Address addr = list.get(0);
                        double lat = addr.getLatitude();
                        double lon = addr.getLongitude();
                        LatLng searchLocation = new LatLng(lat, lon);

                        permission(searchLocation);

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchLocation,16));
                    }
                }

                //장소와 할 일을 적지 않았을 경우
                if(address_editText.length() != 0 && todo_editText.length() != 0){
                }
                else{
                    Toast.makeText(MapsActivity.this,getString(R.string.maps_toast1),Toast.LENGTH_SHORT).show();
                }

            }
        });
        //지오코딩 버튼처리----------------------------------

        //저장버튼 클릭시 지오펜스 추가, 할일 db에 저장
        save_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(address_editText.length() != 0 && todo_editText.length() != 0){
                    addGeofence(markerOptions.getPosition(),GEOFENCE_RADIUS);

                    //주소, 할 일 입력 창에 작성한 text를 DB에 저장
                    String location_title = address_editText.getText().toString();
                    String todo_snippet = todo_editText.getText().toString();

                    Intent intent = new Intent();
                    intent.putExtra("place",location_title);
                    intent.putExtra("contents",todo_snippet);
                    setResult(1,intent);
                    finish();
                }
                else{
                    Toast.makeText(MapsActivity.this,getString(R.string.maps_toast1),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    //맵 시작시 서울이 default
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng seoul = new LatLng(37.56, 126.97);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul,16));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);
    }

    //구글맵에서 현재위치 permission 확인
    private void enableUserLocation(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, FINE_LOCATION_ACCESS_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, FINE_LOCATION_ACCESS_REQUEST_CODE);
        }
    } else {
            //위치가 켜져 있는지 확인
                LocationManager lm = (LocationManager)
                        getSystemService(Context. LOCATION_SERVICE ) ;
                boolean gps_enabled = false;

                try {
                    gps_enabled = lm.isProviderEnabled(LocationManager. GPS_PROVIDER ) ;
                } catch (Exception e) {
                    e.printStackTrace() ;
                }

                if (gps_enabled) {
                    //켜져 있을 시 현재위치 버튼 보이기기
                   mMap.setMyLocationEnabled(true);
                } else{
                    //위치 권한 설정 이유 설명, 위치 설정창으로 넘어가기
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(getString(R.string.location_access));
                builder.setMessage(getString(R.string.location_access_alert));
                builder.setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent settingsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(settingsIntent);
                                mMap.setMyLocationEnabled(true);
                            }
                        });
                builder.setNegativeButton(getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), getString(R.string.location_access_toast), Toast.LENGTH_LONG).show();
                            }
                        });
                builder.show();
            }
        }
    }

    //ACCESS_FINE_LOCATION과 ACCESS_BACKGROUND_LOCATION에 대한 권한 요청 결과를 가져옴
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //ACCESS_FINE_LOCATION에 대한 권한 요청 결과
        if(requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission이 있을 때
                mMap.setMyLocationEnabled(true);
            }else{
                //permission이 없을 때
                Toast.makeText(this,getString(R.string.maps_toast2)
                        ,Toast.LENGTH_SHORT).show();
            }
        }
        //ACCESS_BACKGROUND_LOCATION에 대한 권한 요청 결과
        if(requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //permission이 있을 때
                Toast.makeText(this, getString(R.string.maps_toast3),Toast.LENGTH_SHORT).show();
            }else{
                //permission이 없을 때
                Toast.makeText(this,getString(R.string.maps_toast4)
                        ,Toast.LENGTH_SHORT).show();
            }
        }
    }

    //구글맵 길게 클릭 시 퍼미션 확인 후 마커표시
    @Override
    public void onMapLongClick(LatLng latLng){
        permission(latLng);
    }

    //백그라운드 퍼미션, 마커 추가
    public void permission(LatLng latLng){
        if(address_editText.length() != 0 && todo_editText.length() != 0) {
            if (Build.VERSION.SDK_INT >= 29) {
                //background permission 필요함
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    setMarker(latLng);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                    }
                }
            } else {
                setMarker(latLng);
            }
        }
        else{
            Toast.makeText(MapsActivity.this,getString(R.string.maps_toast1),Toast.LENGTH_SHORT).show();
        }
    }

    //마커세팅
    private void setMarker(LatLng latLng){
        mMap.clear(); //마지막에 있는 marker만 표시되도록
        addMarker(latLng);      //마커추가
        addCircle(latLng, GEOFENCE_RADIUS); //마커 반경추가
    }

    //지오펜스 개체 만들기
    public void addGeofence(LatLng latLng, float radius){

        Geofence geofence = geoFenceHelper.getGeofence(GEOFENCE_ID, latLng, radius,
                Geofence.GEOFENCE_TRANSITION_ENTER);

        GeofencingRequest geofencingRequest = geoFenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geoFenceHelper.getPendingIntent();

        geofencingClient.addGeofences(geofencingRequest,pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    //Geofence 추가 성공
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess: Geofence Added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    //Geofence 추가 실패
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geoFenceHelper.getErrorString(e);
                        Log.d(TAG,"onFailure: " + errorMessage);
                    }
                });

    }

    //마커 title,snippet 추가
    private void addMarker(final LatLng latLng){

        //주소 입력 창에 입력한 text를 marker의 title로 사용
        //할 일 입력 창에 입력학 text를 marker의 snippet으로 사용
        String location_title = address_editText.getText().toString();
        String todo_snippet = todo_editText.getText().toString();

        markerOptions.position(latLng);
        markerOptions.title(location_title);
        markerOptions.snippet(todo_snippet);

        mMap.addMarker(markerOptions);
    }

    //반경설정
    private void addCircle(LatLng latLng, float radius){
        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);       //원의 중앙을 받아온 위도, 경도로 설정
        circleOptions.radius(radius);       //반경설정
        circleOptions.strokeColor(Color.argb(255,255,0,0)); //색상
        circleOptions.fillColor(Color.argb(64,255,0,0));    //색상
        circleOptions.strokeWidth(4);   //두께
        mMap.addCircle(circleOptions);
    }
}
