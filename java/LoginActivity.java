package com.example.kimhyju.place;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.kakao.auth.ErrorCode;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeResponseCallback;
import com.kakao.usermgmt.response.model.UserProfile;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.net.MalformedURLException;


public class LoginActivity extends AppCompatActivity {
    SessionCallback callback;
    com.kakao.usermgmt.LoginButton loginButton;
    long userID = 0;//사용자 고유번호
    String pImage = "";//사용자 프로필 경로
    String nickName;
///////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        NetworkUtil.setNetworkPolicy();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        loginButton=(com.kakao.usermgmt.LoginButton)findViewById(R.id.com_kakao_login);
        loginButton.setVisibility(View.INVISIBLE);
        Handler handler=new Handler();
        checkDangerousPermissions();
        Session.getCurrentSession().checkAccessTokenInfo();

        //이미 로그인되어있는지를 판단, 로고화면을 1초동안 보여줌
        if(Session.getCurrentSession().isOpened()==true){
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.fade,R.anim.hold);
                        finish();
                    }
                },1000);


        }else{
            loginButton.setVisibility(View.VISIBLE);
        }

        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
    }

    //재 로그인 요청
    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade,R.anim.hold);
        finish();
    }

    //간편로그인시 호출되는 부분
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //간편로그인시 호출 ,없으면 간편로그인시 로그인 성공화면으로 넘어가지 않음
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

    //SessionCallback 클래스 구현
    private class SessionCallback implements ISessionCallback {

        @Override
        public void onSessionOpened() {

            UserManagement.requestMe(new MeResponseCallback() {

                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);

                    ErrorCode result = ErrorCode.valueOf(errorResult.getErrorCode());
                    if (result == ErrorCode.CLIENT_ERROR_CODE) {
                        finish();
                    } else {
                        //재 로그인
                        Toast.makeText(getApplicationContext(),"다시 로그인 해주세요.",Toast.LENGTH_SHORT).show();
                        redirectLoginActivity();
                    }
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {
                }

                @Override
                public void onNotSignedUp() {
                }

                @Override
                public void onSuccess(UserProfile userProfile) {
                    //로그인 성공 시 로그인한 사용자의 일련번호, 닉네임, 이미지url 리턴
                    //사용자 캐시 정보 업데이트
                    if (userProfile != null) {
                        userProfile.saveUserToCache();
                    }
                    Logger.e("succeeded to update user profile",userProfile,"\n");
                    //////////////////

                    nickName = userProfile.getNickname();//닉네임
                    userID = userProfile.getId();//사용자 고유번호
                    pImage = userProfile.getProfileImagePath();//사용자 프로필 경로*/
                    Log.e("UserProfile", userProfile.toString());//전체 정보 출력
                    insertuser();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade,R.anim.hold);
                    finish();

                }
            });

        }

        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            // 세션 연결이 실패했을때
            if(exception != null) {
                Toast.makeText(getApplicationContext(),"세션 연결 실패",Toast.LENGTH_SHORT).show();
                Logger.e(exception);
            }
        }
    }

    //카카오톡으로 로그인하면 데이터베이스에 사용자 정보를 저장
    public void insertuser(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/insert.php");
            String result=request.PhPinsert(String.valueOf(userID),String.valueOf(pImage),String.valueOf(nickName));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"가입되었습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    //권한 체크
    private void checkDangerousPermissions() {
        String[] permissions = {//import android.Manifest;
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.INTERNET//쓰기 권한
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    // 사용자의 권한 확인 후 사용자의 권한에 대한 응답 결과를 확인하는 콜백 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
            }
        }
    }



}

