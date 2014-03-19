/*     */ package com.jibo.v4.view;
/*     */ 
/*     */ import android.os.Build;
import android.view.MotionEvent;
/*     */ 
/*     */ public class MotionEventCompat
/*     */ {
/*     */   static final MotionEventVersionImpl IMPL;
/*     */   public static final int ACTION_MASK = 255;
/*     */   public static final int ACTION_POINTER_DOWN = 5;
/*     */   public static final int ACTION_POINTER_UP = 6;
/*     */   public static final int ACTION_HOVER_MOVE = 7;
/*     */   public static final int ACTION_SCROLL = 8;
/*     */   public static final int ACTION_POINTER_INDEX_MASK = 65280;
/*     */   public static final int ACTION_POINTER_INDEX_SHIFT = 8;
/*     */ 
/*     */   public static int getActionMasked(MotionEvent event)
/*     */   {
/* 147 */     return event.getAction() & 0xFF;
/*     */   }
/*     */ 
/*     */   public static int getActionIndex(MotionEvent event)
/*     */   {
/* 155 */     return (event.getAction() & 0xFF00) >> 8;
/*     */   }
/*     */ 
/*     */   public static int findPointerIndex(MotionEvent event, int pointerId)
/*     */   {
/* 165 */     return IMPL.findPointerIndex(event, pointerId);
/*     */   }
/*     */ 
/*     */   public static int getPointerId(MotionEvent event, int pointerIndex)
/*     */   {
/* 174 */     return IMPL.getPointerId(event, pointerIndex);
/*     */   }
/*     */ 
/*     */   public static float getX(MotionEvent event, int pointerIndex)
/*     */   {
/* 183 */     return IMPL.getX(event, pointerIndex);
/*     */   }
/*     */ 
/*     */   public static float getY(MotionEvent event, int pointerIndex)
/*     */   {
/* 192 */     return IMPL.getY(event, pointerIndex);
/*     */   }
/*     */ 
/*     */   static
/*     */   {
/*  98 */     if (Build.VERSION.SDK_INT >= 5)
/*  99 */       IMPL = new EclairMotionEventVersionImpl();
/*     */     else
/* 101 */       IMPL = new BaseMotionEventVersionImpl();
/*     */   }
/*     */ 
/*     */   static class EclairMotionEventVersionImpl
/*     */     implements MotionEventCompat.MotionEventVersionImpl
/*     */   {
/*     */     public int findPointerIndex(MotionEvent event, int pointerId)
/*     */     {
/*  77 */       return MotionEventCompatEclair.findPointerIndex(event, pointerId);
/*     */     }
/*     */ 
/*     */     public int getPointerId(MotionEvent event, int pointerIndex) {
/*  81 */       return MotionEventCompatEclair.getPointerId(event, pointerIndex);
/*     */     }
/*     */ 
/*     */     public float getX(MotionEvent event, int pointerIndex) {
/*  85 */       return MotionEventCompatEclair.getX(event, pointerIndex);
/*     */     }
/*     */ 
/*     */     public float getY(MotionEvent event, int pointerIndex) {
/*  89 */       return MotionEventCompatEclair.getY(event, pointerIndex);
/*     */     }
/*     */   }
/*     */ 
/*     */   static class BaseMotionEventVersionImpl
/*     */     implements MotionEventCompat.MotionEventVersionImpl
/*     */   {
/*     */     public int findPointerIndex(MotionEvent event, int pointerId)
/*     */     {
/*  41 */       if (pointerId == 0)
/*     */       {
/*  43 */         return 0;
/*     */       }
/*  45 */       return -1;
/*     */     }
/*     */ 
/*     */     public int getPointerId(MotionEvent event, int pointerIndex) {
/*  49 */       if (pointerIndex == 0)
/*     */       {
/*  51 */         return 0;
/*     */       }
/*  53 */       throw new IndexOutOfBoundsException("Pre-Eclair does not support multiple pointers");
/*     */     }
/*     */ 
/*     */     public float getX(MotionEvent event, int pointerIndex) {
/*  57 */       if (pointerIndex == 0) {
/*  58 */         return event.getX();
/*     */       }
/*  60 */       throw new IndexOutOfBoundsException("Pre-Eclair does not support multiple pointers");
/*     */     }
/*     */ 
/*     */     public float getY(MotionEvent event, int pointerIndex) {
/*  64 */       if (pointerIndex == 0) {
/*  65 */         return event.getY();
/*     */       }
/*  67 */       throw new IndexOutOfBoundsException("Pre-Eclair does not support multiple pointers");
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface MotionEventVersionImpl
/*     */   {
/*     */     public abstract int findPointerIndex(MotionEvent paramMotionEvent, int paramInt);
/*     */ 
/*     */     public abstract int getPointerId(MotionEvent paramMotionEvent, int paramInt);
/*     */ 
/*     */     public abstract float getX(MotionEvent paramMotionEvent, int paramInt);
/*     */ 
/*     */     public abstract float getY(MotionEvent paramMotionEvent, int paramInt);
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.MotionEventCompat
 * JD-Core Version:    0.6.0
 */