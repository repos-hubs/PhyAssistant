package com.jibo.data.entity;

public class SyncHistoryEntity {
	private String username;
	private int vid;
	private String vName;
	private int vColId;
	private int vparentId;
	private String time;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getVid() {
		return vid;
	}
	public void setVid(int vid) {
		this.vid = vid;
	}
	public String getvName() {
		return vName;
	}
	public void setvName(String vName) {
		this.vName = vName;
	}
	public int getvColId() {
		return vColId;
	}
	public void setvColId(int vColId) {
		this.vColId = vColId;
	}
	public int getVparentId() {
		return vparentId;
	}
	public void setVparentId(int vparentId) {
		this.vparentId = vparentId;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
