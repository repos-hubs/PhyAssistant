package com.jibo.common;

import java.util.Comparator;
import java.util.Map;

public class ComparatorDrugList implements Comparator {

	public int compare(Object arg0, Object arg1) {
		Map<String,String> user0 = (Map<String,String>) arg0;
		Map<String,String> user1 = (Map<String,String>) arg1;
		// 首先比较年龄，如果年龄相同，则比较名字
		int flag = user0.get("drugName").compareTo(user1.get("drugName"));
		return flag;
	}

}