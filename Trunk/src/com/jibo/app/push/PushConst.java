package com.jibo.app.push;

public class PushConst {
public static Boolean pushFlag = false;
public static final int News_MODULE = 0;
public static int pushmodule = -1;
public static String id;
public static final int RESEARCH_MODULE = 1;
public static final int SECURE_MODULE = 2;
public static void resetPush(){
	pushFlag = null;
	pushmodule = -1;
}
}
