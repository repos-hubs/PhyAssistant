package com.jibo.data.entity;

import java.io.Serializable;
/**药品信息详细数据
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class InteractionProductEntity extends SimpleDrugInfoEntity implements Serializable{

	private String key; 
	private String drugName;
	private String cultureInfo; 
	private String createdStamp; 
	private String lastUpdatedStamp; 
	private String status;
	private String pid; 
	private String isOTC;
	private String isTCM;
	private String isAHFS;
	private String saleRank;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getDrugName() {
		return drugName;
	}
	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}
	public String getCultureInfo() {
		return cultureInfo;
	}
	public void setCultureInfo(String cultureInfo) {
		this.cultureInfo = cultureInfo;
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
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getIsOTC() {
		return isOTC;
	}
	public void setIsOTC(String isOTC) {
		this.isOTC = isOTC;
	}
	public String getIsTCM() {
		return isTCM;
	}
	public void setIsTCM(String isTCM) {
		this.isTCM = isTCM;
	}
	public String getIsAHFS() {
		return isAHFS;
	}
	public void setIsAHFS(String isAHFS) {
		this.isAHFS = isAHFS;
	}
	public String getSaleRank() {
		return saleRank;
	}
	public void setSaleRank(String saleRank) {
		this.saleRank = saleRank;
	}
	
	
}
