package com.jibo.data.entity;

import java.io.Serializable;
/**药品信息详细数据
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class InteractionEntity extends SimpleDrugInfoEntity implements Serializable{

	private String interactionId; 
	private String comments; 
	private String drug; 
	private String description; 
	private String cultureInfo; 
	private String key; 
	private String createdStamp; 
	private String lastUpdatedStamp; 
	private String status;
	private String iid;
	private String drugInfo;
	public String getDrugInfo() {
		return drugInfo;
	}
	public void setDrugInfo(String drugInfo) {
		this.drugInfo = drugInfo;
	}
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
	public String getDrug() {
		return drug;
	}
	public void setDrug(String drugInfo) {
		this.drug = drug;
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
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	
}
