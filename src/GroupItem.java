package com.example.kimhyju.place;

import java.io.Serializable;

public class GroupItem implements Serializable{
    String groupname;
    String relationid;
    public GroupItem(String _relationid,String _groupname){
        this.relationid=_relationid;
        this.groupname=_groupname;
    };
    public GroupItem(String s){
        this.groupname=s;
    }
    public void setGroupname(String s){
        groupname=s;
    }
    public String getGroupname(){
        return groupname;
    }
    public String getRelationid(){return relationid;}
}
