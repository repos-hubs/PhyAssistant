/*    */ package com.jibo.v4.view;
/*    */ 
/*    */ import android.view.VelocityTracker;
/*    */ 
/*    */ class VelocityTrackerCompatHoneycomb
/*    */ {
/*    */   public static float getXVelocity(VelocityTracker tracker, int pointerId)
/*    */   {
/* 26 */     return tracker.getXVelocity(pointerId);
/*    */   }
/*    */   public static float getYVelocity(VelocityTracker tracker, int pointerId) {
/* 29 */     return tracker.getYVelocity(pointerId);
/*    */   }
/*    */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.VelocityTrackerCompatHoneycomb
 * JD-Core Version:    0.6.0
 */