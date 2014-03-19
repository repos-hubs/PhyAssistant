package com.jibo.base.src.request.config;

import java.util.List;

import com.jibo.base.src.request.ScrollCounter;

public class DBInfo extends DescInfo {

	public DBInfo(int requeatLog, String requestUrl,
			List<ScrollCounter> batch) {
		setInfo(requeatLog, requestUrl, batch);
		// TODO Auto-generated constructor stub
	}

	public DBInfo(DescInfo descInfo) {
		setDescInfo(descInfo);
		// TODO Auto-generated constructor stub
	}

	public DBInfo(String[] propertyKey, String[] propertyVal,
			int requeatLog, String requestUrl, List<ScrollCounter> batch,
			String debugKey) {
		setInfo(propertyKey, propertyVal, requeatLog, requestUrl, batch,
				debugKey, -1);
		// TODO Auto-generated constructor stub
	}

	public DBInfo(String[] propertyKey, String[] propertyVal,
			int requeatLog, String requestUrl, String debugKey) {
		setInfo(propertyKey, propertyVal, requeatLog, requestUrl, debugKey);
		// TODO Auto-generated constructor stub
	}

}
