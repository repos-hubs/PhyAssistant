package com.jibo.v4.pagerui;

import java.util.List;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.jibo.util.Res;
import com.jibo.v4.view.PagerView;
import com.jibo.v4.view.PagerView.OnPageChangeListener;

public abstract class PagerHelper {
	private List<View> contentViews; // Tab椤甸敓鏂ゆ嫹閿熷彨鎲嬫嫹
	private ImageView indicator;// 閿熸枻鎷烽敓鏂ゆ嫹鍥剧墖
	private TextView t1, t2;// 椤甸敓鏂ゆ嫹澶撮敓鏂ゆ嫹
	private int firstPositionOffset = 0;// 閿熸枻鎷烽敓鏂ゆ嫹鍥剧墖鍋忛敓鏂ゆ嫹閿熸枻锟�
	private int currIndex = 0;// 閿熸枻鎷峰墠椤甸敓鏂ゆ嫹閿熸枻鎷烽敓锟�	
	private int bmpW;// 閿熸枻鎷烽敓鏂ゆ嫹鍥剧墖閿熸枻鎷烽敓锟�	
	private int initOffset;
	private int widthCapability = 3;// 閿熸枻鎷烽敓鏂ゆ嫹閿熷彨纭锋嫹閿熸枻锟�
	private int defaultPosition = 1;// 閿熸枻鎷峰墠閿熸枻鎷峰仠閿熻妭绗》鎷烽敓鏂ゆ嫹
	private int unitOffset;
	private int defaultIndicateDuration;
	protected LocalActivityManager mLocalActivityManager;
	protected Activity yourHomeActivity;

	public int getCurrIndex() {
		return currIndex;
	}

	public PagerHelper(Activity yourActivity, Bundle savedInstanceState) {
		super();
		this.yourHomeActivity = yourActivity;
		initActivityManager(savedInstanceState);
	}

	public void initActivityManager(Bundle savedInstanceState) {
		this.mLocalActivityManager = new LocalActivityManager(yourHomeActivity,
				true);
		this.mLocalActivityManager.dispatchCreate(savedInstanceState);
		this.mLocalActivityManager.dispatchResume();
	}

	public void onPause() {
		this.mLocalActivityManager
				.dispatchPause(yourHomeActivity.isFinishing());
	}

	public void onResume() {
		this.mLocalActivityManager.dispatchResume();
	}

	public void onDestroy() {

		this.mLocalActivityManager.dispatchDestroy(yourHomeActivity
				.isFinishing());
		contentViews.clear();
	}

	public void setup(Activity yourActivity, int defaultPosition,
			List<PageInfo> pageInfos) {

//		yourActivity.setContentView((Integer)Res.getAttr("layout","news"));
		setupContent(pageInfos);
		// mPager = (PagerView) yourActivity.findViewById(R.id.vPager);
		//
		int pages = pageInfos.size();
		// contentViews = new ArrayList<View>();
		// for (int i = 0; i < pages; i++) {
		// PageInfo pageInfo = (PageInfo) pageInfos.get(i);
		// Intent intent = new Intent(yourActivity, pageInfo.activityClazz);
		// if (null != pageInfo.intent) {
		// intent.setAction(pageInfo.intent.getAction());
		// intent.putExtras(pageInfo.intent);
		// intent.setData(pageInfo.intent.getData());
		// }
		// intent.setFlags(i);
		//
		// View activityView = activityToView(intent);
		// activityView.setId(1);
		// contentViews.add(activityView);
		// // this.mPagerAdapter.addPage(startParam.mCls, null,
		// // startParam.mIndicatorResId, activityView);
		// }
		// View ct1 = new TextView(this.getBaseContext());
		// ct1.setText("hello");
		// TextView ct2 = new TextView(this.getBaseContext());
		// ct2.setText("hi");
		// TextView ct3 = new TextView(this.getBaseContext());
		// ct3.setText("come");
		// listViews.add(ct1);
		// listViews.add(ct2);
		// listViews.add(ct3);
//		setTitleFlingFeature(yourActivity, pages, defaultPosition);
		getViewPager().setCurrentItem(defaultPosition);
		getViewPager().requestFocusFromTouch();

	}

	protected abstract void setupContent(List<PageInfo> pageInfos);

	private void setTitleFlingFeature(Activity yourActivity, int viewsCount,
			int defaultPosition) {
		widthCapability = viewsCount;

		indicator = (ImageView) yourActivity.findViewById((Integer)Res.getAttr("R.id.cursor"));
		bmpW = BitmapFactory.decodeResource(yourActivity.getResources(),
				(Integer)Res.getAttr("R.drawable.cur")).getWidth();// 閿熸枻鎷峰彇鍥剧墖閿熸枻鎷烽敓
		DisplayMetrics dm = new DisplayMetrics();
		yourActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenW = dm.widthPixels;// 閿熸枻鎷峰彇閿熻鎲嬫嫹閿熺粸鍖℃嫹閿燂拷
		unitOffset = screenW / widthCapability;
		firstPositionOffset = (unitOffset - bmpW) / 2;// 閿熸枻鎷烽敓鏂ゆ嫹鍋忛敓鏂ゆ嫹閿熸枻锟�
		initOffset = firstPositionOffset;

		Matrix matrix = new Matrix();
		matrix.postTranslate(initOffset, 0);
		indicator.setImageMatrix(matrix);

		getViewPager().setOnPageChangeListener(new PageChangeListener());
	}

	public abstract PagerView getViewPager();

	public void focusTouch(int pageIndex) {
		getViewPager().setCurrentItem(pageIndex);
	}

	// 椤甸敓鏂ゆ嫹閿熷彨浼欐嫹閿熸枻鎷烽敓鏂ゆ嫹
	public class PageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int targetIndex) {
//			imageSLideAnimation(targetIndex);
		}

		private void imageSLideAnimation(int targetIndex) {
			if (targetIndex == currIndex) {
				Log.i("", "== currIndex" + currIndex);
				return;
			}
			Animation animation = null;
			int currIndexOffset = unitOffset * currIndex;
			int targetIndexOffset = unitOffset * targetIndex;
			int fromXDelta = currIndexOffset;
			int toXDelta = targetIndexOffset;
			animation = new TranslateAnimation(fromXDelta, toXDelta, 0, 0);
			currIndex = targetIndex;
			animation.setFillAfter(true);// True閿熸枻鎷峰浘鐗囧仠閿熻妭璁规嫹閿熸枻鎷烽敓鏂ゆ嫹閿熸枻鎷蜂綅閿熸枻锟�
			animation.setDuration(defaultIndicateDuration == -1 ? 288
					: defaultIndicateDuration);
			indicator.startAnimation(animation);
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	}

	public static Activity getItemActivity(int position,LocalActivityManager mLocalActivityManager) {
		return mLocalActivityManager.getActivity(String.valueOf(position));
	}
	
	public class TitleOnClickListener implements OnClickListener {
		int index;

		public TitleOnClickListener(int i) {
			
			this.index = i;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			focusTouch(index);
		}

	};

	public View.OnClickListener getOnClickListener(int idx) {
		// TODO Auto-generated method stub
		return new TitleOnClickListener(idx);
	}
}
