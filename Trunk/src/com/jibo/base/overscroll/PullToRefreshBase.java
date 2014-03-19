package com.jibo.base.overscroll;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.api.android.GBApp.R;
import com.jibo.util.Logs;
import com.jibo.util.tips.DipUtil;
import com.umeng.analytics.MobclickAgent;

public abstract class PullToRefreshBase<T extends View> extends LinearLayout {
	public boolean tmpFlag = true;
	
	final class SmoothScrollRunnable implements Runnable {

		static final int ANIMATION_DURATION_MS = 190;
		static final int ANIMATION_FPS = 1000 / 60;

		private final Interpolator interpolator;
		private final int scrollToY;
		private final int scrollFromY;
		private final Handler handler;

		private boolean continueRunning = true;
		private long startTime = -1;
		private int currentY = -1;

		public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
			this.handler = handler;
			this.scrollFromY = fromY;
			this.scrollToY = toY;
			this.interpolator = new AccelerateDecelerateInterpolator();
		}

		@Override
		public void run() {

			/**
			 * Only set startTime if this is the first time we're starting, else
			 * actually calculate the Y delta
			 */
			if (startTime == -1) {
				startTime = System.currentTimeMillis();
			} else {

				/**
				 * We do do all calculations in long to reduce software float
				 * calculations. We use 1000 as it gives us good accuracy and
				 * small rounding errors
				 */
				long normalizedTime = (1000 * (System.currentTimeMillis() - startTime))
						/ ANIMATION_DURATION_MS;
				normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

				final int deltaY = Math
						.round((scrollFromY - scrollToY)
								* interpolator
										.getInterpolation(normalizedTime / 1000f));
				this.currentY = scrollFromY - deltaY;
				setHeaderScroll(currentY);
			}

			// If we're not at the target Y, keep going...
			if (continueRunning && scrollToY != currentY) {
				handler.postDelayed(this, ANIMATION_FPS);
			}
		}

