package com.jibo.data.entity;

import java.util.ArrayList;

public class DownloadPacketEntity {
	private String appVersion;
	private String appURL;
	private String updateType;
	private int packetCount;
	ArrayList<PacketEntity> dataPacket;
	
	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getAppURL() {
		return appURL;
	}

	public void setAppURL(String appURL) {
		this.appURL = appURL;
	}

	public String getUpdateType() {
		return updateType;
	}

	public void setUpdateType(String updateType) {
		this.updateType = updateType;
	}

	public int getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}

	public ArrayList<PacketEntity> getDataPacket() {
		return dataPacket;
	}

	public void setDataPacket(ArrayList<PacketEntity> dataPacket) {
		this.dataPacket = dataPacket;
	}

}
