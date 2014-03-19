package com.jibo.base;

import com.jibo.GBApplication;

import android.content.Context;

public class ContextUtil {
	public static Context context;
	static {
		context = GBApplication.gbapp;
	}
}