		public void stop() {
			this.continueRunning = false;
			this.handler.removeCallbacks(this);
		}
	};

	// ===========================================================
	// Constants
	// ===========================================================

	static final float FRICTION = 2.0f;

	static final int PULL_TO_REFRESH = 0x0;
	static final int RELEASE_TO_REFRESH = 0x1;
	static final int REFRESHING = 0x2;
	static final int MANUAL_REFRESHING = 0x3;

	public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
	public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
	public static final int MODE_BOTH = 0x3;

	// ===========================================================
	// Fields
	// ===========================================================

	private int touchSlop;

	private float initialMotionY;
	private float lastMotionX;
	private float lastMotionY;
	public boolean isBeingDragged = false;

	private int state = PULL_TO_REFRESH;
	private int mode = MODE_PULL_DOWN_TO_REFRESH;
	private int currentMode;

	private boolean disableScrollingWhileRefreshing = true;

	View refreshableView;
	private boolean isPullToRefreshEnabled = false;

	private FilterLayout headerLayout;
	private LoadingLayout footerLayout;
	private int headerHeight;

	private final Handler handler = new Handler();

	private OnRefreshListener onRefreshListener;

	private SmoothScrollRunnable currentSmoothScrollRunnable;

	private AttributeSet attrs;

	private TextView mRefreshViewText;

	private ImageView mRefreshViewImage;

	private ProgressBar mRefreshViewProgress;

	private TextView mRefreshViewLastUpdated;

	private int touchHeight;
	private int newHeight;

	private RotateAnimation mFlipAnimation;

	private RotateAnimation mReverseFlipAnimation;

	// ===========================================================
	// Constructors
	// ===========================================================

	public PullToRefreshBase(Context context) {
		super(context);
		init(context, null);
	}

	public PullToRefreshBase(Context context, int mode) {
		super(context);
		this.mode = mode;
		init(context, null);
	}

	public PullToRefreshBase(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.contexts = context;
		this.attrs = attrs;
		init();
	}

	public boolean init() {
		init(contexts, attrs);
		
		return true;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	/**
	 * Deprecated. Use {@link #getRefreshableView()} from now on.
	 * 
	 * @deprecated
	 * @return The Refreshable View which is currently wrapped
	 */
	public final View getAdapterView() {
		return refreshableView;
	}

	public SmoothScrollRunnable getCurrentSmoothScrollRunnable() {
		return currentSmoothScrollRunnable;
	}

	public void setCurrentSmoothScrollRunnable(
			SmoothScrollRunnable currentSmoothScrollRunnable) {
		this.currentSmoothScrollRunnable = currentSmoothScrollRunnable;
	}

	/**
	 * Get the Wrapped Refreshable View. Anything returned here has already been
	 * added to the content view.
	 * 
	 * @return The View which is currently wrapped
	 */
	public final View getRefreshableView() {
		return refreshableView;
	}

	/**
	 * Whether Pull-to-Refresh is enabled
	 * 
	 * @return enabled
	 */
	public final boolean isPullToRefreshEnabled() {
		return isPullToRefreshEnabled;
	}

	/**
	 * Returns whether the widget has disabled scrolling on the Refreshable View
	 * while refreshing.
	 * 
	 * @param true if the widget has disabled scrolling while refreshing
	 */
	public final boolean isDisableScrollingWhileRefreshing() {
		return disableScrollingWhileRefreshing;
	}

	/**
	 * Returns whether the Widget is currently in the Refreshing state
	 * 
	 * @return true if the Widget is currently refreshing
	 */
	public final boolean isRefreshing() {
		return state == REFRESHING || state == MANUAL_REFRESHING;
	}

	/**
	 * By default the Widget disabled scrolling on the Refreshable View while
	 * refreshing. This method can change this behaviour.
	 * 
	 * @param disableScrollingWhileRefreshing
	 *            - true if you want to disable scrolling while refreshing
	 */
	public final void setDisableScrollingWhileRefreshing(
			boolean disableScrollingWhileRefreshing) {
		this.disableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
	}

	/**
	 * Mark the current Refresh as complete. Will Reset the UI and hide the
	 * Refreshing View
	 */
	public final void onRefreshComplete() {
		if (state != PULL_TO_REFRESH) {
			resetHeader();
			tmpFlag = true;
		}
	}

	/**
	 * Set OnRefreshListener for the Widget
	 * 
	 * @param listener
	 *            - Listener to be used when the Widget is set to Refresh
	 */
	public final void setOnRefreshListener(OnRefreshListener listener) {
		onRefreshListener = listener;
	}

	/**
	 * A mutator to enable/disable Pull-to-Refresh for the current View
	 * 
	 * @param enable
	 *            Whether Pull-To-Refresh should be used
	 */
	public final void setPullToRefreshEnabled(boolean enable) {
		this.isPullToRefreshEnabled = enable;
	}

	/**
	 * Set Text to show when the Widget is being pulled, and will refresh when
	 * released
	 * 
	 * @param releaseLabel
	 *            - String to display
	 */
	public void setReleaseLabel(String releaseLabel) {
		if (null != headerLayout) {
			headerLayout.setReleaseLabel(releaseLabel);
		}
		if (null != footerLayout) {
			footerLayout.setReleaseLabel(releaseLabel);
		}
	}

	/**
	 * Set Text to show when the Widget is being Pulled
	 * 
	 * @param pullLabel
	 *            - String to display
	 */
	public void setPullLabel(String pullLabel) {
		if (null != headerLayout) {
			headerLayout.setPullLabel(pullLabel);
		}
		if (null != footerLayout) {
			footerLayout.setPullLabel(pullLabel);
		}
	}

	/**
	 * Set Text to show when the Widget is refreshing
	 * 
	 * @param refreshingLabel
	 *            - String to display
	 */
	public void setRefreshingLabel(String refreshingLabel) {

		if (null != footerLayout) {
			footerLayout.setRefreshingLabel(refreshingLabel);
		}
	}

	public final void setRefreshing() {
		this.setRefreshing(true);
	}

	/**
	 * Sets the Widget to be in the refresh state. The UI will be updated to
	 * show the 'Refreshing' view.
	 * 
	 * @param doScroll
	 *            - true if you want to force a scroll to the Refreshing view.
	 */
	public final void setRefreshing(boolean doScroll) {
		if (!isRefreshing()) {
			setRefreshingInternal(doScroll);
			state = MANUAL_REFRESHING;
		}
	}

	public final boolean hasPullFromTop() {
		return currentMode != MODE_PULL_UP_TO_REFRESH;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public final boolean onTouchEvent(MotionEvent event) {
		Logs.i(" -- onTouchEvent 111");
		if (!isPullToRefreshEnabled) {
			return false;
		}
		Logs.i(" -- onTouchEvent 11122");
		if (isRefreshing() && disableScrollingWhileRefreshing) {
			return super.onTouchEvent(event);
		}
		Logs.i(" -- onTouchEvent 111222");
		if (event.getAction() == MotionEvent.ACTION_DOWN
				&& event.getEdgeFlags() != 0) {
			return false;
		}
		Logs.i(" -- onTouchEvent 111222_22");
		switch (event.getAction()) {

		case MotionEvent.ACTION_MOVE: {
			if (isBeingDragged||tmpFlag) {
				
				lastMotionY = event.getY();
				this.pullEvent();
				return true;
			}
			break;
		}

		case MotionEvent.ACTION_DOWN: {
			Logs.i(" -- onTouchEvent 111222_333");
			if (isReadyForPull()) {
				lastMotionY = initialMotionY = event.getY();
				return true;
			}
			break;
		}

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP: {
			
			Logs.i(" -- isBeingDragged up" + isBeingDragged);
			
			if (isBeingDragged||tmpFlag) {
				Logs.i(" ==  isBeingDragged = false 111");
				isBeingDragged = false;

				Logs.i(" -- state == RELEASE_TO_REFRESH "
						+ (state == RELEASE_TO_REFRESH));
				if (state == RELEASE_TO_REFRESH) {
					setRefreshingInternal(true);
					smoothScrollTo(-headerHeight);
					refreshLayout.findViewById(R.id.blank).setVisibility(View.GONE);
					tmpFlag = false;
					// onRefreshListener.onRefresh();
				} else {
					//滑动到哪里
					MobclickAgent.onEvent(this.getContext(), "research_filter_pulldown");
					smoothScrollTo(-15);
					refreshLayout.findViewById(R.id.blank).setVisibility(View.GONE);
				}
				return true;
			}
			break;
		}
		}

		return false;
	}

	@Override
	public final boolean onInterceptTouchEvent(MotionEvent event) {

		if (!isPullToRefreshEnabled) {
			return false;
		}

		if (isRefreshing() && disableScrollingWhileRefreshing) {
			return super.onInterceptTouchEvent(event);
		}

		final int action = event.getAction();
		//向上拉
		if (action == MotionEvent.ACTION_CANCEL
				|| action == MotionEvent.ACTION_UP) {
			Logs.i(" ==  isBeingDragged = false 11122");
			isBeingDragged = false;
			return false;
		}

		if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
			return true;
		}

		switch (action) {
		case MotionEvent.ACTION_MOVE: {
			Logs.i(" ==移动");
			//添加刷新箭头
			arrowsAnimation();
			if (isReadyForPull()) {
				Logs.i("111 move");
				final float y = event.getY();
				final float dy = y - lastMotionY;
				final float yDiff = Math.abs(dy);
				final float xDiff = Math.abs(event.getX() - lastMotionX);
				Logs.i("++"+yDiff+"++"+touchSlop+"++"+xDiff);
				if (yDiff > touchSlop && yDiff > xDiff) {
					if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH)
							&& dy >= 0.0001f && isReadyForPullDown()) {
						lastMotionY = y;
						Logs.i("==这是什么1");//显示第一屏
						isBeingDragged = true;
						if (mode == MODE_BOTH) {
							currentMode = MODE_PULL_DOWN_TO_REFRESH;
						}
					} else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH)
							&& dy <= 0.0001f && isReadyForPullUp()) {
						lastMotionY = y;
						isBeingDragged = true;
						Logs.i("==这是什么2");
						if (mode == MODE_BOTH) {
							currentMode = MODE_PULL_UP_TO_REFRESH;
						}
					}
				}
			}
