/*     */ package com.jibo.v4.view;
/*     */ 
/*     */ import android.view.KeyEvent;
/*     */ 
/*     */ public class KeyEventCompat
/*     */ {
/* 102 */   static final KeyEventVersionImpl IMPL = new BaseKeyEventVersionImpl();
/*     */ 
/*     */   public static int normalizeMetaState(int metaState)
/*     */   {
/* 109 */     return IMPL.normalizeMetaState(metaState);
/*     */   }
/*     */ 
/*     */   public static boolean metaStateHasModifiers(int metaState, int modifiers) {
/* 113 */     return IMPL.metaStateHasModifiers(metaState, modifiers);
/*     */   }
/*     */ 
/*     */   public static boolean metaStateHasNoModifiers(int metaState) {
/* 117 */     return IMPL.metaStateHasNoModifiers(metaState);
/*     */   }
/*     */ 
/*     */   public static boolean hasModifiers(KeyEvent event, int modifiers) {
/* 121 */     return IMPL.metaStateHasModifiers(event.getMetaState(), modifiers);
/*     */   }
/*     */ 
/*     */   public static boolean hasNoModifiers(KeyEvent event) {
/* 125 */     return IMPL.metaStateHasNoModifiers(event.getMetaState());
/*     */   }
/*     */ 
/*     */   static class BaseKeyEventVersionImpl
/*     */     implements KeyEventCompat.KeyEventVersionImpl
/*     */   {
/*     */     private static final int META_MODIFIER_MASK = 247;
/*     */     private static final int META_ALL_MASK = 247;
/*     */ 
/*     */     private static int metaStateFilterDirectionalModifiers(int metaState, int modifiers, int basic, int left, int right)
/*     */     {
/*  48 */       boolean wantBasic = (modifiers & basic) != 0;
/*  49 */       int directional = left | right;
/*  50 */       boolean wantLeftOrRight = (modifiers & directional) != 0;
/*     */ 
/*  52 */       if (wantBasic) {
/*  53 */         if (wantLeftOrRight) {
/*  54 */           throw new IllegalArgumentException("bad arguments");
/*     */         }
/*  56 */         return metaState & (directional ^ 0xFFFFFFFF);
/*  57 */       }if (wantLeftOrRight) {
/*  58 */         return metaState & (basic ^ 0xFFFFFFFF);
/*     */       }
/*  60 */       return metaState;
/*     */     }
/*     */ 
/*     */     public int normalizeMetaState(int metaState)
/*     */     {
/*  66 */       if ((metaState & 0xC0) != 0) {
/*  67 */         metaState |= 1;
/*     */       }
/*  69 */       if ((metaState & 0x30) != 0) {
/*  70 */         metaState |= 2;
/*     */       }
/*  72 */       return metaState & 0xF7;
/*     */     }
/*     */ 
/*     */     public boolean metaStateHasModifiers(int metaState, int modifiers)
/*     */     {
/*  77 */       metaState = normalizeMetaState(metaState) & 0xF7;
/*  78 */       metaState = metaStateFilterDirectionalModifiers(metaState, modifiers, 1, 64, 128);
/*     */ 
/*  80 */       metaState = metaStateFilterDirectionalModifiers(metaState, modifiers, 2, 16, 32);
/*     */ 
/*  82 */       return metaState == modifiers;
/*     */     }
/*     */ 
/*     */     public boolean metaStateHasNoModifiers(int metaState)
/*     */     {
/*  87 */       return (normalizeMetaState(metaState) & 0xF7) == 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface KeyEventVersionImpl
/*     */   {
/*     */     public abstract int normalizeMetaState(int paramInt);
/*     */ 
/*     */     public abstract boolean metaStateHasModifiers(int paramInt1, int paramInt2);
/*     */ 
/*     */     public abstract boolean metaStateHasNoModifiers(int paramInt);
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.KeyEventCompat
 * JD-Core Version:    0.6.0
 */