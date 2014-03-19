package com.jibo.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class ProfileEntity implements Parcelable{
	private String id;
	private String name;
	private String rankInChina;
	private String rankInHospital;
	private String paperCount;
	private String coauthorCount;
	private String cahsp;
	private String caohsp;
	private String keywords;
	private String cagrp;
	private String bigSpecialty;
	private String specialty;
	private String hospitalName;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRankInChina() {
		return rankInChina;
	}
	public void setRankInChina(String rankInChina) {
		this.rankInChina = rankInChina;
	}
	public String getRankInHospital() {
		return rankInHospital;
	}
	public void setRankInHospital(String rankInHospital) {
		this.rankInHospital = rankInHospital;
	}
	public String getPaperCount() {
		return paperCount;
	}
	public void setPaperCount(String paperCount) {
		this.paperCount = paperCount;
	}
	public String getCoauthorCount() {
		return coauthorCount;
	}
	public void setCoauthorCount(String coauthorCount) {
		this.coauthorCount = coauthorCount;
	}
	public String getCahsp() {
		return cahsp;
	}
	public void setCahsp(String cahsp) {
		this.cahsp = cahsp;
	}
	public String getCaohsp() {
		return caohsp;
	}
	public void setCaohsp(String caohsp) {
		this.caohsp = caohsp;
	}
	public String getKeywords() {
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	public String getCagrp() {
		return cagrp;
	}
	public void setCagrp(String cagrp) {
		this.cagrp = cagrp;
	}
	public String getBigSpecialty() {
		return bigSpecialty;
	}
	public void setBigSpecialty(String bigSpecialty) {
		this.bigSpecialty = bigSpecialty;
	}
	public String getSpecialty() {
		return specialty;
	}
	public void setSpecialty(String specialty) {
		this.specialty = specialty;
	}
	public String getHospitalName() {
		return hospitalName;
	}
	public void setHospitalName(String hospitalName) {
		this.hospitalName = hospitalName;
	}
	
	public static final Parcelable.Creator<ProfileEntity> CREATOR = new Creator<ProfileEntity>() {  
		@Override
		public ProfileEntity createFromParcel(Parcel source) {
			ProfileEntity profileEntity = new ProfileEntity();  
			profileEntity.setId(source.readString());
			profileEntity.setName(source.readString());
			profileEntity.setRankInChina(source.readString());
			profileEntity.setRankInHospital(source.readString());
			profileEntity.setPaperCount(source.readString());
			profileEntity.setCoauthorCount(source.readString());
			profileEntity.setCahsp(source.readString());
			profileEntity.setCaohsp(source.readString());
			profileEntity.setKeywords(source.readString());
			profileEntity.setCagrp(source.readString());
			profileEntity.setBigSpecialty(source.readString());
			profileEntity.setSpecialty(source.readString());
			profileEntity.setHospitalName(source.readString());
			return profileEntity;  
		}
		
		@Override  
		public ProfileEntity[] newArray(int size) {  
		return new ProfileEntity[size];  
		}  
	}; 
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);  
		dest.writeString(name);
		dest.writeString(rankInChina);
		dest.writeString(rankInHospital);
		dest.writeString(paperCount);
		dest.writeString(coauthorCount);
		dest.writeString(cahsp);
		dest.writeString(caohsp);
		dest.writeString(keywords);
		dest.writeString(cagrp);
		dest.writeString(bigSpecialty);
		dest.writeString(specialty);
		dest.writeString(hospitalName);
	}
}
