package com.jibo.util.tips;

import android.app.Activity;
import android.content.Context;

public class DipUtil {
    public static float scale;
    
    public static float getDensity(Context context){   
    	return scale = context.getResources().getDisplayMetrics().density;
    }
    
    public static float getScale(Activity context){   
    	return context.getResources().getDisplayMetrics().scaledDensity;
    }
    
    public static int dip2px(Context context, float dipValue){   
    	return (int)(dipValue * getDensity(context)+ 0.5f);   
    }
    
	/**
	  * 将sp值转换为px值，保证文字大小不变
	  * 
	  * @param spValue
	  * @param fontScale（DisplayMetrics类中属性scaledDensity）
	  * @return
	  */
	 public static int sp2px(float spValue, float fontScale) {
	  return (int) (spValue * fontScale + 0.5f);
	 }
	 
	 /**
	  * 将px值转换为sp值，保证文字大小不变
	  * 
	  * @param pxValue
	  * @param fontScale（DisplayMetrics类中属性scaledDensity）
	  * @return
	  */
	 public static int px2sp(float pxValue, float fontScale) {
	  return (int) (pxValue / fontScale + 0.5f);
	 }
	
}
