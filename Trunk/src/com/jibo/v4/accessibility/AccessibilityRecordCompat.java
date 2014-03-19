/*     */ package com.jibo.v4.accessibility;
/*     */ 
/*     */ import java.util.List;

import android.os.Parcelable;
import android.view.View;
/*     */ 
/*     */ public class AccessibilityRecordCompat
/*     */ {
/* 262 */   private static final AccessibilityRecordImpl IMPL = new AccessibilityRecordStubImpl();
/*     */   private final Object mRecord;
/*     */ 
/*     */   public AccessibilityRecordCompat(Object record)
/*     */   {
/* 274 */     this.mRecord = record;
/*     */   }
/*     */ 
/*     */   public Object getImpl()
/*     */   {
/* 281 */     return this.mRecord;
/*     */   }
/*     */ 
/*     */   public static AccessibilityRecordCompat obtain(AccessibilityRecordCompat record)
/*     */   {
/* 292 */     return new AccessibilityRecordCompat(IMPL.obtain(record.mRecord));
/*     */   }
/*     */ 
/*     */   public static AccessibilityRecordCompat obtain()
/*     */   {
/* 302 */     return new AccessibilityRecordCompat(IMPL.obtain());
/*     */   }
/*     */ 
/*     */   public void setSource(View source)
/*     */   {
/* 313 */     IMPL.setSource(this.mRecord, source);
/*     */   }
/*     */ 
/*     */   public AccessibilityNodeInfoCompat getSource()
/*     */   {
/* 329 */     return new AccessibilityNodeInfoCompat(IMPL.getSource(this.mRecord));
/*     */   }
/*     */ 
/*     */   public int getWindowId()
/*     */   {
/* 338 */     return IMPL.getWindowId(this.mRecord);
/*     */   }
/*     */ 
/*     */   public boolean isChecked()
/*     */   {
/* 347 */     return IMPL.isChecked(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setChecked(boolean isChecked)
/*     */   {
/* 358 */     IMPL.setChecked(this.mRecord, isChecked);
/*     */   }
/*     */ 
/*     */   public boolean isEnabled()
/*     */   {
/* 367 */     return IMPL.isEnabled(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setEnabled(boolean isEnabled)
/*     */   {
/* 378 */     IMPL.setEnabled(this.mRecord, isEnabled);
/*     */   }
/*     */ 
/*     */   public boolean isPassword()
/*     */   {
/* 387 */     return IMPL.isPassword(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setPassword(boolean isPassword)
/*     */   {
/* 398 */     IMPL.setPassword(this.mRecord, isPassword);
/*     */   }
/*     */ 
/*     */   public boolean isFullScreen()
/*     */   {
/* 407 */     return IMPL.isFullScreen(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setFullScreen(boolean isFullScreen)
/*     */   {
/* 418 */     IMPL.setFullScreen(this.mRecord, isFullScreen);
/*     */   }
/*     */ 
/*     */   public boolean isScrollable()
/*     */   {
/* 427 */     return IMPL.isScrollable(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setScrollable(boolean scrollable)
/*     */   {
/* 438 */     IMPL.setScrollable(this.mRecord, scrollable);
/*     */   }
/*     */ 
/*     */   public int getItemCount()
/*     */   {
/* 447 */     return IMPL.getItemCount(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setItemCount(int itemCount)
/*     */   {
/* 458 */     IMPL.setItemCount(this.mRecord, itemCount);
/*     */   }
/*     */ 
/*     */   public int getCurrentItemIndex()
/*     */   {
/* 467 */     return IMPL.getCurrentItemIndex(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setCurrentItemIndex(int currentItemIndex)
/*     */   {
/* 478 */     IMPL.setCurrentItemIndex(this.mRecord, currentItemIndex);
/*     */   }
/*     */ 
/*     */   public int getFromIndex()
/*     */   {
/* 490 */     return IMPL.getFromIndex(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setFromIndex(int fromIndex)
/*     */   {
/* 504 */     IMPL.setFromIndex(this.mRecord, fromIndex);
/*     */   }
/*     */ 
/*     */   public int getToIndex()
/*     */   {
/* 514 */     return IMPL.getToIndex(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setToIndex(int toIndex)
/*     */   {
/* 524 */     IMPL.setToIndex(this.mRecord, toIndex);
/*     */   }
/*     */ 
/*     */   public int getScrollX()
/*     */   {
/* 533 */     return IMPL.getScrollX(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setScrollX(int scrollX)
/*     */   {
/* 542 */     IMPL.setScrollX(this.mRecord, scrollX);
/*     */   }
/*     */ 
/*     */   public int getScrollY()
/*     */   {
/* 551 */     return IMPL.getScrollY(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setScrollY(int scrollY)
/*     */   {
/* 560 */     IMPL.setScrollY(this.mRecord, scrollY);
/*     */   }
/*     */ 
/*     */   public int getAddedCount()
/*     */   {
/* 605 */     return IMPL.getAddedCount(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setAddedCount(int addedCount)
/*     */   {
/* 616 */     IMPL.setAddedCount(this.mRecord, addedCount);
/*     */   }
/*     */ 
/*     */   public int getRemovedCount()
/*     */   {
/* 625 */     return IMPL.getRemovedCount(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setRemovedCount(int removedCount)
/*     */   {
/* 636 */     IMPL.setRemovedCount(this.mRecord, removedCount);
/*     */   }
/*     */ 
/*     */   public CharSequence getClassName()
/*     */   {
/* 645 */     return IMPL.getClassName(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setClassName(CharSequence className)
/*     */   {
/* 656 */     IMPL.setClassName(this.mRecord, className);
/*     */   }
/*     */ 
/*     */   public List<CharSequence> getText()
/*     */   {
/* 666 */     return IMPL.getText(this.mRecord);
/*     */   }
/*     */ 
/*     */   public CharSequence getBeforeText()
/*     */   {
/* 675 */     return IMPL.getBeforeText(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setBeforeText(CharSequence beforeText)
/*     */   {
/* 686 */     IMPL.setBeforeText(this.mRecord, beforeText);
/*     */   }
/*     */ 
/*     */   public CharSequence getContentDescription()
/*     */   {
/* 695 */     return IMPL.getContentDescription(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setContentDescription(CharSequence contentDescription)
/*     */   {
/* 706 */     IMPL.setContentDescription(this.mRecord, contentDescription);
/*     */   }
/*     */ 
/*     */   public Parcelable getParcelableData()
/*     */   {
/* 715 */     return IMPL.getParcelableData(this.mRecord);
/*     */   }
/*     */ 
/*     */   public void setParcelableData(Parcelable parcelableData)
/*     */   {
/* 726 */     IMPL.setParcelableData(this.mRecord, parcelableData);
/*     */   }
/*     */ 
/*     */   public void recycle()
/*     */   {
/* 739 */     IMPL.recycle(this.mRecord);
/*     */   }
/*     */ 
/*     */   public int hashCode()
/*     */   {
/* 744 */     return this.mRecord == null ? 0 : this.mRecord.hashCode();
/*     */   }
/*     */ 
/*     */   public boolean equals(Object obj)
/*     */   {
/* 750 */     if (this == obj) {
/* 751 */       return true;
/*     */     }
/* 753 */     if (obj == null) {
/* 754 */       return false;
/*     */     }
/* 756 */     if (getClass() != obj.getClass()) {
/* 757 */       return false;
/*     */     }
/* 759 */     AccessibilityRecordCompat other = (AccessibilityRecordCompat)obj;
/* 760 */     if (this.mRecord == null) {
/* 761 */       if (other.mRecord != null)
/* 762 */         return false;
/*     */     }
/* 764 */     else if (!this.mRecord.equals(other.mRecord)) {
/* 765 */       return false;
/*     */     }
/* 767 */     return true;
/*     */   }
/*     */ 
/*     */   static class AccessibilityRecordStubImpl
/*     */     implements AccessibilityRecordCompat.AccessibilityRecordImpl
/*     */   {
/*     */     public Object obtain()
/*     */     {
/*  79 */       return null;
/*     */     }
/*     */ 
/*     */     public Object obtain(Object record) {
/*  83 */       return null;
/*     */     }
/*     */ 
/*     */     public int getAddedCount(Object record) {
/*  87 */       return 0;
/*     */     }
/*     */ 
/*     */     public CharSequence getBeforeText(Object record) {
/*  91 */       return null;
/*     */     }
/*     */ 
/*     */     public CharSequence getClassName(Object record) {
/*  95 */       return null;
/*     */     }
/*     */ 
/*     */     public CharSequence getContentDescription(Object record) {
/*  99 */       return null;
/*     */     }
/*     */ 
/*     */     public int getCurrentItemIndex(Object record) {
/* 103 */       return 0;
/*     */     }
/*     */ 
/*     */     public int getFromIndex(Object record) {
/* 107 */       return 0;
/*     */     }
/*     */ 
/*     */     public int getItemCount(Object record) {
/* 111 */       return 0;
/*     */     }
/*     */ 
/*     */     public int getMaxScrollX(Object record) {
/* 115 */       return 0;
/*     */     }
/*     */ 
/*     */     public int getMaxScrollY(Object record) {
/* 119 */       return 0;
/*     */     }
/*     */ 
/*     */     public Parcelable getParcelableData(Object record) {
/* 123 */       return null;
/*     */     }
/*     */ 
/*     */     public int getRemovedCount(Object record) {
/* 127 */       return 0;
/*     */     }
/*     */ 
/*     */     public int getScrollX(Object record) {
/* 131 */       return 0;
/*     */     }
/*     */ 
/*     */     public int getScrollY(Object record) {
/* 135 */       return 0;
/*     */     }
/*     */ 
/*     */     public Object getSource(Object record) {
/* 139 */       return null;
/*     */     }
/*     */ 
/*     */     public List<CharSequence> getText(Object record) {
/* 143 */       return null;
/*     */     }
/*     */ 
/*     */     public int getToIndex(Object record) {
/* 147 */       return 0;
/*     */     }
/*     */ 
/*     */     public int getWindowId(Object record) {
/* 151 */       return 0;
/*     */     }
/*     */ 
/*     */     public boolean isChecked(Object record) {
/* 155 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isEnabled(Object record) {
/* 159 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isFullScreen(Object record) {
/* 163 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isPassword(Object record) {
/* 167 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean isScrollable(Object record) {
/* 171 */       return false;
/*     */     }
/*     */ 
/*     */     public void recycle(Object record)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setAddedCount(Object record, int addedCount)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setBeforeText(Object record, CharSequence beforeText)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setChecked(Object record, boolean isChecked)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setClassName(Object record, CharSequence className)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setContentDescription(Object record, CharSequence contentDescription)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setCurrentItemIndex(Object record, int currentItemIndex)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setEnabled(Object record, boolean isEnabled)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setFromIndex(Object record, int fromIndex)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setFullScreen(Object record, boolean isFullScreen)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setItemCount(Object record, int itemCount)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setMaxScrollX(Object record, int maxScrollX)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setMaxScrollY(Object record, int maxScrollY)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setParcelableData(Object record, Parcelable parcelableData)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setPassword(Object record, boolean isPassword)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setRemovedCount(Object record, int removedCount)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setScrollX(Object record, int scrollX)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setScrollY(Object record, int scrollY)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setScrollable(Object record, boolean scrollable)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setSource(Object record, View source)
/*     */     {
/*     */     }
/*     */ 
/*     */     public void setToIndex(Object record, int toIndex)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface AccessibilityRecordImpl
/*     */   {
/*     */     public abstract Object obtain();
/*     */ 
/*     */     public abstract Object obtain(Object paramObject);
/*     */ 
/*     */     public abstract void setSource(Object paramObject, View paramView);
/*     */ 
/*     */     public abstract Object getSource(Object paramObject);
/*     */ 
/*     */     public abstract int getWindowId(Object paramObject);
/*     */ 
/*     */     public abstract boolean isChecked(Object paramObject);
/*     */ 
/*     */     public abstract void setChecked(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isEnabled(Object paramObject);
/*     */ 
/*     */     public abstract void setEnabled(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isPassword(Object paramObject);
/*     */ 
/*     */     public abstract void setPassword(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isFullScreen(Object paramObject);
/*     */ 
/*     */     public abstract void setFullScreen(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract boolean isScrollable(Object paramObject);
/*     */ 
/*     */     public abstract void setScrollable(Object paramObject, boolean paramBoolean);
/*     */ 
/*     */     public abstract int getItemCount(Object paramObject);
/*     */ 
/*     */     public abstract void setItemCount(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getCurrentItemIndex(Object paramObject);
/*     */ 
/*     */     public abstract void setCurrentItemIndex(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getFromIndex(Object paramObject);
/*     */ 
/*     */     public abstract void setFromIndex(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getToIndex(Object paramObject);
/*     */ 
/*     */     public abstract void setToIndex(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getScrollX(Object paramObject);
/*     */ 
/*     */     public abstract void setScrollX(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getScrollY(Object paramObject);
/*     */ 
/*     */     public abstract void setScrollY(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getMaxScrollX(Object paramObject);
/*     */ 
/*     */     public abstract void setMaxScrollX(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getMaxScrollY(Object paramObject);
/*     */ 
/*     */     public abstract void setMaxScrollY(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getAddedCount(Object paramObject);
/*     */ 
/*     */     public abstract void setAddedCount(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract int getRemovedCount(Object paramObject);
/*     */ 
/*     */     public abstract void setRemovedCount(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract CharSequence getClassName(Object paramObject);
/*     */ 
/*     */     public abstract void setClassName(Object paramObject, CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract List<CharSequence> getText(Object paramObject);
/*     */ 
/*     */     public abstract CharSequence getBeforeText(Object paramObject);
/*     */ 
/*     */     public abstract void setBeforeText(Object paramObject, CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract CharSequence getContentDescription(Object paramObject);
/*     */ 
/*     */     public abstract void setContentDescription(Object paramObject, CharSequence paramCharSequence);
/*     */ 
/*     */     public abstract Parcelable getParcelableData(Object paramObject);
/*     */ 
/*     */     public abstract void setParcelableData(Object paramObject, Parcelable paramParcelable);
/*     */ 
/*     */     public abstract void recycle(Object paramObject);
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.accessibility.AccessibilityRecordCompat
 * JD-Core Version:    0.6.0
 */