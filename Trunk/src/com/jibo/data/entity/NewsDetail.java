package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class NewsDetail implements Parcelable{
	public static NewsDetail newsDetail;
	public String content = "";
	public String id;
	public String title;
	public String date;
	public String category;
	public String source;
	public String newsSource;
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
}
