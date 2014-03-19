package com.jibo.data;

import java.util.ArrayList;
import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

public class GetCategoryUpdatePaser extends SoapDataPaser {
	private String url = null;
	private HashMap<String,String> updateMap;
	private ArrayList<HashMap<String,String>> mapList = new ArrayList<HashMap<String,String>>();
	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("getCategoryUpdateResult");
		url = detail.getProperty(0).toString();
		SoapObject updateItem = (SoapObject) detail.getProperty("items");
		for(int i=0; i<updateItem.getPropertyCount(); i++) {
			SoapObject item = (SoapObject) updateItem.getProperty(i);
			updateMap = new HashMap<String, String>();
			String cate = item.getProperty(0).toString();
			String ver = item.getProperty(1).toString();
			updateMap.put(cate, ver);
			mapList.add(updateMap);
		}
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ArrayList<HashMap<String, String>> getMapList() {
		return mapList;
	}
	public void setMapList(ArrayList<HashMap<String, String>> mapList) {
		this.mapList = mapList;
	}
	
}
