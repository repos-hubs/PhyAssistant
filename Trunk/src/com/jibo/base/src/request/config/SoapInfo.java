package com.jibo.base.src.request.config;

import java.util.List;

import com.jibo.base.src.request.ScrollCounter;

public class SoapInfo extends DescInfo {

	public SoapInfo(int requeatLog, String requestUrl,
			List<ScrollCounter> batch) {
		setInfo(requeatLog, requestUrl, batch);
		// TODO Auto-generated constructor stub
	}

	public SoapInfo(DescInfo descInfo) {
		setDescInfo(descInfo);
		// TODO Auto-generated constructor stub
	}

	public SoapInfo(String[] propertyKey, String[] propertyVal,
			int requeatLog, String requestUrl, List<ScrollCounter> batch,
			String debugKey) {
		setInfo(propertyKey, propertyVal, requeatLog, requestUrl, batch,
				debugKey, -1);
		// TODO Auto-generated constructor stub
	}

	public SoapInfo(String[] propertyKey, String[] propertyVal,
			int requeatLog, String requestUrl, String debugKey) {
		setInfo(propertyKey, propertyVal, requeatLog, requestUrl, debugKey);
		// TODO Auto-generated constructor stub
	}

}
