/*     */ package com.jibo.v4.accessibility;
/*     */ 
/*     */ import java.util.ArrayList;
import java.util.List;

import android.graphics.Rect;
import android.view.View;
/*     */ 
/*     */ public class AccessibilityNodeInfoCompat
/*     */ {
/* 274 */   private static final AccessibilityNodeInfoImpl IMPL = new AccessibilityNodeInfoStubImpl();
/*     */   private final Object mInfo;
/*     */   public static final int ACTION_FOCUS = 1;
/*     */   public static final int ACTION_CLEAR_FOCUS = 2;
/*     */   public static final int ACTION_SELECT = 4;
/*     */   public static final int ACTION_CLEAR_SELECTION = 8;
/*     */ 
/*     */   public AccessibilityNodeInfoCompat(Object info)
/*     */   {
/* 308 */     this.mInfo = info;
/*     */   }
/*     */ 
/*     */   public Object getImpl()
/*     */   {
/* 317 */     return this.mInfo;
/*     */   }
/*     */ 
/*     */   public static AccessibilityNodeInfoCompat obtain(View source)
/*     */   {
/* 328 */     return new AccessibilityNodeInfoCompat(IMPL.obtain(source));
/*     */   }
/*     */ 
/*     */   public static AccessibilityNodeInfoCompat obtain()
/*     */   {
/* 337 */     return new AccessibilityNodeInfoCompat(IMPL.obtain());
/*     */   }
/*     */ 
/*     */   public static AccessibilityNodeInfoCompat obtain(AccessibilityNodeInfoCompat info)
/*     */   {
/* 348 */     return new AccessibilityNodeInfoCompat(IMPL.obtain(info.mInfo));
/*     */   }
/*     */ 
/*     */   public void setSource(View source)
/*     */   {
/* 357 */     IMPL.setSource(this.mInfo, source);
/*     */   }
/*     */ 
/*     */   public int getWindowId()
/*     */   {
/* 366 */     return IMPL.getWindowId(this.mInfo);
/*     */   }
/*     */ 
/*     */   public int getChildCount()
/*     */   {
/* 375 */     return IMPL.getChildCount(this.mInfo);
/*     */   }
/*     */ 
/*     */   public AccessibilityNodeInfoCompat getChild(int index)
/*     */   {
/* 392 */     return new AccessibilityNodeInfoCompat(IMPL.getChild(this.mInfo, index));
/*     */   }
/*     */ 
/*     */   public void addChild(View child)
/*     */   {
/* 407 */     IMPL.addChild(this.mInfo, child);
/*     */   }
/*     */ 
/*     */   public int getActions()
/*     */   {
/* 420 */     return IMPL.getActions(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void addAction(int action)
/*     */   {
/* 435 */     IMPL.addAction(this.mInfo, action);
/*     */   }
/*     */ 
/*     */   public boolean performAction(int action)
/*     */   {
/* 451 */     return IMPL.performAction(this.mInfo, action);
/*     */   }
/*     */ 
/*     */   public List<AccessibilityNodeInfoCompat> findAccessibilityNodeInfosByText(String text)
/*     */   {
/* 468 */     List result = new ArrayList();
/* 469 */     List infos = IMPL.findAccessibilityNodeInfosByText(this.mInfo, text);
/* 470 */     int infoCount = infos.size();
/* 471 */     for (int i = 0; i < infoCount; i++) {
/* 472 */       Object info = infos.get(i);
/* 473 */       result.add(new AccessibilityNodeInfoCompat(info));
/*     */     }
/* 475 */     return result;
/*     */   }
/*     */ 
/*     */   public AccessibilityNodeInfoCompat getParent()
/*     */   {
/* 489 */     return new AccessibilityNodeInfoCompat(IMPL.getParent(this.mInfo));
/*     */   }
/*     */ 
/*     */   public void setParent(View parent)
/*     */   {
/* 504 */     IMPL.setParent(this.mInfo, parent);
/*     */   }
/*     */ 
/*     */   public void getBoundsInParent(Rect outBounds)
/*     */   {
/* 513 */     IMPL.getBoundsInParent(this.mInfo, outBounds);
/*     */   }
/*     */ 
/*     */   public void setBoundsInParent(Rect bounds)
/*     */   {
/* 528 */     IMPL.setBoundsInParent(this.mInfo, bounds);
/*     */   }
/*     */ 
/*     */   public void getBoundsInScreen(Rect outBounds)
/*     */   {
/* 537 */     IMPL.getBoundsInScreen(this.mInfo, outBounds);
/*     */   }
/*     */ 
/*     */   public void setBoundsInScreen(Rect bounds)
/*     */   {
/* 552 */     IMPL.setBoundsInParent(this.mInfo, bounds);
/*     */   }
/*     */ 
/*     */   public boolean isCheckable()
/*     */   {
/* 561 */     return IMPL.isCheckable(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setCheckable(boolean checkable)
/*     */   {
/* 576 */     IMPL.setCheckable(this.mInfo, checkable);
/*     */   }
/*     */ 
/*     */   public boolean isChecked()
/*     */   {
/* 585 */     return IMPL.isChecked(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setChecked(boolean checked)
/*     */   {
/* 600 */     IMPL.setChecked(this.mInfo, checked);
/*     */   }
/*     */ 
/*     */   public boolean isFocusable()
/*     */   {
/* 609 */     return IMPL.isFocusable(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setFocusable(boolean focusable)
/*     */   {
/* 624 */     IMPL.setFocusable(this.mInfo, focusable);
/*     */   }
/*     */ 
/*     */   public boolean isFocused()
/*     */   {
/* 633 */     return IMPL.isFocused(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setFocused(boolean focused)
/*     */   {
/* 648 */     IMPL.setFocused(this.mInfo, focused);
/*     */   }
/*     */ 
/*     */   public boolean isSelected()
/*     */   {
/* 657 */     return IMPL.isSelected(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setSelected(boolean selected)
/*     */   {
/* 672 */     IMPL.setSelected(this.mInfo, selected);
/*     */   }
/*     */ 
/*     */   public boolean isClickable()
/*     */   {
/* 681 */     return IMPL.isClickable(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setClickable(boolean clickable)
/*     */   {
/* 696 */     IMPL.setClickable(this.mInfo, clickable);
/*     */   }
/*     */ 
/*     */   public boolean isLongClickable()
/*     */   {
/* 705 */     return IMPL.isLongClickable(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setLongClickable(boolean longClickable)
/*     */   {
/* 720 */     IMPL.setLongClickable(this.mInfo, longClickable);
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 729 */     return IMPL.isEnabled(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean enabled)
/*     */   {
/* 744 */     IMPL.setEnabled(this.mInfo, enabled);
/*     */   }
/*     */ 
/*     */   public boolean isPassword()
/*     */   {
/* 753 */     return IMPL.isPassword(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setPassword(boolean password)
/*     */   {
/* 768 */     IMPL.setPassword(this.mInfo, password);
/*     */   }
/*     */ 
/*     */   public boolean isScrollable()
/*     */   {
/* 777 */     return IMPL.isScrollable(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setScrollable(boolean scrollable)
/*     */   {
/* 792 */     IMPL.setScrollable(this.mInfo, scrollable);
/*     */   }
/*     */ 
/*     */   public CharSequence getPackageName()
/*     */   {
/* 801 */     return IMPL.getPackageName(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setPackageName(CharSequence packageName)
/*     */   {
/* 816 */     IMPL.setPackageName(this.mInfo, packageName);
/*     */   }
/*     */ 
/*     */   public CharSequence getClassName()
/*     */   {
/* 825 */     return IMPL.getClassName(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setClassName(CharSequence className)
/*     */   {
/* 840 */     IMPL.setClassName(this.mInfo, className);
/*     */   }
/*     */ 
/*     */   public CharSequence getText()
/*     */   {
/* 849 */     return IMPL.getText(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setText(CharSequence text)
/*     */   {
/* 864 */     IMPL.setText(this.mInfo, text);
/*     */   }
/*     */ 
/*     */   public CharSequence getContentDescription()
/*     */   {
/* 873 */     return IMPL.getContentDescription(this.mInfo);
/*     */   }
/*     */ 
/*     */   public void setContentDescription(CharSequence contentDescription)
/*     */   {
/* 888 */     IMPL.setContentDescription(this.mInfo, contentDescription);
/*     */   }
/*     */ 
/*     */   public void recycle()
/*     */   {
/* 899 */     IMPL.recycle(this.mInfo);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 904 */     return this.mInfo == null ? 0 : this.mInfo.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 909 */     if (this == obj) {
/* 910 */       return true;
/*     */     }
/* 912 */     if (obj == null) {
/* 913 */       return false;
/*     */     }
/* 915 */     if (getClass() != obj.getClass()) {
/* 916 */       return false;
/*     */     }
/* 918 */     AccessibilityNodeInfoCompat other = (AccessibilityNodeInfoCompat)obj;
/* 919 */     if (this.mInfo == null) {
/* 920 */       if (other.mInfo != null)
/* 921 */         return false;
/*     */     }
/* 923 */     else if (!this.mInfo.equals(other.mInfo)) {
/* 924 */       return false;
/*     */     }
/* 926 */     return true;
/*     */   }
/*     */ 
/*     */   static class AccessibilityNodeInfoStubImpl
/*     */     implements AccessibilityNodeInfoCompat.AccessibilityNodeInfoImpl
/*     */   {
/*     */     public Object obtain()
/*     */     {
/*  82 */       return null;
/*     */     }
/*     */ 
/*     */     public Object obtain(View source) {
/*  86 */       return null;
/*     */     }
/*     */ 
/*     */     public Object obtain(Object info) {
/*  90 */       return null;
/*     */     }
/*     */ 
/*     */     public void addAction(Object info, int action)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void addChild(Object info, View child)
/*     */     {
/*     */     }
/*     */ 
/*     */     public List<Object> findAccessibilityNodeInfosByText(Object info, String text) {
/* 102 */       return null;
/*     */     }
/*     */ 
/*     */     public int getActions(Object info) {
/* 106 */       return 0;
/*     */     }
/*     */ 
/*     */     public void getBoundsInParent(Object info, Rect outBounds)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void getBoundsInScreen(Object info, Rect outBounds)
/*     */     {
/*     */     }
/*     */ 
/*     */     public Object getChild(Object info, int index) {
/* 118 */       return null;
/*     */     }
/*     */ 
/*     */     public int getChildCount(Object info) {
/* 122 */       return 0;
/*     */     }
/*     */ 
/*     */     public CharSequence getClassName(Object info) {
/* 126 */       return null;
/*     */     }
/*     */ 
/*     */     public CharSequence getContentDescription(Object info) {
/* 130 */       return null;
/*     */     }
/*     */ 
/*     */     public CharSequence getPackageName(Object info) {
/* 134 */       return null;
/*     */     }
/*     */ 
/*     */     public AccessibilityNodeInfoCompat getParent(Object info) {
/* 138 */       return null;
/*     */     }
/*     */ 
/*     */     public CharSequence getText(Object info) {
/* 142 */       return null;
/*     */     }
/*     */ 
/*     */     public int getWindowId(Object info) {
/* 146 */       return 0;
/*     */     }
/*     */ 
/*     */     public boolean isCheckable(Object info) {
/* 150 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isChecked(Object info) {
/* 154 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isClickable(Object info) {
/* 158 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isEnabled(Object info) {
/* 162 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isFocusable(Object info) {
/* 166 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isFocused(Object info) {
/* 170 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isLongClickable(Object info) {
/* 174 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isPassword(Object info) {
/* 178 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isScrollable(Object info) {
/* 182 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isSelected(Object info) {
/* 186 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean performAction(Object info, int action) {
/* 190 */       return false;
/*     */     }
/*     */ 
/*     */     public void setBoundsInParent(Object info, Rect bounds)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setBoundsInScreen(Object info, Rect bounds)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setCheckable(Object info, boolean checkable)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setChecked(Object info, boolean checked)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setClassName(Object info, CharSequence className)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setClickable(Object info, boolean clickable)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setContentDescription(Object info, CharSequence contentDescription)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setEnabled(Object info, boolean enabled)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setFocusable(Object info, boolean focusable)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setFocused(Object info, boolean focused)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setLongClickable(Object info, boolean longClickable)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setPackageName(Object info, CharSequence packageName)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setParent(Object info, View parent)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setPassword(Object info, boolean password)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setScrollable(Object info, boolean scrollable)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setSelected(Object info, boolean selected)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setSource(Object info, View source)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setText(Object info, CharSequence text)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void recycle(Object info)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface AccessibilityNodeInfoImpl
/*     */   {
/*     */     public abstract Object obtain();
/*     */ 
/*     */     public abstract Object obtain(View paramView);
/*     */ 
/*     */     public abstract Object obtain(Object paramObject);
/*     */ 
/*     */     public abstract void setSource(Object paramObject, View paramView);
/*     */ 
/*     */     public abstract int getWindowId(Object paramObject);
/*     */ 
/*     */     public abstract int getChildCount(Object paramObject);
/*     */ 
/*     */     public abstract Object getChild(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract void addChild(Object paramObject, View paramView);
/*     */ 
/*     */     public abstract int getActions(Object paramObject);
/*     */ 
/*     */     public abstract void addAction(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract boolean performAction(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract List<Object> findAccessibilityNodeInfosByText(Object paramObject, String paramString);
/*     */ 
/*     */     public abstract Object getParent(Object paramObject);
/*     */ 
/*     */     public abstract void setParent(Object paramObject, View paramView);
/*     */ 
/*     */     public abstract void getBoundsInParent(Object paramObject, Rect paramRect);
/*     */ 
/*     */     public abstract void setBoundsInParent(Object paramObject, Rect paramRect);
/*     */ 
/*     */     public abstract void getBoundsInScreen(Object paramObject, Rect paramRect);
/*     */ 
/*     */     public abstract void setBoundsInScreen(Object paramObject, Rect paramRect);
/*     */ 
/*     */     public abstract boolean isCheckable(Object paramObject);
/*     */ 
/*     */     public abstract void setCheckable(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isChecked(Object paramObject);
/*     */ 
/*     */     public abstract void setChecked(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isFocusable(Object paramObject);
/*     */ 
/*     */     public abstract void setFocusable(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isFocused(Object paramObject);
/*     */ 
/*     */     public abstract void setFocused(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isSelected(Object paramObject);
/*     */ 
/*     */     public abstract void setSelected(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isClickable(Object paramObject);
/*     */ 
/*     */     public abstract void setClickable(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isLongClickable(Object paramObject);
/*     */ 
/*     */     public abstract void setLongClickable(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isEnabled(Object paramObject);
/*     */ 
/*     */     public abstract void setEnabled(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isPassword(Object paramObject);
/*     */ 
/*     */     public abstract void setPassword(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isScrollable(Object paramObject);
/*     */ 
/*     */     public abstract void setScrollable(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract CharSequence getPackageName(Object paramObject);
/*     */ 
/*     */     public abstract void setPackageName(Object paramObject, CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract CharSequence getClassName(Object paramObject);
/*     */ 
/*     */     public abstract void setClassName(Object paramObject, CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract CharSequence getText(Object paramObject);
/*     */ 
/*     */     public abstract void setText(Object paramObject, CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract CharSequence getContentDescription(Object paramObject);
/*     */ 
/*     */     public abstract void setContentDescription(Object paramObject, CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract void recycle(Object paramObject);
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.accessibility.AccessibilityNodeInfoCompat
 * JD-Core Version:    0.6.0
 */