package com.yan.email;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yan.email.bean.Itemmail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.mail.internet.MimeUtility;

/**
 * Created by Yan on 2016/11/13.
 */

public class MyAdapter extends BaseAdapter{
    private ArrayList<Itemmail> itemmails;
    private LayoutInflater layoutInflater;

    public MyAdapter(Context context,ArrayList<Itemmail> itemmails) {
        this.itemmails = itemmails;
        layoutInflater=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return itemmails.size();
    }

    @Override
    public Object getItem(int position) {
        return itemmails.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Viewholder viewholder=null;
        Itemmail itemmail=itemmails.get(position);
        if(convertView==null){
            convertView=layoutInflater.inflate(R.layout.itemlayout,null);
            viewholder=new Viewholder(convertView);
            convertView.setTag(viewholder);
        }else{
            viewholder= (Viewholder) convertView.getTag();
        }
        try {
            viewholder.from.setText("发件人："+itemmail.from);
            viewholder.subject.setText(MimeUtility.decodeText(itemmail.subject));
            viewholder.time.setText(itemmail.time);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    class Viewholder{
        public TextView from;
        public TextView subject;
        public TextView time;

        public Viewholder(View v) {
            this.from = (TextView) v.findViewById(R.id.from);
            this.subject = (TextView) v.findViewById(R.id.subject);
            this.time = (TextView) v.findViewById(R.id.time);
        }
    }
}
