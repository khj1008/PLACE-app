package com.example.kimhyju.place;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
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

public class MainpopupActivity extends Activity implements View.OnClickListener{
    TabHost tabHost;
    private static String TAG="phptest_listActivity";
    private static final String TAG_JSON="kimhyju";
    private static final String TAG_GROUPNAME="group_name";
    String mJsonString;
    ListView getgroup_list,setgroup_list;
    String userID;
    String address, address1;
    Double lat,lng;
    String currentpoint;
    LinearLayout writecontent, groupchoice;
    Animation transRightMid, transMidRight, transLeftMid, transMidLeft;
    Button mainpop_cancel,mainpop_next,mainpop_back,mainpop_confirm,mainpop_cancel1,mainpop_confirm1;
    EditText placename, placecontent;
    String place_name,place_content;
    String username;
    ArrayList<String> groupname_list;
    ArrayAdapter<String> adapter;
    ArrayList<MarkerInfo> marker_list;
    String gn;
    GetMarker task1;
    boolean page =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mainpopup);
        NetworkUtil.setNetworkPolicy();
        tabHost=(TabHost)findViewById(R.id.tab_host_main);
        getgroup_list=(ListView)findViewById(R.id.check_getgroup_list);
        setgroup_list=(ListView)findViewById(R.id.check_setgroup_list);
        writecontent=(LinearLayout)findViewById(R.id.writecontent);
        groupchoice=(LinearLayout)findViewById(R.id.groupchoice);
        transLeftMid= AnimationUtils.loadAnimation(this,R.anim.translate_leftmid);
        transMidLeft= AnimationUtils.loadAnimation(this,R.anim.translate_midleft);
        transRightMid= AnimationUtils.loadAnimation(this,R.anim.translate_rightmid);
        transMidRight= AnimationUtils.loadAnimation(this,R.anim.translate_midright);
        mainpop_cancel=(Button)findViewById(R.id.mainpop_cancel);
        mainpop_next=(Button)findViewById(R.id.mainpop_next);
        mainpop_back=(Button)findViewById(R.id.mainpop_back);
        mainpop_confirm=(Button)findViewById(R.id.mainpop_confirm);
        mainpop_cancel1=(Button)findViewById(R.id.mainpop_cancel1);
        mainpop_confirm1=(Button)findViewById(R.id.mainpop_confirm1);
        placename=(EditText)findViewById(R.id.place_name);
        placecontent=(EditText)findViewById(R.id.place_content);
        mainpop_cancel.setOnClickListener(this);
        mainpop_next.setOnClickListener(this);
        mainpop_back.setOnClickListener(this);
        mainpop_confirm.setOnClickListener(this);
        mainpop_cancel1.setOnClickListener(this);
        mainpop_confirm1.setOnClickListener(this);
        groupname_list=new ArrayList<>();

        SlidingPageAnimationListener animationListener=new SlidingPageAnimationListener();
        tabHost.setup();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("1").setContent(R.id.tab1_main).setIndicator("마커 추가하기");
        TabHost.TabSpec tab2 = tabHost.newTabSpec("2").setContent(R.id.tab2_main).setIndicator("마커 가져오기");
        tabHost.addTab(tab1);
        tabHost.addTab(tab2);
        adapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_single_choice,groupname_list);

        userID=getIntent().getStringExtra("userid");
        lat=getIntent().getDoubleExtra("lat",0);
        lng=getIntent().getDoubleExtra("lng",0);
        username=getIntent().getStringExtra("username");
        currentpoint=getIntent().getStringExtra("currentpoint");
        address="http://180.71.13.212:8181/getgroup.php?userid="+userID;
        GetData task=new GetData();
        task.execute(address);
        marker_list=new ArrayList<>();

        transLeftMid.setAnimationListener(animationListener);
        transMidRight.setAnimationListener(animationListener);
        transRightMid.setAnimationListener(animationListener);
        transMidLeft.setAnimationListener(animationListener);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.mainpop_cancel:
            case R.id.mainpop_cancel1:
                finish();
                break;
            case R.id.mainpop_next://마커 추가하기 탭에서 다음 버튼을 누르면 슬라이드되면서 그룹선택 페이지가 나온다.
                place_name=placename.getText().toString();
                place_content=placecontent.getText().toString();
                if(place_name.isEmpty()==true||place_content.isEmpty()==true){
                    Toast.makeText(getApplicationContext(),"제목과 내용을 입력해주세요!",Toast.LENGTH_LONG).show();
                }else {
                    groupchoice.setVisibility(View.VISIBLE);
                    groupchoice.startAnimation(transRightMid);
                    writecontent.startAnimation(transMidLeft);
                }
                break;
            case R.id.mainpop_back:
                writecontent.setVisibility(View.VISIBLE);
                groupchoice.startAnimation(transMidRight);
                writecontent.startAnimation(transLeftMid);
                break;
            case R.id.mainpop_confirm://마커 추가하기
                place_name=placename.getText().toString();
                place_content=placecontent.getText().toString();
                int getposition=getgroup_list.getCheckedItemPosition();
                if(getposition!=-1) {
                    gn = groupname_list.get(getposition);
                    addmarker();
                    address1 = "http://180.71.13.212:8181/getmarker.php?name_group=" + gn;
                    task1 = new GetMarker();
                    task1.execute(address1);
                }else{
                    Toast.makeText(getApplicationContext(),"그룹을 선택해주세요!",Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.mainpop_confirm1://마커 가져오기
                int setposition=setgroup_list.getCheckedItemPosition();
                if(setposition!=-1) {
                    gn = groupname_list.get(setposition);
                    address1 = "http://180.71.13.212:8181/getmarker.php?name_group=" + gn;
                    task1 = new GetMarker();
                    task1.execute(address1);
                }else{
                    Toast.makeText(getApplicationContext(),"그룹을 선택해주세요!",Toast.LENGTH_LONG).show();
                }
                break;

        }
    }

    //마커를 데이터베이스에 등록한다.
    public void addmarker(){
        try{
            PHPRequest request=new PHPRequest("http://180.71.13.212:8181/addmarker.php");
            String result=request.PhPaddmarker(String.valueOf(currentpoint),String.valueOf(lat),String.valueOf(lng),String.valueOf(place_name),String.valueOf(gn),String.valueOf(place_content),String.valueOf(username),String.valueOf(userID));
            if(result.equals("1")){
                Toast.makeText(getApplicationContext(),"마커를 등록했습니다!",Toast.LENGTH_LONG).show();
            }else if(result.equals("-1")){
                Toast.makeText(getApplicationContext(),"마커 등록에 실패했습니다!",Toast.LENGTH_LONG).show();
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }
    }


    //그룹목록을 데이터베이스에서 가져오는 클래스
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
                String groupname = item.getString(TAG_GROUPNAME);
                groupname_list.add(groupname);
            }
            getgroup_list.setAdapter(adapter);
            setgroup_list.setAdapter(adapter);
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }

    //마커추가하기 탭에서 화면을 넘기는 애니메이션 클래스
    private class SlidingPageAnimationListener implements Animation.AnimationListener{
        @Override
        public void onAnimationStart(Animation animation) {
        }
        @Override
        public void onAnimationEnd(Animation animation) {
            if(page){
                groupchoice.setVisibility(View.INVISIBLE);
                page=false;
            }else{
                writecontent.setVisibility(View.INVISIBLE);
                page=true;
            }
        }
        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    }

    //그룹에 속한 마커를 가져오는 클래스
    private class GetMarker extends AsyncTask<String, Void, String> {
        String errorString = null;
        ProgressDialog progressDialog=new ProgressDialog(MainpopupActivity.this);
        @Override
        protected void onPreExecute() {
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("로딩중입니다...");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            mJsonString=result;
            try {
                showMarker();
            }catch(Exception e){
                Log.d("error","에러발생");
            }
            //메인 팝업 액티비티에서 마커를 가져와 ArrayList에 저장하고 그 리스트를 intent로 메인액티비티에 넘겨준다.
            progressDialog.dismiss();
            Intent intent=new Intent(MainpopupActivity.this,MainActivity.class);
            intent.putExtra("markers",marker_list);
            setResult(RESULT_OK,intent);
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
    private void showMarker(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                String markerid=item.getString("markerid");
                Double lat=item.getDouble("lat");
                Double lng=item.getDouble("lng");
                String name_place=item.getString("name_place");
                String content=item.getString("content");
                String name_user=item.getString("name_user");
                marker_list.add(new MarkerInfo(markerid,lat,lng,name_place,name_user,content));
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}
