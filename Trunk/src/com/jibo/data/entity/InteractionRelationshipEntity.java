package com.jibo.data.entity;

import java.io.Serializable;
/**药品信息详细数据
 * @author peter.pan
 * */
@SuppressWarnings("serial")
public class InteractionRelationshipEntity extends SimpleDrugInfoEntity implements Serializable{

	private String key; 
	private String pid; 
	private String iid; 
	private String role; 
	private String createdStamp; 
	private String lastUpdatedStamp; 
	private String status;
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getIid() {
		return iid;
	}
	public void setIid(String iid) {
		this.iid = iid;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
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
