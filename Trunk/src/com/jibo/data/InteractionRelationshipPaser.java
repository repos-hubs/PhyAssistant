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
import com.jibo.data.entity.InteractionRelationshipEntity;

public class InteractionRelationshipPaser extends SoapDataPaser{
	private String rescode = "0";//ÏìÓ¦µÄ×´Ì¬Âë
	private ArrayList<InteractionRelationshipEntity> coauthorList;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		coauthorList = new ArrayList<InteractionRelationshipEntity>();
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
				SoapObject interactionRelationshipObject = (SoapObject) detail.getProperty(i);
				InteractionRelationshipEntity entity = new InteractionRelationshipEntity();
				entity.setKey(interactionRelationshipObject.getProperty("Key").toString());
				entity.setPid(interactionRelationshipObject.getProperty("PID").toString());
				entity.setIid(interactionRelationshipObject.getProperty("IID").toString());
				entity.setRole(interactionRelationshipObject.getProperty("Role").toString());
				String creatStr = interactionRelationshipObject.getProperty("CreatedStampString").toString().replace("T", " ");
				entity.setCreatedStamp(Util.formatDate(creatStr));
				String updatStr = interactionRelationshipObject.getProperty("LastUpdatedStampString").toString().replace("T", " ");
				entity.setLastUpdatedStamp(Util.formatDate(updatStr));
				entity.setStatus(interactionRelationshipObject.getProperty("State").toString());
				coauthorList.add(entity);
			}
		}
	}
	public ArrayList<InteractionRelationshipEntity> getCoauthorList() {
		return coauthorList;
	}
	public void setCoauthorList(ArrayList<InteractionRelationshipEntity> coauthorList) {
		this.coauthorList = coauthorList;
	}
	
	public String getRescode() {
		return rescode;
	}
}
