package com.jibo.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{ 
    private Context mContext; 
    private ArrayList<Drawable> imgList; 
    public ImageAdapter(Context c, ArrayList<Drawable> imgList) { 
        mContext = c;
        this.imgList = imgList;
    } 
    @Override 
    public int getCount() { 
        return imgList.size(); 
    } 

    @Override 
    public Object getItem(int position) { 
        return position; 
    } 

    @Override 
    public long getItemId(int position) { 
        return position; 
    } 

    @Override 
    public View getView(int position, View convertView, ViewGroup parent) { 
        ImageView i = new ImageView (mContext); 
        i.setPadding(10, 0, 10, 0);
        i.setBackgroundDrawable(imgList.get(position)); 
        i.setScaleType(ImageView.ScaleType.FIT_XY); 
        //从imgSizes取得图片大小 
        return i; 
    } 
     
}
