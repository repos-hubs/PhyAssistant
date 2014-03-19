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

public class InteractionPaser extends SoapDataPaser{
	private String rescode = "0";//ÏìÓ¦µÄ×´Ì¬Âë
	private ArrayList<InteractionEntity> coauthorList;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		coauthorList = new ArrayList<InteractionEntity>();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject result1 = (SoapObject) result.getProperty(0);
		String isSuccess = result1.getProperty(0).toString();
		if (isSuccess.equals("false")) {
			return;
		}
		SoapObject detail = (SoapObject) result1.getProperty(1);
		if("anyType{}".equals(detail.toString())){
			return;
		}
		rescode = "200";
		if(detail != null){
			for(int i=0; i<detail.getPropertyCount(); i++) {
				SoapObject interactionObject = (SoapObject) detail.getProperty(i);
				InteractionEntity entity = new InteractionEntity();
//				entity.setInteractionId(interactionObject.getProperty("InteractionId").toString());
				entity.setKey(interactionObject.getProperty("Key").toString());
				entity.setDescription(interactionObject.getProperty("Description").toString());
				entity.setComments(interactionObject.getProperty("Comments").toString());
				entity.setDrug(interactionObject.getProperty("DrugInfo").toString());
				entity.setCultureInfo(interactionObject.getProperty("CultureInfo").toString());
				entity.setLastUpdatedStamp(Util.formatDate(interactionObject.getProperty("LastUpdatedStampString").toString().replace("T", " ")));
				entity.setStatus(interactionObject.getProperty("State").toString());
				entity.setIid(interactionObject.getProperty("InteractionId").toString());
				entity.setCreatedStamp(Util.formatDate(interactionObject.getProperty("CreatedStampString").toString().replace("T", " ")));
				coauthorList.add(entity);
			}
		}
	}
	
	public ArrayList<InteractionEntity> getCoauthorList() {
		return coauthorList;
	}
	public void setCoauthorList(ArrayList<InteractionEntity> coauthorList) {
		this.coauthorList = coauthorList;
	}
	
	public String getRescode() {
		return rescode;
	}
}
