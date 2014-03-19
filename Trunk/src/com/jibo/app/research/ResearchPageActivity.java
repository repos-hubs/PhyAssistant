package com.jibo.app.research;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.BaseSearchActivity;
import com.jibo.activity.HomePageActivity;
import com.jibo.app.push.PushConst;
import com.jibo.app.updatespot.SpotUtil;
import com.jibo.common.Constant;
import com.jibo.ui.NavigateView;
import com.jibo.ui.NavigateView.GotoBackFirstInit;
import com.jibo.ui.NavigateView.OnChangeListener;
import com.jibo.util.ActivityPool;
import com.jibo.util.Res;
import com.jibo.util.tips.DipUtil;
import com.jibo.v4.pagerui.PageActivity;
import com.jibo.v4.pagerui.PageInfo;
import com.jibo.v4.pagerui.ViewPagerHelper;
import com.jibo.v4.view.PagerView.OnPageChangeListener;
import com.umeng.analytics.MobclickAgent;

public class ResearchPageActivity extends BaseSearchActivity {

	private ArrayList<PageInfo> pageInfos;
	private ViewPagerHelper viewPagerHelper;
	private NavigateView navigateView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// if (Constant.testinFlag) {
		// GBApplication.handleTestin();
		// }
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.researchnew);
		super.onCreate(savedInstanceState);
		MobclickAgent.onError(this);
		try {
			ActivityPool.getInstance().addActivity(this);
			Res.initR(getBaseContext());
			setPopupWindowType(Constant.MENU_TYPE_1);
			int toPage = 0;

			pageInfos = new ArrayList<PageInfo>();
			PageInfo info = genPageInfo(LatestActivity.class, this,
					this.getClass());
			PageInfo proInfo = genPageInfo(BrowseActivity.class, this,
					this.getClass());
			PageInfo induInfo = genPageInfo(CollectionActivity.class, this,
					this.getClass());
			pageInfos.add(info);
			pageInfos.add(proInfo);
			pageInfos.add(induInfo);
			viewPagerHelper = new ViewPagerHelper(this, savedInstanceState);
			viewPagerHelper.setup(this, toPage, pageInfos);
			PageActivity acty = (PageActivity) ActivityPool.getInstance().activityMap
					.get((pageInfos.get(toPage).mCls));
			acty.runStart();
			navigateView = (NavigateView) findViewById(R.id.navigateView);
			navigateView.changeUI(toPage);
			navigateView.setText(NavigateView.NORMAL_TYPE, R.string.lastest);
			navigateView.setText(NavigateView.CATEGORY_TYPE, R.string.browse);
			navigateView.setText(NavigateView.OTHER_TYPE, R.string.care);
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
										.getCurrentItem()).mCls))).onBack(true,
								false);

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
							final PageActivity acty = (PageActivity) ActivityPool
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
					.setText(R.string.research);
			
			uploadLoginLogNew("Activity", "Research", "create", null);
			SpotUtil.spots.get(SpotUtil.SUBSCRIBED_JOURNALS).addSpotView(
					navigateView.findViewById(R.id.other_layout).findViewById(
							R.id.hotspot),this);
			SpotUtil.spots.get(SpotUtil.LASTEST_SPOT).addSpotView(
					navigateView.findViewById(R.id.normal_layout).findViewById(
							R.id.hotspot),this);
			SpotUtil.spots.get(SpotUtil.SUBSCRIBED_JOURNALS).doSpot(this);
			SpotUtil.spots.get(SpotUtil.LASTEST_SPOT).doSpot(this);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static PageInfo genPageInfo(Class<? extends Activity> activityClz,
			Context context, Class clazz) {
		PageInfo sdcardpageInfo;
		sdcardpageInfo = new PageInfo(context, null, null, null,null, activityClz,
				clazz.getName());
		return sdcardpageInfo;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		try {
			((PageActivity) ActivityPool.getInstance().activityMap
					.get((pageInfos.get(viewPagerHelper.mViewPager
							.getCurrentItem()).mCls))).resume();
		} catch (Exception e) {

		}

	}

	public int childWindowHeight;

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (childWindowHeight == 0) {
			childWindowHeight = this.getWindow().getDecorView().getHeight()
					- this.findViewById(R.id.header_panel).getHeight()
					- DipUtil.dip2px(getBaseContext(), 40);
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
		return ActivityPool.getInstance().activityMap
				.get((pageInfos.get(viewPagerHelper.mViewPager.getCurrentItem()).mCls))
				.dispatchKeyEvent(event);
	}

}
