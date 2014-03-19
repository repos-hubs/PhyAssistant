package com.jibo.data;

import java.util.ArrayList;

import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;

import com.jibo.data.entity.MarketPackageEntity;
import com.jibo.data.entity.SurveyEntity;

public class SurveyInfoPaser extends SoapDataPaser{
	private ArrayList<SurveyEntity> surveyList;
	private String ID;
	private String title;
	private String estimateTime;
	private String pay;
	private String payUnit;
	private String pCount;
	private String region;
	private String city;
	private String hospitalLevel;
	private String surveySource;
	private String isVerify;
	@Override
	public void paser(SoapSerializationEnvelope response) {
		surveyList = new ArrayList<SurveyEntity>();
		SoapObject result = (SoapObject) response.bodyIn;
		SoapObject resultList = (SoapObject) result.getProperty("NewGetSurveyListResult");
		String propertyValue;
//        <surveyID>string</surveyID>
//        <title>string</title>
//        <content>string</content>
//        <keyWords>string</keyWords>
//        <estimatedTime>string</estimatedTime>
//        <pay>string</pay>
//        <payUnit>string</payUnit>
//        <region>string</region>
//        <city>string</city>
//        <hospitalLevel>string</hospitalLevel>
//        <surveySource>string</surveySource>
//        <isVerify>string</isVerify>
//        <pCount>string</pCount>
//        <beginTime>string</beginTime>
//        <endTime>string</endTime>
		for(int i=0; i<resultList.getPropertyCount(); i++) {
			SurveyEntity entity = new SurveyEntity();
			SoapObject surveyObject = (SoapObject) resultList.getProperty(i);
			propertyValue = surveyObject.getProperty("surveyID").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setID(propertyValue);
			} else {
				entity.setTitle("");
			}
				
			propertyValue = surveyObject.getProperty("title").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setTitle(propertyValue);
			} else {
				entity.setTitle("");
			}
			
			propertyValue = surveyObject.getProperty("content").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setContent(propertyValue);
			} else {
				entity.setContent("");
			}
			
			propertyValue = surveyObject.getProperty("estimatedTime").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setEstimateTime(propertyValue);
			} else {
				entity.setEstimateTime("");
			}
			
			propertyValue = surveyObject.getProperty("pay").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setPay(propertyValue);
			} else {
				entity.setPay("");
			}
			
			propertyValue = surveyObject.getProperty("payUnit").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setPayUnit(propertyValue);
			} else {
				entity.setPayUnit("");
			}
			
			propertyValue = surveyObject.getProperty("pCount").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setpCount(propertyValue);
			} else {
				entity.setpCount("");
			}
				
			propertyValue = surveyObject.getProperty("region").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setRegion(propertyValue);
			} else {
				entity.setRegion("");
			}
			
			propertyValue = surveyObject.getProperty("city").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setCity(propertyValue);
			} else {
				entity.setCity("");
			}
			
			propertyValue = surveyObject.getProperty("hospitalLevel").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setHospitalLevel(propertyValue);
			} else {
				entity.setHospitalLevel("");
			}
			propertyValue = surveyObject.getProperty("department").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setDepartment(propertyValue);
			} else {
				entity.setDepartment("");
			}
			
			propertyValue = surveyObject.getProperty("surveySource").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setSurveySource(propertyValue);
			} else {
				entity.setSurveySource("");
			}
			
			propertyValue = surveyObject.getProperty("limitCount").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setLimitPersonCount(propertyValue);
			} else {
				entity.setLimitPersonCount("");
			}
			
			propertyValue = surveyObject.getProperty("beginTime").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setStartTime(propertyValue);
			} else {
				entity.setStartTime("");
			}
			
			propertyValue = surveyObject.getProperty("endTime").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setEndTime(propertyValue);
			} else {
				entity.setEndTime("");
			}
			propertyValue = surveyObject.getProperty("keyWords").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setKeyWords(propertyValue);
			} else {
				entity.setKeyWords("");
			}
			propertyValue = surveyObject.getProperty("isVerify").toString();
			if(!"anyType{}".equals(propertyValue)) {
				entity.setIsVerify(propertyValue);
			} else {
				entity.setIsVerify("");
			}
			surveyList.add(entity);
		}
	}
	public ArrayList<SurveyEntity> getSurveyList() {
		return surveyList;
	}
	public void setSurveyList(ArrayList<SurveyEntity> surveyList) {
		this.surveyList = surveyList;
	}
	
}
