package com.jibo.app.news;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseSearchActivity;
import com.jibo.activity.HomePageActivity;
import com.jibo.app.DetailsData;
import com.jibo.app.push.PushConst;
import com.jibo.base.src.EntityObj;
import com.jibo.common.Constant;
import com.jibo.common.SharedPreferencesMgr;
import com.jibo.ui.NavigateView;
import com.jibo.ui.NavigateView.GotoBackFirstInit;
import com.jibo.ui.NavigateView.OnChangeListener;
import com.jibo.util.ActivityPool;
import com.jibo.util.Logs;
import com.jibo.util.Res;
import com.jibo.v4.pagerui.PageActivity;
import com.jibo.v4.pagerui.PageInfo;
import com.jibo.v4.pagerui.ViewPagerHelper;
import com.jibo.v4.view.PagerView.OnPageChangeListener;

public class NewsPageActivity extends BaseSearchActivity {

	private ArrayList<PageInfo> pageInfos;
	private ViewPagerHelper viewPagerHelper;
	private NavigateView navigateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.setContentView(R.layout.news);
		super.onCreate(savedInstanceState);
		ActivityPool.getInstance().addActivity(this);
		Res.initR(getBaseContext());

		setPopupWindowType(Constant.MENU_TYPE_1);
		try {
			int toPage = 0;
			
			pageInfos = new ArrayList<PageInfo>();
			PageInfo info = genPageInfo(com.jibo.news.AllActivity.class, this,this.getIntent(),this.getClass());
			PageInfo proInfo = genPageInfo(com.jibo.news.ProActivity.class, this,this.getIntent(),this.getClass());
			proInfo.mIntent.putExtra("bigCategory",
					getResources().getString(R.string.academic));
			String dept = SharedPreferencesMgr.getDept();
			if (null != dept && !"".equals(dept)) {
				proInfo.mIntent.putExtra("pro", dept);
				toPage = 1;
			}if (PushConst.pushFlag==null||PushConst.pushFlag) {
				if (PushConst.pushmodule == PushConst.News_MODULE) {
					toPage = 0;
				}
			}
			PageInfo induInfo = genPageInfo(com.jibo.news.InduActivity.class,
					this,this.getIntent(),this.getClass());
			induInfo.mIntent.putExtra("bigCategory",
					getResources().getString(R.string.industry_dynamics));
			pageInfos.add(info);
			pageInfos.add(proInfo);
			pageInfos.add(induInfo);
			Logs.i("--- 111");
			viewPagerHelper = new ViewPagerHelper(this, savedInstanceState);
			Logs.i("--- 222");
			viewPagerHelper.setup(this, toPage, pageInfos);
			Logs.i("--- 333"+toPage);
			final PageActivity acty = (PageActivity) ActivityPool.getInstance().activityMap
					.get((pageInfos.get(toPage).mCls));
			acty.runStart();
			super.onCreate(savedInstanceState);
			navigateView = (NavigateView) findViewById(R.id.navigateView);
			navigateView.changeUI(toPage);
			navigateView.setText(NavigateView.NORMAL_TYPE, R.string.the_latest);
			navigateView
					.setText(NavigateView.CATEGORY_TYPE, R.string.specialty);
			navigateView.setText(NavigateView.OTHER_TYPE,
					R.string.industry_info);
			navigateView.setOnChangeListener(new OnChangeListener() {
				@Override
				public void onChange(int type) {
					viewPagerHelper.mViewPager.setCurrentItem(type);
					navigateView.changeUI(type);
				}
			});

			navigateView.setGotoBackListener(new GotoBackFirstInit() {

				@Override
				public void gotoBack(int type) {

					if (viewPagerHelper.mViewPager.getCurrentItem() == type
							&& type != 0) {
						((PageActivity) ActivityPool.getInstance().activityMap
								.get((pageInfos.get(viewPagerHelper.mViewPager
										.getCurrentItem()).mCls))).onBack(true,false);

					}
				}
			});
			viewPagerHelper.mViewPager
					.setOnPageChangeListener(new OnPageChangeListener() {

						@Override
						public void onPageScrolled(int paramInt1,
								float paramFloat, int paramInt2) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onPageSelected(int type) {
							// TODO Auto-generated method stub
							navigateView.changeUI(type);
							PageActivity acty = (PageActivity) ActivityPool
									.getInstance().activityMap.get((pageInfos
									.get(viewPagerHelper.mViewPager
											.getCurrentItem()).mCls));
							acty.runStart();
						}

						@Override
						public void onPageScrollStateChanged(int paramInt) {
							// TODO Auto-generated method stub

						}

					});
			((TextView) findViewById(R.id.txt_header_title))
					.setText(R.string.news);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static PageInfo genPageInfo(Class<? extends Activity> activityClz,
			Context context,Intent intent,Class clazz) {
		PageInfo sdcardpageInfo;
		sdcardpageInfo = new PageInfo(context, null, null, null,intent, activityClz,clazz.getName());
		return sdcardpageInfo;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			((AllActivity) ActivityPool.getInstance().activityMap
					.get((pageInfos.get(viewPagerHelper.mViewPager
							.getCurrentItem()).mCls))).resume();
		} catch (Exception e) {

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(PushConst.pushFlag==null||PushConst.pushFlag){
				finish();
				startActivity(new Intent(this,HomePageActivity.class));
				PushConst.pushFlag = false;
				return true;
			}
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
		boolean bool = false;
		try{
		bool= ActivityPool.getInstance().activityMap
				.get((pageInfos.get(viewPagerHelper.mViewPager.getCurrentItem()).mCls))
				.dispatchKeyEvent(event);
		}catch(Exception e){
			
		}
		return bool;
	}

}
