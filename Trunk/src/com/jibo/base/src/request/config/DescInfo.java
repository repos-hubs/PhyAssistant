package com.jibo.base.src.request.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.jibo.base.src.request.AsycRequestHandler;
import com.jibo.base.src.request.ScrollCounter;

public abstract class DescInfo {
	public boolean isCheck;
	public Integer requestType = -1;
	public static final Integer REQUEST_TYPE_NORMAL = 0;
	public static final Integer REQUEST_TYPE_LOGIC = 1;
	public static final Integer REQUEST_TYPE_PAGED = 2;
	public String debugKey;
	public int idx;
	public Properties propertyInfo = new Properties();
	public int requestLog;
	public String requestUrl;
	public AsycRequestHandler responseHandler;
	public List<ScrollCounter> batch = new ArrayList<ScrollCounter>(0);
	public List<String> propertyKey;
	public List<String> propertyVal;
	public List<String> runtimeVal;

	public void setDescInfo(DescInfo descInfo) {
		setInfo(descInfo.propertyKey, descInfo.propertyVal,
				descInfo.requestLog, descInfo.requestUrl, descInfo.batch,
				descInfo.debugKey, descInfo.requestType ==-1?DescInfo.REQUEST_TYPE_NORMAL:descInfo.requestType);
		this.propertyInfo = descInfo.propertyInfo;
		this.runtimeVal = descInfo.runtimeVal;
	}

	public void setInfo(String[] propertyKey, String[] propertyVal,
			int requeatLog, String requestUrl, List<ScrollCounter> batch,
			String debugKey, int requesrType) {
		this.propertyKey = Arrays.asList(propertyKey);
		this.propertyVal = Arrays.asList(propertyVal);
		setRuntimeVal(this.propertyVal);
		this.requestLog = requeatLog;
		this.requestUrl = requestUrl;
		if (batch != null)
			setBatch(batch);
		this.debugKey = debugKey == null ? "" : " " + debugKey;
		this.requestType = requesrType;
	}

	public void setInfo(List<String> propertyKey, List<String> propertyVal,
			int requeatLog, String requestUrl, List<ScrollCounter> batch,
			String debugKey, int requesrType) {
		this.propertyKey = propertyKey;
		this.propertyVal = propertyVal;
		setRuntimeVal(this.propertyVal);
		this.requestLog = requeatLog;
		this.requestUrl = requestUrl;
		if (batch != null)
			setBatch(batch);
		this.debugKey = debugKey == null ? "" : " " + debugKey;
		this.requestType = requesrType;
	}

	public void setRuntimeVal(List<String> paraVal) {
		this.runtimeVal = new ArrayList<String>(propertyVal);
	}

	public void putInProperties(List<String> propertyKey,
			List<String> propertyVal) {
		if (propertyKey != null) {
			for (int i = 0; i < propertyKey.size(); i++) {
				if (!propertyVal.get(i).trim().equals(""))
					this.propertyInfo.setProperty(propertyKey.get(i),
							propertyVal.get(i));
				else if (this.propertyInfo.getProperty(propertyKey.get(i)) == null) {
					this.propertyInfo.setProperty(propertyKey.get(i), "");
				}
			}
		}
	}

	public void setInfo(String[] propertyKey, String[] propertyVal,
			int requeatLog, String requestUrl, String debugKey) {
		setInfo(propertyKey, propertyVal, requeatLog, requestUrl, null,
				debugKey, -1);
	}

	void setInfo(int requeatLog, String requestUrl,
			List<ScrollCounter> batch) {
		setInfo(new ArrayList(0), new ArrayList(0), requeatLog, requestUrl,
				batch, "", -1);
	}

	public void setBatch(List<ScrollCounter> batch) {
		this.batch = batch;
	}

	public List<String> getPropertyKey() {
		return propertyKey;
	}

	public void setPropertyKey(List<String> propertyKey) {
		this.propertyKey = propertyKey;
	}

	public List<String> getPropertyVal() {
		return propertyVal;
	}

	public void setPropertyVal(List<String> propertyVal) {
		this.propertyVal = propertyVal;
	}

	public List<String> getRuntimeVal() {
		return runtimeVal;
	}

}
