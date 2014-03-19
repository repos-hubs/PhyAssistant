package com.jibo.data;

import java.util.ArrayList;
import java.util.Iterator;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jibo.common.Util;
import com.jibo.data.entity.CoauthorEntity;
import com.jibo.data.entity.InteractionEntity;
import com.jibo.data.entity.InteractionPidsEntity;

public class InteractionFindPaser extends SoapDataPaser{
	private String rescode = "0";//ÏìÓ¦µÄ×´Ì¬Âë
	private ArrayList<InteractionPidsEntity> coauthorList;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		coauthorList = new ArrayList<InteractionPidsEntity>();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject result1 = (SoapObject) result.getProperty(0);
		String isSuccess = result1.getProperty(0).toString();
		SoapObject detail = (SoapObject) result1.getProperty(1);
		if ("anyType{}".equals(detail.toString())) {
			return;
		}
		rescode = "200";
		if(detail != null){
			for(int i=0; i<detail.getPropertyCount(); i++) {
				SoapObject interactionObject = (SoapObject) detail.getProperty(i);
				InteractionPidsEntity entity = new InteractionPidsEntity();
				entity.setKey(interactionObject.getProperty("Key").toString());
				entity.setDescription(interactionObject.getProperty("Description").toString());
				entity.setComments(interactionObject.getProperty("Comments").toString().replace("anyType{}", ""));
				entity.setDrugInfo(interactionObject.getProperty("DrugInfo").toString());
				entity.setCultureInfo(interactionObject.getProperty("CultureInfo").toString());
				entity.setLastUpdatedStamp(Util.formatDate(interactionObject.getProperty("LastUpdatedStampString").toString().replace("T", " ")));
				entity.setStatus(interactionObject.getProperty("State").toString());
				
				entity.setPid1(interactionObject.getProperty("DrugProductId1").toString());
				entity.setPid2(interactionObject.getProperty("DrugProductId2").toString());
				entity.setPid1DrugName(interactionObject.getProperty("DrugProductName1").toString());
				entity.setPid2DrugName(interactionObject.getProperty("DrugProductName2").toString());
				coauthorList.add(entity);
			}
		}
	}

	public ArrayList<InteractionPidsEntity> getCoauthorList() {
		return coauthorList;
	}
	public void setCoauthorList(ArrayList<InteractionPidsEntity> coauthorList) {
		this.coauthorList = coauthorList;
	}
	
	public String getRescode() {
		return rescode;
	}
	
}
