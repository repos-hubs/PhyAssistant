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
import com.jibo.data.entity.InteractionProductEntity;

public class InteractionDrugProductPaser extends SoapDataPaser{
	private ArrayList<InteractionProductEntity> coauthorList;
	private String rescode = "0";
	@Override
	public void paser(SoapSerializationEnvelope response) {
		coauthorList = new ArrayList<InteractionProductEntity>();
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
				SoapObject interactionProductObject = (SoapObject) detail.getProperty(i);
				InteractionProductEntity entity = new InteractionProductEntity();
				entity.setKey(interactionProductObject.getProperty("Key").toString());
				entity.setDrugName(interactionProductObject.getProperty("Name").toString());
				entity.setCultureInfo(interactionProductObject.getProperty("CultureInfo").toString());
				entity.setLastUpdatedStamp(Util.formatDate(interactionProductObject.getProperty("LastUpdatedStamp").toString().replace("T", " ")));
				entity.setStatus(interactionProductObject.getProperty("State").toString());
				entity.setIsOTC(interactionProductObject.getProperty("IsOTC").toString());
				entity.setIsTCM(interactionProductObject.getProperty("IsTCM").toString());
				entity.setIsAHFS(interactionProductObject.getProperty("IsAHFS").toString());
				entity.setSaleRank(interactionProductObject.getProperty("SaleRank").toString());
				entity.setPid(interactionProductObject.getProperty("DrugProductId").toString());
				coauthorList.add(entity);
			}
		}
	}
	
	public ArrayList<InteractionProductEntity> getCoauthorList() {
		return coauthorList;
	}
	public void setCoauthorList(ArrayList<InteractionProductEntity> coauthorList) {
		this.coauthorList = coauthorList;
	}
	
	public String getRescode() {
		return rescode;
	}

	
}
