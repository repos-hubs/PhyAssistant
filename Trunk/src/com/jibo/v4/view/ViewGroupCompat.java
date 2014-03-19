/*    */ package com.jibo.v4.view;
/*    */ 
/*    */ import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
/*    */ 
/*    */ public class ViewGroupCompat
/*    */ {
/* 45 */   static final ViewGroupCompatImpl IMPL = new ViewGroupCompatStubImpl();
/*    */ 
/*    */   public static boolean onRequestSendAccessibilityEvent(ViewGroup group, View child, AccessibilityEvent event)
/*    */   {
/* 73 */     return IMPL.onRequestSendAccessibilityEvent(group, child, event);
/*    */   }
/*    */ 
/*    */   static class ViewGroupCompatStubImpl
/*    */     implements ViewGroupCompat.ViewGroupCompatImpl
/*    */   {
/*    */     public boolean onRequestSendAccessibilityEvent(ViewGroup group, View child, AccessibilityEvent event)
/*    */     {
/* 36 */       return true;
/*    */     }
/*    */   }
/*    */ 
/*    */   static abstract interface ViewGroupCompatImpl
/*    */   {
/*    */     public abstract boolean onRequestSendAccessibilityEvent(ViewGroup paramViewGroup, View paramView, AccessibilityEvent paramAccessibilityEvent);
/*    */   }
/*    */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.ViewGroupCompat
 * JD-Core Version:    0.6.0
 */