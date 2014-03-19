/*      */ package com.jibo.v4.view;
/*      */ 
/*      */ import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.jibo.v4.os.ParcelableCompat;
import com.jibo.v4.os.ParcelableCompatCreatorCallbacks;
import com.jibo.v4.widget.EdgeEffectCompat;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.animation.Interpolator;
import android.widget.Scroller;
/*      */ 
/*      */ public class PagerView extends ViewGroup
/*      */ {
/*      */   private static final String TAG = "ViewPager";
/*      */   private static final boolean DEBUG = false;
/*      */   private static final boolean USE_CACHE = false;
/*      */   private static final int DEFAULT_OFFSCREEN_PAGES = 1;
/*      */   private static final int MAX_SETTLE_DURATION = 800;
/*   73 */   private static final Comparator<ItemInfo> COMPARATOR = new Comparator<ItemInfo>()
/*      */   {
/*      */     public int compare(PagerView.ItemInfo lhs, PagerView.ItemInfo rhs) {
/*   76 */       return lhs.position - rhs.position;
/*      */     }
/*   73 */   };
/*      */ 
/*   79 */   private static final Interpolator sInterpolator = new Interpolator()
/*      */   {
/*      */     public float getInterpolation(float t)
/*      */     {
/*   83 */       t -= 1.0F;
/*   84 */       return t * t * t + 1.0F;
/*      */     }
/*   79 */   };
/*      */ 
/*   88 */   private final ArrayList<ItemInfo> mItems = new ArrayList();
/*      */   private PagerAdapter mAdapter;
/*      */   private int mCurItem;
/*   92 */   private int mRestoredCurItem = -1;
/*   93 */   private Parcelable mRestoredAdapterState = null;
/*   94 */   private ClassLoader mRestoredClassLoader = null;
/*      */   private Scroller mScroller;
/*      */   private PagerAdapter.DataSetObserver mObserver;
/*      */   private int mPageMargin;
/*      */   private Drawable mMarginDrawable;
/*      */   private int mChildWidthMeasureSpec;
/*      */   private int mChildHeightMeasureSpec;
/*      */   private boolean mInLayout;
/*      */   private boolean mScrollingCacheEnabled;
/*      */   private boolean mPopulatePending;
/*      */   private boolean mScrolling;
/*  109 */   private int mOffscreenPageLimit = 1;
/*      */   private boolean mIsBeingDragged;
/*      */   private boolean mIsUnableToDrag;
/*      */   private int mTouchSlop;
/*      */   private float mInitialMotionX;
/*      */   private float mLastMotionX;
/*      */   private float mLastMotionY;
/*  124 */   private int mActivePointerId = -1;
/*      */   private static final int INVALID_POINTER = -1;
/*      */   private VelocityTracker mVelocityTracker;
/*      */   private int mMinimumVelocity;
/*      */   private int mMaximumVelocity;
/*      */   private float mBaseLineFlingVelocity;
/*      */   private float mFlingVelocityInfluence;
/*      */   private boolean mFakeDragging;
/*      */   private long mFakeDragBeginTime;
/*      */   private EdgeEffectCompat mLeftEdge;
/*      */   private EdgeEffectCompat mRightEdge;
/*  146 */   private boolean mFirstLayout = true;
/*      */   private OnPageChangeListener mOnPageChangeListener;
/*      */   public static final int SCROLL_STATE_IDLE = 0;
/*      */   public static final int SCROLL_STATE_DRAGGING = 1;
/*      */   public static final int SCROLL_STATE_SETTLING = 2;
/*  166 */   private int mScrollState = 0;
/*      */ 
/*      */   public PagerView(Context context)
/*      */   {
/*  228 */     super(context);
/*  229 */     initViewPager();
/*      */   }
/*      */ 
/*      */   public PagerView(Context context, AttributeSet attrs) {
/*  233 */     super(context, attrs);
/*  234 */     initViewPager();
/*      */   }
/*      */ 
/*      */   void initViewPager() {
/*  238 */     setWillNotDraw(false);
/*  239 */     setDescendantFocusability(262144);
/*  240 */     setFocusable(true);
/*  241 */     Context context = getContext();
/*  242 */     this.mScroller = new Scroller(context, sInterpolator);
/*  243 */     ViewConfiguration configuration = ViewConfiguration.get(context);
/*  244 */     this.mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
/*  245 */     this.mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
/*  246 */     this.mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
/*  247 */     this.mLeftEdge = new EdgeEffectCompat(context);
/*  248 */     this.mRightEdge = new EdgeEffectCompat(context);
/*      */ 
/*  250 */     float density = context.getResources().getDisplayMetrics().density;
/*  251 */     this.mBaseLineFlingVelocity = (2500.0F * density);
/*  252 */     this.mFlingVelocityInfluence = 0.4F;
/*      */   }
/*      */ 
/*      */   private void setScrollState(int newState) {
/*  256 */     if (this.mScrollState == newState) {
/*  257 */       return;
/*      */     }
/*      */ 
/*  260 */     this.mScrollState = newState;
/*  261 */     if (this.mOnPageChangeListener != null)
/*  262 */       this.mOnPageChangeListener.onPageScrollStateChanged(newState);
/*      */   }
/*      */ 
/*      */   public void setAdapter(PagerAdapter adapter)
/*      */   {
/*  267 */     if (this.mAdapter != null) {
/*  268 */       this.mAdapter.setDataSetObserver(null);
/*  269 */       this.mAdapter.startUpdate(this);
/*  270 */       for (int i = 0; i < this.mItems.size(); i++) {
/*  271 */         ItemInfo ii = (ItemInfo)this.mItems.get(i);
/*  272 */         this.mAdapter.destroyItem(this, ii.position, ii.object);
/*      */       }
/*  274 */       this.mAdapter.finishUpdate(this);
/*  275 */       this.mItems.clear();
/*  276 */       removeAllViews();
/*  277 */       this.mCurItem = 0;
/*  278 */       scrollTo(0, 0);
/*      */     }
/*      */ 
/*  281 */     this.mAdapter = adapter;
/*      */ 
/*  283 */     if (this.mAdapter != null) {
/*  284 */       if (this.mObserver == null) {
/*  285 */         this.mObserver = new DataSetObserver();
/*      */       }
/*  287 */       this.mAdapter.setDataSetObserver(this.mObserver);
/*  288 */       this.mPopulatePending = false;
/*  289 */       if (this.mRestoredCurItem >= 0) {
/*  290 */         this.mAdapter.restoreState(this.mRestoredAdapterState, this.mRestoredClassLoader);
/*  291 */         setCurrentItemInternal(this.mRestoredCurItem, false, true);
/*  292 */         this.mRestoredCurItem = -1;
/*  293 */         this.mRestoredAdapterState = null;
/*  294 */         this.mRestoredClassLoader = null;
/*      */       } else {
/*  296 */         populate();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public PagerAdapter getAdapter() {
/*  302 */     return this.mAdapter;
/*      */   }
/*      */ 
/*      */   public void setCurrentItem(int item)
/*      */   {
/*  313 */     this.mPopulatePending = false;
/*  314 */     setCurrentItemInternal(item, !this.mFirstLayout, false);
/*      */   }
/*      */ 
/*      */   public void setCurrentItem(int item, boolean smoothScroll)
/*      */   {
/*  324 */     this.mPopulatePending = false;
/*  325 */     setCurrentItemInternal(item, smoothScroll, false);
/*      */   }
/*      */ 
/*      */   public int getCurrentItem() {
/*  329 */     return this.mCurItem;
/*      */   }
/*      */ 
/*      */   void setCurrentItemInternal(int item, boolean smoothScroll, boolean always) {
/*  333 */     setCurrentItemInternal(item, smoothScroll, always, 0);
/*      */   }
/*      */ 
/*      */   void setCurrentItemInternal(int item, boolean smoothScroll, boolean always, int velocity) {
/*  337 */     if ((this.mAdapter == null) || (this.mAdapter.getCount() <= 0)) {
/*  338 */       setScrollingCacheEnabled(false);
/*  339 */       return;
/*      */     }
/*  341 */     if ((!always) && (this.mCurItem == item) && (this.mItems.size() != 0)) {
/*  342 */       setScrollingCacheEnabled(false);
/*  343 */       return;
/*      */     }
/*  345 */     if (item < 0)
/*  346 */       item = 0;
/*  347 */     else if (item >= this.mAdapter.getCount()) {
/*  348 */       item = this.mAdapter.getCount() - 1;
/*      */     }
/*  350 */     int pageLimit = this.mOffscreenPageLimit;
/*  351 */     if ((item > this.mCurItem + pageLimit) || (item < this.mCurItem - pageLimit))
/*      */     {
/*  355 */       for (int i = 0; i < this.mItems.size(); i++) {
/*  356 */         ((ItemInfo)this.mItems.get(i)).scrolling = true;
/*      */       }
/*      */     }
/*  359 */     boolean dispatchSelected = this.mCurItem != item;
/*  360 */     this.mCurItem = item;
/*  361 */     populate();
/*  362 */     int destX = (getWidth() + this.mPageMargin) * item;
/*  363 */     if (smoothScroll) {
/*  364 */       smoothScrollTo(destX, 0, 350);
/*  365 */       if ((dispatchSelected) && (this.mOnPageChangeListener != null))
/*  366 */         this.mOnPageChangeListener.onPageSelected(item);
/*      */     }
/*      */     else {
/*  369 */       if ((dispatchSelected) && (this.mOnPageChangeListener != null)) {
/*  370 */         this.mOnPageChangeListener.onPageSelected(item);
/*      */       }
/*  372 */       completeScroll();
/*  373 */       scrollTo(destX, 0);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setOnPageChangeListener(OnPageChangeListener listener) {
/*  378 */     this.mOnPageChangeListener = listener;
/*      */   }
/*      */ 
/*      */   public int getOffscreenPageLimit()
/*      */   {
/*  389 */     return this.mOffscreenPageLimit;
/*      */   }
/*      */ 
/*      */   public void setOffscreenPageLimit(int limit)
/*      */   {
/*  410 */     if (limit < 1) {
/*  411 */       Log.w("ViewPager", "Requested offscreen page limit " + limit + " too small; defaulting to " + 1);
/*      */ 
/*  413 */       limit = 1;
/*      */     }
/*  415 */     if (limit != this.mOffscreenPageLimit) {
/*  416 */       this.mOffscreenPageLimit = limit;
/*  417 */       populate();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setPageMargin(int marginPixels)
/*      */   {
/*  430 */     int oldMargin = this.mPageMargin;
/*  431 */     this.mPageMargin = marginPixels;
/*      */ 
/*  433 */     int width = getWidth();
/*  434 */     recomputeScrollPosition(width, width, marginPixels, oldMargin);
/*      */ 
/*  436 */     requestLayout();
/*      */   }
/*      */ 
/*      */   public int getPageMargin()
/*      */   {
/*  445 */     return this.mPageMargin;
/*      */   }
/*      */ 
/*      */   public void setPageMarginDrawable(Drawable d)
/*      */   {
/*  454 */     this.mMarginDrawable = d;
/*  455 */     if (d != null) refreshDrawableState();
/*  456 */     setWillNotDraw(d == null);
/*  457 */     invalidate();
/*      */   }
/*      */ 
/*      */   public void setPageMarginDrawable(int resId)
/*      */   {
/*  466 */     setPageMarginDrawable(getContext().getResources().getDrawable(resId));
/*      */   }
/*      */ 
/*      */   protected boolean verifyDrawable(Drawable who)
/*      */   {
/*  471 */     return (super.verifyDrawable(who)) || (who == this.mMarginDrawable);
/*      */   }
/*      */ 
/*      */   protected void drawableStateChanged()
/*      */   {
/*  476 */     super.drawableStateChanged();
/*  477 */     Drawable d = this.mMarginDrawable;
/*  478 */     if ((d != null) && (d.isStateful()))
/*  479 */       d.setState(getDrawableState());
/*      */   }
/*      */ 
/*      */   float distanceInfluenceForSnapDuration(float f)
/*      */   {
/*  488 */     f -= 0.5F;
/*  489 */     f = (float)(f * 0.47123891676382D);
/*  490 */     return (float)Math.sin(f);
/*      */   }
/*      */ 
/*      */   void smoothScrollTo(int x, int y)
/*      */   {
/*  500 */     smoothScrollTo(x, y, 0);
/*      */   }
/*      */ 
/*      */   void smoothScrollTo(int x, int y, int velocity)
/*      */   {
/*  511 */     if (getChildCount() == 0)
/*      */     {
/*  513 */       setScrollingCacheEnabled(false);
/*  514 */       return;
/*      */     }
/*  516 */     int sx = getScrollX();
/*  517 */     int sy = getScrollY();
/*  518 */     int dx = x - sx;
/*  519 */     int dy = y - sy;
/*  520 */     if ((dx == 0) && (dy == 0)) {
/*  521 */       completeScroll();
/*  522 */       setScrollState(0);
/*  523 */       return;
/*      */     }
/*      */ 
/*  526 */     setScrollingCacheEnabled(true);
/*  527 */     this.mScrolling = true;
/*  528 */     setScrollState(2);
/*      */ 
/*  530 */     float pageDelta = Math.abs(dx) * 100.0F/ (getWidth() + this.mPageMargin);
/*  531 */     int duration = (int)pageDelta;
/*      */ 
/*  533 */     velocity = Math.abs(velocity);
/*  534 */     if (velocity > 0)
/*  535 */       duration = (int)(duration + duration / (velocity / this.mBaseLineFlingVelocity) * this.mFlingVelocityInfluence);
/*      */     else {
/*  537 */       duration += 100;
/*      */     }
/*  539 */     duration = Math.min(duration, 800);
/*      */ 
/*  541 */     this.mScroller.startScroll(sx, sy, dx, dy, duration);
/*  542 */     invalidate();
/*      */   }
/*      */ 
/*      */   void addNewItem(int position, int index) {
/*  546 */     ItemInfo ii = new ItemInfo();
/*  547 */     ii.position = position;
/*  548 */     ii.object = this.mAdapter.instantiateItem(this, position);
/*  549 */     if (index < 0)
/*  550 */       this.mItems.add(ii);
/*      */     else
/*  552 */       this.mItems.add(index, ii);
/*      */   }
/*      */ 
/*      */   void dataSetChanged()
/*      */   {
/*  559 */     boolean needPopulate = (this.mItems.size() < 3) && (this.mItems.size() < this.mAdapter.getCount());
/*  560 */     int newCurrItem = -1;
/*      */ 
/*  562 */     for (int i = 0; i < this.mItems.size(); i++) {
/*  563 */       ItemInfo ii = (ItemInfo)this.mItems.get(i);
/*  564 */       int newPos = this.mAdapter.getItemPosition(ii.object);
/*      */ 
/*  566 */       if (newPos == -1)
/*      */       {
/*      */         continue;
/*      */       }
/*  570 */       if (newPos == -2) {
/*  571 */         this.mItems.remove(i);
/*  572 */         i--;
/*  573 */         this.mAdapter.destroyItem(this, ii.position, ii.object);
/*  574 */         needPopulate = true;
/*      */ 
/*  576 */         if (this.mCurItem != ii.position)
/*      */           continue;
/*  578 */         newCurrItem = Math.max(0, Math.min(this.mCurItem, this.mAdapter.getCount() - 1));
/*      */       }
/*  583 */       else if (ii.position != newPos) {
/*  584 */         if (ii.position == this.mCurItem)
/*      */         {
/*  586 */           newCurrItem = newPos;
/*      */         }
/*      */ 
/*  589 */         ii.position = newPos;
/*  590 */         needPopulate = true;
/*      */       }
/*      */     }
/*      */ 
/*  594 */     Collections.sort(this.mItems, COMPARATOR);
/*      */ 
/*  596 */     if (newCurrItem >= 0)
/*      */     {
/*  598 */       setCurrentItemInternal(newCurrItem, false, true);
/*  599 */       needPopulate = true;
/*      */     }
/*  601 */     if (needPopulate) {
/*  602 */       populate();
/*  603 */       requestLayout();
/*      */     }
/*      */   }
/*      */ 
/*      */   void populate() {
/*  608 */     if (this.mAdapter == null) {
/*  609 */       return;
/*      */     }
/*      */ 
/*  616 */     if (this.mPopulatePending)
/*      */     {
/*  618 */       return;
/*      */     }
/*      */ 
/*  624 */     if (getWindowToken() == null) {
/*  625 */       return;
/*      */     }
/*      */ 
/*  628 */     this.mAdapter.startUpdate(this);
/*      */ 
/*  630 */     int pageLimit = this.mOffscreenPageLimit;
/*  631 */     int startPos = Math.max(0, this.mCurItem - pageLimit);
/*  632 */     int N = this.mAdapter.getCount();
/*  633 */     int endPos = Math.min(N - 1, this.mCurItem + pageLimit);
/*      */ 
/*  638 */     int lastPos = -1;
/*  639 */     for (int i = 0; i < this.mItems.size(); i++) {
/*  640 */       ItemInfo ii = (ItemInfo)this.mItems.get(i);
/*  641 */       if (((ii.position < startPos) || (ii.position > endPos)) && (!ii.scrolling))
/*      */       {
/*  643 */         this.mItems.remove(i);
/*  644 */         i--;
/*  645 */         this.mAdapter.destroyItem(this, ii.position, ii.object);
/*  646 */       } else if ((lastPos < endPos) && (ii.position > startPos))
/*      */       {
/*  650 */         lastPos++;
/*  651 */         if (lastPos < startPos) {
/*  652 */           lastPos = startPos;
/*      */         }
/*  654 */         while ((lastPos <= endPos) && (lastPos < ii.position))
/*      */         {
/*  656 */           addNewItem(lastPos, i);
/*  657 */           lastPos++;
/*  658 */           i++;
/*      */         }
/*      */       }
/*  661 */       lastPos = ii.position;
/*      */     }
/*      */ 
/*  665 */     lastPos = this.mItems.size() > 0 ? ((ItemInfo)this.mItems.get(this.mItems.size() - 1)).position : -1;
/*  666 */     if (lastPos < endPos) {
/*  667 */       lastPos++;
/*  668 */       lastPos = lastPos > startPos ? lastPos : startPos;
/*  669 */       while (lastPos <= endPos)
/*      */       {
/*  671 */         addNewItem(lastPos, -1);
/*  672 */         lastPos++;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  683 */     ItemInfo curItem = null;
/*  684 */     for (int i = 0; i < this.mItems.size(); i++) {
/*  685 */       if (((ItemInfo)this.mItems.get(i)).position == this.mCurItem) {
/*  686 */         curItem = (ItemInfo)this.mItems.get(i);
/*  687 */         break;
/*      */       }
/*      */     }
/*  690 */     this.mAdapter.setPrimaryItem(this, this.mCurItem, curItem != null ? curItem.object : null);
/*      */ 
/*  692 */     this.mAdapter.finishUpdate(this);
/*      */ 
/*  694 */     if (hasFocus()) {
/*  695 */       View currentFocused = findFocus();
/*  696 */       ItemInfo ii = currentFocused != null ? infoForAnyChild(currentFocused) : null;
/*  697 */       if ((ii == null) || (ii.position != this.mCurItem))
/*  698 */         for (int i = 0; i < getChildCount(); i++) {
/*  699 */           View child = getChildAt(i);
/*  700 */           ii = infoForChild(child);
/*  701 */           if ((ii != null) && (ii.position == this.mCurItem) && 
/*  702 */             (child.requestFocus(2)))
/*      */             break;
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public Parcelable onSaveInstanceState()
/*      */   {
/*  759 */     Parcelable superState = super.onSaveInstanceState();
/*  760 */     SavedState ss = new SavedState(superState);
/*  761 */     ss.position = this.mCurItem;
/*  762 */     if (this.mAdapter != null) {
/*  763 */       ss.adapterState = this.mAdapter.saveState();
/*      */     }
/*  765 */     return ss;
/*      */   }
/*      */ 
/*      */   public void onRestoreInstanceState(Parcelable state)
/*      */   {
/*  770 */     if (!(state instanceof SavedState)) {
/*  771 */       super.onRestoreInstanceState(state);
/*  772 */       return;
/*      */     }
/*      */ 
/*  775 */     SavedState ss = (SavedState)state;
/*  776 */     super.onRestoreInstanceState(ss.getSuperState());
/*      */ 
/*  778 */     if (this.mAdapter != null) {
/*  779 */       this.mAdapter.restoreState(ss.adapterState, ss.loader);
/*  780 */       setCurrentItemInternal(ss.position, false, true);
/*      */     } else {
/*  782 */       this.mRestoredCurItem = ss.position;
/*  783 */       this.mRestoredAdapterState = ss.adapterState;
/*  784 */       this.mRestoredClassLoader = ss.loader;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addView(View child, int index, ViewGroup.LayoutParams params)
/*      */   {
/*  790 */     if (this.mInLayout) {
/*  791 */       addViewInLayout(child, index, params);
/*  792 */       child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
/*      */     } else {
/*  794 */       super.addView(child, index, params);
/*      */     }
/*      */   }
/*      */ 
/*      */   ItemInfo infoForChild(View child)
/*      */   {
/*  807 */     for (int i = 0; i < this.mItems.size(); i++) {
/*  808 */       ItemInfo ii = (ItemInfo)this.mItems.get(i);
/*  809 */       if (this.mAdapter.isViewFromObject(child, ii.object)) {
/*  810 */         return ii;
/*      */       }
/*      */     }
/*  813 */     return null;
/*      */   }
/*      */ 
/*      */   ItemInfo infoForAnyChild(View child)
/*      */   {
/*      */     ViewParent parent;
/*  818 */     while ((parent = child.getParent()) != this) {
/*  819 */       if ((parent == null) || (!(parent instanceof View))) {
/*  820 */         return null;
/*      */       }
/*  822 */       child = (View)parent;
/*      */     }
/*  824 */     return infoForChild(child);
/*      */   }
/*      */ 
/*      */   protected void onAttachedToWindow()
/*      */   {
/*  829 */     super.onAttachedToWindow();
/*  830 */     this.mFirstLayout = true;
/*      */   }
/*      */ 
/*      */   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
/*      */   {
/*  840 */     setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
/*      */ 
/*  844 */     this.mChildWidthMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft() - getPaddingRight(), 1073741824);
/*      */ 
/*  846 */     this.mChildHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(getMeasuredHeight() - getPaddingTop() - getPaddingBottom(), 1073741824);
/*      */ 
/*  850 */     this.mInLayout = true;
/*  851 */     populate();
/*  852 */     this.mInLayout = false;
/*      */ 
/*  855 */     int size = getChildCount();
/*  856 */     for (int i = 0; i < size; i++) {
/*  857 */       View child = getChildAt(i);
/*  858 */       if (child.getVisibility() == 8) {
/*      */         continue;
/*      */       }
/*  861 */       child.measure(this.mChildWidthMeasureSpec, this.mChildHeightMeasureSpec);
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void onSizeChanged(int w, int h, int oldw, int oldh)
/*      */   {
/*  868 */     super.onSizeChanged(w, h, oldw, oldh);
/*      */ 
/*  871 */     if (w != oldw)
/*  872 */       recomputeScrollPosition(w, oldw, this.mPageMargin, this.mPageMargin);
/*      */   }
/*      */ 
/*      */   private void recomputeScrollPosition(int width, int oldWidth, int margin, int oldMargin)
/*      */   {
/*  877 */     int widthWithMargin = width + margin;
/*  878 */     if (oldWidth > 0) {
/*  879 */       int oldScrollPos = getScrollX();
/*  880 */       int oldwwm = oldWidth + oldMargin;
/*  881 */       int oldScrollItem = oldScrollPos / oldwwm;
/*  882 */       float scrollOffset = oldScrollPos % oldwwm / oldwwm;
/*  883 */       int scrollPos = (int)((oldScrollItem + scrollOffset) * widthWithMargin);
/*  884 */       scrollTo(scrollPos, getScrollY());
/*  885 */       if (!this.mScroller.isFinished())
/*      */       {
/*  887 */         int newDuration = this.mScroller.getDuration() - this.mScroller.timePassed();
/*  888 */         this.mScroller.startScroll(scrollPos, 0, this.mCurItem * widthWithMargin, 0, newDuration);
/*      */       }
/*      */     } else {
/*  891 */       int scrollPos = this.mCurItem * widthWithMargin;
/*  892 */       if (scrollPos != getScrollX()) {
/*  893 */         completeScroll();
/*  894 */         scrollTo(scrollPos, getScrollY());
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void onLayout(boolean changed, int l, int t, int r, int b)
/*      */   {
/*  901 */     this.mInLayout = true;
/*  902 */     populate();
/*  903 */     this.mInLayout = false;
/*      */ 
/*  905 */     int count = getChildCount();
/*  906 */     int width = r - l;
/*      */ 
/*  908 */     for (int i = 0; i < count; i++) {
/*  909 */       View child = getChildAt(i);
/*      */       ItemInfo ii;
/*  911 */       if ((child.getVisibility() != 8) && ((ii = infoForChild(child)) != null)) {
/*  912 */         int loff = (width + this.mPageMargin) * ii.position;
/*  913 */         int childLeft = getPaddingLeft() + loff;
/*  914 */         int childTop = getPaddingTop();
/*      */ 
/*  918 */         child.layout(childLeft, childTop, childLeft + child.getMeasuredWidth(), childTop + child.getMeasuredHeight());
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/*  923 */     this.mFirstLayout = false;
/*      */   }
/*      */ 
/*      */   public void computeScroll()
/*      */   {
/*  929 */     if ((!this.mScroller.isFinished()) && 
/*  930 */       (this.mScroller.computeScrollOffset()))
/*      */     {
/*  932 */       int oldX = getScrollX();
/*  933 */       int oldY = getScrollY();
/*  934 */       int x = this.mScroller.getCurrX();
/*  935 */       int y = this.mScroller.getCurrY();
/*      */ 
/*  937 */       if ((oldX != x) || (oldY != y)) {
/*  938 */         scrollTo(x, y);
/*      */       }
/*      */ 
/*  941 */       if (this.mOnPageChangeListener != null) {
/*  942 */         int widthWithMargin = getWidth() + this.mPageMargin;
/*  943 */         int position = x / widthWithMargin;
/*  944 */         int offsetPixels = x % widthWithMargin;
/*  945 */         float offset = offsetPixels / widthWithMargin;
/*  946 */         this.mOnPageChangeListener.onPageScrolled(position, offset, offsetPixels);
/*      */       }
/*      */ 
/*  950 */       invalidate();
/*  951 */       return;
/*      */     }
/*      */ 
/*  956 */     completeScroll();
/*      */   }
/*      */ 
/*      */   private void completeScroll() {
/*  960 */     boolean needPopulate = this.mScrolling;
/*  961 */     if (needPopulate)
/*      */     {
/*  963 */       setScrollingCacheEnabled(false);
/*  964 */       this.mScroller.abortAnimation();
/*  965 */       int oldX = getScrollX();
/*  966 */       int oldY = getScrollY();
/*  967 */       int x = this.mScroller.getCurrX();
/*  968 */       int y = this.mScroller.getCurrY();
/*  969 */       if ((oldX != x) || (oldY != y)) {
/*  970 */         scrollTo(x, y);
/*      */       }
/*  972 */       setScrollState(0);
/*      */     }
/*  974 */     this.mPopulatePending = false;
/*  975 */     this.mScrolling = false;
/*  976 */     for (int i = 0; i < this.mItems.size(); i++) {
/*  977 */       ItemInfo ii = (ItemInfo)this.mItems.get(i);
/*  978 */       if (ii.scrolling) {
/*  979 */         needPopulate = true;
/*  980 */         ii.scrolling = false;
/*      */       }
/*      */     }
/*  983 */     if (needPopulate)
/*  984 */       populate();
/*      */   }
/*      */ 
/*      */   public boolean onInterceptTouchEvent(MotionEvent ev)
/*      */   {
/*  996 */     int action = ev.getAction() & 0xFF;
/*      */ 
/*  999 */     if ((action == 3) || (action == 1))
/*      */     {
/* 1002 */       this.mIsBeingDragged = false;
/* 1003 */       this.mIsUnableToDrag = false;
/* 1004 */       this.mActivePointerId = -1;
/* 1005 */       return false;
/*      */     }
/*      */ 
/* 1010 */     if (action != 0) {
/* 1011 */       if (this.mIsBeingDragged)
/*      */       {
/* 1013 */         return true;
/*      */       }
/* 1015 */       if (this.mIsUnableToDrag)
/*      */       {
/* 1017 */         return false;
/*      */       }
/*      */     }
/*      */ 
/* 1021 */     switch (action)
/*      */     {
/*      */     case 2:
/* 1032 */       int activePointerId = this.mActivePointerId;
/* 1033 */       if (activePointerId == -1)
/*      */       {
/*      */         break;
/*      */       }
/*      */ 
/* 1038 */       int pointerIndex = MotionEventCompat.findPointerIndex(ev, activePointerId);
/* 1039 */       float x = MotionEventCompat.getX(ev, pointerIndex);
/* 1040 */       float dx = x - this.mLastMotionX;
/* 1041 */       float xDiff = Math.abs(dx);
/* 1042 */       float y = MotionEventCompat.getY(ev, pointerIndex);
/* 1043 */       float yDiff = Math.abs(y - this.mLastMotionY);
/* 1044 */       int scrollX = getScrollX();
/* 1045 */       boolean atEdge = ((dx > 0.0F) && (scrollX == 0)) || ((dx < 0.0F) && (this.mAdapter != null) && (scrollX >= (this.mAdapter.getCount() - 1) * getWidth() - 1));
/*      */ 
/* 1049 */       if (canScroll(this, false, (int)dx, (int)x, (int)y))
/*      */       {
/* 1051 */         this.mInitialMotionX = (this.mLastMotionX = x);
/* 1052 */         this.mLastMotionY = y;
/* 1053 */         return false;
/*      */       }
/* 1055 */       if ((xDiff > this.mTouchSlop) && (xDiff > yDiff))
/*      */       {
/* 1057 */         this.mIsBeingDragged = true;
/* 1058 */         setScrollState(1);
/* 1059 */         this.mLastMotionX = x;
/* 1060 */         setScrollingCacheEnabled(true);
/*      */       } else {
/* 1062 */         if (yDiff <= this.mTouchSlop)
/*      */         {
/*      */           break;
/*      */         }
/*      */ 
/* 1068 */         this.mIsUnableToDrag = true; } break;
/*      */     case 0:
/* 1079 */       this.mLastMotionX = (this.mInitialMotionX = ev.getX());
/* 1080 */       this.mLastMotionY = ev.getY();
/* 1081 */       this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
/*      */ 
/* 1083 */       if (this.mScrollState == 2)
/*      */       {
/* 1085 */         this.mIsBeingDragged = true;
/* 1086 */         this.mIsUnableToDrag = false;
/* 1087 */         setScrollState(1);
/*      */       } else {
/* 1089 */         completeScroll();
/* 1090 */         this.mIsBeingDragged = false;
/* 1091 */         this.mIsUnableToDrag = false;
/*      */       }
/*      */ 
/* 1097 */       break;
/*      */     case 6:
/* 1101 */       onSecondaryPointerUp(ev);
/*      */     }
/*      */ 
/* 1109 */     return this.mIsBeingDragged;
/*      */   }
/*      */ 
/*      */   public boolean onTouchEvent(MotionEvent ev)
/*      */   {
/* 1114 */     if (this.mFakeDragging)
/*      */     {
/* 1118 */       return true;
/*      */     }
/*      */ 
/* 1121 */     if ((ev.getAction() == 0) && (ev.getEdgeFlags() != 0))
/*      */     {
/* 1124 */       return false;
/*      */     }
/*      */ 
/* 1127 */     if ((this.mAdapter == null) || (this.mAdapter.getCount() == 0))
/*      */     {
/* 1129 */       return false;
/*      */     }
/*      */ 
/* 1132 */     if (this.mVelocityTracker == null) {
/* 1133 */       this.mVelocityTracker = VelocityTracker.obtain();
/*      */     }
/* 1135 */     this.mVelocityTracker.addMovement(ev);
/*      */ 
/* 1137 */     int action = ev.getAction();
/* 1138 */     boolean needsInvalidate = false;
/*      */ 
/* 1140 */     switch (action & 0xFF)
/*      */     {
/*      */     case 0:
/* 1146 */       completeScroll();
/*      */ 
/* 1149 */       this.mLastMotionX = (this.mInitialMotionX = ev.getX());
/* 1150 */       this.mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
/* 1151 */       break;
/*      */     case 2:
/* 1154 */       if (!this.mIsBeingDragged) {
/* 1155 */         int pointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
/* 1156 */         float x = MotionEventCompat.getX(ev, pointerIndex);
/* 1157 */         float xDiff = Math.abs(x - this.mLastMotionX);
/* 1158 */         float y = MotionEventCompat.getY(ev, pointerIndex);
/* 1159 */         float yDiff = Math.abs(y - this.mLastMotionY);
/*      */ 
/* 1161 */         if ((xDiff > this.mTouchSlop) && (xDiff > yDiff))
/*      */         {
/* 1163 */           this.mIsBeingDragged = true;
/* 1164 */           this.mLastMotionX = x;
/* 1165 */           setScrollState(1);
/* 1166 */           setScrollingCacheEnabled(true);
/*      */         }
/*      */       }
/* 1169 */       if (!this.mIsBeingDragged)
/*      */         break;
/* 1171 */       int activePointerIndex = MotionEventCompat.findPointerIndex(ev, this.mActivePointerId);
/*      */ 
/* 1173 */       float x = MotionEventCompat.getX(ev, activePointerIndex);
/* 1174 */       float deltaX = this.mLastMotionX - x;
/* 1175 */       this.mLastMotionX = x;
/* 1176 */       float oldScrollX = getScrollX();
/* 1177 */       float scrollX = oldScrollX + deltaX;
/* 1178 */       int width = getWidth();
/* 1179 */       int widthWithMargin = width + this.mPageMargin;
/*      */ 
/* 1181 */       int lastItemIndex = this.mAdapter.getCount() - 1;
/* 1182 */       float leftBound = Math.max(0, (this.mCurItem - 1) * widthWithMargin);
/* 1183 */       float rightBound = Math.min(this.mCurItem + 1, lastItemIndex) * widthWithMargin;
/*      */ 
/* 1185 */       if (scrollX < leftBound) {
/* 1186 */         if (leftBound == 0.0F) {
/* 1187 */           float over = -scrollX;
/* 1188 */           needsInvalidate = this.mLeftEdge.onPull(over / width);
/*      */         }
/* 1190 */         scrollX = leftBound;
/* 1191 */       } else if (scrollX > rightBound) {
/* 1192 */         if (rightBound == lastItemIndex * widthWithMargin) {
/* 1193 */           float over = scrollX - rightBound;
/* 1194 */           needsInvalidate = this.mRightEdge.onPull(over / width);
/*      */         }
/* 1196 */         scrollX = rightBound;
/*      */       }
/*      */ 
/* 1199 */       this.mLastMotionX += scrollX - (int)scrollX;
/* 1200 */       scrollTo((int)scrollX, getScrollY());
/* 1201 */       if (this.mOnPageChangeListener != null) {
/* 1202 */         int position = (int)scrollX / widthWithMargin;
/* 1203 */         int positionOffsetPixels = (int)scrollX % widthWithMargin;
/* 1204 */         float positionOffset = positionOffsetPixels / widthWithMargin;
/* 1205 */         this.mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
/*      */       }
/*      */ 
/* 1208 */       break;
/*      */     case 1:
/* 1211 */       if (!this.mIsBeingDragged) break;
/* 1212 */       VelocityTracker velocityTracker = this.mVelocityTracker;
/* 1213 */       velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
/* 1214 */       int initialVelocity = (int)VelocityTrackerCompat.getXVelocity(velocityTracker, this.mActivePointerId);
/*      */ 
/* 1216 */       this.mPopulatePending = true;
/* 1217 */       widthWithMargin = getWidth() + this.mPageMargin;
/* 1218 */       scrollX = getScrollX();
/* 1219 */       int currentPage = (int) (scrollX / widthWithMargin);
/* 1220 */       int nextPage = initialVelocity > 0 ? currentPage : currentPage + 1;
/* 1221 */       setCurrentItemInternal(nextPage, true, true, initialVelocity);
/*      */ 
/* 1223 */       this.mActivePointerId = -1;
/* 1224 */       endDrag();
/* 1225 */       needsInvalidate = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease();
/* 1226 */       break;
/*      */     case 3:
/* 1229 */       if (!this.mIsBeingDragged) break;
/* 1230 */       setCurrentItemInternal(this.mCurItem, true, true);
/* 1231 */       this.mActivePointerId = -1;
/* 1232 */       endDrag();
/* 1233 */       needsInvalidate = this.mLeftEdge.onRelease() | this.mRightEdge.onRelease(); break;
/*      */     case 5:
/* 1237 */       int index = MotionEventCompat.getActionIndex(ev);
/* 1238 */       x = MotionEventCompat.getX(ev, index);
/* 1239 */       this.mLastMotionX = x;
/* 1240 */       this.mActivePointerId = MotionEventCompat.getPointerId(ev, index);
/* 1241 */       break;
/*      */     case 6:
/* 1244 */       onSecondaryPointerUp(ev);
/* 1245 */       this.mLastMotionX = MotionEventCompat.getX(ev, MotionEventCompat.findPointerIndex(ev, this.mActivePointerId));
/*      */     case 4:
/*      */     }
/*      */ 
/* 1249 */     if (needsInvalidate) {
/* 1250 */       invalidate();
/*      */     }
/* 1252 */     return true;
/*      */   }
/*      */ 
/*      */   public void draw(Canvas canvas)
/*      */   {
/* 1257 */     super.draw(canvas);
/* 1258 */     boolean needsInvalidate = false;
/*      */ 
/* 1260 */     int overScrollMode = ViewCompat.getOverScrollMode(this);
/* 1261 */     if ((overScrollMode == 0) || ((overScrollMode == 1) && (this.mAdapter != null) && (this.mAdapter.getCount() > 1)))
/*      */     {
/* 1264 */       if (!this.mLeftEdge.isFinished()) {
/* 1265 */         int restoreCount = canvas.save();
/* 1266 */         int height = getHeight() - getPaddingTop() - getPaddingBottom();
/*      */ 
/* 1268 */         canvas.rotate(270.0F);
/* 1269 */         canvas.translate(-height + getPaddingTop(), 0.0F);
/* 1270 */         this.mLeftEdge.setSize(height, getWidth());
/* 1271 */         needsInvalidate |= this.mLeftEdge.draw(canvas);
/* 1272 */         canvas.restoreToCount(restoreCount);
/*      */       }
/* 1274 */       if (!this.mRightEdge.isFinished()) {
/* 1275 */         int restoreCount = canvas.save();
/* 1276 */         int width = getWidth();
/* 1277 */         int height = getHeight() - getPaddingTop() - getPaddingBottom();
/* 1278 */         int itemCount = this.mAdapter != null ? this.mAdapter.getCount() : 1;
/*      */ 
/* 1280 */         canvas.rotate(90.0F);
/* 1281 */         canvas.translate(-getPaddingTop(), -itemCount * (width + this.mPageMargin) + this.mPageMargin);
/*      */ 
/* 1283 */         this.mRightEdge.setSize(height, width);
/* 1284 */         needsInvalidate |= this.mRightEdge.draw(canvas);
/* 1285 */         canvas.restoreToCount(restoreCount);
/*      */       }
/*      */     } else {
/* 1288 */       this.mLeftEdge.finish();
/* 1289 */       this.mRightEdge.finish();
/*      */     }
/*      */ 
/* 1292 */     if (needsInvalidate)
/*      */     {
/* 1294 */       invalidate();
/*      */     }
/*      */   }
/*      */ 
/*      */   protected void onDraw(Canvas canvas)
/*      */   {
/* 1300 */     super.onDraw(canvas);
/*      */ 
/* 1303 */     if ((this.mPageMargin > 0) && (this.mMarginDrawable != null)) {
/* 1304 */       int scrollX = getScrollX();
/* 1305 */       int width = getWidth();
/* 1306 */       int offset = scrollX % (width + this.mPageMargin);
/* 1307 */       if (offset != 0)
/*      */       {
/* 1309 */         int left = scrollX - offset + width;
/* 1310 */         this.mMarginDrawable.setBounds(left, 0, left + this.mPageMargin, getHeight());
/* 1311 */         this.mMarginDrawable.draw(canvas);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean beginFakeDrag()
/*      */   {
/* 1334 */     if (this.mIsBeingDragged) {
/* 1335 */       return false;
/*      */     }
/* 1337 */     this.mFakeDragging = true;
/* 1338 */     setScrollState(1);
/* 1339 */     this.mInitialMotionX = (this.mLastMotionX = 0.0F);
/* 1340 */     if (this.mVelocityTracker == null)
/* 1341 */       this.mVelocityTracker = VelocityTracker.obtain();
/*      */     else {
/* 1343 */       this.mVelocityTracker.clear();
/*      */     }
/* 1345 */     long time = SystemClock.uptimeMillis();
/* 1346 */     MotionEvent ev = MotionEvent.obtain(time, time, 0, 0.0F, 0.0F, 0);
/* 1347 */     this.mVelocityTracker.addMovement(ev);
/* 1348 */     ev.recycle();
/* 1349 */     this.mFakeDragBeginTime = time;
/* 1350 */     return true;
/*      */   }
/*      */ 
/*      */   public void endFakeDrag()
/*      */   {
/* 1360 */     if (!this.mFakeDragging) {
/* 1361 */       throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
/*      */     }
/*      */ 
/* 1364 */     VelocityTracker velocityTracker = this.mVelocityTracker;
/* 1365 */     velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
/* 1366 */     int initialVelocity = (int)VelocityTrackerCompat.getYVelocity(velocityTracker, this.mActivePointerId);
/*      */ 
/* 1368 */     this.mPopulatePending = true;
/* 1369 */     if ((Math.abs(initialVelocity) > this.mMinimumVelocity) || (Math.abs(this.mInitialMotionX - this.mLastMotionX) >= getWidth() / 3))
/*      */     {
/* 1371 */       if (this.mLastMotionX > this.mInitialMotionX)
/* 1372 */         setCurrentItemInternal(this.mCurItem - 1, true, true);
/*      */       else
/* 1374 */         setCurrentItemInternal(this.mCurItem + 1, true, true);
/*      */     }
/*      */     else {
/* 1377 */       setCurrentItemInternal(this.mCurItem, true, true);
/*      */     }
/* 1379 */     endDrag();
/*      */ 
/* 1381 */     this.mFakeDragging = false;
/*      */   }
/*      */ 
/*      */   public void fakeDragBy(float xOffset)
/*      */   {
/* 1392 */     if (!this.mFakeDragging) {
/* 1393 */       throw new IllegalStateException("No fake drag in progress. Call beginFakeDrag first.");
/*      */     }
/*      */ 
/* 1396 */     this.mLastMotionX += xOffset;
/* 1397 */     float scrollX = getScrollX() - xOffset;
/* 1398 */     int width = getWidth();
/* 1399 */     int widthWithMargin = width + this.mPageMargin;
/*      */ 
/* 1401 */     float leftBound = Math.max(0, (this.mCurItem - 1) * widthWithMargin);
/* 1402 */     float rightBound = Math.min(this.mCurItem + 1, this.mAdapter.getCount() - 1) * widthWithMargin;
/*      */ 
/* 1404 */     if (scrollX < leftBound)
/* 1405 */       scrollX = leftBound;
/* 1406 */     else if (scrollX > rightBound) {
/* 1407 */       scrollX = rightBound;
/*      */     }
/*      */ 
/* 1410 */     this.mLastMotionX += scrollX - (int)scrollX;
/* 1411 */     scrollTo((int)scrollX, getScrollY());
/* 1412 */     if (this.mOnPageChangeListener != null) {
/* 1413 */       int position = (int)scrollX / widthWithMargin;
/* 1414 */       int positionOffsetPixels = (int)scrollX % widthWithMargin;
/* 1415 */       float positionOffset = positionOffsetPixels / widthWithMargin;
/* 1416 */       this.mOnPageChangeListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
/*      */     }
/*      */ 
/* 1421 */     long time = SystemClock.uptimeMillis();
/* 1422 */     MotionEvent ev = MotionEvent.obtain(this.mFakeDragBeginTime, time, 2, this.mLastMotionX, 0.0F, 0);
/*      */ 
/* 1424 */     this.mVelocityTracker.addMovement(ev);
/* 1425 */     ev.recycle();
/*      */   }
/*      */ 
/*      */   public boolean isFakeDragging()
/*      */   {
/* 1438 */     return this.mFakeDragging;
/*      */   }
/*      */ 
/*      */   private void onSecondaryPointerUp(MotionEvent ev) {
/* 1442 */     int pointerIndex = MotionEventCompat.getActionIndex(ev);
/* 1443 */     int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
/* 1444 */     if (pointerId == this.mActivePointerId)
/*      */     {
/* 1447 */       int newPointerIndex = pointerIndex == 0 ? 1 : 0;
/* 1448 */       this.mLastMotionX = MotionEventCompat.getX(ev, newPointerIndex);
/* 1449 */       this.mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
/* 1450 */       if (this.mVelocityTracker != null)
/* 1451 */         this.mVelocityTracker.clear();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void endDrag()
/*      */   {
/* 1457 */     this.mIsBeingDragged = false;
/* 1458 */     this.mIsUnableToDrag = false;
/*      */ 
/* 1460 */     if (this.mVelocityTracker != null) {
/* 1461 */       this.mVelocityTracker.recycle();
/* 1462 */       this.mVelocityTracker = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void setScrollingCacheEnabled(boolean enabled) {
/* 1467 */     if (this.mScrollingCacheEnabled != enabled)
/* 1468 */       this.mScrollingCacheEnabled = enabled;
/*      */   }
/*      */ 
/*      */   protected boolean canScroll(View v, boolean checkV, int dx, int x, int y)
/*      */   {
/* 1493 */     if ((v instanceof ViewGroup)) {
/* 1494 */       ViewGroup group = (ViewGroup)v;
/* 1495 */       int scrollX = v.getScrollX();
/* 1496 */       int scrollY = v.getScrollY();
/* 1497 */       int count = group.getChildCount();
/*      */ 
/* 1499 */       for (int i = count - 1; i >= 0; i--)
/*      */       {
/* 1502 */         View child = group.getChildAt(i);
/* 1503 */         if ((x + scrollX >= child.getLeft()) && (x + scrollX < child.getRight()) && (y + scrollY >= child.getTop()) && (y + scrollY < child.getBottom()) && (canScroll(child, true, dx, x + scrollX - child.getLeft(), y + scrollY - child.getTop())))
/*      */         {
/* 1507 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1512 */     return (checkV) && (ViewCompat.canScrollHorizontally(v, -dx));
/*      */   }
/*      */ 
/*      */   public boolean dispatchKeyEvent(KeyEvent event)
/*      */   {
/* 1518 */     return (super.dispatchKeyEvent(event)) || (executeKeyEvent(event));
/*      */   }
/*      */ 
/*      */   public boolean executeKeyEvent(KeyEvent event)
/*      */   {
/* 1530 */     boolean handled = false;
/* 1531 */     if (event.getAction() == 0) {
/* 1532 */       switch (event.getKeyCode()) {
/*      */       case 21:
/* 1534 */         handled = arrowScroll(17);
/* 1535 */         break;
/*      */       case 22:
/* 1537 */         handled = arrowScroll(66);
/* 1538 */         break;
/*      */       case 61:
/* 1540 */         if (KeyEventCompat.hasNoModifiers(event)) {
/* 1541 */           handled = arrowScroll(2); } else {
/* 1542 */           if (!KeyEventCompat.hasModifiers(event, 1)) break;
/* 1543 */           handled = arrowScroll(1);
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1548 */     return handled;
/*      */   }
/*      */ 
/*      */   public boolean arrowScroll(int direction) {
/* 1552 */     View currentFocused = findFocus();
/* 1553 */     if (currentFocused == this) currentFocused = null;
/*      */ 
/* 1555 */     boolean handled = false;
/*      */ 
/* 1557 */     View nextFocused = FocusFinder.getInstance().findNextFocus(this, currentFocused, direction);
/*      */ 
/* 1559 */     if ((nextFocused != null) && (nextFocused != currentFocused)) {
/* 1560 */       if (direction == 17)
/*      */       {
/* 1563 */         if ((currentFocused != null) && (nextFocused.getLeft() >= currentFocused.getLeft()))
/* 1564 */           handled = pageLeft();
/*      */         else
/* 1566 */           handled = nextFocused.requestFocus();
/*      */       }
/* 1568 */       else if (direction == 66)
/*      */       {
/* 1571 */         if ((currentFocused != null) && (nextFocused.getLeft() <= currentFocused.getLeft()))
/* 1572 */           handled = pageRight();
/*      */         else
/* 1574 */           handled = nextFocused.requestFocus();
/*      */       }
/*      */     }
/* 1577 */     else if ((direction == 17) || (direction == 1))
/*      */     {
/* 1579 */       handled = pageLeft();
/* 1580 */     } else if ((direction == 66) || (direction == 2))
/*      */     {
/* 1582 */       handled = pageRight();
/*      */     }
/* 1584 */     if (handled) {
/* 1585 */       playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
/*      */     }
/* 1587 */     return handled;
/*      */   }
/*      */ 
/*      */   boolean pageLeft() {
/* 1591 */     if (this.mCurItem > 0) {
/* 1592 */       setCurrentItem(this.mCurItem - 1, true);
/* 1593 */       return true;
/*      */     }
/* 1595 */     return false;
/*      */   }
/*      */ 
/*      */   boolean pageRight() {
/* 1599 */     if ((this.mAdapter != null) && (this.mCurItem < this.mAdapter.getCount() - 1)) {
/* 1600 */       setCurrentItem(this.mCurItem + 1, true);
/* 1601 */       return true;
/*      */     }
/* 1603 */     return false;
/*      */   }
/*      */ 
/*      */   public void addFocusables(ArrayList<View> views, int direction, int focusableMode)
/*      */   {
/* 1611 */     int focusableCount = views.size();
/*      */ 
/* 1613 */     int descendantFocusability = getDescendantFocusability();
/*      */ 
/* 1615 */     if (descendantFocusability != 393216) {
/* 1616 */       for (int i = 0; i < getChildCount(); i++) {
/* 1617 */         View child = getChildAt(i);
/* 1618 */         if (child.getVisibility() == 0) {
/* 1619 */           ItemInfo ii = infoForChild(child);
/* 1620 */           if ((ii != null) && (ii.position == this.mCurItem)) {
/* 1621 */             child.addFocusables(views, direction, focusableMode);
/*      */           }
/*      */ 
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 1631 */     if ((descendantFocusability != 262144) || (focusableCount == views.size()))
/*      */     {
/* 1637 */       if (!isFocusable()) {
/* 1638 */         return;
/*      */       }
/* 1640 */       if (((focusableMode & 0x1) == 1) && (isInTouchMode()) && (!isFocusableInTouchMode()))
/*      */       {
/* 1642 */         return;
/*      */       }
/* 1644 */       if (views != null)
/* 1645 */         views.add(this);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addTouchables(ArrayList<View> views)
/*      */   {
/* 1658 */     for (int i = 0; i < getChildCount(); i++) {
/* 1659 */       View child = getChildAt(i);
/* 1660 */       if (child.getVisibility() == 0) {
/* 1661 */         ItemInfo ii = infoForChild(child);
/* 1662 */         if ((ii != null) && (ii.position == this.mCurItem))
/* 1663 */           child.addTouchables(views);
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect)
/*      */   {
/* 1678 */     int count = getChildCount();
/*      */     int end;
/*      */     int index = 0;
/*      */     int increment = 0;
/*      */     
/* 1679 */     if ((direction & 0x2) != 0) {
/* 1680 */       
/* 1681 */       index = 0;
/* 1681 */       increment = 1;
/* 1682 */       end = count;
/*      */     } else {
/* 1684 */       index = count - 1;
/* 1685 */       increment = -1;
/* 1686 */       end = -1;
/*      */     }
/* 1688 */     for (int i = index; i != end; i += increment) {
/* 1689 */       View child = getChildAt(i);
/* 1690 */       if (child.getVisibility() == 0) {
/* 1691 */         ItemInfo ii = infoForChild(child);
/* 1692 */         if ((ii != null) && (ii.position == this.mCurItem) && 
/* 1693 */           (child.requestFocus(direction, previouslyFocusedRect))) {
/* 1694 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1699 */     return false;
/*      */   }
/*      */ 
/*      */   public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event)
/*      */   {
/* 1709 */     int childCount = getChildCount();
/* 1710 */     for (int i = 0; i < childCount; i++) {
/* 1711 */       View child = getChildAt(i);
/* 1712 */       if (child.getVisibility() == 0) {
/* 1713 */         ItemInfo ii = infoForChild(child);
/* 1714 */         if ((ii != null) && (ii.position == this.mCurItem) && (child.dispatchPopulateAccessibilityEvent(event)))
/*      */         {
/* 1716 */           return true;
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/* 1721 */     return false;
/*      */   }
/*      */   private class DataSetObserver implements PagerAdapter.DataSetObserver {
/*      */     private DataSetObserver() {
/*      */     }
/*      */     public void onDataSetChanged() {
/* 1727 */       PagerView.this.dataSetChanged();
/*      */     }
/*      */   }
/*      */ 
/*      */   public static class SavedState extends View.BaseSavedState
/*      */   {

	public SavedState(Parcel arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public SavedState(Parcelable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
		// TODO Auto-generated constructor stub
	
/*      */     public int position;
/*      */     public Parcelable adapterState;
/*      */     public ClassLoader loader;
/*  734 */     public static final Parcelable.Creator<SavedState> CREATOR = ParcelableCompat.newCreator(new ParcelableCompatCreatorCallbacks<SavedState>()
/*      */     {
/*      */       public PagerView.SavedState createFromParcel(Parcel in, ClassLoader loader)
/*      */       {
/*  738 */         return new PagerView.SavedState(in);
/*      */       }
/*      */ 
/*      */       public PagerView.SavedState[] newArray(int size) {
/*  742 */         return new PagerView.SavedState[size];
/*      */       }
/*      */     });
/*      */ 
/*      */ 
/*      */     public void writeToParcel(Parcel out, int flags)
/*      */     {
/*  722 */       super.writeToParcel(out, flags);
/*  723 */       out.writeInt(this.position);
/*  724 */       out.writeParcelable(this.adapterState, flags);
/*      */     }
/*      */ 
/*      */     public String toString()
/*      */     {
/*  729 */       return "FragmentPager.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " position=" + this.position + "}";
/*      */     }
/*      */ 
/*      */     SavedState(Parcel in, ClassLoader loader)
/*      */     {
/*  747 */       super(in);
/*  748 */       if (loader == null) {
/*  749 */         loader = getClass().getClassLoader();
/*      */       }
/*  751 */       this.position = in.readInt();
/*  752 */       this.adapterState = in.readParcelable(loader);
/*  753 */       this.loader = loader;
/*      */     }
/*      */   }
/*      */ 
/*      */   public class SimpleOnPageChangeListener
/*      */     implements PagerView.OnPageChangeListener
/*      */   {
/*      */     public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void onPageSelected(int position)
/*      */     {
/*      */     }
/*      */ 
/*      */     public void onPageScrollStateChanged(int state)
/*      */     {
/*      */     }
/*      */   }
/*      */ 
/*      */   public static abstract interface OnPageChangeListener
/*      */   {
/*      */     public abstract void onPageScrolled(int paramInt1, float paramFloat, int paramInt2);
/*      */ 
/*      */     public abstract void onPageSelected(int paramInt);
/*      */ 
/*      */     public abstract void onPageScrollStateChanged(int paramInt);
/*      */   }
/*      */ 
/*      */   static class ItemInfo
/*      */   {
/*      */     Object object;
/*      */     int position;
/*      */     boolean scrolling;
/*      */   }
/*      */ }
