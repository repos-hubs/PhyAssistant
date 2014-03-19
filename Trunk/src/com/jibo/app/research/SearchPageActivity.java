package com.jibo.app.research;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseSearchActivity;
import com.jibo.common.Constant;
import com.jibo.ui.NavigateView;
import com.jibo.util.ActivityPool;
import com.jibo.util.Res;
import com.jibo.v4.pagerui.PageInfo;
import com.jibo.v4.pagerui.ViewPagerHelper;
import com.jibo.v4.view.PagerView.OnPageChangeListener;

public class SearchPageActivity extends BaseSearchActivity {

	private ArrayList<PageInfo> pageInfos;
	private ViewPagerHelper viewPagerHelper;
	private NavigateView navigateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.news);
		super.onCreate(savedInstanceState);
		ActivityPool.getInstance().addActivity(this);
		Res.initR(getBaseContext());
		//this.findViewById(R.id.actv_searchEdit).setCursorVisible(false);
//		setPopupWindowType(Constant.MENU_TYPE_1);
		int toPage = 0;

		pageInfos = new ArrayList<PageInfo>();
		PageInfo proInfo = ViewPagerHelper.genPageInfo(
				com.jibo.app.research.SearchActivity.class, this, this.getClass());
		proInfo.mIntent.putExtra(BaseSearchActivity.SEARCH_TEXT, getIntent()
				.getStringExtra(BaseSearchActivity.SEARCH_TEXT));
		pageInfos.add(proInfo);
		viewPagerHelper = new ViewPagerHelper(this, savedInstanceState);
		viewPagerHelper.setup(this, toPage, pageInfos);
		super.onCreate(savedInstanceState);
		navigateView = (NavigateView) findViewById(R.id.navigateView);
		navigateView.setVisibility(View.GONE);
		((RelativeLayout.LayoutParams) viewPagerHelper.mViewPager
				.getLayoutParams()).topMargin = 0;
		viewPagerHelper.mViewPager
				.setOnPageChangeListener(new OnPageChangeListener() {

					@Override
					public void onPageScrolled(int paramInt1, float paramFloat,
							int paramInt2) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageSelected(int type) {
						// TODO Auto-generated method stub
						navigateView.changeUI(type);

					}

					@Override
					public void onPageScrollStateChanged(int paramInt) {
						// TODO Auto-generated method stub

					}

				});
		((TextView) findViewById(R.id.txt_header_title)).setText(R.string.research);
	closeInput();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (hasPop && popflg) {
				dissMissPop();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		if ((hasPop && popflg) || !mSearchEdit.getText().toString().equals("")) {
			return super.dispatchKeyEvent(event);
		}
		return ActivityPool.getInstance().activityMap
				.get((pageInfos.get(viewPagerHelper.mViewPager.getCurrentItem()).mCls))
				.dispatchKeyEvent(event);
	}

	@Override
	public void launchChild() {
		// TODO Auto-generated method stub
		mSearchEdit.setText(this.searchData);
		com.jibo.app.research.SearchActivity search = ((com.jibo.app.research.SearchActivity) ActivityPool
				.getInstance().activityMap
				.get(com.jibo.app.research.SearchActivity.class));
		search.searchvalue = this.searchData;
		search.searchText();
	}

}
