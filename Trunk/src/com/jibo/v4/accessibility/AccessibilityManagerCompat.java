/*     */ package com.jibo.v4.accessibility;
/*     */ 
/*     */ import java.util.List;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;
/*     */ 
/*     */ public class AccessibilityManagerCompat
/*     */ {
/*  78 */   private static final AccessibilityManagerVersionImpl IMPL = new AccessibilityManagerStubImpl();
/*     */ 
/*     */   public static boolean addAccessibilityStateChangeListener(AccessibilityManager manager, AccessibilityStateChangeListenerCompat listener)
/*     */   {
/*  94 */     return IMPL.addAccessibilityStateChangeListener(manager, listener);
/*     */   }
/*     */ 
/*     */   public static boolean removeAccessibilityStateChangeListener(AccessibilityManager manager, AccessibilityStateChangeListenerCompat listener)
/*     */   {
/* 106 */     return IMPL.removeAccessibilityStateChangeListener(manager, listener);
/*     */   }
/*     */ 
/*     */   public static List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(AccessibilityManager manager)
/*     */   {
/* 117 */     return IMPL.getInstalledAccessibilityServiceList(manager);
/*     */   }
/*     */ 
/*     */   public static List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(AccessibilityManager manager, int feedbackTypeFlags)
/*     */   {
/* 136 */     return IMPL.getEnabledAccessibilityServiceList(manager, feedbackTypeFlags);
/*     */   }
/*     */ 
/*     */   public static abstract class AccessibilityStateChangeListenerCompat
/*     */   {
/*     */     final Object mListener;
/*     */ 
/*     */     public AccessibilityStateChangeListenerCompat()
/*     */     {
/* 146 */       this.mListener = AccessibilityManagerCompat.IMPL.newAccessiblityStateChangeListener(this);
/*     */     }
/*     */ 
/*     */     public abstract void onAccessibilityStateChanged(boolean paramBoolean);
/*     */   }
/*     */ 
/*     */   static class AccessibilityManagerStubImpl
/*     */     implements AccessibilityManagerCompat.AccessibilityManagerVersionImpl
/*     */   {
/*     */     public Object newAccessiblityStateChangeListener(AccessibilityManagerCompat.AccessibilityStateChangeListenerCompat listener)
/*     */     {
/*  46 */       return null;
/*     */     }
/*     */ 
/*     */     public boolean addAccessibilityStateChangeListener(AccessibilityManager manager, AccessibilityManagerCompat.AccessibilityStateChangeListenerCompat listener)
/*     */     {
/*  51 */       return false;
/*     */     }
/*     */ 
/*     */     public boolean removeAccessibilityStateChangeListener(AccessibilityManager manager, AccessibilityManagerCompat.AccessibilityStateChangeListenerCompat listener)
/*     */     {
/*  56 */       return false;
/*     */     }
/*     */ 
/*     */     public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(AccessibilityManager manager, int feedbackTypeFlags)
/*     */     {
/*  61 */       return null;
/*     */     }
/*     */ 
/*     */     public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(AccessibilityManager manager)
/*     */     {
/*  66 */       return null;
/*     */     }
/*     */ 
/*     */     public boolean isTouchExplorationEnabled(AccessibilityManager manager) {
/*  70 */       return false;
/*     */     }
/*     */   }
/*     */ 
/*     */   static abstract interface AccessibilityManagerVersionImpl
/*     */   {
/*     */     public abstract Object newAccessiblityStateChangeListener(AccessibilityManagerCompat.AccessibilityStateChangeListenerCompat paramAccessibilityStateChangeListenerCompat);
/*     */ 
/*     */     public abstract boolean addAccessibilityStateChangeListener(AccessibilityManager paramAccessibilityManager, AccessibilityManagerCompat.AccessibilityStateChangeListenerCompat paramAccessibilityStateChangeListenerCompat);
/*     */ 
/*     */     public abstract boolean removeAccessibilityStateChangeListener(AccessibilityManager paramAccessibilityManager, AccessibilityManagerCompat.AccessibilityStateChangeListenerCompat paramAccessibilityStateChangeListenerCompat);
/*     */ 
/*     */     public abstract List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(AccessibilityManager paramAccessibilityManager, int paramInt);
/*     */ 
/*     */     public abstract List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(AccessibilityManager paramAccessibilityManager);
/*     */ 
/*     */     public abstract boolean isTouchExplorationEnabled(AccessibilityManager paramAccessibilityManager);
/*     */   }
/*     */ }

/* Location:           d:\我的文档\桌面\lewa.os.jar
 * Qualified Name:     android.support.lewa.view.accessibility.AccessibilityManagerCompat
 * JD-Core Version:    0.6.0
 */