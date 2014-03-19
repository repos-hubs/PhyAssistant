package com.jibo.v4.pagerui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

public class PageInfo{
	public static final String PARENT_CLAZZ = "parentClazz";
	public String action;
	public Object data;
	public String[] categories;
	public Class mCls;
	public Intent mIntent;
	public String title;

	public PageInfo(Class activityClazz) {
		super();
		this.mCls = activityClazz;
	}

	public PageInfo(Context context, String action, Uri data, String[] cat,Intent intent,
			Class activityClazz,String parentClass) {
		super();
		this.action = action;
		this.data = data;
		this.categories = cat;
		this.mCls = activityClazz;
		mIntent = new Intent();
		if (action != null) {
			mIntent.setAction(action);
		}
		if (data != null) {
			mIntent.setData(data);
		}
		if(intent!=null){
			mIntent.putExtras(intent);
		}
		if (activityClazz != null) {
			mIntent.setClassName(activityClazz.getPackage().getName(),activityClazz.getSimpleName());
		}
		if (categories != null) {
			for (String category : categories) {
				mIntent.addCategory(category);
			}
		}
		if(parentClass!=null){
			mIntent.putExtra(PARENT_CLAZZ, parentClass);
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((mCls == null) ? 0 : mCls.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PageInfo other = (PageInfo) obj;
		if (mCls == null) {
			if (other.mCls != null)
				return false;
		} else if (!mCls.equals(other.mCls))
			return false;
		return true;
	}

}
