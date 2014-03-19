package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class PackageInfoEntity implements Parcelable {
	private String title;
	private String type;
	private String full_url;
	private String update_url;
	private String dbName;
	private String dataType;
	private String version;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getDbName() {
		return dbName;
	}
	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getFull_url() {
		return full_url;
	}
	public void setFull_url(String full_url) {
		this.full_url = full_url;
	}
	public String getUpdate_url() {
		return update_url;
	}
	public void setUpdate_url(String update_url) {
		this.update_url = update_url;
	}

	public static final Parcelable.Creator<PackageInfoEntity> CREATOR = new Creator<PackageInfoEntity>() {  
		@Override
		public PackageInfoEntity createFromParcel(Parcel source) {
			PackageInfoEntity pif = new PackageInfoEntity();
			pif.setTitle(source.readString());
			pif.setType(source.readString());
			pif.setFull_url(source.readString());
			pif.setUpdate_url(source.readString());
			pif.setDbName(source.readString());
			pif.setDataType(source.readString());
			pif.setVersion(source.readString());
			return pif;  
		}
		
		@Override  
		public PackageInfoEntity[] newArray(int size) {  
		return new PackageInfoEntity[size];  
		}  
	}; 
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);  
		dest.writeString(type);
		dest.writeString(full_url);
		dest.writeString(update_url);
		dest.writeString(dbName);
		dest.writeString(dataType);
		dest.writeString(version);
	}
}
