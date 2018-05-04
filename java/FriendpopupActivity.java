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

public class FriendpopupActivity extends Activity implements View.OnClickListener{
    String friend_userid,friend_name="",friend_image="";
    EditText friend_userid_input;
    Button cancel_friend,addfriend;
    String me_userid,me_name,me_image;
    String friendid1,friendid2;
    String mJsonString;
    String address;
    ArrayList<FriendItem> friendlist;
    Boolean iscontained=false;
    private static String TAG="phptest_listActivity";
    private static final String TAG_JSON="kimhyju";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_friendpopup);
        NetworkUtil.setNetworkPolicy();
        me_userid=getIntent().getStringExtra("userid");
        me_name=getIntent().getStringExtra("username");
        me_image=getIntent().getStringExtra("userimage");
        friend_userid_input=(EditText)findViewById(R.id.friendID);
        cancel_friend=(Button)findViewById(R.id.cancel_friend);
        addfriend=(Button)findViewById(R.id.addfriend);
        cancel_friend.setOnClickListener(this);
        addfriend.setOnClickListener(this);
        friendlist=new ArrayList<>();
        friendlist=(ArrayList<FriendItem>)getIntent().getSerializableExtra("friendlist");
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.cancel_friend:
                finish();
                break;
            case R.id.addfriend:
                //추가하려는 친구가 이미 친구로 존재하지 않을때만 친구가 추가되도록함
                friend_userid=friend_userid_input.getText().toString();
                for(int i=0; i<friendlist.size();i++){
                    if(friendlist.get(i).getUserid().equals(friend_userid)){
                        iscontained=true;
                        Toast.makeText(getApplicationContext(),"이미 친구입니다!",Toast.LENGTH_LONG).show();
                        break;
                    }else{
                        iscontained=false;
                    }
                }
                if(iscontained==false) {
                    friendid1 = me_userid + friend_userid;
                    friendid2 = friend_userid + me_userid;
                    address = "http://180.71.13.212:8181/getfriendinfo.php?userid=" + friend_userid;
                    GetData task = new GetData();
                    task.execute(address);
                }
                break;
        }
    }

    public void addfriend1(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/addfriend.php");
            String result=request.PhPaddfriend(String.valueOf(friendid1),String.valueOf(me_name),String.valueOf(me_userid),String.valueOf(friend_name),String.valueOf(friend_userid),String.valueOf(friend_image));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"친구가 추가되었습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){

            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }

    public void addfriend2(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/addfriend.php");
            String result=request.PhPaddfriend(String.valueOf(friendid2),String.valueOf(friend_name),String.valueOf(friend_userid),String.valueOf(me_name),String.valueOf(me_userid),String.valueOf(me_image));
            if(result.equals("1")){
            }else if(result.equals("-1")){
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
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
            mJsonString=result;
            try {
                showResult();
            }catch(Exception e){
            }
            if(!friend_name.equals("")&&!friend_image.equals("")) {
                addfriend1();
                addfriend2();
            }else{
                Toast.makeText(getApplicationContext(),"없는 친구입니다!",Toast.LENGTH_LONG).show();
            }
            Intent intent=new Intent(FriendpopupActivity.this,FriendLoadingActivity.class);
            intent.putExtra("userid",me_userid);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            super.onPostExecute(result);
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
                String imgurl = item.getString("imgurl");
                String name=item.getString("name");
                friend_image=imgurl;
                friend_name=name;
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}
