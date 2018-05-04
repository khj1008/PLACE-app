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

import static com.example.kimhyju.place.R.id.friend_list;

public class GroupActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener , View.OnClickListener{
    View nav_head_view;
    private ImageView profileImage;
    private TextView nameText;
    private Button confirmID;
    String nickName = "";//닉네임
    long userID = 0;//사용자 고유번호
    String userid;
    String pImage = "";//사용자 프로필 경로
    String user_email;
    TextView emailText;
    Toolbar toolbar;
    ArrayList<GroupItem> list;
    ListView group_list;
    GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        NetworkUtil.setNetworkPolicy();
        list=new ArrayList<>();
        list=(ArrayList<GroupItem>)getIntent().getSerializableExtra("list");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        nav_head_view=navigationView.getHeaderView(0);
        profileImage=(ImageView)nav_head_view.findViewById(R.id.profileImage);
        nameText=(TextView)nav_head_view.findViewById(R.id.nameText1);
        emailText=(TextView)nav_head_view.findViewById(R.id.email);
        confirmID=(Button)nav_head_view.findViewById(R.id.confirmID);
        toolbar = (Toolbar) findViewById(R.id.toolbar_group);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("그룹목록");
        confirmID.setOnClickListener(this);
        group_list=(ListView)findViewById(R.id.group_list);
        adapter=new GroupAdapter(getApplicationContext(),R.layout.grouplist,list);
        Session.getCurrentSession().checkAccessTokenInfo();//확인 차 한번 더 토큰확인

        //FloatingActionButton을 누르면 그룹을 생성또는 가입하기 위해 GroupLoadingActivity에서 가져왔던 그룹리스트를 GrouppopupActivity로 전달한다.
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.group_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),GrouppopupActivity.class);
                intent.putExtra("userid",userid);
                intent.putExtra("list",list);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        requestMe();
        adapter.notifyDataSetChanged();
        group_list.setAdapter(adapter);

        //그룹목록을 길게 터치하면 삭제버튼이 나타난다. 삭제버튼을 누르면 그룹이 삭제된다.
        group_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                PopupMenu popup=new PopupMenu(GroupActivity.this,view);
                getMenuInflater().inflate(R.menu.popupmenu,popup.getMenu());
                final int index=position;
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.action_delete){
                            String relationid=list.get(index).getRelationid();
                            Intent intent=new Intent(GroupActivity.this,GroupLoadingActivity.class);
                            deletegroup(relationid);
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

    }

    //뒤로가기버튼을 눌렀을때 메뉴가 열려있으면 메뉴가 닫히고 메뉴가 닫혀있으면 액티비티가 종료된다
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
        if (id == friend_list) {
            Intent intent=new Intent(getApplicationContext(),FriendLoadingActivity.class);
            intent.putExtra("userid",userid);
            startActivity(intent);
            finish();
        } else if (id == R.id.group_list) {

        } else if (id == R.id.logout) {
            UserManagement.requestLogout(new LogoutResponseCallback() {
                @Override
                public void onCompleteLogout() {
                    Intent intent = new Intent(GroupActivity.this, LoginActivity.class);
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
            }
            @Override
            public void onSessionClosed(ErrorResult errorResult) {

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
            }
        });
    }

    //탈퇴하기
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

    //
    public void deletegroup(String relationid){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/deletegroup.php");
            String result=request.PhPdeletegroup(String.valueOf(relationid));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"그룹에서 탈퇴했습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
                Toast.makeText(getApplicationContext(),"Fail!",Toast.LENGTH_LONG).show();
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

}
