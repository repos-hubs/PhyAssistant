package com.jibo.v4.pagerui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.api.android.GBApp.R;
import com.jibo.util.Logs;
import com.jibo.util.Res;
import com.jibo.v4.view.PagerAdapter;
import com.jibo.v4.view.PagerView;

public class ViewPagerHelper extends PagerHelper implements
		ActivityResultBridge {
	private MPagerAdapter mPagerAdapter;
	public PagerView mViewPager;
	private ActivityResultBridge.ActivityResultReceiver mResultReceiver;
	private ArrayList<PendingContentLoader> mContentLoaders;
	private LoadPendingContentHandler mLoadContentHandler;
	private List<com.jibo.v4.pagerui.PageInfo> viewClasses;
	private int pagernums;
	private int currentScreen;

	public ArrayList<PendingContentLoader> getmContentLoaders() {
		return mContentLoaders;
	}

	public int getCurrentScreen() {
		return currentScreen;
	}

	public ViewPagerHelper(Activity yourActivity, Bundle savedInstanceState) {
		super(yourActivity, savedInstanceState);
		// TODO Auto-generated constructor stub
	}

	protected void setupContent(
			List<com.jibo.v4.pagerui.PageInfo> pageInfos) {
		this.mPagerAdapter = new MPagerAdapter(yourHomeActivity, getViewPager());

		// /* 64 */ this.mIndicator =
		// ((ViewPagerIndicator)findViewById(this.mIndicatorResId));
		// /* 65 */ this.mViewPager.setOnPageChangeListener(this.mIndicator);
		pagernums = pageInfos.size();
		/* 67 */for (int i = 0; i < this.pagernums; i++) {
			/* 68 */com.jibo.v4.pagerui.PageInfo pageInfo = (com.jibo.v4.pagerui.PageInfo) pageInfos
					.get(i);
			
			/* 69 */addPageInfo(i, pageInfo);
		}
		Logs.i("--- -111 ");
		/* 82 */getViewPager().setAdapter(this.mPagerAdapter);
		// /* 83 */getViewPager().setCurrentItem(this.currentScreen);
		Logs.i("--- -222 ");
		contentLoad(-1);
	}

	private void addPageInfo(int i,
			com.jibo.v4.pagerui.PageInfo pageInfo) {
		Intent intent = new Intent(yourHomeActivity, pageInfo.mCls);
		/* 70 */if (null != pageInfo.mIntent) {
			/* 71 */intent.setAction(pageInfo.mIntent.getAction());
			/* 72 */intent.putExtras(pageInfo.mIntent);
			/* 73 */intent.setData(pageInfo.mIntent.getData());
		}
		/* 75 */intent.setFlags(i);

		/* 77 */View view = activityToView(intent, this, currentScreen);

		/* 79 */view.setId(1);
		/* 80 */this.mPagerAdapter.addPage(pageInfo.mCls, null,
		// startParam.mIndicatorResId,
				"", view);
	}

	private void contentLoad(int pos) {
		/* 93 */if (null != this.mContentLoaders) {
			/* 94 */this.mLoadContentHandler = new LoadPendingContentHandler();
			/* 95 */Message message = this.mLoadContentHandler.obtainMessage();
			message.arg1 = pos;
			/* 96 */this.mLoadContentHandler.sendMessageDelayed(message, 100L);

		}
	}

	public void extend(com.jibo.v4.pagerui.PageInfo pageInfo,int pos) {
		addPageInfo(pos, pageInfo);
		contentLoad(pos);
		this.mPagerAdapter.notifyDataSetChanged();
	}

	public void onPause() {
		// /* 102 */super.onPause();

		/* 104 */this.mLocalActivityManager.dispatchPause(yourHomeActivity
				.isFinishing());
	}

	public void onResume() {
		// /* 109 */super.onResume();
		/* 110 */mLocalActivityManager.dispatchResume();
	}

	public void onDestroy() {
		// /* 116 */super.onDestroy();
		/* 117 */mLocalActivityManager.dispatchDestroy(yourHomeActivity
				.isFinishing());
		/* 118 */this.mPagerAdapter.mPages.clear();
		// /* 119 */ MPagerAdapter.(this.mPagerAdapter, null);
	}

	public void onConfigurationChanged(Configuration newConfig) {
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		/* 137 */if (null != this.mResultReceiver) {
			/* 138 */for (int i = 0; i < this.viewClasses.size(); i++) {
				/* 139 */Activity childActivity = getItemActivity(i,
						mLocalActivityManager);
				/* 140 */if ((null != childActivity)
						&& ((childActivity instanceof ActivityResultBridge.ActivityResultReceiver))) {
					/* 141 */((ActivityResultBridge.ActivityResultReceiver) childActivity)
							.handleActivityResult(this.mResultReceiver,
									requestCode, resultCode, intent);
				}

			}

			/* 148 */this.mResultReceiver = null;
		}
	}

	public void startActivityForResult(
			ActivityResultBridge.ActivityResultReceiver resultReceiver,
			Intent intent, int requestCode) {
		/* 154 */this.mResultReceiver = resultReceiver;
		/* 155 */yourHomeActivity.startActivityForResult(intent, requestCode);
	}
	public static PageInfo genPageInfo(Class<? extends Activity> activityClz,
			Context context,Class clazz) {
		PageInfo sdcardpageInfo;
		sdcardpageInfo = new PageInfo(context, null, null, null, null,activityClz,clazz.getName());
		return sdcardpageInfo;
	}
	public int getCurrentPage(int id) {
		/* 176 */return getViewPager().getCurrentItem();
	}

	public static Activity getItemActivity(int position,
			LocalActivityManager mLocalActivityManager) {
		/* 181 */return mLocalActivityManager.getActivity(String
				.valueOf(position));
	}

	public static View activityToView(Intent intent,
			ViewPagerHelper viewPagerHelper, int currentScreen) {
		/* 195 */Window w = viewPagerHelper.mLocalActivityManager
				.startActivity(String.valueOf(intent.getFlags()), intent);

		/* 197 */View wd = w != null ? w.getDecorView() : null;
		/* 198 */if (wd != null) {
			/* 199 */wd.setVisibility(0);
			/* 200 */wd.setFocusableInTouchMode(true);
			/* 201 */((ViewGroup) wd).setDescendantFocusability(262144);

			/* 203 */int position = intent.getFlags();
			/* 204 */Activity childActivity = getItemActivity(position,
					viewPagerHelper.mLocalActivityManager);
			/* 205 */if (null != childActivity) {
				/* 206 */if ((childActivity instanceof ActivityResultBridge.ActivityResultReceiver)) {
					/* 207 */((ActivityResultBridge.ActivityResultReceiver) childActivity)
							.registerActivityResultBridge(viewPagerHelper);
				}

				/* 210 */if ((childActivity instanceof PendingContentLoader)) {
					/* 211 */if (null == viewPagerHelper.mContentLoaders) {
						/* 212 */viewPagerHelper.mContentLoaders = new ArrayList();
					}

					/* 215 */if (position == currentScreen)
						/* 216 */viewPagerHelper.mContentLoaders.add(0,
								(PendingContentLoader) childActivity);
					else {
						/* 218 */viewPagerHelper.mContentLoaders
								.add((PendingContentLoader) childActivity);
					}
				}
			}
		}
		/* 223 */return wd;
	}



	private class LoadPendingContentHandler extends Handler {
		private LoadPendingContentHandler() {
		}

		public void handleMessage(Message msg) {
			int pos = msg.arg1;
			int temp;
			/* 345 */if (null != ViewPagerHelper.this.mContentLoaders) {
				/* 346 */int loaderCount = ViewPagerHelper.this.mContentLoaders
						.size();
				/* 348 */if (loaderCount > 0) {
					temp = pos == -1 ? 0 : pos;
					/* 349 */PendingContentLoader contentLoader = (PendingContentLoader) ViewPagerHelper.this.mContentLoaders
							.get(temp);
					/* 350 */contentLoader.loadContent();
					/* 352 */ViewPagerHelper.this.mContentLoaders.remove(temp);
					if (pos == -1) {
						/* 353 */if (loaderCount > 1) {
							/* 356 */Message message = obtainMessage();
							/* 357 */sendMessageDelayed(message, 500L);
						}
					}
				}
			}
		}
	}

	public void setDisplayScreen(int id) {
		this.currentScreen = id;
		if (null != this.mViewPager) {
			this.mViewPager.setCurrentItem(id);
		}
	}

	protected class MPagerAdapter extends PagerAdapter {
		private final Context mContext;
		private final PagerView mPager;
		private ViewPagerHelper.PageCacheInfo info;
		/* 262 */private final ArrayList<PageCacheInfo> mPages = new ArrayList<PageCacheInfo>();

		public MPagerAdapter(Context context, PagerView mViewPager) {
			/* 265 */this.mPager = mViewPager;
			/* 266 */this.mContext = context;
		}

		public void addPage(Class<?> clss, String title, View mView) {
			/* 270 */addPage(clss, null, title, mView);
		}

		public void addPage(Class<?> clss, Bundle args, int res, View mView) {
			/* 274 */addPage(clss, null, this.mContext.getResources()
					.getString(res), mView);
		}

		public void addPage(Class<?> clss, Bundle args, String title, View mView) {
			/* 278 */this.info = new ViewPagerHelper.PageCacheInfo(clss, title,
					mView);
			/* 279 */this.mPages.add(this.info);
			/* 280 */notifyDataSetChanged();
		}

		public int getCount() {
			/* 285 */return this.mPages.size();
		}

		public String getTitle(int pos) {
			/* 290 */return ((ViewPagerHelper.PageCacheInfo) this.mPages.get(pos)).args;
		}

		public void destroyItem(View arg0, int position, Object arg2) {
			/* 296 */((PagerView) arg0)
					.removeView(((ViewPagerHelper.PageCacheInfo) this.mPages
							.get(position)).mView);
		}

		public void finishUpdate(View arg0) {
		}

		public Object instantiateItem(View arg0, int position) {
			/* 308 */View convertView = null;
			/* 309 */if (((ViewPagerHelper.PageCacheInfo) this.mPages.get(position)).mView != null
					&& ((PagerView) arg0).getChildCount() >= position) {
				/* 310 */convertView = ((ViewPagerHelper.PageCacheInfo) this.mPages
						.get(position)).mView;
				/* 311 */((PagerView) arg0)
						.addView(((ViewPagerHelper.PageCacheInfo) this.mPages
								.get(position)).mView, position);
			}

			/* 313 */return convertView;
		}

		public boolean isViewFromObject(View arg0, Object arg1) {
			/* 319 */return arg0 == arg1;
		}

		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		public Parcelable saveState() {
			/* 331 */return null;
		}

		public void startUpdate(View arg0) {
		}

	}

	static final class PageCacheInfo {
		private final Class<?> clss;
		private final String args;
		private final View mView;

		PageCacheInfo(Class<?> _clss, String _args, View mmView) {
			/* 250 */this.clss = _clss;
			/* 251 */this.args = _args;
			/* 252 */this.mView = mmView;
		}

	}

	
	public PagerView getViewPager(String id) {
		// TODO Auto-generated method stub
		if (this.mViewPager == null) {
			this.mViewPager = ((PagerView) yourHomeActivity
					.findViewById((Integer) Res.getAttr(id)));
		}
		return this.mViewPager;
	}
	@Override
	public PagerView getViewPager() {
		// TODO Auto-generated method stub
		if (this.mViewPager == null) {
			this.mViewPager = (PagerView) yourHomeActivity
					.findViewById(R.id.vPager);
		}
		return this.mViewPager;
	}

}

/*
 * Location: d:\æˆ‘çš„æ–‡æ¡£\æ¡Œé�¢\lewa.os.jar Qualified Name:
 * com.lewa.os.ui.ViewPagerIndicatorActivity JD-Core Version: 0.6.0
 */