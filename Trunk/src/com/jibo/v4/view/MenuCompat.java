/*    */ package com.jibo.v4.view;
/*    */ 
/*    */ import android.view.MenuItem;
/*    */ 
/*    */ public class MenuCompat
/*    */ {
/* 52 */   static final MenuVersionImpl IMPL = new BaseMenuVersionImpl();
/*    */ 
/*    */   public static boolean setShowAsAction(MenuItem item, int actionEnum)
/*    */   {
/* 64 */     return IMPL.setShowAsAction(item, actionEnum);
/*    */   }
/*    */ 
/*    */   static class BaseMenuVersionImpl
/*    */     implements MenuCompat.MenuVersionImpl
/*    */   {
/*    */     public boolean setShowAsAction(MenuItem item, int actionEnum)
/*    */     {
/* 38 */       return false;
/*    */     }
/*    */   }
/*    */ 
/*    */   static abstract interface MenuVersionImpl
/*    */   {
/*    */     public abstract boolean setShowAsAction(MenuItem paramMenuItem, int paramInt);
/*    */   }
/*    */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.MenuCompat
 * JD-Core Version:    0.6.0
 */