package com.jibo.data.entity;

public class DownloadInfoEntity {
	private int id;
	private String title;
	private long packageSize;
	private long hasRead;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public long getPackageSize() {
		return packageSize;
	}
	public void setPackageSize(long packageSize) {
		this.packageSize = packageSize;
	}
	public long getHasRead() {
		return hasRead;
	}
	public void setHasRead(long hasRead) {
		this.hasRead = hasRead;
	}
	
}
