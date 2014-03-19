package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.SurveyHistoryEntity;

public class SurveyMineHistory extends SoapDataPaser {
	private ArrayList<SurveyHistoryEntity> historyList;

	@Override
	public void paser(SoapSerializationEnvelope response) {
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result.getProperty("NewGetSurveyHistoryResult");
		historyList = new ArrayList<SurveyHistoryEntity>();
		
		System.out.println("result     "+result);
		System.out.println("resultList     "+resultList);
		for(int i=0; i<resultList.getPropertyCount(); i++) {
			SoapObject historyObj = (SoapObject) resultList.getProperty(i);
			SurveyHistoryEntity en = new SurveyHistoryEntity();
			String propertyValue = historyObj.getProperty("surveyID").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setId(propertyValue);
			} else {
				en.setId("");
			}
			
			propertyValue = historyObj.getProperty("title").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setTitle(propertyValue);
			} else {
				en.setTitle("");
			}
			
			propertyValue = historyObj.getProperty("submitTime").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setSubmitTime(propertyValue);
			} else {
				en.setSubmitTime("");
			}
			
			propertyValue = historyObj.getProperty("pay").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setPay(propertyValue);
			} else {
				en.setPay("");
			}
			
			propertyValue = historyObj.getProperty("payUnit").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setPayUnit(propertyValue);
			} else {
				en.setPayUnit("");
			}
			
			propertyValue = historyObj.getProperty("surveyStatus").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setSurveyStatus(propertyValue);
			} else {
				en.setSurveyStatus("");
			}
			
			propertyValue = historyObj.getProperty("resultRemark").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setResultRemark(propertyValue);
			} else {
				en.setResultRemark("");
			}
			
			propertyValue = historyObj.getProperty("updateTime").toString();
			if (!"anyType{}".equals(propertyValue)) {
				en.setUpdateTime(propertyValue);
			} else {
				en.setUpdateTime("");
			}
			
			historyList.add(en);
		}
		
	}

	public ArrayList<SurveyHistoryEntity> getHistoryList() {
		return historyList;
	}

	public void setHistoryList(ArrayList<SurveyHistoryEntity> historyList) {
		this.historyList = historyList;
	}

}
