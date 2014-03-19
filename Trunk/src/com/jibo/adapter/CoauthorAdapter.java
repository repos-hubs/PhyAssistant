package com.jibo.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.api.android.GBApp.R;import com.jibo.data.entity.CoauthorEntity;

public class CoauthorAdapter extends BaseAdapter {
	 
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SEPARATOR = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

    private ArrayList mData = new ArrayList();
    private LayoutInflater mInflater;
    private TreeSet mSeparatorsSet = new TreeSet();
    
    public CoauthorAdapter(Context context) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addItem(CoauthorEntity item) {
        mData.add(item);
    }

    public void addSeparatorItem(final HashMap<String, String> item) {
        mData.add(item);
        mSeparatorsSet.add(mData.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    
    public static class ViewHolder {
    	public TextView textView;
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        System.out.println("getView " + position + " " + convertView + " type = " + type);
        if (convertView == null) {
            holder = new ViewHolder();
            switch (type) {
                case TYPE_ITEM:
                    convertView = mInflater.inflate(R.layout.list_item_text_text_text, null);
                    holder.textView1 = (TextView)convertView.findViewById(R.id.ListText1);
                    holder.textView2 = (TextView)convertView.findViewById(R.id.ListText2);
                    holder.textView3 = (TextView)convertView.findViewById(R.id.ListText3);
                    break;
                case TYPE_SEPARATOR:
                    convertView = mInflater.inflate(R.layout.list_item_text, null);
                    holder.textView = (TextView)convertView.findViewById(R.id.ListText);
                    break;
            }
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        switch(type) {
        case TYPE_ITEM:
        	CoauthorEntity en = (CoauthorEntity)mData.get(position);
        	holder.textView1.setText(en.getCoauthorName());
            holder.textView2.setText(en.getHospitalName());
            holder.textView3.setText(en.getCoPaperCount());
            
        	break;
        case TYPE_SEPARATOR:
        	String title;
        	if(position==0) {
        		title = ((HashMap<String, String>)mData.get(position)).get("title1");
        	} else {
        		title = ((HashMap<String, String>)mData.get(position)).get("title2");
        	}
        	
        	holder.textView.setText(title.split(":")[0]+"          "+title.split(":")[1]);
        	break;
        }
        return convertView;
    }

}


