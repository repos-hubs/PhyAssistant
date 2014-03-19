package com.jibo.app.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Scroller;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.activity.HomePageActivity;
import com.jibo.app.DetailsData;
import com.jibo.base.adapter.MapAdapter.AdaptInfo;
import com.jibo.base.src.EntityObj;
import com.jibo.data.entity.NewsDetail;
import com.jibo.util.HtmlContract;
import com.jibo.util.Logs;

public class DetailWebView extends WebView implements OnGestureListener ,OnClickListener, OnTouchListener{
	private static final String TAG = "ScrollLayout";
	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mCurScreen = 0;
	private int mDefaultScreen = 0;

	private static final int TOUCH_STATE_REST = 0;
	private static final int TOUCH_STATE_SCROLLING = 1;

	private static int SNAP_VELOCITY;
	private static final double tan30 = Math.sqrt(3) / 3D;
	private int mTouchState = TOUCH_STATE_REST;
	private int mTouchSlop;
	private float mLastMotionX;
	private Context mContext;

	public static int s_pageID;
	private List<EntityObj> pageIds = new ArrayList<EntityObj>(0);
	private float dxConstant;
	private View topView;
	private float mLastMotionY;
	private AttributeSet attrs;
	private GestureDetector gd;

	public void setPageIds(List<EntityObj> pageIds, Integer id) {
		if (pageIds != null) {
			this.pageIds = pageIds;
		}
		Logs.i("mCurScreen " + id);
		this.mCurScreen = id;
	}

	public DetailWebView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		this.attrs = attrs;
		mContext = context;
		SNAP_VELOCITY = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();

