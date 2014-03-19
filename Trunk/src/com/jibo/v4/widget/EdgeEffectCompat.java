/*     */ package com.jibo.v4.widget;
/*     */ 
/*     */ import android.content.Context;
import android.graphics.Canvas;
/*     */ 
/*     */ public class EdgeEffectCompat
/*     */ {
/*     */   private Object mEdgeEffect;
/*  36 */   private static final EdgeEffectImpl IMPL = new BaseEdgeEffectImpl();
/*     */ 
/*     */   public EdgeEffectCompat(Context context)
/*     */   {
/*  96 */     this.mEdgeEffect = IMPL.newEdgeEffect(context);
/*     */   }
/*     */ 
/*     */   public void setSize(int width, int height)
/*     */   {
/* 106 */     IMPL.setSize(this.mEdgeEffect, width, height);
/*     */   }
/*     */ 
/*     */   public boolean isFinished()
/*     */   {
/* 117 */     return IMPL.isFinished(this.mEdgeEffect);
/*     */   }
/*     */ 
/*     */   public void finish()
/*     */   {
/* 125 */     IMPL.finish(this.mEdgeEffect);
/*     */   }
/*     */ 
/*     */   public boolean onPull(float deltaDistance)
/*     */   {
/* 140 */     return IMPL.onPull(this.mEdgeEffect, deltaDistance);
/*     */   }
/*     */ 
/*     */   public boolean onRelease()
/*     */   {
/* 152 */     return IMPL.onRelease(this.mEdgeEffect);
/*     */   }
/*     */ 
/*     */   public boolean onAbsorb(int velocity)
/*     */   {
/* 167 */     return IMPL.onAbsorb(this.mEdgeEffect, velocity);
/*     */   }
/*     */ 
/*     */   public boolean draw(Canvas canvas)
/*     */   {
/* 181 */     return IMPL.draw(this.mEdgeEffect, canvas);
/*     */   }
/*     */ 
/*     */   static class BaseEdgeEffectImpl
/*     */     implements EdgeEffectCompat.EdgeEffectImpl
/*     */   {
/*     */     public Object newEdgeEffect(Context context)
/*     */     {
/*  55 */       return null;
/*     */     }
/*     */ 
/*     */     public void setSize(Object edgeEffect, int width, int height) {
/*     */     }
/*     */ 
/*     */     public boolean isFinished(Object edgeEffect) {
/*  62 */       return true;
/*     */     }
/*     */ 
/*     */     public void finish(Object edgeEffect) {
/*     */     }
/*     */ 
/*     */     public boolean onPull(Object edgeEffect, float deltaDistance) {
/*  69 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean onRelease(Object edgeEffect) {
/*  73 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean onAbsorb(Object edgeEffect, int velocity) {
/*  77 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean draw(Object edgeEffect, Canvas canvas) {
/*  81 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface EdgeEffectImpl
/*     */   {
/*     */     public abstract Object newEdgeEffect(Context paramContext);
/*     */ 
/*     */     public abstract void setSize(Object paramObject, int paramInt1, int paramInt2);
/*     */ 
/*     */     public abstract boolean isFinished(Object paramObject);
/*     */ 
/*     */     public abstract void finish(Object paramObject);
/*     */ 
/*     */     public abstract boolean onPull(Object paramObject, float paramFloat);
/*     */ 
/*     */     public abstract boolean onRelease(Object paramObject);
/*     */ 
/*     */     public abstract boolean onAbsorb(Object paramObject, int paramInt);
/*     */ 
/*     */     public abstract boolean draw(Object paramObject, Canvas paramCanvas);
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.widget.EdgeEffectCompat
 * JD-Core Version:    0.6.0
 */