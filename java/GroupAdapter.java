package com.example.kimhyju.place;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupAdapter extends ArrayAdapter {
    ArrayList<GroupItem> GroupItemList=new ArrayList<>();
    TextView groupname;

    public GroupAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<GroupItem> item) {
        super(context, resource, item);
        this.GroupItemList=item;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grouplist, parent, false);
        }
        GroupItem groupitem=GroupItemList.get(position);
        groupname=(TextView)convertView.findViewById(R.id.groupname);
        groupname.setText(groupitem.getGroupname());
        return convertView;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public Object getItem(int position) {
        return GroupItemList.get(position);
    }
    @Override
    public int getCount() {
        return GroupItemList.size();
    }



}