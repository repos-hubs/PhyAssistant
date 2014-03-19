/*    */ package com.jibo.v4.os;
/*    */ 
/*    */ import android.os.Parcel;
import android.os.Parcelable;
/*    */ 
/*    */ public class ParcelableCompat
/*    */ {
/*    */   public static <T> Parcelable.Creator<T> newCreator(ParcelableCompatCreatorCallbacks<T> callbacks)
/*    */   {
/* 26 */     return new CompatCreator(callbacks);
/*    */   }
/*    */   static class CompatCreator<T> implements Parcelable.Creator<T> {
/*    */     final ParcelableCompatCreatorCallbacks<T> mCallbacks;
/*    */ 
/*    */     public CompatCreator(ParcelableCompatCreatorCallbacks<T> callbacks) {
/* 33 */       this.mCallbacks = callbacks;
/*    */     }
/*    */ 
/*    */     public T createFromParcel(Parcel source)
/*    */     {
/* 38 */       return this.mCallbacks.createFromParcel(source, null);
/*    */     }
/*    */ 
/*    */     public T[] newArray(int size)
/*    */     {
/* 43 */       return this.mCallbacks.newArray(size);
/*    */     }
/*    */   }
/*    */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.os.ParcelableCompat
 * JD-Core Version:    0.6.0
 */