package com.jibo.base.src;

import java.util.Arrays;
import java.util.List;


public class SrcUtil {
	public static String listFormat(String list) {
		return list.replaceFirst("\\[", "").replaceFirst("\\]", "")
				.replaceAll("\\,", "_");
	}
	public static String formatClassStr(String clazz) {
		return clazz.replaceAll("\\.", "_");
	}
	public static String seek(List<String> strings1,List<String> strings2,String content){
		int idx = strings1.indexOf(content);
		if(idx==-1)return null;
		return strings2.get(idx);
	}
}
