package com.example.kimhyju.place;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class FriendLoadingActivity extends Activity{
    private static String TAG="phptest_listActivity";
    private static final String TAG_JSON="kimhyju";
    ArrayList<String> list;
    String mJsonString;
    String userid;
    String address;
    ArrayList<FriendItem> friendItems;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkUtil.setNetworkPolicy();
        list=new ArrayList<>();
        userid=getIntent().getStringExtra("userid");
        address="http://180.71.13.212:8181/getfriend.php?me_userid="+userid;
        friendItems=new ArrayList<>();
        GetData task=new GetData();
        task.execute(address);
    }

    //친구목록을 데이터베이스에서 받아오고 ArrayList에 저장한다. 친구목록을 모두 받아오면 intent에 리스트를 넘겨주고 FrienActivity를 실행한다.
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
            Intent intent=new Intent(FriendLoadingActivity.this,FriendActivity.class);
            intent.putExtra("friendlist",friendItems);
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
                String friendname = item.getString("friend_name");
                String frienduserid=item.getString("friend_userid");
                String friendimage = item.getString("friend_image");
                FriendItem friendItem=new FriendItem(friendimage,friendname,frienduserid);
                friendItems.add(friendItem);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}
