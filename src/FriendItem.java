package com.example.kimhyju.place;

import java.io.Serializable;

public class FriendItem implements Serializable{
    String pImage;
    String nickName;
    String userid;

    public FriendItem(String _pImage,String _nickName,String _userid){
        pImage=_pImage;
        nickName=_nickName;
        userid=_userid;
    }
    public void setpImage(String _pImage){
        pImage=_pImage;
    }
    public void setNickName(String _nickName){
        nickName=_nickName;
    }
    public String getpImage(){
        return pImage;
    }
    public String getNickName(){
        return nickName;
    }
    public String getUserid(){return userid;}
}
