/*     */ package com.jibo.v4.view;
/*     */ 
/*     */ import com.jibo.v4.accessibility.AccessibilityNodeInfoCompat;

import android.os.Build;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
/*     */ 
/*     */ public class ViewCompat
/*     */ {
/*     */   public static final int OVER_SCROLL_ALWAYS = 0;
/*     */   public static final int OVER_SCROLL_IF_CONTENT_SCROLLS = 1;
/*     */   public static final int OVER_SCROLL_NEVER = 2;
/*     */   static final ViewCompatImpl IMPL;
/*     */ 
/*     */   public static boolean canScrollHorizontally(View v, int direction)
/*     */   {
/* 106 */     return IMPL.canScrollHorizontally(v, direction);
/*     */   }
/*     */ 
/*     */   public static boolean canScrollVertically(View v, int direction) {
/* 110 */     return IMPL.canScrollVertically(v, direction);
/*     */   }
/*     */ 
/*     */   public static int getOverScrollMode(View v) {
/* 114 */     return IMPL.getOverScrollMode(v);
/*     */   }
/*     */ 
/*     */   public static void setOverScrollMode(View v, int mode) {
/* 118 */     IMPL.setOverScrollMode(v, mode);
/*     */   }
/*     */ 
/*     */   public static void onPopulateAccessibilityEvent(View v, AccessibilityEvent event) {
/* 122 */     IMPL.onPopulateAccessibilityEvent(v, event);
/*     */   }
/*     */ 
/*     */   public static void onInitializeAccessibilityEvent(View v, AccessibilityEvent event) {
/* 126 */     IMPL.onInitializeAccessibilityEvent(v, event);
/*     */   }
/*     */ 
/*     */   public static void onInitializeAccessibilityNodeInfo(View v, AccessibilityNodeInfoCompat info) {
/* 130 */     IMPL.onInitializeAccessibilityNodeInfo(v, info);
/*     */   }
/*     */ 
/*     */ 
/*     */   static
/*     */   {
/*  97 */     int version = Build.VERSION.SDK_INT;
/*  98 */     if (version >= 9)
///*  99 */       IMPL = new GBViewCompatImpl();
	/* 101 */       IMPL = new BaseViewCompatImpl();
/*     */     else
/* 101 */       IMPL = new BaseViewCompatImpl();
/*     */   }
/*     */ 
///*     */   static class GBViewCompatImpl extends ViewCompat.BaseViewCompatImpl
///*     */   {
///*     */     public int getOverScrollMode(View v)
///*     */     {
///*  85 */       return ViewCompatGingerbread.getOverScrollMode(v);
///*     */     }
///*     */ 
///*     */     public void setOverScrollMode(View v, int mode) {
///*  89 */       ViewCompatGingerbread.setOverScrollMode(v, mode);
///*     */     }
///*     */   }
/*     */ 
/*     */   static class BaseViewCompatImpl
/*     */     implements ViewCompat.ViewCompatImpl
/*     */   {
/*     */     public boolean canScrollHorizontally(View v, int direction)
/*     */     {
/*  57 */       return false;
/*     */     }
/*     */     public boolean canScrollVertically(View v, int direction) {
/*  60 */       return false;
/*     */     }
/*     */     public int getOverScrollMode(View v) {
/*  63 */       return 2;
/*     */     }
/*     */ 
/*     */     public void setOverScrollMode(View v, int mode)
/*     */     {
/*     */     }
/*     */ 
/*     */ 
/*     */     public void onPopulateAccessibilityEvent(View v, AccessibilityEvent event)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onInitializeAccessibilityEvent(View v, AccessibilityEvent event)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void onInitializeAccessibilityNodeInfo(View v, AccessibilityNodeInfoCompat info)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface ViewCompatImpl
/*     */   {
/*     */     public abstract boolean canScrollHorizontally(View paramView, int paramInt);
/*     */ 
/*     */     public abstract boolean canScrollVertically(View paramView, int paramInt);
/*     */ 
/*     */     public abstract int getOverScrollMode(View paramView);
/*     */ 
/*     */     public abstract void setOverScrollMode(View paramView, int paramInt);
/*     */ 
/*     */     public abstract void onInitializeAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent);
/*     */ 
/*     */     public abstract void onPopulateAccessibilityEvent(View paramView, AccessibilityEvent paramAccessibilityEvent);
/*     */ 
/*     */     public abstract void onInitializeAccessibilityNodeInfo(View paramView, AccessibilityNodeInfoCompat paramAccessibilityNodeInfoCompat);
/*     */ 
///*     */     public abstract void setAccessibilityDelegate(View paramView, AccessibilityDelegateCompat paramAccessibilityDelegateCompat);
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.ViewCompat
 * JD-Core Version:    0.6.0
 */