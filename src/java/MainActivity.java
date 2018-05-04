package com.example.kimhyju.place;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.callback.UnLinkResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.helper.log.Logger;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.kimhyju.place.R.id.group_list;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, GoogleMap.OnCameraMoveStartedListener, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener ,GoogleMap.InfoWindowAdapter, GoogleMap.OnInfoWindowLongClickListener, GoogleMap.OnInfoWindowClickListener{
    View nav_head_view, mView;
    Toolbar toolbar;
    private ImageView profileImage;
    private TextView nameText;
    private Button confirmID;

    String nickName = "";//닉네임
    long userID = 0;//사용자 고유번호
    String pImage = "";//사용자 프로필 경로
    String user_email;
    String userid;

    Double melatitude;
    Double melongitude;

    LocationManager manager;
    GPSListener gpsListener;

    GoogleMap map;
    Geocoder gc;
    GoogleApiClient mGoogleApiClient;
    Marker currentmarker;
    ArrayList<MarkerInfo> marker_list;
    ArrayList<Marker> markers;
    String currentpoint="";
    String mepoint="";
    String name_place,writer_place,content_place;
    Double lat, lng;
    TextView emailText,place_name,place_writer,place_content;

    Boolean flag=false;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int PLACE_GETMARKER_REQUEST_CODE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        NetworkUtil.setNetworkPolicy();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_head_view=navigationView.getHeaderView(0);
        profileImage=(ImageView)nav_head_view.findViewById(R.id.profileImage);
        nameText=(TextView)nav_head_view.findViewById(R.id.nameText1);
        emailText=(TextView)nav_head_view.findViewById(R.id.email);
        confirmID=(Button)nav_head_view.findViewById(R.id.confirmID);
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        gc=new Geocoder(this, Locale.KOREAN);
        marker_list=new ArrayList<>();
        markers=new ArrayList<>();
        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();

        //MainActivity에서 로그인이 되어있는지 한번더 확인
        Session.getCurrentSession().checkAccessTokenInfo();
        if(Session.getCurrentSession().isOpened()==false)
        {
            //세션이 닫힌 경우 다시 초기 화면으로 이동
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(0,0);
            finish();
        }
        else{
            //세션이 열린 경우 사용자 정보 가져오기
            requestMe();
        }

        setSupportActionBar(toolbar);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //FloatingActionButton클릭이벤트 구현
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),MainpopupActivity.class);
                intent.putExtra("userid",userid);
                if(!currentpoint.equals("")) {
                    intent.putExtra("currentpoint", currentpoint);
                    intent.putExtra("lat", lat);
                    intent.putExtra("lng", lng);
                }else{
                    intent.putExtra("currentpoint",mepoint);
                    intent.putExtra("lat", melatitude);
                    intent.putExtra("lng", melongitude);
                }
                intent.putExtra("username",nickName);
                startActivityForResult(intent,PLACE_GETMARKER_REQUEST_CODE);
            }
        });

        //네비게시연 뷰 구현
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        //구글지도를 프래그먼트에 띄운다.
        SupportMapFragment fragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map=googleMap;

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    map.setMyLocationEnabled(true);
                }
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.534892, 126.983210),5)); //처음시작위치 정의
                map.setOnCameraMoveStartedListener(MainActivity.this);//카메라가 움직이면 실행될 메소드
                map.getUiSettings().setRotateGesturesEnabled(false);//지도가 회전하는기능을 false
                map.setOnMapClickListener(MainActivity.this);//지도를 터치하면 실행되는 동작
                map.setOnMarkerClickListener(MainActivity.this);//마커를 클릭하면 실행되는 동작
                map.setInfoWindowAdapter(MainActivity.this);//마커를 클릭하면 나타나는 정보창을 adapter를 통해서 구현
                map.setOnInfoWindowLongClickListener(MainActivity.this);
                map.setOnInfoWindowClickListener(MainActivity.this);
            }
        });



        try{
            MapsInitializer.initialize(this);
        }catch (Exception e){
            e.printStackTrace();
        }
        startLocationService();
        confirmID.setOnClickListener(this);
    }



    //네비게이션 뷰에서 내ID를 확인하는 버튼 구현
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.confirmID){
            Toast.makeText(getApplicationContext(),"당신의 ID번호는 "+userID+"입니다.",Toast.LENGTH_LONG).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setCheckable(false);
        if (id == R.id.friend_list) {
            Intent intent=new Intent(getApplicationContext(),FriendLoadingActivity.class);
            intent.putExtra("userid",userid);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == group_list) {
            Intent intent=new Intent(getApplicationContext(),GroupLoadingActivity.class);
            intent.putExtra("userid",userid);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (id == R.id.logout) {
            //로그아웃
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });
        } else if (id == R.id.unlink) {
            onClickUnlink();

        }else if(id==R.id.main){

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //현재 지도에 마커가 없으면 클릭한 위치에 마커를 생성하고 마커가 있으면 기존 마커를 삭제하고 새로 클릭한 위치에 마커를 생성
        if(currentmarker!=null){
            currentmarker.remove();
            currentmarker=map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }else{
            currentmarker=map.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
        lat=latLng.latitude;
        lng=latLng.longitude;
        currentpoint=lat.toString()+lng.toString();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
            //마커가 클릭하면 그룹에서 가져온 마커리스트중 일치하는 마커의 정보를 받아와 정보창을 띄운다.
            LatLng position = marker.getPosition();
            Double lat = position.latitude;
            Double lng = position.longitude;
            String cp = Double.toString(lat) + Double.toString(lng);
            int i;
            for (i = 0; i < marker_list.size(); i++) {
                if (marker_list.get(i).getMarkerid().equals(cp)) {
                    flag = true;
                    this.name_place = marker_list.get(i).getPlace_nmae();
                    this.writer_place = marker_list.get(i).getWriter();
                    this.content_place = marker_list.get(i).getPlace_content();
                    marker.showInfoWindow();
                    break;
                } else {
                    flag = false;
                }
            }
        return false;
    }

    //정보창을 길게 클릭하면 마커를 삭제하는 확인창이 팝업된다.
    @Override
    public void onInfoWindowLongClick(final Marker marker) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("마커 삭제");
        alert.setMessage("마커를 삭제 하시겠습니까?");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                marker.remove();
                LatLng latLng=marker.getPosition();
                String lat= Double.toString(latLng.latitude);
                String lng=Double.toString(latLng.longitude);
                String markerid=lat+lng;
                deletemarker(markerid);
            }

        });
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }

        });
        alert.show();
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
    }

    @Override
    //뒤로가기 버튼을 누르면 실행되는 동작
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_item, menu);
        return true;
    }

    //우측상단의 검색버튼을 클릭하면 실행되는 동작
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_search) {
            try {
                //검색버튼을 클릭하면 구글의 장소검색 자동완성 인텐트를 실행한다.
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                // TODO: Handle the error.
            } catch (GooglePlayServicesNotAvailableException e) {
                // TODO: Handle the error.
            }

        }
        return false;
    }

    @Override

    //startForActivity를 통해 실행한 액티비티에서 받아오는 정보
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //장소검색 자동완성 인텐트에서 클릭한 장소에대한 위치값을 받아와 마커를 생성하고 그 위치로 줌되도록 한다.
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                LatLng point=place.getLatLng();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(point,17));
                if(currentmarker!=null){
                    currentmarker.remove();
                    currentmarker=map.addMarker(new MarkerOptions().position(point));
                }else{
                    currentmarker=map.addMarker(new MarkerOptions().position(point));
                }
                manager.removeUpdates(gpsListener);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            } else if (resultCode == RESULT_CANCELED) {
            }
        }
        //플로팅 버튼을 눌러 마커 추가하기 또는 마커가져오기에서 선택한 그룹의 마커들을 가져와 리스트에 저장한다.
        if(requestCode==PLACE_GETMARKER_REQUEST_CODE){
            if(resultCode==RESULT_OK){
                for(int i=0;i<markers.size();i++){
                    markers.get(i).remove();
                }
                marker_list=(ArrayList<MarkerInfo>)data.getSerializableExtra("markers");
                for(int i=0; i<marker_list.size();i++){
                    Double lat=marker_list.get(i).getLat();
                    Double lng=marker_list.get(i).getLng();
                    Marker m = map.addMarker(new MarkerOptions().position(new LatLng(lat,lng)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    markers.add(m);
                }
            }
        }

    }

    //로그인된 사용자 정보를 받아온다.
    private void requestMe() {
        UserManagement.requestMe(new MeResponseCallback() {
            @Override
            public void onFailure(ErrorResult errorResult) {
                String message = "failed to get user info. msg=" + errorResult;
                Logger.d(message);

                //redirectLoginActivity();
            }

            @Override
            public void onSessionClosed(ErrorResult errorResult) {
                //redirectLoginActivity();
            }

            @Override
            public void onSuccess(UserProfile userProfile) {
                Logger.d("UserProfile : " + userProfile);
                nickName = userProfile.getNickname();//닉네임
                userID = userProfile.getId();//사용자 고유번호
                pImage = userProfile.getProfileImagePath();//사용자 프로필 경로
                user_email=userProfile.getEmail();
                emailText.setText(user_email);
                nameText.setText(nickName);
                userid=String.valueOf(userID);
                LinkImage();
            }

            @Override
            public void onNotSignedUp() {
                //showSignup();
            }
        });
    }

    //카카오톡에서 받아온 프로필이미지의 url주소를 이용해 비트맵이미지를 얻어온다.
    Handler handler = new Handler();
    public void LinkImage() {
        if (pImage.equals("img")) ;
        else {
            //new ImageDownload().execute(pImage);
            // 인터넷 상의 이미지 보여주기

            // 1. 권한을 획득한다 (인터넷에 접근할수 있는 권한을 획득한다)  - 메니페스트 파일
            // 2. Thread 에서 웹의 이미지를 가져오기
            // 3. 외부쓰레드에서 메인 UI에 접근위해 Handler 사용

            //Thread t = new Thread(Runnable 객체 생성);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {    // 오래 걸릴 작업 구현
                    // TODO Auto-generated method stub
                    try {
                        final URL url = new URL(pImage);
                        InputStream is = url.openStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {  // 화면에 그려줄 작업
                                profileImage.setImageBitmap(bm);
                                Log.d("imageurl",String.valueOf(url));
                            }
                        });
                        profileImage.setImageBitmap(bm); //비트맵 객체로 보여주기
                    } catch (Exception e) {
                    }
                }
            });
            t.start();
        }
    }

    //탈퇴하기
    private void onClickUnlink() {
        final String appendMessage = getString(R.string.com_kakao_confirm_unlink);
        new AlertDialog.Builder(this)
                .setMessage(appendMessage)
                .setPositiveButton(getString(R.string.com_kakao_ok_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UserManagement.requestUnlink(new UnLinkResponseCallback() {
                                    @Override
                                    public void onFailure(ErrorResult errorResult) {
                                        Logger.e(errorResult.toString());
                                    }

                                    @Override
                                    public void onSessionClosed(ErrorResult errorResult) {
                                        //redirectLoginActivity();
                                    }

                                    @Override
                                    public void onNotSignedUp() {
                                        //redirectSignupActivity();
                                    }

                                    @Override
                                    public void onSuccess(Long userId) {
                                        deleteuser(userID);
                                        Toast.makeText(getApplicationContext(), "탈퇴되었습니다.", Toast.LENGTH_LONG).show();
                                        //redirectLoginActivity();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        startActivity(intent);
                                        overridePendingTransition(0,0);
                                        finish();
                                    }
                                });
                                dialog.dismiss();

                            }
                        })
                .setNegativeButton(getString(R.string.com_kakao_cancel_button),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();

    }

    //탈퇴하기를 클릭하고 onClickUnlink메소드가 실행되면 데이터베이스에서 사용자 정보를 삭제한다.
    public void deleteuser(long userid){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/delete.php");
            String result=request.PhPdelete(String.valueOf(userid));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"탈퇴되었습니다.",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    //GPSListener구현
    public class GPSListener implements LocationListener {
        public void onLocationChanged(Location location){
            melatitude=location.getLatitude();
            melongitude=location.getLongitude();
            mepoint=melatitude.toString()+melongitude.toString();
            showCurrentLocation(melatitude, melongitude);
        }
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    public void showCurrentLocation(Double latitude,Double longitude){
        LatLng curPoint=new LatLng(latitude,longitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint,17));
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void startLocationService(){
        manager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        gpsListener=new GPSListener();
        long minTime=10000;
        float minDistance=0;
        try {
            manager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, //위치 정보 확인 방법 설정
                    minTime, // 위치 정보 갱신 시간 설정
                    minDistance, //위치 정보 갱신을 위한 최소 이동거리 설정
                    gpsListener);//위치가 변동될 때마다 위치 정보 갱신을 위한 리스너 설정
            manager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    minTime,
                    minDistance,
                    gpsListener);
            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastLocation != null) {
                Double latitude = lastLocation.getLatitude();
                Double longitude = lastLocation.getLongitude();
            }
        } catch(SecurityException ex) {
            ex.printStackTrace();
        }

    }

    //카메라가 움직이면 gpsListener의 Update를 제거한다.
    @Override
    public void onCameraMoveStarted(int reason) {
        if(reason== GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
            manager.removeUpdates(gpsListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    //마커를 클릭했을때 나타나는 정보창을 구현한다.
    @Override
    public View getInfoWindow(Marker marker) {
        mView=getLayoutInflater().inflate(R.layout.information,null);
        place_name=(TextView)mView.findViewById(R.id.place_name);
        place_writer=(TextView)mView.findViewById(R.id.place_writer);
        place_content=(TextView)mView.findViewById(R.id.place_content);
        place_name.setText(name_place);
        place_writer.setText(writer_place);
        place_content.setText(content_place);
        if(flag==true){
            return mView;
        }else {
            return null;
        }
    }

    @Override
    public View getInfoContents(Marker marker) {
         return null;
    }

    //마커를 데이터베이스에서 삭제한다.
    public void deletemarker(String markerid){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/deletemarker.php");
            String result=request.PhPdeletemarker(String.valueOf(markerid));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"마커를 제거했습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
                Toast.makeText(getApplicationContext(),"Fail!",Toast.LENGTH_LONG).show();
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

}