//			if(isSecondItemVisible()){
//				
//			}
			break;
		}
		//向下拉
		case MotionEvent.ACTION_DOWN: {
			Logs.i(" ==按下");
			if (isReadyForPull()) {
				lastMotionY = initialMotionY = event.getY();
				lastMotionX = event.getX();
				isBeingDragged = false;
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			Logs.i(" ==上提");
		}
		}

		return isBeingDragged;
	}
	protected void arrowsAnimation(){
		if(refreshLayout==null){
			return;
		}
		mRefreshViewText =
	            (TextView) refreshLayout.findViewById(R.id.pull_to_refresh_text);
	        mRefreshViewImage =
	            (ImageView) refreshLayout.findViewById(R.id.pull_to_refresh_image);
	        mRefreshViewProgress =
	            (ProgressBar) refreshLayout.findViewById(R.id.pull_to_refresh_progress);
	        mRefreshViewLastUpdated =
	            (TextView) refreshLayout.findViewById(R.id.pull_to_refresh_updated_at);
	        mFlipAnimation = new RotateAnimation(0, -180,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
	        mFlipAnimation.setInterpolator(new LinearInterpolator());
	        mFlipAnimation.setDuration(250);
	        mFlipAnimation.setFillAfter(true);
	        mReverseFlipAnimation = new RotateAnimation(-180, 0,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
	                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
	        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
	        mReverseFlipAnimation.setDuration(250);
	        mReverseFlipAnimation.setFillAfter(true);
		if(Math.abs(newHeight)<=refreshLayout.getHeight()-20){
			Logs.i("refreshLayoutgetHeight1()"+refreshLayout.getHeight());
			mRefreshViewText.setText(R.string.pull_to_refresh_pull_label);
			mRefreshViewImage.setVisibility(View.VISIBLE);
			mRefreshViewImage.clearAnimation();
            mRefreshViewImage.startAnimation(mFlipAnimation);
            //mRefreshState = RELEASE_TO_REFRESH;
		}else{
			Logs.i("refreshLayoutgetHeight2()"+refreshLayout.getHeight());
			mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
			mRefreshViewImage.clearAnimation();
            mRefreshViewImage.startAnimation(mReverseFlipAnimation);
			mRefreshViewText.setText(R.string.pull_to_refresh_release_label);
		}
	}
	protected void addRefreshableView(Context context, View refreshableView) {
		addView(refreshableView, new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, 0, 1.0f));
	}

	/**
	 * This is implemented by derived classes to return the created View. If you
	 * need to use a custom View (such as a custom ListView), override this
	 * method and return an instance of your custom class.
	 * 
	 * Be sure to set the ID of the view in this method, especially if you're
	 * using a ListActivity or ListFragment.
	 * 
	 * @param context
	 * @param attrs
	 *            AttributeSet from wrapped class. Means that anything you
	 *            include in the XML layout declaration will be routed to the
	 *            created View
	 * @return New instance of the Refreshable View
	 */
	protected abstract View createRefreshableView(Context context,
			AttributeSet attrs);

	protected final int getCurrentMode() {
		return currentMode;
	}

	public final LoadingLayout getFooterLayout() {
		return footerLayout;
	}

	public final FilterLayout getHeaderLayout() {
		return headerLayout;
	}

	protected final int getHeaderHeight() {
		return headerHeight;
	}

	protected final int getMode() {
		return mode;
	}

	/**
	 * Implemented by derived class to return whether the View is in a state
	 * where the user can Pull to Refresh by scrolling down.
	 * 
	 * @return true if the View is currently the correct state (for example, top
	 *         of a ListView)
	 */
	protected abstract boolean isReadyForPullDown();

	/**
	 * Implemented by derived class to return whether the View is in a state
	 * where the user can Pull to Refresh by scrolling up.
	 * 
	 * @return true if the View is currently in the correct state (for example,
	 *         bottom of a ListView)
	 */
	protected abstract boolean isReadyForPullUp();

	// ===========================================================
	// Methods
	// ===========================================================

	protected void resetHeader() {
		state = PULL_TO_REFRESH;
		Logs.i(" ==  isBeingDragged = false 1115");
		isBeingDragged = false;

		if (null != headerLayout) {
			headerLayout.reset();
		}
		if (null != footerLayout) {
			footerLayout.reset();
		}

		smoothScrollTo(-15);
	}

	protected void setRefreshingInternal(boolean doScroll) {
		state = REFRESHING;

		if (null != headerLayout) {
			headerLayout.refreshing();
		}
		if (null != footerLayout) {
			footerLayout.refreshing();
		}
		if (doScroll) {
			Log.i("== headerHeight3" , headerHeight+"");
			smoothScrollTo(currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight
					: headerHeight);
		}
	}

	protected final void setHeaderScroll(int y) {
		scrollTo(0, y);
	}

	protected final void smoothScrollTo(int y) {
		if (null != currentSmoothScrollRunnable) {
			currentSmoothScrollRunnable.stop();
		}
Logs.i("++自动缩回"+this.getScrollY());
		if (this.getScrollY() != y) {
			this.currentSmoothScrollRunnable = new SmoothScrollRunnable(
					handler, getScrollY(), y);
			handler.post(currentSmoothScrollRunnable);
		}
	}

	Context contexts;
	TypedArray a;

	private int tapHeight;

	public View refreshLayout;

	private int refreshHeight;
	public View refresh;
	private void init(Context context, AttributeSet attrs) {
		refresh = LayoutInflater.from(context).inflate(R.layout.pull_to_refresh_header, null);
		this.contexts = context;
		setOrientation(LinearLayout.VERTICAL);
		tapHeight = DipUtil.dip2px(contexts, 50);
		touchSlop = ViewConfiguration.getTouchSlop();
		// Styleables from XML
		a = context.obtainStyledAttributes(attrs, R.styleable.PullToRefresh);
		this.a = a;
		// Refreshable View
		// By passing the attrs, we can add ListView/GridView params via XML
		refreshableView = this.createRefreshableView(context, attrs);
		this.addRefreshableView(context, refreshableView);
//		this.setPadding(0, refresh.getHeight(), 0, 0);
		if (a.hasValue(R.styleable.PullToRefresh_mode)) {
			mode = a.getInteger(R.styleable.PullToRefresh_mode,
					MODE_PULL_DOWN_TO_REFRESH);
		}
		
	}

	public void initHeaderLayout(int headerPartHeight) {
		initByAttr(headerPartHeight);
		
	}

	/**将filterLayout添加到scrollview
	 * @param headerPartHeight
	 */
	public void initByAttr(int headerPartHeight) {
		// Loading View Strings
		String pullLabel = contexts
				.getString(R.string.pull_to_refresh_pull_label);
		String refreshingLabel = contexts
				.getString(R.string.pull_to_refresh_refreshing_label);
		String releaseLabel = contexts
				.getString(R.string.pull_to_refresh_release_label);

		// Add Loading Views
		if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
			headerLayout = new FilterLayout(contexts,
					MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullLabel,
					refreshingLabel);
			headerLayout.findViewById(R.id.scroll).setLayoutParams(
					new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.FILL_PARENT,
							headerPartHeight));
			addView(headerLayout, 0, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(headerLayout);
			refreshLayout = LayoutInflater.from(getContext()).inflate(R.layout.pull_to_refresh_header, null);
			addView(refreshLayout, 1, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			
			measureView(refreshLayout);
			headerHeight = headerLayout.getMeasuredHeight()+refreshLayout.getMeasuredHeight();
		}
		if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
			footerLayout = new LoadingLayout(contexts, MODE_PULL_UP_TO_REFRESH,
					releaseLabel, pullLabel, refreshingLabel);
			addView(footerLayout, new LinearLayout.LayoutParams(
					ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT));
			measureView(footerLayout);
			headerHeight = footerLayout.getMeasuredHeight();
		}

		// Styleables from XML
		if (a.hasValue(R.styleable.PullToRefresh_headerTextColor)) {
			final int color = a.getColor(
					R.styleable.PullToRefresh_headerTextColor, Color.BLACK);
			if (null != headerLayout) {
				headerLayout.setTextColor(color);
			}
			if (null != footerLayout) {
				footerLayout.setTextColor(color);
			}
		}
		if (a.hasValue(R.styleable.PullToRefresh_headerBackground)) {
			this.setBackgroundResource(a.getResourceId(
					R.styleable.PullToRefresh_headerBackground, Color.WHITE));
		}
		if (a.hasValue(R.styleable.PullToRefresh_adapterViewBackground)) {
			refreshableView.setBackgroundResource(a.getResourceId(
					R.styleable.PullToRefresh_adapterViewBackground,
					Color.WHITE));
		}
		a.recycle();

		// Hide Loading Views
		Logs.i("mode是"+mode);
		Log.i("=== headerHeight==" , headerHeight+"");
		switch (mode) {
		case MODE_BOTH:
			setPadding(0, -headerHeight, 0, -headerHeight);
			break;
		case MODE_PULL_UP_TO_REFRESH:
			setPadding(0, 0, 0, -headerHeight);
			break;
		case MODE_PULL_DOWN_TO_REFRESH:
		default:
			setPadding(0, -headerHeight, 0, 0);
			break;
		}

		// If we're not using MODE_BOTH, then just set currentMode to current
		// mode
		if (mode != MODE_BOTH) {
			currentMode = mode;
		}
	}

	private void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * Actions a Pull Event
	 * 
	 * @return true if the Event has been handled, false if there has been no
	 *         change
	 */
	private boolean pullEvent() {

		final int oldHeight = this.getScrollY();

		switch (currentMode) {
		case MODE_PULL_UP_TO_REFRESH:
			newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0)
					/ FRICTION);
			// newHeight = Math.round((initialMotionY - lastMotionY) /
			// FRICTION);
			break;
		case MODE_PULL_DOWN_TO_REFRESH:
		default:
			newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0)
					/ FRICTION);
			// newHeight = Math.round((initialMotionY - lastMotionY) /
			// FRICTION);
			break;
		}

		setHeaderScroll(newHeight);
		Logs.i("--- newHeight " + newHeight);
		if (newHeight != 0) {
			Logs.i("--- state " + state + " " + (state == PULL_TO_REFRESH));
			Logs.i("--- tapHeight " + tapHeight);
			//下拉多少后才会进入下一屏
			if (state == PULL_TO_REFRESH && tapHeight < Math.abs(newHeight)) {
				state = RELEASE_TO_REFRESH;

				switch (currentMode) {
				case MODE_PULL_UP_TO_REFRESH:
					footerLayout.releaseToRefresh();
					break;
				case MODE_PULL_DOWN_TO_REFRESH:
					headerLayout.releaseToRefresh();
					break;
				}

				return true;

			} else if (state == RELEASE_TO_REFRESH
					&& tapHeight >= Math.abs(newHeight)) {
				state = PULL_TO_REFRESH;

				switch (currentMode) {
				case MODE_PULL_UP_TO_REFRESH:
					footerLayout.pullToRefresh();
					break;
				case MODE_PULL_DOWN_TO_REFRESH:
					Logs.i("++释放回到原来");
					headerLayout.pullToRefresh();
					break;
				}

				return true;
			}
		}

		return oldHeight != newHeight;
	}

	private boolean isReadyForPull() {
		switch (mode) {
		case MODE_PULL_DOWN_TO_REFRESH:
			return isReadyForPullDown();
		case MODE_PULL_UP_TO_REFRESH:
			return isReadyForPullUp();
		case MODE_BOTH:
			return isReadyForPullUp() || isReadyForPullDown();
		}
		return false;
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

	public static interface OnRefreshListener {

		public void onRefresh();

	}

	public static interface OnLastItemVisibleListener {

		public void onLastItemVisible();

	}

	@Override
	public void setLongClickable(boolean longClickable) {
		getRefreshableView().setLongClickable(longClickable);
	}
}
