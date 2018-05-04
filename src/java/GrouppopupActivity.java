package com.example.kimhyju.place;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

public class GrouppopupActivity extends Activity implements View.OnClickListener{
    TabHost tabHost;
    EditText makegroup_name,joingroup_name;
    EditText makegroup_password,joingroup_password;
    String groupname;
    String grouppassword;
    Button cancel_group, makegroup, cancel_joingroup,joingroup;
    String userID;
    private static String TAG="phptest_listActivity";
    private static final String TAG_JSON="kimhyju";
    private static final String TAG_PASSWORD="group_password";
    String mJsonString;
    String address;
    String pw, gn;
    ArrayList<String> list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_grouppopup);
        NetworkUtil.setNetworkPolicy();
        makegroup_name=(EditText)findViewById(R.id.makegroup_name);
        makegroup_password=(EditText)findViewById(R.id.makegroup_password);
        cancel_group=(Button)findViewById(R.id.cancel_group);
        makegroup=(Button)findViewById(R.id.makegroup);
        tabHost=(TabHost)findViewById(R.id.tab_host_group);
        cancel_joingroup=(Button)findViewById(R.id.cancel_joingroup);
        joingroup=(Button)findViewById(R.id.joingroup);
        joingroup_name=(EditText)findViewById(R.id.joingroup_name);
        joingroup_password=(EditText)findViewById(R.id.joingroup_password);
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("1").setContent(R.id.tab1_group).setIndicator("그룹 생성하기");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("2").setContent(R.id.tab2_group).setIndicator("그룹 가입하기");
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        cancel_group.setOnClickListener(this);
        makegroup.setOnClickListener(this);
        joingroup.setOnClickListener(this);
        cancel_joingroup.setOnClickListener(this);
        userID=getIntent().getStringExtra("userid");
        list=getIntent().getStringArrayListExtra("list");


    }

    //그룹 생성
    public boolean addgroup(){
        boolean flag=true;
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/addgroup.php");
            String result=request.PhPaddgroup(String.valueOf(groupname),String.valueOf(grouppassword));
            if(result.equals("1")){
                flag=true;
                Toast.makeText(getApplicationContext(),"그룹이 생성되었습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
                //그룹명을 Primary Key로 지정해 그룹명이 중복되면 그룹생성이 안되도록한다.
                Toast.makeText(getApplicationContext(),"이미 사용되고 있는 그룹명입니다!",Toast.LENGTH_LONG).show();
                flag=false;
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
        return flag;
    }

    //그룹을 생성하면 그룹에 나를 포함시킨다.
    public void adduserintogroup(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/adduserintogroup.php");
            String relationid=UUID.randomUUID().toString();
            String result=request.PhPadduserintogroup(String.valueOf(relationid),String.valueOf(groupname),String.valueOf(userID));
            if(result.equals("1")){
            }else if(result.equals("-1")){
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    //그룹에 가입
    public void joingroup(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/adduserintogroup.php");
            String relationid=UUID.randomUUID().toString();
            String result=request.PhPadduserintogroup(String.valueOf(relationid),String.valueOf(gn),String.valueOf(userID));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"그룹에 가입했습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){

            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cancel_group:
                finish();
                break;
            case R.id.makegroup:
                groupname=makegroup_name.getText().toString();
                grouppassword=makegroup_password.getText().toString();
                boolean flag=addgroup();
                if(flag==true)adduserintogroup();
                Intent intent=new Intent(GrouppopupActivity.this,GroupLoadingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("userid",userID);
                startActivity(intent);
                finish();
                break;
            case R.id.cancel_joingroup:
                finish();
                break;
            case R.id.joingroup:
                gn=joingroup_name.getText().toString();
                pw=joingroup_password.getText().toString();
                //현재 리스트에 없는 그룹에만 가입할수 있도록함.
                if(!list.contains(gn)) {
                    address = "http://180.71.13.212:8181/getgroup_password.php?group_name=" + gn;
                    //비밀번호가 일치해야지만 가입되도록 하는 코딩을 GetData클래스 안에서 구현
                    GetData task = new GetData();
                    task.execute(address);
                }
                else{
                    Toast.makeText(getApplicationContext(),"이미 그룹에 가입되어있습니다!",Toast.LENGTH_LONG).show();
                    finish();
                }

                break;
        }
    }

    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mJsonString=result;
            try {
                showResult();
            }catch(Exception e){
                Log.d("error","에러발생");
            }
            Intent intent=new Intent(GrouppopupActivity.this,GroupLoadingActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("userid",userID);
            startActivity(intent);
            finish();
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);
                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder sb = new StringBuilder();
                String line;
                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }
                bufferedReader.close();
                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }
    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String password = item.getString(TAG_PASSWORD);
                //비밀번호가 일치하면 그룹에 가입하는 메소드인 joingroup을 실행
                if(pw.equals(password)){
                    joingroup();
                }
                else{
                    Toast.makeText(getApplicationContext(),"비밀번호가 틀렸습니다!",Toast.LENGTH_LONG).show();
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}
