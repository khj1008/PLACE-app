package com.example.kimhyju.place;

import java.io.Serializable;

public class MarkerInfo implements Serializable{
    String markerid;
    Double lat;
    Double lng;
    String place_nmae;
    String writer;
    String place_content;
    public MarkerInfo(String _markerid,Double _lat,Double _lng,String _place_name,String _writer,String _place_content){
        markerid=_markerid;
        lat=_lat;
        lng=_lng;
        place_nmae=_place_name;
        writer=_writer;
        place_content=_place_content;
    }
    public String getMarkerid(){
        return markerid;
    }
    public Double getLat(){
        return lat;
    }
    public Double getLng(){
        return lng;
    }
    public String getPlace_nmae(){
        return place_nmae;
    }
    public String getWriter(){
        return writer;
    }
    public String getPlace_content(){
        return place_content;
    }
    public void setMarkerid(String _markerid){
        markerid=_markerid;
    }
    public void setLat(Double _lat){
        lat=_lat;
    }
    public void setLng(Double _lng){
        lng=_lng;
    }
    public void setPlace_nmae(String _place_name){
        place_nmae=_place_name;
    }
    public void setWriter(String _writer){
        writer=_writer;
    }
    public void setPlace_content(String _place_content){
        place_content=_place_content;
    }
}
