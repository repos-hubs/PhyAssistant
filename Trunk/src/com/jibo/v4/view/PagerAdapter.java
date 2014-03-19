/*     */ package com.jibo.v4.view;
/*     */ 
/*     */ import android.os.Parcelable;
import android.view.View;
/*     */ 
/*     */ public abstract class PagerAdapter
/*     */ {
/*     */   private DataSetObserver mObserver;
/*     */   public static final int POSITION_UNCHANGED = -1;
/*     */   public static final int POSITION_NONE = -2;
/*     */ 
/*     */   public abstract int getCount();
/*     */ 
/*     */   public abstract void startUpdate(View paramView);
/*     */ 
/*     */   public abstract Object instantiateItem(View paramView, int paramInt);
/*     */ 
/*     */   public abstract void destroyItem(View paramView, int paramInt, Object paramObject);
/*     */ 
/*     */   public void setPrimaryItem(View container, int position, Object object)
/*     */   {
/*     */   }
/*     */ 
/*     */   public abstract void finishUpdate(View paramView);
/*     */ 
/*     */   public abstract boolean isViewFromObject(View paramView, Object paramObject);
/*     */ 
/*     */   public abstract Parcelable saveState();
/*     */ 
/*     */   public abstract void restoreState(Parcelable paramParcelable, ClassLoader paramClassLoader);
/*     */ 
/*     */   public int getItemPosition(Object object)
/*     */   {
/* 122 */     return -1;
/*     */   }
/*     */ 
/*     */   public void notifyDataSetChanged()
/*     */   {
/* 130 */     if (this.mObserver != null)
/* 131 */       this.mObserver.onDataSetChanged();
/*     */   }
/*     */ 
/*     */   void setDataSetObserver(DataSetObserver observer)
/*     */   {
/* 136 */     this.mObserver = observer;
/*     */   }
/*     */ 
/*     */   static abstract interface DataSetObserver
/*     */   {
/*     */     public abstract void onDataSetChanged();
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.PagerAdapter
 * JD-Core Version:    0.6.0
 */