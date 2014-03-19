package com.jibo.adapter;


import android.support.v4.view.ViewPager.OnPageChangeListener;

public class ViewPageChangeListener implements OnPageChangeListener {

	private IPageChange pagechange; 
	
	public ViewPageChangeListener(IPageChange page)
	{
		pagechange =page;
	}
	public void onPageScrollStateChanged(int arg0) {

	}

	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	public void onPageSelected(int index) {
		if(pagechange != null)
			pagechange.setCurrentPoint(index);
	
	}
	
	public interface IPageChange {
		void setCurrentPoint(int index);
	}

}
