package com.jibo.data.entity;

import java.io.Serializable;
/**药品信息详细数据
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class InteractionPidsEntity extends SimpleDrugInfoEntity implements Serializable{

	private String key; 
	private String createdStamp; 
	private String lastUpdatedStamp; 
	private String status;
	private String interactionId; 
	private String pid1; 
	private String pid2;
	private String pid1DrugName; 
	private String pid2DrugName;
	private String pid1InteractionNum;
	private String pid2InteractionNum; 
	public String getPid1InteractionNum() {
		return pid1InteractionNum;
	}
	public void setPid1InteractionNum(String pid1InteractionNum) {
		this.pid1InteractionNum = pid1InteractionNum;
	}
	public String getPid2InteractionNum() {
		return pid2InteractionNum;
	}
	public void setPid2InteractionNum(String pid2InteractionNum) {
		this.pid2InteractionNum = pid2InteractionNum;
	}
	public String getPid1DrugName() {
		return pid1DrugName;
	}
	public void setPid1DrugName(String pid1DrugName) {
		this.pid1DrugName = pid1DrugName;
	}
	public String getPid2DrugName() {
		return pid2DrugName;
	}
	public void setPid2DrugName(String pid2DrugName) {
		this.pid2DrugName = pid2DrugName;
	}
	public String getPid1() {
		return pid1;
	}
	public void setPid1(String pid1) {
		this.pid1 = pid1;
	}
	public String getPid2() {
		return pid2;
	}
	public void setPid2(String pid2) {
		this.pid2 = pid2;
	}
	private String comments; 
	private String drugInfo; 
	private String description; 
	private String cultureInfo; 
	public String getInteractionId() {
		return interactionId;
	}
	public void setInteractionId(String interactionId) {
		this.interactionId = interactionId;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getDrugInfo() {
		return drugInfo;
	}
	public void setDrugInfo(String drugInfo) {
		this.drugInfo = drugInfo;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCultureInfo() {
		return cultureInfo;
	}
	public void setCultureInfo(String cultureInfo) {
		this.cultureInfo = cultureInfo;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getCreatedStamp() {
		return createdStamp;
	}
	public void setCreatedStamp(String createdStamp) {
		this.createdStamp = createdStamp;
	}
	public String getLastUpdatedStamp() {
		return lastUpdatedStamp;
	}
	public void setLastUpdatedStamp(String lastUpdatedStamp) {
		this.lastUpdatedStamp = lastUpdatedStamp;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	
}
