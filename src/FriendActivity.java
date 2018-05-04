package com.example.kimhyju.place;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

public class FriendActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener{
    private static String TAG = "phptest_MainActivity";
    View nav_head_view;
    private ImageView profileImage;
    private TextView nameText;
    private Button confirmID;
    String pImage="";
    String nickName = "";
    long userID = 0;
    String userid;
    String user_email;
    TextView emailText;
    Toolbar toolbar;
    ListView friend_list;
    ArrayList<FriendItem> friends;
    FriendAdapter adapter;
    String friendid1;
    String friendid2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        NetworkUtil.setNetworkPolicy();
        requestMe();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_head_view=navigationView.getHeaderView(0);
        profileImage=(ImageView)nav_head_view.findViewById(R.id.profileImage);
        nameText=(TextView)nav_head_view.findViewById(R.id.nameText1);
        emailText=(TextView)nav_head_view.findViewById(R.id.email);
        confirmID=(Button)nav_head_view.findViewById(R.id.confirmID);
        toolbar = (Toolbar) findViewById(R.id.toolbar_friend);
        friend_list=(ListView)findViewById(R.id.friend_list);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("친구목록");
        friends=(ArrayList<FriendItem>)getIntent().getSerializableExtra("friendlist");

        //친구 추가하기 FloatingACtionButton
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(FriendActivity.this,FriendpopupActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("username",nickName);
                intent.putExtra("userimage",pImage);
                intent.putExtra("friendlist",friends);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        confirmID.setOnClickListener(this);

        //친구목록을 길게 터치하면 삭제버튼이 나타난다.
        friend_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popup=new PopupMenu(FriendActivity.this,view);
                getMenuInflater().inflate(R.menu.popupmenu,popup.getMenu());
                final int index=position;
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.action_delete){
                            String frienduserid=friends.get(index).getUserid();
                            friendid1=userid+frienduserid;
                            friendid2=frienduserid+userid;
                            deletefriend1();
                            deletefriend2();
                            Intent intent=new Intent(FriendActivity.this,FriendLoadingActivity.class);
                            intent.putExtra("userid",userid);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                        return false;
                    }
                });
                popup.show();
                return false;
            }
        });
        adapter=new FriendAdapter(getApplicationContext(),R.layout.friendlist,friends);
        friend_list.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        item.setCheckable(false);
        if (id == R.id.friend_list) {
            // Handle the camera action
        } else if (id == R.id.group_list) {
            Intent intent=new Intent(FriendActivity.this, GroupLoadingActivity.class);
            intent.putExtra("userid",userid);
            startActivity(intent);
            finish();
        } else if (id == R.id.logout) {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Intent intent = new Intent(FriendActivity.this, LoginActivity.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);
                    finish();
                }
            });
        } else if (id == R.id.unlink) {
            onClickUnlink();

        }else if(id==R.id.main){
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


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

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.confirmID){
            Toast.makeText(getApplicationContext(),"당신의 ID번호는 "+userID+"입니다.",Toast.LENGTH_LONG).show();
        }
    }


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

    public void deleteuser(long userid){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/delete.php");
            String result=request.PhPdelete(String.valueOf(userid));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"Success!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
                Toast.makeText(getApplicationContext(),"Fail!",Toast.LENGTH_LONG).show();
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    //친구 삭제(나-친구)
    public void deletefriend1(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/deletefriend.php");
            String result=request.PhPdeletefriend(String.valueOf(friendid1));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"친구를 삭제했습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }
    //친구삭제(친구-나)
    public void deletefriend2(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/deletefriend.php");
            String result=request.PhPdeletefriend(String.valueOf(friendid2));
            if(result.equals("1")){
            }else if(result.equals("-1")){
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

}
