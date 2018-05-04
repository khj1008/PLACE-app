package com.example.kimhyju.place;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class FriendAdapter extends ArrayAdapter {
    ArrayList<FriendItem> FriendItemList=new ArrayList<>();
    TextView friendname;
    ImageView friendimage;
    public FriendAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<FriendItem> item) {
        super(context, resource, item);
        this.FriendItemList=item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friendlist, parent, false);
        }
        FriendItem frienditem=FriendItemList.get(position);
        friendname=(TextView)convertView.findViewById(R.id.friendname);
        friendimage=(ImageView)convertView.findViewById(R.id.friendimage);
        friendname.setText(frienditem.getNickName());
        //친구의 이미지경로를 불러와 이미지뷰에 비트맵으로 입력한다.
        LinkImage(frienditem.getpImage(),friendimage);
        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Object getItem(int position) {
        return FriendItemList.get(position);
    }
    @Override
    public int getCount() {
        return FriendItemList.size();
    }

    Handler handler = new Handler();
    public void LinkImage(final String pImage,final ImageView profileImage) {
        if (pImage.equals("img")) ;
        else {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    try {
                        final URL url = new URL(pImage);
                        InputStream is = url.openStream();
                        final Bitmap bm = BitmapFactory.decodeStream(is);
                        handler.post(new Runnable() {

                            @Override
                            public void run() {
                                profileImage.setImageBitmap(bm);
                                Log.d("imageurl",String.valueOf(url));
                            }
                        });
                        profileImage.setImageBitmap(bm);
                    } catch (Exception e) {
                    }
                }
            });
            t.start();
        }
    }
}
