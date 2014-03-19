package com.jibo.data.entity;

public class SurveyHistoryEntity {
	private String id;
	private String title;
	private String submitTime;
	private String pay;
	private String payUnit;
	private String surveyStatus;
	private String resultRemark;
	private String updateTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubmitTime() {
		return submitTime;
	}
	public void setSubmitTime(String submitTime) {
		this.submitTime = submitTime;
	}
	public String getPay() {
		return pay;
	}
	public void setPay(String pay) {
		this.pay = pay;
	}
	public String getPayUnit() {
		return payUnit;
	}
	public void setPayUnit(String payUnit) {
		this.payUnit = payUnit;
	}
	public String getSurveyStatus() {
		if(surveyStatus==null) surveyStatus="";
		return surveyStatus;
	}
	public void setSurveyStatus(String surveyStatus) {
		this.surveyStatus = surveyStatus;
	}
	public String getResultRemark() {
		if(resultRemark==null) resultRemark="";
		return resultRemark;
	}
	public void setResultRemark(String resultRemark) {
		this.resultRemark = resultRemark;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	
}
