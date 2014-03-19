package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class SurveyEntity implements Parcelable{
	private String ID;
	private String title;
	private String content;
	private String estimateTime;
	private String pay;
	private String payUnit;
	private String pCount;
	private String region;
	private String city;
	private String hospitalLevel;
	private String surveySource;
	private String department;
	private String isVerify;
	private String startTime;
	private String limitPersonCount;
	private String endTime;
	private String keyWords;
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getLimitPersonCount() {
		return limitPersonCount;
	}
	public void setLimitPersonCount(String limitPersonCount) {
		this.limitPersonCount = limitPersonCount;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDepartment() {
		if(department==null) department = "";
		return department;
	}
	public void setDepartment(String department) {
		this.department = department;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getEstimateTime() {
		return estimateTime;
	}
	public void setEstimateTime(String estimateTime) {
		this.estimateTime = estimateTime;
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
	public String getpCount() {
		return pCount;
	}
	public void setpCount(String pCount) {
		this.pCount = pCount;
	}
	public String getRegion() {
		if(region==null) region="";
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getHospitalLevel() {
		return hospitalLevel;
	}
	public void setHospitalLevel(String hospitalLevel) {
		this.hospitalLevel = hospitalLevel;
	}
	public String getSurveySource() {
		return surveySource;
	}
	public void setSurveySource(String surveySource) {
		this.surveySource = surveySource;
	}
	public String getIsVerify() {
		return isVerify;
	}
	public void setIsVerify(String isVerify) {
		this.isVerify = isVerify;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getKeyWords() {
		return keyWords;
	}
	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}
	public static final Parcelable.Creator<SurveyEntity> CREATOR = new Creator<SurveyEntity>() {  
		@Override
		public SurveyEntity createFromParcel(Parcel source) {
			SurveyEntity surveyEntity = new SurveyEntity();  
			surveyEntity.setID(source.readString());
			surveyEntity.setTitle(source.readString());
			surveyEntity.setContent(source.readString());
			surveyEntity.setKeyWords(source.readString());
			surveyEntity.setEstimateTime(source.readString());
			surveyEntity.setPay(source.readString());
			surveyEntity.setPayUnit(source.readString());
			surveyEntity.setRegion(source.readString());
			surveyEntity.setCity(source.readString());
			surveyEntity.setHospitalLevel(source.readString());
			surveyEntity.setSurveySource(source.readString());
			surveyEntity.setSurveySource(source.readString());
			surveyEntity.setIsVerify(source.readString());
			surveyEntity.setpCount(source.readString());
			surveyEntity.setLimitPersonCount(source.readString());
			surveyEntity.setStartTime(source.readString());
			surveyEntity.setEndTime(source.readString());
			return surveyEntity;  
		}
		
		@Override  
		public SurveyEntity[] newArray(int size) {  
			return new SurveyEntity[size];  
		}  
	}; 
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(ID);  
		dest.writeString(title);
		dest.writeString(content);
		dest.writeString(keyWords);
		dest.writeString(estimateTime);
		dest.writeString(pay);
		dest.writeString(payUnit);
		dest.writeString(region);
		dest.writeString(city);
		dest.writeString(hospitalLevel);
		dest.writeString(surveySource);
		dest.writeString(department);
		dest.writeString(isVerify);
		dest.writeString(pCount);
		dest.writeString(limitPersonCount);
		dest.writeString(startTime);
		dest.writeString(endTime);
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SurveyEntity other = (SurveyEntity) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}
}