		dxConstant = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth() / 7f;
		gd = new GestureDetector(this);
		this.setOnClickListener(this);
		this.setOnTouchListener(this);
		pageSwitch();
	}
	
	private void pageSwitch(){
		this.getRootView().post(new Runnable() {
			@Override
			public void run() {
				((TextView) DetailWebView.this.getRootView().findViewById(R.id.prepage))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						snapToScreen(mCurScreen - 1);
					}
				});
				((TextView) DetailWebView.this.getRootView().findViewById(R.id.nextpage))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						snapToScreen(mCurScreen + 1);
					}
				});
			}
		});
		
	}

	public AttributeSet getAttrs() {
		return attrs;
	}

	public void setAttrs(AttributeSet attrs) {
		this.attrs = attrs;
	}

	public DetailWebView(Context context) {
		super(context);
		mContext = context;
		SNAP_VELOCITY = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		pageSwitch();
	}

	
	public DetailWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mScroller = new Scroller(context);
		SNAP_VELOCITY = ((Activity) context).getWindowManager()
				.getDefaultDisplay().getWidth();
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
		addJavascriptInterface(new Object() {

			@SuppressWarnings("unused")
			public void scaleWindow() {
				activity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						scaleView(activity, fullscreen = !fullscreen);
					}

				});
			}
		}, "jscall");
		getSettings().setJavaScriptEnabled(true);
		getSettings().setDomStorageEnabled(true);
		pageSwitch();
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		if (changed) {
			int childLeft = 0;
			final int childCount = getChildCount();

			for (int i = 0; i < childCount; i++) {
				final View childView = getChildAt(i);
				if (childView.getVisibility() != View.GONE) {
					final int childWidth = childView.getMeasuredWidth();
					childView.layout(childLeft, 0, childLeft + childWidth,
							childView.getMeasuredHeight());
					childLeft += childWidth;
				}
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		final int width = MeasureSpec.getSize(widthMeasureSpec);
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		if (widthMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only canmCurScreen run at EXACTLY mode!");
		}

		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		if (heightMode != MeasureSpec.EXACTLY) {
			throw new IllegalStateException(
					"ScrollLayout only can run at EXACTLY mode!");
		}

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
		}
		scrollTo(mCurScreen * width, 0);
	}

	public void snapToDestination() {
		// final int screenWidth = getWidth();
		// final int destScreen = (getScrollX() + screenWidth / 2) /
		// screenWidth;
		// snapToScreen(destScreen);
		// s_pageID = mCurScreen;
		// if (mContext.getClass() == HomePageActivity.class)
		// ((HomePagYTeActivity) mContext).drawCircle();
	}

	boolean loadFlag = false;

	public int getmCurScreen() {
		return mCurScreen;
	}

	public void setmCurScreen(int mCurScreen) {
		this.mCurScreen = mCurScreen;
	}

	public void snapToScreen(int whichScreen) {
		try{
		if (whichScreen == DetailsData.getEntities().size() || whichScreen == -1) {
			return;
		}
		EntityObj en = DetailsData.getEntities().get(whichScreen);
		if (en == null || en.getObject("newsDetail") == null
				|| ((NewsDetail) en.getObject("newsDetail")).id == null) {
			return;
		}
		DetailsData.viewedNews.add(en.get(en.getId()));
		
		mCurScreen = whichScreen;
		((View) getParent()).findViewById(R.id.pagebar).setVisibility(
				View.GONE);
		
		Logs.i("whichScreen " + whichScreen);
		DetailWebView tdl = (DetailWebView) LayoutInflater.from(getContext())
				.inflate(R.layout.detail_webview, null);

		tdl.getSettings().setDefaultFontSize(
				this.getSettings().getDefaultFontSize());
		// tdl.setFullscreen(this.isFullscreen());
		tdl.setActivity(activity);
		tdl.setLayoutParams(getLayoutParams());
		HtmlContract.updateWebContent(tdl, en, LayoutInflater
				.from(getContext()).inflate(R.layout.detail_header, null), this
				.getRootView());
		ViewGroup container = ((ViewGroup) this.getRootView()
				.findViewById(R.id.vPager).getParent());
		container.removeView(this);
		container.addView(tdl, 0);
		tdl.setmCurScreen(mCurScreen);
		tdl.setId(R.id.vPager);
		this.destroy();
		
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	public int getCurScreen() {
		return mCurScreen;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else {
			super.computeScroll();
		}
	}

	public void scaleView(Activity activity, boolean toshow) {

		if (getRootView().findViewById(R.id.counterfeitContent).getVisibility() == View.GONE) {
			this.getRootView().findViewById(R.id.counterfeitContent)
					.setVisibility(View.VISIBLE);
			this.getRootView().findViewById(R.id.header_panel)
					.setVisibility(View.VISIBLE);
		} else {
			this.getRootView().findViewById(R.id.counterfeitContent)
					.setVisibility(View.GONE);
			this.getRootView().findViewById(R.id.header_panel)
					.setVisibility(View.GONE);
		}

		// int[] position = new int[2];
		// getLocationInWindow(position);
		// AnimationSet animationSet = new AnimationSet(true);
		// int centerx =
		// activity.getWindowManager().getDefaultDisplay().getWidth()/2;
		// int centery =
		// activity.getWindowManager().getDefaultDisplay().getHeight()/2;
		// int yView = position[1]+this.getHeight();
		// int ratio =
		// activity.getWindowManager().getDefaultDisplay().getHeight()/2/this.getHeight();
		// int offsetView = Math.abs(yView - centery);
		// // TranslateAnimation ta = new TranslateAnimation(0, 0, position[1],
		// position[1]+offsetView);
		// // ta.setDuration(500);
		//
		// ScaleAnimation scaleAnimation = new ScaleAnimation(1,1,1,1.5f,
		// Animation.RELATIVE_TO_SELF,0.5f, //使用动画播放图片
		// Animation.RELATIVE_TO_SELF,0.5f);
		// scaleAnimation.setDuration(1000);
		// // animationSet.addAnimation(ta);
		// animationSet.addAnimation(scaleAnimation);
		// animationSet.setFillAfter(true); //让其保持动画结束时的状态。
		// startAnimation(animationSet);
		// this.
	}

	boolean touch;
	boolean fullscreen = false;
	boolean moved = false;

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		// TODO Auto-generated method stub
		super.onScrollChanged(l, t, oldl, oldt);
		Logs.i("oldl " + (l - oldl) + " t-oldt " + (t - oldt));
		if (l == oldl) {

		}
	}

	public boolean isFullscreen() {
		return fullscreen;
	}

	public void setFullscreen(boolean fullscreen) {
		this.fullscreen = fullscreen;
	}

	boolean fromMove;

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		try {
			if ((this.getContentHeight() * getScale() - (getHeight() + getScrollY())) == 0) {
				// showPageBar();
			}
			if (mVelocityTracker == null) {
				mVelocityTracker = VelocityTracker.obtain();
			}
			mVelocityTracker.addMovement(event);

			final int action = event.getAction();
			final float x = event.getX();
			final float y = event.getY();

			switch (action) {
			case MotionEvent.ACTION_DOWN:
				Log.e(TAG, "event down!");
				if (!mScroller.isFinished()) {
					mScroller.abortAnimation();
				}
				oX = x;
				oY = y;
				mLastMotionX = x;
				mLastMotionY = y;
				return gd.onTouchEvent(event);
			case MotionEvent.ACTION_MOVE:
				((View) getParent()).findViewById(R.id.pagebar).setVisibility(
						View.GONE);
				
				fromMove = true;
				// if(true){
				// return false;
				//
				// }
				float deltaX = (mLastMotionX - x);
				mLastMotionX = x;
				float rightDx = -deltaX;
				float rightDy = (float) (y - mLastMotionY);
				mLastMotionY = y;
				float absRightDx = Math.abs(rightDx);
				float absRightDy = Math.abs(rightDy);
				Logs.i(" absRightDx " + absRightDx + " absRightDy "
						+ absRightDy);
				// if(){
				// return true;
				// }
				if (absRightDx > 50 && absRightDy < 50) {
					moved = true;
				}
				// if ((absRightDy / absRightDx) > 1)
				// Logs.i(" rightDx " + rightDx + " rightDy " + rightDy
				// + " absRightDx/absRightDy " + absRightDy
				// / absRightDx + " tan30 " + tan30);
				// touch = true;
				// return super.onTouchEvent(event);
				// }

				return gd.onTouchEvent(event);
				// if ((deltaX < 0 && mCurScreen == 0)
				// || (deltaX > 0 && mCurScreen == getChildCount() - 1)) {
				// return false;
				// }
				// scrollBy(deltaX, 0);

			case MotionEvent.ACTION_UP:

				Log.e(TAG, "event : up");
				dx = x - oX;
				dy = y - oY;
				// if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000);
				int velocityX = (int) velocityTracker.getXVelocity();
				int velocityY = (int) velocityTracker.getYVelocity();
				Log.e(TAG, "111 velocityX:" + velocityX + " dx " + dx);
				Log.e(TAG, "111 velocityY " + velocityY + " SNAP_VELOCITY "
						+ SNAP_VELOCITY + " dy " + dy + " dxConstant "
						+ dxConstant);
				if (Math.abs(dx) < Math.abs(50) && Math.abs(dy) < Math.abs(50)) {
					if (moved) {
						moved = false;
						return gd.onTouchEvent(event);
					}
					System.out.println("11111222222");
				} else if (velocityX > SNAP_VELOCITY && mCurScreen > 0
						&& dx > dxConstant) {
					// // Fling enough to move left
					Logs.i(" --- dy --- " + dy);

					if (dy > 0) {
						Logs.i(" --- <1 ---" + Math.abs(dy / dx));
						if (Math.abs(dy / dx) < 1) {
							snapToScreen(mCurScreen - 1);
						} else {
							return gd.onTouchEvent(event);
						}
					} else {
						Logs.i(" --- <1 ---" + Math.abs(dy / dx));
						if (Math.abs(dy / dx) < 1) {
							snapToScreen(mCurScreen - 1);
						} else {
							return gd.onTouchEvent(event);
						}
					}
					Log.e(TAG, "snap left");
					// snapToScreen(mCurScreen - 1);
					s_pageID--;
					if (mContext.getClass() == HomePageActivity.class)
						((HomePageActivity) mContext).drawCircle();
				} else if (velocityX < -SNAP_VELOCITY && dx < -dxConstant) {
					// Fling enough to move right
					Log.e(TAG, "snap right");
					Logs.i(" --- dy --- " + dy);
					boolean res;
					if (dy > 0) {
						Logs.i(" --- <1 ---" + Math.abs(dy / dx));
						if (Math.abs(dy / dx) < 1) {
							snapToScreen(mCurScreen + 1);
							return gd.onTouchEvent(event);
						} else {
							return gd.onTouchEvent(event);
						}
					} else {
						Logs.i(" --- <1 ---" + Math.abs(dy / dx));
						if (Math.abs(dy / dx) < 1) {
							snapToScreen(mCurScreen + 1);
							return gd.onTouchEvent(event);
						} else {
							return gd.onTouchEvent(event);
						}
					}
					// snapToScreen(mCurScreen + 1);
					// s_pageID++;
					// if (mContext.getClass() == HomePageActivity.class)
					// ((HomePageActivity) mContext).drawCircle();

				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				// }
				mTouchState = TOUCH_STATE_REST;
				break;
			case MotionEvent.ACTION_CANCEL:
				mTouchState = TOUCH_STATE_REST;
				break;
			}

			boolean result = fromMove ? gd.onTouchEvent(event) : true;
			fromMove = false;

			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void scaleView() {
		scaleView(this.activity, fullscreen = !fullscreen);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			getRootView().findViewById(R.id.pagebar).setVisibility(
					View.INVISIBLE);
		}

	};
	boolean pagebarNeverShowed = true;

	private void showPageBar() {
		// TODO Auto-generated method stub
		if (pagebarNeverShowed) {
			pagebarNeverShowed = false;
			((TextView) this.getRootView().findViewById(R.id.prepage))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							snapToScreen(mCurScreen - 1);
						}

					});
			((TextView) this.getRootView().findViewById(R.id.nextpage))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							snapToScreen(mCurScreen + 1);
						}

					});
		}
		((TextView) this.getRootView().findViewById(R.id.currpage))
				.setText(mCurScreen + "/" + DetailsData.getEntities().size());
		this.getRootView().findViewById(R.id.pagebar)
				.setVisibility(View.VISIBLE);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(0);
			}

		}, 5000);
	}

	public static int getS_pageID() {
		return s_pageID;
	}

	Activity activity;

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	float oX;
	float oY;

	float dx;
	float dy;
	boolean isLarged;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE)
				&& (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			final int xDiff = (int) Math.abs(mLastMotionX - x);
			if (xDiff > mTouchSlop) {
				mTouchState = TOUCH_STATE_SCROLLING;
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST
					: TOUCH_STATE_SCROLLING;

			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mTouchState = TOUCH_STATE_REST;
			break;
		}

		return mTouchState != TOUCH_STATE_REST;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if ((int) (getContentHeight() * getScale()) == (getHeight() + getScrollY()) 
				&& this.getRootView().findViewById(R.id.header_panel).getVisibility() == View.GONE) {
			TextView tx1 = (TextView) this.getRootView().findViewById(R.id.currpage);
			if(mCurScreen == 0){
				((TextView)((View) getParent()).findViewById(R.id.prepage)).setTextColor(Color.WHITE);
			}else{
				((TextView)((View) getParent()).findViewById(R.id.prepage)).setTextColor(Color.BLACK);
			}
			if(mCurScreen + 1 == DetailsData.getEntities().size()){
				((TextView)((View) getParent()).findViewById(R.id.nextpage)).setTextColor(Color.WHITE);
			}else{
				((TextView)((View) getParent()).findViewById(R.id.nextpage)).setTextColor(Color.BLACK);
			}
			tx1.setText(mCurScreen + 1 + "/" + DetailsData.getEntities().size());
			((View) getParent()).findViewById(R.id.pagebar).setVisibility(
					View.VISIBLE);
		}else{
			((View) getParent()).findViewById(R.id.pagebar).setVisibility(
					View.GONE);
		}
		return false;
	}


	@Override
	public void onClick(View v) {
		scaleView(this.activity, fullscreen = !fullscreen);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			((View) getParent()).post(new Runnable() {
				
				@Override
				public void run() {
					((View) getParent()).findViewById(R.id.pagebar).setVisibility(
							View.GONE);
				}
			});
			
		}
		return super.onKeyDown(keyCode, event);
	}

}