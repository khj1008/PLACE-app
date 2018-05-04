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

//바로 그룹목록으로 넘어가면 그룹목록을 데이터베이스에 받아오기전에 화면이 출력되어 간혹 그룹목록이 화면에 나타나지 않는 현상이
//발생함으로써 GroupLoadingActivity를 구현해 그룹목록을 데이터베이스에서 받아오기가 완료되면 intent로 그룹목록을 넘겨주고
//GroupActivity가 실행되도록 하였다.
public class GroupLoadingActivity extends Activity {
    private static String TAG="phptest_listActivity";
    private static final String TAG_JSON="kimhyju";
    ArrayList<GroupItem> list;
    String mJsonString;
    String userid;
    String address;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NetworkUtil.setNetworkPolicy();
        list=new ArrayList<>();
        userid=getIntent().getStringExtra("userid");
        address="http://180.71.13.212:8181/getgroup.php?userid="+userid;
        GetData task=new GetData();
        task.execute(address);
    }

    //그룹목록을 데이터베이스에서 받아와서 그룹액티비티로 보내기위해 리스트에 저장한다
    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //그룹을 받아와서 리스트에 모두 저장하면 그룹액티비티에 리스트를 넘겨주고 그룹액티비티를 실행한다
        @Override
        protected void onPostExecute(String result) {
            mJsonString=result;
            try {
                showResult();
            }catch(Exception e){

            }
            Intent intent=new Intent(GroupLoadingActivity.this,GroupActivity.class);
            intent.putExtra("list",list);
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
                String relationid=item.getString("relationid");
                String groupname = item.getString("group_name");
                GroupItem gi=new GroupItem(relationid, groupname);
                list.add(gi);
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}
