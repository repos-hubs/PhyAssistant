package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.SyncHistoryEntity;

public class SyncHistoryPaser extends SoapDataPaser {
	private ArrayList<SyncHistoryEntity> historyList;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		historyList = new ArrayList<SyncHistoryEntity>();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject detail = (SoapObject) result.getProperty("Getview_history_newTwoResult");		
		for(int i=0; i<detail.getPropertyCount(); i++) {
			SoapObject item = (SoapObject) detail.getProperty(i);
			System.out.println("SyncHistory      "+item.toString());
			SyncHistoryEntity historyEntity = new SyncHistoryEntity();
			String parentID = item.getProperty("vparent").toString();
			if("anyType{}".equals(parentID)) parentID = "-1";
			historyEntity.setVparentId(Integer.parseInt(parentID));
			historyEntity.setUsername(item.getProperty("userid").toString());
			String vName = item.getProperty("vname").toString();
			if("anyType{}".equals(vName)) vName = "";
			historyEntity.setvName(vName);
			String vid = item.getProperty("vid").toString();
			if("anyType{}".equals(vid)) vid = "-1";
			historyEntity.setVid(Integer.parseInt(vid));
			String vcolid = item.getProperty("vcolid").toString();
			if("anyType{}".equals(vcolid)) vcolid = "-1";
			historyEntity.setvColId(Integer.parseInt(vcolid));
			
			historyEntity.setTime(item.getProperty("time").toString());
			historyList.add(historyEntity);
		}
	}
	public ArrayList<SyncHistoryEntity> getHistoryList() {
		return historyList;
	}
	public void setHistoryList(ArrayList<SyncHistoryEntity> historyList) {
		this.historyList = historyList;
	}
	
}
