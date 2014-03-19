package com.jibo.adapter;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

public class ViewPageAdapter extends PagerAdapter {
	private List<View> pageViews = null;

	@Override
	public int getCount() {
		return pageViews.size();
	}

	public ViewPageAdapter(List<View> views) {
		pageViews = views;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
		// return POSITION_NONE;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(pageViews.get(arg1));
	}

	/***
	 * ��ʼ��view
	 */
	@Override
	public Object instantiateItem(View arg0, int arg1) {
		// ((ViewPager) arg0).removeView(pageViews.get(arg1));
		View nowView = pageViews.get(arg1);

//		if (this.getCount() > arg1
//				&& ((ViewPager) arg0).getChildAt(arg1) == pageViews.get(arg1)) {
//			return nowView;
//		} else {
			((ViewPager) arg0).addView(nowView, 0);
//		}
		return nowView;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

	@Override
	public void finishUpdate(View arg0) {
	}

}
